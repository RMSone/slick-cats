/**
  * Copyright 2017-present, Risk Management Solutions, Inc.
  * All rights reserved.
  *
  * This source code is licensed under the BSD-style license found in the
  * LICENSE file in the root directory of this source tree.
  */

package com.rms.miu.slickcats

import cats.data.EitherT
import cats.instances.AllInstances
import cats.laws.discipline.SemigroupalTests.Isomorphisms
import cats.laws.discipline._
import cats.syntax.all._
import cats.{Comonad, Eq}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline
import slick.dbio.DBIO

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class DBIOInstancesTest extends AnyFunSuite with Matchers with FunSuiteDiscipline with Checkers with AllInstances with DBIOInstances {
  private val timeout = 3.seconds
  private val db = slick.memory.MemoryProfile.backend.Database(global)


  def dbioEither[A](f: DBIO[A]): DBIO[Either[Throwable, A]] =
    f.map(Right[Throwable, A]).asTry.map {
      case Success(x) => x
      case Failure(t) => Left(t)
    }

  implicit def eqfa[A: Eq]: Eq[DBIO[A]] =
    (fx: DBIO[A], fy: DBIO[A]) => {
      val fz = dbioEither(fx) zip dbioEither(fy)
      Await.result(db.run(fz.map { case (tx, ty) => tx === ty }), timeout)
    }

  // Implicit resolution has a hard time with DBIO[A] vs DBIOAction[A, NoStream, All] nested in EitherT etc.
  implicit def eqEitherTDbio[A: Eq]: Eq[EitherT[DBIO, Throwable, A]] = {
    (x: EitherT[DBIO, Throwable, A], y: EitherT[DBIO, Throwable, A]) => {
      val fz = x.value zip y.value
      Await.result(db.run(fz.map { case (tx, ty) => tx === ty }), timeout)
    }
  }

  implicit def arbDBIO[T](implicit a: Arbitrary[T]): Arbitrary[DBIO[T]] =
    Arbitrary(Gen.oneOf(arbitrary[T].map(DBIO.successful), arbitrary[Throwable].map(DBIO.failed)))

  implicit val throwableEq: Eq[Throwable] = Eq.fromUniversalEquals
  implicit val comonad: Comonad[DBIO] = dbioComonad(timeout, db)
  implicit val iso: Isomorphisms[DBIO] = SemigroupalTests.Isomorphisms.invariant[DBIO]

  // Need non-fatal Throwable for Future recoverWith/handleError
  implicit val nonFatalArbitrary: Arbitrary[Throwable] =
    Arbitrary(arbitrary[Exception].map(identity))

  implicit def cogenForDbio[A]: Cogen[DBIO[A]] =
    Cogen[Unit].contramap(_ => ())

  checkAll("DBIO[Int]", MonadErrorTests[DBIO, Throwable].monadError[Int, Int, Int])
  checkAll("DBIO[Int]", ComonadTests[DBIO].comonad[Int, Int, Int])
}
