
name := "play-java-functional-actions"
organization := "io.methvin"
organizationName := "Greg Methvin"
startYear := Some(2017)
licenses := Seq(
  "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
)

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.3"

val PlayVersion = play.core.PlayVersion.current

lazy val root = (project in file("."))
  .enablePlugins(AutomateHeaderPlugin)

libraryDependencies ++= Seq(
  component("play"),
  component("play-test") % Test,
  component("play-ehcache") % Test
)

