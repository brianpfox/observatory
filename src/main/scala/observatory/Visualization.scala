package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math.{Pi, sin, cos, acos, abs}

/**
  * 2nd milestone: basic visualization
  */
object Visualization extends SparkSessionTrait {

  val RADIUS: Double = 6371.0
  final val p: Double = 2.0

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val tempRDD = spark.sparkContext.parallelize(temperatures.toSeq)

    // map over tempRDD computing distance from each location to the target location.
    val dist_tempRDD = tempRDD.map(_ match {
      case (knownLocation: Location, temperature: Temperature) =>
        (greatCircleDistance()(location, knownLocation), temperature)
    }).cache()

    val zeroRDD = dist_tempRDD.filter(_._1 == 0.0)
    if (zeroRDD.count() > 0) zeroRDD.take(1)(0)._2
    else {
      //https://en.wikipedia.org/wiki/Inverse_distance_weighting
      val weightedTempsRDD = dist_tempRDD.map(_ match {
        case (distance: Double, temperature: Temperature) => {
          if (distance > 1) {
            (1 / Math.pow(distance, p)) * temperature
          }
          else
            temperature
        }
      })
      weightedTempsRDD.sum()
    }
  }

  def greatCircleDistance(radius: Double = Visualization.RADIUS)(arbitraryPoint: Location, knownPoint: Location): Double = {
    val deltaSig = {
      if (arbitraryPoint == knownPoint) 0
      else {
        val antiLon = if (knownPoint.lon >= 0) -1 * (180 - knownPoint.lon) else (180 + knownPoint.lon)
        if (arbitraryPoint.lat == -1 * knownPoint.lat && arbitraryPoint.lon == antiLon) Pi
        else {
          val sins = sin(arbitraryPoint.lat) * sin(knownPoint.lat)
          val coss = cos(arbitraryPoint.lat) * cos(knownPoint.lat) * cos(abs(arbitraryPoint.lon - knownPoint.lon))
          acos(sins + coss)
        }
      }
    }

    radius * deltaSig
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    ???
  }

}

