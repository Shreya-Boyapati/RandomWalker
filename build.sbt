import sbt.Keys.{libraryDependencies, scalacOptions}

import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val scalaTestVersion = "3.2.15"
val typeSafeConfigVersion = "1.4.2"
val logbackVersion = "1.3.11"
val sfl4sVersion = "2.0.7"
val graphVizVersion = "0.18.1"
val sparkVersion = "3.5.0"
val guavaVersion = "31.1-jre"
val netBuddyVersion = "1.14.4"
val catsVersion = "2.9.0"
val apacheCommonsVersion = "2.13.0"
val jGraphTlibVersion = "1.5.2"
val scalaParCollVersion = "1.0.4"
val guavaAdapter2jGraphtVersion = "1.5.2"

lazy val commonDependencies = Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % scalaParCollVersion,
  "com.typesafe" % "config" % typeSafeConfigVersion, // Typesafe Config Library
  "ch.qos.logback" % "logback-classic" % logbackVersion, // Logback Classic Logger
  "org.slf4j" % "slf4j-api" % sfl4sVersion, // SLF4J API Module
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalatestplus" %% "mockito-4-2" % "3.2.12.0-RC2" % Test,
  "org.apache.spark" %% "spark-core" % sparkVersion, // Spark Core
  "org.apache.spark" %% "spark-sql" % sparkVersion, // Spark SQL
  "org.apache.spark" %% "spark-graphx" % sparkVersion, // Spark GraphX
  "org.apache.spark" %% "spark-mllib" % sparkVersion, // Spark MLlib
  "org.apache.hadoop" % "hadoop-client" % "3.3.4"
  //"org.apache.spark" %% "spark-streaming" % sparkVersion,
  //"org.apache.spark" %% "spark-streaming-twitter" % sparkVersion
).map(_.exclude("org.slf4j", "*"))

scalacOptions += "-Ytasty-reader"

lazy val root = (project in file("."))
  .settings(
    name := "hw2",
    libraryDependencies ++= commonDependencies ++ Seq(
      "com.google.guava" % "guava" % guavaVersion,
    ))

