package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math.{Pi, sin, cos, acos, abs}

/**
  * 2nd milestone: basic visualization
  */
object Visualization extends SparkSessionTrait {

  final val EARTH_RADIUS: Double = 6371.0

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val tempRDD = spark.sparkContext.parallelize(temperatures.toSeq)

    // map over tempRDD computing distance from each location to the target location.
    ???
  }

  def greatCircleDistance(arbitraryPoint: Location, knownPoint: Location): Double = {
    val deltaSig = {
      if (arbitraryPoint == knownPoint) 0
      else if (arbitraryPoint.lat == -1 * knownPoint.lat && arbitraryPoint.lon == -1 * (180 - knownPoint.lon)) Pi
      else {
        val sins = sin(arbitraryPoint.lat) * sin(knownPoint.lat)
        val coss = cos(arbitraryPoint.lat) * cos(knownPoint.lat) * cos(abs(arbitraryPoint.lon - knownPoint.lon))
        acos(sins + coss)
      }
    }

    EARTH_RADIUS * deltaSig
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

