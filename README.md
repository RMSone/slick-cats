SlickCats
==========

[Cats](https://github.com/typelevel/cats) instances for [Slick's](http://slick.typesafe.com/) `DBIO` including:
* Monad
* MonadError
* CoflatMap
* Group
* Monoid
* Semigroup
* Comonad
* Order
* PartialOrder
* Equals

## Using
To add *slick-cats* dependency to a project, add the following to your build definition:
```scala
libraryDependencies += "com.rms.miu" %% "slick-cats" % version
```

Because of possible binary incompatibilities here are the dependency versions used in each release

| slick-cats version | slick version | cats version  |
|:------------------:|:-------------:|:------------: |
|         0.1        |     3.1.1     |     0.5.0     |
|         0.2        |     3.1.1     |     0.6.0     |
|         0.3        |     3.1.1     |     0.7.0     |
|         0.4        |     3.1.1     |     0.8.x     |
|         0.4.1      |     3.1.1     |     0.9.x     |
|         0.5-M1     |     3.2.0-M2  |     0.8.x     |
|         0.5-M2     |     3.2.0-M2  |     0.9.x     |
|         0.5        |     3.2.0     |     0.9.x     |
|         0.6        |     3.2.0     |     0.9.x     |
|         0.7-MF     |     3.2.1     |     1.0.0-MF  |
|         0.7.1-RC1  |     3.2.1     |     1.0.0-RC1 |
|         0.7.1      |     3.2.1     |     1.0.x     |

Artifacts are publicly available on Maven Central starting from version *0.6*.

## Accessing the Instances
Some or all of the following imports may be needed:
```scala
import cats._
import cats.implicits._
import slick.dbio._
import com.rms.miu.slickcats.DBIOInstances._
```
Additionally, be sure to have an implicit `ExecutionContext` in scope. The implicit conversions require it
and will fail with non obvious errors if it's missing.
```scala
scala> implicitly[Monad[DBIO]]
<console>:25: error: could not find implicit value for parameter e: cats.Monad[slick.dbio.DBIO]
       implicitly[Monad[DBIO]]
                 ^
```

```scala
import scala.concurrent.ExecutionContext.Implicits.global
```

instances will be available for:
```scala
implicitly[Monad[DBIO]]
implicitly[MonadError[DBIO, Throwable]]
implicitly[CoflatMap[DBIO]]
implicitly[Functor[DBIO]]
implicitly[Applicative[DBIO]]
```

If a Monoid exists for `A`, here taken as Int, then the following is also available
```scala
implicitly[Group[DBIO[Int]]]
implicitly[Semigroup[DBIO[Int]]]
implicitly[Monoid[DBIO[Int]]]
```

## Known Issues
Instances are supplied for `DBIO[A]` only. Despite being the same thing,
type aliases will not match for implicit conversion. This means that the following

```scala
scala> def monad[F[_] : Monad, A](fa: F[A]): F[A] = fa
monad: [F[_], A](fa: F[A])(implicit evidence$1: cats.Monad[F])F[A]

scala> val fail1: DBIOAction[String, NoStream, Effect.All] = DBIO.successful("hello")
fail1: slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect.All] = SuccessAction(hello)

scala> val fail2 = DBIO.successful("hello")
fail2: slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect] = SuccessAction(hello)

scala> val success: DBIO[String] = DBIO.successful("hello")
success: slick.dbio.DBIO[String] = SuccessAction(hello)
```
will _not_ compile
```scala
scala> monad(fail1)
<console>:28: error: no type parameters for method monad: (fa: F[A])(implicit evidence$1: cats.Monad[F])F[A] exist so that it can be applied to arguments (slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect.All])
 --- because ---
argument expression's type is not compatible with formal parameter type;
 found   : slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect.All]
 required: ?F[?A]
       monad(fail1)
       ^
<console>:28: error: type mismatch;
 found   : slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect.All]
 required: F[A]
       monad(fail1)
             ^

scala> monad(fail2)
<console>:28: error: no type parameters for method monad: (fa: F[A])(implicit evidence$1: cats.Monad[F])F[A] exist so that it can be applied to arguments (slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect])
 --- because ---
argument expression's type is not compatible with formal parameter type;
 found   : slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect]
 required: ?F[?A]
       monad(fail2)
       ^
<console>:28: error: type mismatch;
 found   : slick.dbio.DBIOAction[String,slick.dbio.NoStream,slick.dbio.Effect]
 required: F[A]
       monad(fail2)
             ^
```
but
```scala
scala> monad(success)
res11: slick.dbio.DBIO[String] = SuccessAction(hello)
```
will compile fine.

## Extras
This README is compiled using [tut](https://github.com/tpolecat/tut) to ensure that only working examples are given.
Feedback of any kind is appreciated. Especially if you have any ideas on getting around the `DBIOAction` issue above.

