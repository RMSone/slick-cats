organization := "com.rms.miu"
name := "slick-cats"
description := "cats and slick"

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.11.11", "2.12.2")
releaseCrossBuild := true

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
  "org.typelevel" %% "cats" % "0.9.0",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
)

tutSettings
tutScalacOptions := tutScalacOptions.value.filterNot(_ == "-Ywarn-unused-import")
tutTargetDirectory := baseDirectory.value

// s3 maven repo

val repoSuffix = "mvn-repo.miuinsights.com"
val releaseRepo = s3(s"releases.$repoSuffix")
val snapshotRepo = s3(s"snapshots.$repoSuffix")

publishMavenStyle := false
publishTo := {
  val repo = if (isSnapshot.value) snapshotRepo else releaseRepo
  Some(s3resolver.value(s"$repo s3 bucket", repo).withIvyPatterns)
}
