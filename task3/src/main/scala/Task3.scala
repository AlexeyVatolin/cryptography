import BinaryTools.{IntToBase, fastPow, toBinary}
import ConsoleTools.greeting
import Gf256.{multiply, toPolinomial}
import spire.math.{UByte, UInt}

import scala.io.StdIn.{readChar, readInt, readLine}

object Task3 {
  def main(args: Array[String]): Unit = {
    val number = greeting()
    number match {
      case 1 => task1()
      case 2 => task2()
      case 3 => task3()
      case 4 => task4()
      case _ => println("Invalid task number")
    }
  }

  def task1(): Unit = {
    println("Fast power")
    println("Input a, b and modulo")
    val a = readInt()
    val b = readInt()
    val modulo = readInt()

    println(fastPow(a, b, modulo))
  }

  def task2(): Unit = {
    println("Extended Euclidean algorithm")
    println("Input two int numbers")
    val n = readInt()
    val m = readInt()
    val (d, a, b) = Euclidean.gcdex(n, m)
    println(s"gcd($n, $m) = $d = $a * $n + $b * $m")
    //gcd(1398, 324) = 6 = −19(1398) + 82(324)
  }

  def task3(): Unit = {
    println("RSA")

    val rsa = new RSA()
    println("Input number to encrypt")
    val number = readInt()
    println(s"p = ${rsa.p}")
    println(s"q = ${rsa.q}")
    println(s"e = ${rsa.e}")
    val encrypted = rsa.encrypt(number, rsa.publicKey)
    val decrypted = rsa.decrypt(encrypted, rsa.privateKey)
    println(s"Encrypted number = $encrypted")
    println(s"Decrypted number = $decrypted")

    if (number == decrypted)
      println("Decrypted number equal with initial number")
    else
      println("Decrypted number not equal with initial number")

  }

  def task4(): Unit = {
    println("GF(256)")
    println("Input operation name")
    println("p - get polynomial form of GF(256) element ")
    println("m - multiply two GF(256) polynomials ")
    val op = readChar()

    if (op == 'p') {
      println("Input GF(256) element in binary format")
      val number = readLine()
      println(toPolinomial(number))
    } else if (op == 'm') {
      println("Input two numbers in binary format")
      val a = readLine().b
      val b = readLine().b
      val result = multiply(UByte(a), UByte(b))
      println(s"result of multiplication = ${toBinary(UInt(result.toInt))}")
    } else
      println("Unsupported operation")
    /*
    10110110 * 1010011 = 110110
    0xb6 * 0x53 = 0x36
     */
  }
}
