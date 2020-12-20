import scala.util.Random

class SieveOfEratosthenes(val n: Int) {

  def sieve(stream: Stream[Int]): Stream[Int] = stream.head #:: sieve(stream.tail.filter(x => x % stream.head != 0))

  def getPrimeNumbers: Array[Int] =
    sieve(Stream.from(2)).takeWhile(_ < n).toArray

  def getRandomPrime(from: Int): Int = {
    val r = new Random()
    val odds = Stream.from(3, 2).takeWhile(_ <= Math.sqrt(n).toInt)
    val composites = odds.flatMap(i => Stream.from(i * i, 2 * i).takeWhile(_ <= n))
    val primesStream = Stream.from(3, step=2).takeWhile(_ <= n).diff(composites).dropWhile(_ < from)
    val randomIndex = r.nextInt(primesStream.size)
    primesStream.slice(randomIndex, randomIndex + 1).head
  }
}
