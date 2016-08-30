organization := "com.rms.miu"

name := "slick-cats"

scalaVersion := "2.11.8"

description := "cats and slick"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats" % "0.7.0",
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.0.0-M8" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
  )

tutSettings

tutScalacOptions := tutScalacOptions.value.filterNot(_ == "-Ywarn-unused-import")

tutTargetDirectory := baseDirectory.value

//s3 maven repo

val repoSuffix = "mvn-repo.miuinsights.com"
val releaseRepo = s3(s"releases.$repoSuffix")
val snapshotRepo = s3(s"snapshots.$repoSuffix")

publishMavenStyle := false

publishTo := {
  val repo = if (isSnapshot.value) snapshotRepo else releaseRepo
  Some(s3resolver.value(s"$repo s3 bucket", repo) withIvyPatterns)
}
