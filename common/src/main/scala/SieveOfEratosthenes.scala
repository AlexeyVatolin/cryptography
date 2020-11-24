
class SieveOfEratosthenes(val n: Int) {

  def sieve(stream: Stream[Int]): Stream[Int] = stream.head #:: sieve(stream.tail.filter(x => x % stream.head != 0))

  def getPrimeNumbers: Array[Int] = {
    sieve(Stream.from(2)).takeWhile(_ < n).toArray
  }
}
