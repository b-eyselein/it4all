name := """bool"""

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

scalacOptions ++= Seq("-feature")

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test"

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)