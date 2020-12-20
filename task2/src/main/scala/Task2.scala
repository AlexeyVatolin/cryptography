import ConsoleTools.greeting
import Euclidean.gcd

import scala.io.StdIn.{readChar, readInt}

object Task2 {
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
    println("All prime numbers less than m")
    println("Input m")
    val m = readInt()

    val primes = new SieveOfEratosthenes(m)
    println(primes.getPrimeNumbers.mkString("", ", ", ""))
  }

  def task2(): Unit = {
    println("Construct multiplicative group of integers modulo n")
    println("Input n")
    val n = readInt()

    println((1 to n).filter(gcd(n, _) == 1).mkString("", ", ", ""))
  }

  def task3(): Unit = {
    println("Calculate euler function")
    println("Input number m")
    val m     = readInt()
    val euler = new EulerFunction()
    println(s"φ($m) = ${euler.phi(m)}")
  }

  def task4(): Unit = {
    println("Input modulo N")
    val n = readInt()
    val op = new ModuloOperations(n)
    println("Input a, b and operation")
    println("+ add")
    println("- sub")
    println("* multiply")
    println("/ div")
    println("^ power")
    val a = readInt()
    val b = readInt()
    val operation = readChar()

    val result = operation match {
      case '+' => op.add(a, b)
      case '-' => op.add(a, b)
      case '*' => op.multiply(a, b)
      case '/' => op.div(a, b)
      case '^' => op.pow(a, b)
    }

    println(s"($a $operation $b) % $n = $result")
  }
}
