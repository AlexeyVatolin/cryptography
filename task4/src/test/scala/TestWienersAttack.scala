import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, equal}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class TestWienersAttack extends AnyFlatSpec {
  def wiener = new WienersAttack()

  it should "attack" in {
    val d = wiener.attack(1073780833, 1220275921)
    d should equal(25)

    val d2 = wiener.attack(BigInt("1779399043"), BigInt("2796304957"))
    d2 should equal(11)

    val d3 = wiener.attack(17993, 90581)
    d3 should equal(5)
  }

  it should "continued fractions" in {
    val trueValues = Array(0, 5, 29, 4, 1, 3, 2, 4)
    val values = wiener.getContinuedFractions(a = 17993 / 90581.0).take(trueValues.length).toArray
    values should be(trueValues)
  }

  it should "suitable fractions" in {
    val k = Array(0, 1, 29, 117, 146, 555, 1256, 5579)
    val d = Array(1, 5, 146, 589, 735, 2794, 6323, 28086)
    val values = wiener.suitableFractions(a = 17993 / 90581.0).take(k.length).toArray
//    val values = wiener.suitableFractions(a = 3367 / 1001.0).take(k.length).toArray
    values should be(k.zip(d).map(x => Fraction(x._1, x._2)))
  }
}
