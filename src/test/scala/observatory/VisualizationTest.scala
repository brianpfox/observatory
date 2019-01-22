package observatory


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

trait VisualizationTest extends FunSuite {//} with Checkers {
  test("greatCircleDistance - A to A") {
    val a = Location(51.477500, -0.461388)
    val b = Location(51.477500, -0.461388)

    val dist = Visualization.greatCircleDistance(a, b)
    assert(dist === 0)
  }

  test("greatCircleDistance - antipodes") {
    val a = Location(90, 0)
    val b = Location(-90, -180)

    val dist = Visualization.greatCircleDistance(a, b)
    assert(dist === scala.math.Pi * Visualization.EARTH_RADIUS)
  }

  //  test("greatCircleDistance - LHR to SYD") {
//    val lhr = Location(51.477500, -0.461388)
//    val syd = Location(-33.946110, 151.177222)
//
//    val dist = Visualization.greatCircleDistance(lhr, syd)
//    assert(dist === Visualization.EARTH_RADIUS * )
//  }

}
