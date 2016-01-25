SlickCats
==========

[Cats](https://github.com/non/cats) instances for [Slick's](http://slick.typesafe.com/) `DBIO` including:
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
SlickCats is not yet published but can be used by publishing locally via `sbt publishLocal` and then adding
the following to your build definition:
```scala
libraryDependencies += "com.rms" %% "slick-cats" % "1.0-SNAPSHOT"
```

## Accessing the Instances
Some or all of the following imports may be needed:
```scala
import cats._
import cats.implicits._
import slick.dbio._
import com.rms.slickcats.DBIOInstances._
```
Additionally, be sure to have an implicit `ExecutionContext` in scope. The implicit conversions require it
and will fail with non obvious errors if it's missing.

## Known Issues
Instances are supplied for `DBIO[A]` only. Despite being the same thing,
type aliases will not match for implicit conversion. This means that the following

```scala
val fail1: DBIOAction[String, NoStream, Effect.All] = DBIO.successful("hello") //Instaces will not be found
val fail2 = DBIO.successful("hello") //Instaces will not be found
val success: DBIO[String] = DBIO.successful("hello") //Instaces will be found!
```

See [DBIOInstances](src/main/tut/DBIOInstance.md) for examples and more details.
