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
  // cannnot use singleton type in a union type
//   type I = 1 | 2 | 3

  // http://docs.scala-lang.org/sips/pending/42.type.html
//  case class IntRange[Min <: Int : SingleInhabitant, Max <: Int : SingleInhabitant](i: Int) {
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

}

// object that contains @static members should have companion class
class FeatureApp() {}

object FeatureApp extends Feature {
  // static field, not-singleton
  @static val TAG = "FeatureApp"

  def main(args: Array[String]): Unit = {
    println(RGB(1, 2, 3))
    println(showColor(ColorType.RGB))
    println(showColor(ColorType.CYMK))

    println(map("hoge" :: "foo" :: "ba" :: Nil, _.length))
    println(`42`)
    println(str)
    println(ints)
    println(TAG.##)
    //  Values of types String and Int cannot be compared with == or !=
    println(1 == 1)
    println(1 != 1)
//    println(1 != "1")
//    println("1" == 1)
//    println(1 == "1")

    println(tupleMap((1, "hoge") :: (2, "foo") :: Nil, _ * 2, _.length))

//    println(IntRange[1, 100](10))
//    println(IntRange[50, 100](10))

    // place blow `Async` and `async` in `Feature` trait, bring failure to compile.... why???
    type Async[A] = implicit ExecutionContext => Future[A]
    def async[A](a: => A): Async[A] = Future.apply(a)
    def await[A](aF: Future[A]): A = Await.result(aF, Duration.Inf)

    import scala.concurrent.ExecutionContext.Implicits.global
    println(await(async(10)))
  }
}
