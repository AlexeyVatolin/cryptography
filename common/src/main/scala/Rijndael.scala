import java.nio.charset.StandardCharsets
import java.security.InvalidParameterException

import BinaryTools.IntToUByte
import Gf256.multiply
import spire.math.UByte


class Rijndael (val keySize: Int) {
  if (keySize != 128 && keySize != 192 && keySize != 256)
    throw new InvalidParameterException()

  private val numRounds = if (keySize == 128) 11 else if (keySize == 192) 13 else 15
  private val keySizeBytes = keySize / 8 // 16 bytes
  private val numColumns = keySizeBytes / 4 // 4
  private val sbox = initSbox()
  private val sboxInv = initSboxInv(sbox)
  private val rcon = initRcon()

  private val mixColumnMatrix: Array[UByte] = Array(2, 3, 1, 1,
    1, 2, 3, 1,
    1, 1, 2, 3,
    3, 1, 1, 2)

  private val mixColumnInvMatrix: Array[UByte] = Array(14, 11, 13,  9,
    9, 14, 11, 13,
    13,  9, 14, 11,
    11, 13,  9, 14)

  private implicit def int2Ubyte(x: Int): UByte = UByte(x)

  def encode(input: String, key: String): Array[UByte] = {
    val inputBytes = stringToBytes(input).grouped(4).toArray.transpose.flatten
    val keySchedule = keyExpansion(key) // .grouped(4).toArray.transpose.flatten
    if (inputBytes.length <= keySize) {
      return encodeBatch(inputBytes, keySchedule)
    }
    new Array[UByte](1)
//    encodeBatch(..., keySchedule)
  }

  def decode(inputBytes: Array[UByte], key: String): String = {
    val keySchedule = keyExpansion(key)
    if (inputBytes.length <= keySize) {
      return bytesToString(decodeBatch(inputBytes, keySchedule))
    }
    "new Array[UByte](1)"
    //    encodeBatch(..., keySchedule)
  }

  def encodeBatch(input: Array[UByte], key: Array[UByte]): Array[UByte] = {
    var state = addRoundKey(input, key.slice(0, keySizeBytes))
    for(i <- 1 until numRounds - 1) {
      state = byteSub(state)
      state = shiftRow(state)
      state = mixColumn(state)
      state = addRoundKey(state, key.slice(keySizeBytes * i, keySizeBytes * (i + 1)))
    }
    state = byteSub(state)
    state = shiftRow(state)
    state = addRoundKey(state, key.slice(keySizeBytes * (numRounds - 1), keySizeBytes * numRounds))
//    val res = state.grouped(4).toArray.transpose.flatten
    state
  }

  def decodeBatch(input: Array[UByte], key: Array[UByte]): Array[UByte] = {
    var state = addRoundKey(input, key.slice(keySizeBytes * (numRounds - 1), keySizeBytes * numRounds))

    var i = numRounds - 1
    while (i >= 1) {
      state = shiftRow(state, inverse = true)
      state = byteSub(state, inverse = true)
      state = addRoundKey(state, key.slice(keySizeBytes * i, keySizeBytes * (i + 1)))
      state = mixColumn(state, inverse = true)
      i -= 1
    }

    state = shiftRow(state, inverse = true)
    state = byteSub(state, inverse = true)
    state = addRoundKey(state, key.slice(0, keySizeBytes))
    state
  }

  def addRoundKey(state: Array[UByte], key: Array[UByte]): Array[UByte] = {
    state.zip(key).map ({ case (x, y) => x ^ y })
  }

  def shiftRow(state: Array[UByte], inverse: Boolean = false) = { // : Array[Byte]
//    		 0,  1,  2,  3,
//    		 4,  5,  6,  7,
//    		 8,  9, 10, 11,
//    		12, 13, 14, 15

//         0,  4,  8, 12,
//         5,  9, 13,  1,
//         10, 14,  2,  6,
//         15,  3,  7, 11

    val tempState = Array.ofDim[UByte](keySizeBytes)
    state.copyToArray(tempState) // нужно для копирования первой колонки
    for (row <- 1 until 4) {
      for (col <- 0 until numColumns) {
        val index = numColumns * row + col
        // left if not inverse else right
        val new_index = (if (inverse) col + row else (col - row)  + numColumns) % numColumns + numColumns * row
        tempState(new_index) = state(index)
      }
    }
    tempState
  }

  def byteSub(state: Array[UByte], inverse: Boolean = false): Array[UByte] = {
    val currentSbox = if (inverse) sboxInv else sbox
    state.map(x => currentSbox(x.toInt))
  }

