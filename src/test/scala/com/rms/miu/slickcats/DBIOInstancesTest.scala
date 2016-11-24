package com.rms.miu.slickcats

import cats.data.EitherT
import cats.laws.discipline._
import cats.implicits._
import cats.instances.AllInstances
import cats.laws.discipline.CartesianTests.Isomorphisms
import cats.{Comonad, Eq}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.scalatest.Discipline
import slick.dbio.DBIO

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class DBIOInstancesTest extends FunSuite with Matchers with Discipline with AllInstances with DBIOInstances {
  private val timeout = 3.seconds
  private val db = slick.memory.MemoryDriver.backend.Database(global)

  def dbioXor[A](f: DBIO[A]): DBIO[Either[Throwable, A]] =
    f.map(Right[Throwable, A]).asTry.map {
      case Success(x) => x
      case Failure(t) => Left(t)
    }

  implicit def eqfa[A: Eq]: Eq[DBIO[A]] =
    new Eq[DBIO[A]] {
      def eqv(fx: DBIO[A], fy: DBIO[A]): Boolean = {
        val fz = dbioXor(fx) zip dbioXor(fy)
        Await.result(db.run(fz.map { case (tx, ty) => tx === ty }), timeout)
      }
    }

  implicit def arbDBIO[T](implicit a: Arbitrary[T]): Arbitrary[DBIO[T]] =
    Arbitrary(Gen.oneOf(arbitrary[T].map(DBIO.successful), arbitrary[Throwable].map(DBIO.failed)))

  implicit val throwableEq: Eq[Throwable] = Eq.fromUniversalEquals
  implicit val comonad: Comonad[DBIO] = dbioComonad(timeout, db)
  implicit val iso: Isomorphisms[DBIO] = CartesianTests.Isomorphisms.invariant[DBIO]

  // Need non-fatal Throwable for Future recoverWith/handleError
  implicit val nonFatalArbitrary: Arbitrary[Throwable] =
    Arbitrary(arbitrary[Exception].map(identity))

  /*
  For reasons that are not clear to me, the implicit resolution fails unless `Eq` instance for `XorT[DBOI]`
  (EqXorTFEA: Eq[XorT[F, E, A]]) is typed explicitly. Among the errors that come out
  are:
    [error]  found   : cats.Eq[slick.dbio.DBIO[Throwable]]
    [error]     (which expands to)  algebra.Eq[slick.dbio.DBIOAction[Throwable,slick.dbio.NoStream,slick.dbio.Effect.All]]
    [error]  required: algebra.Eq[cats.data.XorT[[+R]slick.dbio.DBIOAction[R,slick.dbio.NoStream,slick.dbio.Effect.All],Throwable,Int]]
  and:
    diverging implicit expansion for type org.scalacheck.Arbitrary[T]
      [error] starting with value arbInt in trait ArbitraryLowPriority
      [error]     implicitly))
  See gitter discussion that might be relevant: https://gitter.im/non/cats?at=564798f18b242470793db879
 */
  implicit val eqXorTDbio: Eq[EitherT[DBIO, Throwable, Int]] = implicitly[Eq[EitherT[DBIO, Throwable, Int]]]

  checkAll("DBIO[Int]", MonadErrorTests[DBIO, Throwable].monadError[Int, Int, Int])
  checkAll("DBIO[Int]", ComonadTests[DBIO].comonad[Int, Int, Int])
}
