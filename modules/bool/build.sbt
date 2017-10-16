name := """bool"""

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)