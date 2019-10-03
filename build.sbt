organization := "com.rms.miu"
name := "slick-cats"
description := "cats and slick"

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.12", "2.12.8","2.13.1")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

val catsVersion = "2.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-laws" % catsVersion % Test,
  "org.typelevel" %% "discipline-scalatest" % "0.12.0-M3" % Test,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.2" % Test
)

enablePlugins(MdocPlugin)
scalacOptions in mdoc --= Seq("-Ywarn-unused-import", "-Xlint")
mdocOut := baseDirectory.value

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))
homepage := Some(url("https://github.com/rmsone/slick-cats"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/rmsone/slick-cats"),
    "scm:git@github.com:rmsone/slick-cats.git"
  )
)
developers := List(
  Developer(id="23will", name="William Duncan", email="", url=url("https://github.com/23will")),
  Developer(id="tvaroh", name="Alexander Semenov", email="", url=url("https://github.com/tvaroh")),
  Developer(id="frosforever", name="Yosef Fertel", email="", url=url("https://github.com/frosforever"))
)

publishMavenStyle := true
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
