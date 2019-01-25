package observatory


import org.junit.runner.RunWith
import org.scalactic.TolerantNumerics
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

trait VisualizationTest extends FunSuite {//} with Checkers {
  implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.01)

  test("greatCircleDistance - A to A") {
    val a = Location(51.477500, -0.461388)
    val b = Location(51.477500, -0.461388)

    val dist = Visualization.greatCircleDistance()(a, b)
    assert(dist === 0.0)
  }

  test("greatCircleDistance - antipodes") {
    val a = Location(90, 0)
    val b = Location(-90, -180)
    val c = Location(0, -90)
    val d = Location(0, 90)

    assert(Visualization.greatCircleDistance(1.0)(a, b) === scala.math.Pi * 1.0, "90, 0  vs -90, -180")
    assert(Visualization.greatCircleDistance(1.0)(c, d) === scala.math.Pi * 1.0, "0, -90 vs 0, 90")
  }

  test("greatCircleDistance - other") {
    val a = Location(90, 0)
    val b = Location(90, -180)

    val dist = Visualization.greatCircleDistance(1.0)(a, b)
    assert(dist > 0)
  }

  test("predictTemperature") {
    val loc_temp = List(
      (Location(90, 0), 20.0),
      (Location(90, 90), 10.0),
      (Location(-90, -180), 15.0),
      (Location(0, -90), 5.0),
      (Location(0, 90), 5.0)
    )
    TolerantNumerics
    val target =
    assert(Visualization.predictTemperature(loc_temp, Location(90, 0)) === 20.0,
      "same location as one of the points 1")
    assert(Visualization.predictTemperature(loc_temp, Location(90, 90)) === 10.0,
      "same location as one of the points 2")
    assert(Visualization.predictTemperature(loc_temp, Location(90, 90.0001)) === 10.0,
      "very close location to one of the points")
  }

}
