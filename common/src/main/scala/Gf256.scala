import spire.math.UByte

object Gf256 {
  def toPolinomial(number: String): String = {
    number.reverse
          .zipWithIndex
          .filter(_._1 == '1')
          .map(x => x._2 match {
            case 0 => "1"
            case 1 => "x"
            case _ => s"x^${x._2}"
          })
          .reverse
          .mkString("", " + ", "")
  }

  def multiply(a: UByte, b: UByte): UByte = {
    var p: UByte = UByte(0)
    var a_ = a
    var b_ = b
    for (_ <- 0 until 8) {
      if ((b_ & UByte(1)) != UByte(0)) {
        p = p ^ a_
      }
      val hi_bit_set = (a_ & UByte(0x80)) != UByte(0)
      a_ = a_ <<  1
      if (hi_bit_set) {
        a_ = a_ ^ UByte(0x1B)
      } /* x^8 + x^4 + x^3 + x + 1 */
      b_ = b_ >> 1
    }
    p
  }

}
