organization := "com.rms.miu"
name := "slick-cats"
description := "cats and slick"

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.11", "2.12.4")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.0.0-RC1",
  "org.typelevel" %% "cats-laws" % "1.0.0-RC1",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
)

enablePlugins(TutPlugin)
scalacOptions in Tut --= Seq("-Ywarn-unused-import", "-Xlint")
tutTargetDirectory := baseDirectory.value

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
