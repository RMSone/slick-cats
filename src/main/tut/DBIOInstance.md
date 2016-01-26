Instances for `MonadError`, `CoflatMap`, and `Group` are provided for `DBIO`.

The following imports from cats are useful:

```tut
import cats._
import cats.implicits._
```

If we have methods as follows:
```tut
import slick.dbio._
import scala.language.higherKinds

def monad[F[_] : Monad, A](fa: F[A]): F[A] = fa
def monadError[F[_], E, A](fa: F[A])(implicit ev: MonadError[F, E]) = ()
type MonadError2[F[_]] = MonadError[F, Throwable]
def monadError2[F[_] : MonadError2, E, A](fa: F[A]) = ()
def monadError3[F[_] : ({type L[B[_]] = MonadError[B, Throwable]})#L, E, A](fa: F[A]) = ()
def coflatMap[F[_] : CoflatMap, A](fa: F[A]) = ()
def functor[F[_] : Functor, A](fa: F[A]) = ()
def applicative[F[_] : Applicative, A](fa: F[A]) = ()

def group[A](fa: A)(implicit ev: Group[A]) = ()
def semigroup[A: Semigroup](a1: A) = ()
def monoid[A : Monoid](fa: A) = ()
```

we can then use them with `DBIO` by importing
```tut
import com.rms.miu.slickcats.DBIOInstances._
```
Be sure to also have an implicit `ExecutionContext` in scope. The implicit conversions require it
and will fail with non obvious errors if it's missing.
```tut
import scala.concurrent.ExecutionContext.Implicits.global
```

```tut
val input: DBIO[String] = DBIO.successful("hello")

monad(input)
monadError(input)
monadError2(input)
monadError3(input)
coflatMap(input)
functor(input)
applicative(input)

val grpInput1: DBIO[Int] = DBIO.successful(1)

//Need to provide underlying monoid
implicit val addSemiInt = Monoid.additive[Int]
semigroup(grpInput1)
group(grpInput1)
monoid(grpInput1)
```

Note that instances are supplied for `DBIO[A]` only. Despite being the same thing,
type aliases will not match for implicit conversion. This means that the following

```tut
val input2: DBIOAction[String, NoStream, Effect.All] = DBIO.successful("hello")
val input3 = DBIO.successful("hello")
```
will _not_ compile
```tut:fail
monad(input2)
monad(input3)
```