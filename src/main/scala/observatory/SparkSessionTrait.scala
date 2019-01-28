package observatory

import org.apache.spark.sql.SparkSession

trait SparkSessionTrait {
  import org.apache.log4j.{Level, Logger}
//  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Observatory")
      .config("spark.master", "local")
      .getOrCreate()


}
