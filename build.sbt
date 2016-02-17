
organization := "com.rms.miu"

name := "slick-cats"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

description := "cats and slick"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import" // This might not work well. Try it out for now
)

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats" % "0.4.1",
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
  )

tutSettings

tutScalacOptions := tutScalacOptions.value.filterNot(_ == "-Ywarn-unused-import")
