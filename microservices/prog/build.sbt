name := """prog"""
organization := "de.uni-wuerzburg.informatik.is"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

libraryDependencies += "com.github.docker-java" % "docker-java" % "3.0.9"

EclipseKeys.skipParents in ThisBuild := false
EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true