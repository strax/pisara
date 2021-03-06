package lzwpack.data

import lzwpack._
import cats._
import cats.implicits._
import java.lang.{Long => JavaLong}

import scala.util.Try

/**
  * A `BitBuffer` is a bit sequence backed by a 64-bit [[scala.Long]]. This means that it can hold at most 64 bits.
  * It provides sequential access (head, append etc.) to manipulate variable-sized chunks.
  */
case class BitBuffer(private[data] val data: Long, size: Int) {
  assert(size >= 0)

  /**
    * Reads n bits from input and returns a tuple of (read bits, remaining bits).
    *
    * @param bits amount of bits to read
    * @param allowFewer whether to successfully read and return a value with fewer read bits than requested
    * @throws scala.IndexOutOfBoundsException if this BitBuffer does not contain enough bits to be read
    */
  def read(bits: Int, allowFewer: Boolean = false): (BitBuffer, Code) = {
    if (!allowFewer && bits > size) throw new IndexOutOfBoundsException(s"Tried to read $bits bits from a buffer with $size bits")
    (drop(bits), take(bits).toInt)
  }

  /**
    * Like [[read]], but returns [[scala.None]] instead of throwing an exception if the read failed.
    */
  def readOption(bits: Int): Option[(BitBuffer, Int)] = Try(read(bits)).toOption

  /**
    * Reads a [[scala.Byte]] from this BitBuffer.
    * @return a tuple of remaining and read bytes
    * @throws scala.IndexOutOfBoundsException if this BitBuffer does not contain enough bits to be read
    */
  def readByte: (BitBuffer, Byte) = read(8) match {
    case (remaining, read) => (remaining, read.toByte)
  }

  /**
    * Returns a new BitBuffer with `n` bits discarded from the beginning of the buffer.
    */
  def drop(n: Int): BitBuffer = {
    val tail = data >>> n
    BitBuffer(tail, Math.max(size - n, 0))
  }

  /**
    * Returns a new BitBuffer with all but the first `n` bits discarded from the buffer.
    */
  def take(n: Int): BitBuffer = {
    val mask = (1 << n) - 1
    val head = data & mask
    BitBuffer(head, Math.min(size, n))
  }

  /**
    * Converts this BitBuffer to an [[scala.Int]].
    * Note that if the data exceeds 32 bits then it will be truncated to fit an integer.
    */
  def toInt: Int = data.toInt

  /**
    * Converts this BitBuffer to a [[scala.Byte]].
    * Note that if the data exceeds 8 bits then it will be truncated to fit a byte.
    */
  def toByte: Byte = data.toByte

  /**
    * Returns a new buffer that contains this buffer preceded by the other buffer.
    */
  def append(other: BitBuffer): BitBuffer = {
    require(other.size + size < JavaLong.SIZE, s"${other.size} + ${size} < 64")
    val bb = other.data
    BitBuffer((bb << size) ^ data, other.size + size)
  }

  /**
    * Alias for [[append]]
    */
  def ++(other: BitBuffer): BitBuffer = this append other

  /**
    * Extracts as many values from this buffer as possible. Trailing zero-filled chunks are discarded.
    * @param chunkSize the size of each read value in bytes
    * @return an array of values
    */
  def drain(chunkSize: Int): (BitBuffer, Array[Int]) = {
    assert(chunkSize > 0)
    val chunkCount = size / chunkSize
    val values = new Array[Int](chunkCount)

    @annotation.tailrec
    def go(i: Int, buffer: BitBuffer): Unit = {
      if (i < chunkCount) {
        buffer.read(chunkSize) match {
          case (rest, value) =>
            values(i) = value
            if (rest.data > 0) go(i + 1, rest)
          case _ =>
        }
      }
    }

    go(0, this)
    (drop(chunkCount * chunkSize), values)
  }

  override def toString(): String = s"BitBuffer(${data.bin(size)}, $size bits)"
}

trait BufferInstances {
  /**
    * A BitBuffer forms a monoid under concatenation and an empty buffer.
    */
  implicit object BufferMonoid extends Monoid[BitBuffer] {
    override def empty: BitBuffer = BitBuffer.empty
    override def combine(a: BitBuffer, b: BitBuffer): BitBuffer = a append b
  }

  implicit val bitBufferShow: Show[BitBuffer] = Show.fromToString[BitBuffer]
}

object BitBuffer {
  /**
    * Creates a buffer from a [[scala.Byte]] with an initial size of 8.
    */
  @inline def apply(b: Byte): BitBuffer = BitBuffer(b.unsigned, 8)

  /**
    * Creates a buffer from a [[scala.Int]] with an initial size of the bytes required to represent the integer.
    */
  @inline def apply(data: Int): BitBuffer = BitBuffer(data, data.bitsize)

  def tupled(t: (Int, Int)): BitBuffer = BitBuffer(t._1, t._2)

  /**
    * Returns an empty BitBuffer.
    */
  lazy val empty: BitBuffer = BitBuffer(0, 0)
}