  def mixColumn(state: Array[UByte], inverse: Boolean = false): Array[UByte] = {
    def index(row: Int, col: Int): Int = row * 4 + col
    val stateCopy = Array.fill[UByte](state.length)(0)
    val matrix = if (inverse) mixColumnInvMatrix else mixColumnMatrix

    for (r <- 0 until numColumns) {
      for (c <- 0 until 4) {
        for (o <- 0 until 4) {
          val i = multiply(matrix(c * 4 + o), state(index(r, o)))
          stateCopy(index(r, c)) = stateCopy(index(r, c)) ^ multiply(matrix(c * 4 + o), state(index(r, o)))
        }
      }
    }
    stateCopy
  }

  def keyExpansion(key: String): Array[UByte] = {
    val keySumbols = stringToBytes(key)

    val keySchedule: Array[Array[UByte]] = Array.ofDim[UByte](numRounds * 4, numColumns) // 44 * 4 = 176 for 128
    for (i <- 0 until 4) {
      for (j <- 0 until numColumns) {
        keySchedule(i)(j) = keySumbols(i * 4 + j)
      }
    }

    for (i <- numColumns until 4 * numRounds) { // from 4 to 43
      var temp: Array[UByte] = new Array[UByte](numColumns)
      keySchedule(i - 1).copyToArray(temp)
      if (i % numColumns == 0) {
        temp = xorWord(subWord(rotWord(temp)), rcon(i / numColumns - 1))
      } else if (i % numColumns == 0 && i > 6) {
        temp = subWord(temp)
      }
      keySchedule(i) = xorWord(temp, keySchedule(i - numColumns))
    }

    keySchedule.flatten
  }

  def stringToBytes(input: String): Array[UByte] = {
    val inputBytes = input.getBytes(StandardCharsets.UTF_8)
    val paddedLength = (math.ceil(inputBytes.length.toDouble / keySizeBytes) * keySizeBytes).toInt
    inputBytes.map(UByte(_)) ++ Array.fill[UByte](paddedLength - inputBytes.length)(1.b) // pad array with 0 to paddedLength
  }

  def bytesToString(input: Array[UByte] ): String = {
    new String(input.map(_.toByte), StandardCharsets.US_ASCII)
   }

  private def xorWord(word: Array[UByte], other: Array[UByte]): Array[UByte] = {
    val result = new Array[UByte](word.length)
    for (i <- word.indices) {
      result(i) = word(i) ^ other(i)
    }
    result
  }

  private def rotWord(word: Array[UByte]): Array[UByte] = {
    val firstValue = word(0)
    for (i <- 1 until word.length) {
      word(i - 1) = word(i)
    }
    word(word.length - 1) = firstValue
    word
  }

  private def subWord(word: Array[UByte]): Array[UByte] = {
    for (i <- word.indices) {
      word(i) = sbox(word(i).toInt)
    }
    word
  }

  private def rotateLeft8(x: UByte, shift: Int) = (x << shift) | (x >> (8 - shift))

  private def initSbox(): Array[UByte] =  {
    var p = 1.b
    var q = 1.b

    val sbox = new Array[UByte](256)
    /* loop invariant: p * q == 1 in the Galois field */
    do {
      /* multiply p by 3 */
      p = p ^ (p << 1) ^ (if ((p & 0x80.b) != 0.b) 0x11B.b else 0.b)

      /* divide q by 3 (equals multiplication by 0xf6) */
      q ^= q << 1
      q ^= q << 2
      q ^= q << 4
      q = q ^ (if ((q & 0x80.b) != 0.b) 0x09.b else 0.b)

      /* compute the affine transformation */
      val xformed = q ^ rotateLeft8(q, 1) ^ rotateLeft8(q, 2) ^
        rotateLeft8(q, 3) ^ rotateLeft8(q, 4)
      val res = xformed ^ 0x63.b
      sbox(p.toInt) = res
    } while (p != 1.b);

    /* 0 is a special case since it has no inverse */
    sbox(0) = 0x63.b
    sbox
  }

  private def initSboxInv(sbox: Array[UByte]): Array[UByte] = {
//    sbox.zipWithIndex.map ({ case (elem, index) => sbox(index)})
    val sboxInv = new Array[UByte](sbox.length)
    for (i <- sbox.indices) {
      sboxInv(sbox(i).toInt) = i.b
    }
    sboxInv
  }

  private def initRcon(): Array[Array[UByte]] = {
    val rcon = Array.fill[UByte](numRounds - 1, 4)(0)
    val initValues: Array[UByte] = Array(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36,
      0x6c, 0xd8, 0xab, 0x4d, 0x9a)
    for (i <- 0 until numRounds - 1) {
      rcon(i)(0) = initValues(i)
    }
    rcon
  }

}

