package net.petitviolet.prac


import scala.annotation.static
import scala.concurrent.{Await, ExecutionContext, Future}
import dotty.Show._

import scala.concurrent.duration.Duration


sealed trait ColorType
object ColorType {
  case object RGB extends ColorType
  case object CYMK extends ColorType
}

sealed trait Color(colorType: ColorType)
case class RGB(r: Int, g: Int, b: Int) extends Color(ColorType.RGB)
case class CYMK(c: Int, y: Int, m: Int, k: Int) extends Color(ColorType.CYMK)

trait Feature {

  // union type
  def showColor(colorType: ColorType): RGB | CYMK = {
    colorType match {
      case ColorType.RGB => RGB(1, 2, 3)
      case ColorType.CYMK => CYMK(1, 2, 3, 4)
    }
  }

  implicit class RichOption[A](val opt: Option[A]) {
    def _fold[B](zero: B, f: A => B): B = opt.fold(zero)(f)
  }

  // unnecessary curry-ing function to type detection
  def map[A, B](seq: Seq[A], f: A => B): Seq[B] = {
    seq map f
  }

  // type 42(singleton type) required 42
  val `42`: 42 = 42
  val str: "str" = "str"
//  val ints: scala.collection.immutable.List[Int] = 1 :: 1 :: 1 :: Nil
//  val ints: Seq[1] = 1 :: 1 :: 1 :: Nil
  val ints: Seq[1] = Seq(1, 1, 1)
  // cannnot use singleton type in a union type???
//  type Digits = 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

  // http://docs.scala-lang.org/sips/pending/42.type.html
//  case class IntRange[Min <: Int : Singleton, Max <: Int : Singleton](i: Int) {
//    {
//      val min: Int = inhabitant[Min]
//      val max: Int = inhabitant[Max]
//      require(i > min && i < max)
//    }
//  }

  // without `case`, smart type detection without curry-ing
  def tupleMap[I1, I2, O1, O2](tpes: Seq[(I1, I2)], f1: I1 => O1, f2: I2 => O2): Seq[(O1, O2)] = {
//    tpes map { case (i1, i2) => (f1(i1), f2(i2)) }
    tpes map { (i1, i2) => (f1(i1), f2(i2)) }
  }

  def union(flag: Boolean): Int | String = if (flag) 1 else "foo"


  // union type
  type Number = Int | Long | Float | Double
  def toNumber(s: String): Number = {
    s.last match {
      case 'f' => s.toFloat
      case 'd' => s.toDouble
      case 'L' => s.slice(0, s.size - 1).toLong
      case _ => s.toInt
    }
  }

  // intersection type
  trait A { def foo = "foo" }; trait B { def bar = "bar" }
  trait HasA { def get: A }; trait HasB { def get: B }
  type AB = A & B
  type HasAB = HasA & HasB
  val ab: HasAB = new HasA with HasB {
    def get: AB = new A with B {}
  }
}

case class Id[A](value: Int)
case class IdType[A](value: A)
case class IdVal[A](value: Int) extends AnyVal
trait User
trait Tag

// object that contains @static members should have companion class
class FeatureApp() {

}

object FeatureApp extends Feature {
  // static field, not-singleton
  @static val TAG = "FeatureApp"

  def main(args: Array[String]): Unit = {
    println(union(true))
    println(union(false))

    println(RGB(1, 2, 3))
    println(showColor(ColorType.RGB))
    println(showColor(ColorType.CYMK))

    println(map("hoge" :: "foo" :: "ba" :: Nil, _.length))
    println(`42`)
    println(str)
    println(ints)
    //  Values of types String and Int cannot be compared with == or !=
    assert(1 == 1)
//    assert(1 != "1") // cannot compile
    assert(Id[User](1) == Id[User](1))
    assert(Id[User](1) != Id[User](2))
    assert(Id[Tag](1) == Id[User](1)) // oops...
    assert(IdType[User](new User{}) != IdType[Tag](new Tag{}))
    assert(IdType[Int](1) == IdType[Long](1L)) // oops...
    assert(1 == 1L) // oops...

    println(tupleMap((1, "hoge") :: (2, "foo") :: Nil, _ * 2, _.length))
    println(Some("hoge")._fold(0, _.size))
    println(Some("hoge").fold(0){_.size})

    assert(toNumber("1.0f") == 1.0f)
    assert(toNumber("2.0d") == 2.0d)
    assert(toNumber("3L") == 3L)
    assert(toNumber("4") == 4)
    assert(toNumber("4").isInstanceOf[Double]) // oops...

    println(ab.get)
    println(ab.get.foo)
    println(ab.get.bar)

    val ids: Seq[Id[User]]= Id[User](1) :: Id[User](2) :: Nil
    val idVals: Seq[IdVal[User]] = IdVal[User](1) :: IdVal[User](2) :: Nil
    println(ids)
    println(idVals)

    // place blow `Async` and `async` in `Feature` trait, bring failure to compile.... why???
    type Async[A] = implicit ExecutionContext => Future[A]
    def async[A](a: => A): Async[A] = Future.apply(a)
    def await[A](aF: Future[A]): A = Await.result(aF, Duration.Inf)

    import scala.concurrent.ExecutionContext.Implicits.global
    val asyncF = async {
      Thread.sleep(1000)
      100
    }
    println(asyncF)

    println(await { asyncF })

    // cannot compile...
    // val nullableInt: String? = null
  }
}
