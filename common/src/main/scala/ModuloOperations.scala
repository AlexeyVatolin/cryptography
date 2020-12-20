import Euclidean.gcdex

class ModuloOperations(val modulo: Int) {
  def add(a: Int, b: Int): Int = {
    (a + b) % modulo
  }

  def sub(a: Int, b: Int): Int = {
    (a - b) % modulo
  }

  def multiply(a: Int, b: Int): Int = {
    (a * b) % modulo
  }

  def div(a: Int, b: Int): Int = {
    val (d, x, _) = gcdex(b, modulo)
    if (d != 1)
      throw new ArithmeticException
    val b_inv = (x % modulo + modulo) % modulo
    (a * b_inv) % modulo
  }


  def pow(a: Int, b: Int): Int = {
    if (b == 1) return a
    if (b % 2 == 0) {
      val temp = pow(a, (b / 2))
      return (temp * temp)  % modulo
    }
    a * pow(a, b - 1)  % modulo
  }
}
