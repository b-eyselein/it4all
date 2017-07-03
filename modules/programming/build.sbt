name := "programming"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  Common.dockerjava
)

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)