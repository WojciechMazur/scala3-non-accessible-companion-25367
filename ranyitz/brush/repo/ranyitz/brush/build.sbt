val Scala212: String = "2.12.18"
val Scala213: String = "2.13.11"
val Scala3: String = "3.10.0-RC1-bin-20260626-20f6657-NIGHTLY"

ThisBuild / scalaVersion := Scala213
ThisBuild / version := "0.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "brush",
    libraryDependencies += "io.monix" %% "minitest" % "2.9.6" % "test",
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )

commands ++= CommunityBuildPlugin.commands

Global / resolvers += "The Scala Nightly Repository".at("https://repo.scala-lang.org/artifactory/maven-nightlies/")

ThisBuild / evictionErrorLevel := sbt.util.Level.Warn
