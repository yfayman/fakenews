import sbt.Keys._

scalaVersion := "2.11.8"


// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala)
  .settings(
	libraryDependencies ++= Settings.jvmDependencies,
	libraryDependencies += jdbc,
	libraryDependencies += filters,
	libraryDependencies += ws,
    name := """news-server"""
  )