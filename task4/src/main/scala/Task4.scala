import ConsoleTools.greeting

import scala.io.StdIn.{readInt, readLine}

object Task4 {
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
    println("Rijndael (AES)")
    println("Input text")
    val message      = readLine()
    println("Input key")
    val key      = readLine()
    val aes = new RijndaelT(128)
    val encoded = aes.encode(message, key)
    val decoded = aes.decode(encoded, key)
    println(decoded)

  }

  def task2(): Unit = {
    println("DSA algorithm")
    println("Input some text")
    val message   = readLine()
    println("Input some text for verification")
    val verifyMessage   = readLine()
    val dsa = new DSA()
    val (r, s) = dsa.sign(message)
    val verify = dsa.verify(verifyMessage, r, s)
    println(s"Signature of message: r = $r")
    println(s"s = $s")
    println("Checking the signature")
    println(if (verify) "signature is ok" else "signature is bad :(")
  }

  def task3(): Unit = {
    println("RSA")

    val rsa = new RSA()
    println("Input number to encrypt")
    val number    = readInt()
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

    println("ElGamal and Rabin encryption")
    println("Input number to encrypt")
    val number = readInt()

    val elGamal  = new ElGamal(20)
    val (c1, c2) = elGamal.encrypt(number)
    val message  = elGamal.decrypt(c1, c2)
    println("ElGamal encryption")
    println(s"c1 = $c1, c2 = $c2")
    println(s"decrypted message = $message\n")

    val rabin = new Rabin(20)
    //    rabin.privateKey = RabinPrivateKey(7, 11)
    //    rabin.publicKey = 7 * 11
    val cipher   = rabin.encrypt(number)
    val messages = rabin.decrypt(cipher)
    println("Rabin encryption")
    println(s"encrypted message = $cipher")
    println(s"decrypted message = ${messages}")

  }
}
