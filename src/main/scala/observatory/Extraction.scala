package observatory

import java.nio.file.Paths
import java.time.LocalDate

import org.apache.spark.HashPartitioner
import org.apache.spark.sql.SparkSession

/**
  * 1st milestone: data extraction
  */
object Extraction {

  import org.apache.log4j.{Level, Logger}
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Observatory")
      .config("spark.master", "local")
      .getOrCreate()

  /** @return The filesystem path of the given resource */
  def fsPath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString

  def tempFtoC(tempCelsius: Double): Double = (tempCelsius - 32) * 5 / 9

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    // TODO: Revisit partitioning
      val partitioner = new HashPartitioner(10)

    // Read Stations file and filter out those without GPS coords
    // Convert Lat/Lon into Location objects
    val stationsRawRDD = spark.sparkContext.textFile(fsPath(stationsFile)).map(_.split((","), -1))
    val stationFilteredRDD = stationsRawRDD.filter(parts => parts(2) != "" && parts(3) != "")

    val stationsRDD = stationFilteredRDD.map(parts => {
      val station = Station(parts(0), parts(1))
      val location = Location(parts(2).toDouble, parts(3).toDouble)
      (station, location)
    }).partitionBy(partitioner)

    // Read Temperatures file and: convert Month/Day into LocalDate objects, convert Temperatures to Celsius
    val tempsRDD = spark.sparkContext.textFile(fsPath(temperaturesFile)).map(line => {
      val parts = line.split(",")
      val station = Station(parts(0), parts(1))
      val date = LocalDate.of(year, parts(2).toInt, parts(3).toInt)
      val tempC = tempFtoC(parts(4).toDouble)
      (station, (date, tempC))
    }).partitionBy(partitioner)

    // Join DFs to eliminate data for stations missing GPS, returning LocalDate, Location, Temperature
    val joinedRDD = stationsRDD.join(tempsRDD).map(_ match {
      case (_, (location: Location, (date: LocalDate, temperature: Temperature))) => {
        (date, location, temperature)
      }
    })

    joinedRDD.collect().toSeq
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    val recordsRDD = spark.sparkContext.parallelize(records.toSeq)
    val pairedRDD = recordsRDD.map(_ match {
      case (date: LocalDate, location: Location, temperature: Temperature) => (location, temperature)
    })

    val aggRDD = pairedRDD.aggregateByKey((0.0, 0))((acc, value) => (acc._1 + value, acc._2 + 1), (acc1, acc2) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
    val avgRDD = aggRDD.mapValues(x => x._1 / x._2)
    avgRDD.collect().toSeq
  }

}
