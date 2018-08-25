name := """play-java-seed"""
version := "1.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.8.6"
