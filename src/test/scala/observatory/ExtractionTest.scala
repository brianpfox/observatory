package observatory

import java.time.LocalDate

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

trait ExtractionTest extends FunSuite {
  test("locateTemperatures") {
    val result = Extraction.locateTemperatures(2019, "/stations.csv", "/testTemps.csv")
    assert(result.toSet === Set(
      (LocalDate.of(2019, 8, 11), Location(37.35,-78.433), 27.299999999999997),
      (LocalDate.of(2019, 12, 6), Location(37.358,-78.438),0.0),
      (LocalDate.of(2019, 1, 29), Location(37.358,-78.438),2.000000000000001)
    ), "one should drop out due to missing location")
  }

  test("locationYearlyAverageRecords") {
    val temps = Extraction.locateTemperatures(2019, "/stations.csv", "/testTemps.csv")
    val result = Extraction.locationYearlyAverageRecords(temps)
    assert(result.toSet === Set(
      (Location(37.358,-78.438),1.0000000000000004),
      (Location(37.35,-78.433),27.299999999999997)
    ), "small test file")
  }

//  test("locationYearlyAverageRecords full file") {
//    val temps = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")
//    val result = Extraction.locationYearlyAverageRecords(temps)
//    assert(), "full size file")
//  }
}
