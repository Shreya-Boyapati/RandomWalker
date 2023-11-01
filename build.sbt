import sbt.Keys.{libraryDependencies, scalacOptions}

import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val scalaTestVersion = "3.2.11"
val guavaVersion = "31.1-jre"
val typeSafeConfigVersion = "1.4.2"
val logbackVersion = "1.2.10"
val sfl4sVersion = "2.0.0-alpha5"
val graphVizVersion = "0.18.1"
val netBuddyVersion = "1.14.4"
val catsVersion = "2.9.0"
val apacheCommonsVersion = "2.13.0"
val jGraphTlibVersion = "1.5.2"
val scalaParCollVersion = "1.0.4"
val guavaAdapter2jGraphtVersion = "1.5.2"

scalacOptions += "-Ytasty-reader"

lazy val commonDependencies = Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % scalaParCollVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalatestplus" %% "mockito-4-2" % "3.2.12.0-RC2" % Test,
  "com.typesafe" % "config" % typeSafeConfigVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "net.bytebuddy" % "byte-buddy" % netBuddyVersion
).map(_.exclude("org.slf4j", "*"))

lazy val root = (project in file("."))
  .settings(
    name := "hw2",
    libraryDependencies ++= commonDependencies ++ Seq(
      "com.google.guava" % "guava" % guavaVersion,
  ))

