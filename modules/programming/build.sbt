name := "programming"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  Common.dockerjava
)

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)