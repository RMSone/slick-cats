organization := "com.rms.miu"

name := "slick-cats"

version := "1.0"

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
    "org.typelevel" %% "cats" % "0.6.0",
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
  )

tutSettings

tutScalacOptions := tutScalacOptions.value.filterNot(_ == "-Ywarn-unused-import")

tutTargetDirectory := baseDirectory.value

//s3 maven repo

import ohnosequences.sbt.SbtS3Resolver._
import com.amazonaws.services.s3.model.Region
import com.amazonaws.auth._
import com.amazonaws.auth.profile._

val repoSuffix = "mvn-repo.miuinsights.com"
val releaseRepo = s3(s"releases.$repoSuffix")
val snapshotRepo = s3(s"snapshots.$repoSuffix")

resolvers ++= {
  val releases: Resolver = s3resolver.value("Releases resolver", releaseRepo).withIvyPatterns
  val snapshots: Resolver = s3resolver.value("Snapshots resolver", snapshotRepo).withIvyPatterns
  Seq(releases, snapshots)
}

s3credentials :=
  new ProfileCredentialsProvider(awsProfile.value) |
  new InstanceProfileCredentialsProvider() |
  new EnvironmentVariableCredentialsProvider()

s3region := Region.EU_Ireland
s3overwrite := true
publishMavenStyle := false

publishTo := {
  val repo = if (isSnapshot.value) snapshotRepo else releaseRepo
  Some(s3resolver.value(s"$repo s3 bucket", repo) withIvyPatterns)
}
