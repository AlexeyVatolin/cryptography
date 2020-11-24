import BinaryTools._
import ConsoleTools.greeting
import spire.math.UInt

import scala.io.StdIn.readInt


object Main extends App {
  val number = greeting()

  number match {
    case 1 => task1()
    case 2 => task2()
    case 3 => task3()
    case 4 => task4()
    case _ => println("Invalid task number")
  }

  def task1(): Unit = {
    println("Input a and k")
    val a = readInt()
    val k = readInt()

    println(s"$k bit of number ${a.toBinaryString} is ${getN(a, k).toBinaryString}")
    println(s"Toggle $k bit of number ${a.toBinaryString}, number = ${toggle(a, k).toBinaryString}")

    println("Input i and j")
    val i = readInt()
    val j = readInt()

    println(s"Swap $i and $j bit in a = ${swap(a, i, j).toBinaryString}")

    println("Input m")
    val m = readInt()
    println(s"Zero $m digits in a = ${zeroK(a, m).toBinaryString}")

  }


  def task2(): Unit = {
    println("Input number and i")
    val a = readInt()
    val i = readInt()

    println(s"Merge first $i bits with last $i bits ${toBinary(merge(UInt(a), i))}")
    println(s"Get bits between first and last $i bits ${toBinary(getCentral(UInt(a), i))}")
  }

  def task3(): Unit = {
    val a = readInt()

    println(s"Score all bits in number ${a.toBinaryString} = ${score(UInt(a))}")
  }

  def task4(): Unit = {
    val a = readInt()
    val i = readInt()

    println(s"Circular shift right ${shiftLeft(UInt(a), i)}")
    println(s"Circular shift right ${shiftRight(UInt(a), i)}")
  }
}
