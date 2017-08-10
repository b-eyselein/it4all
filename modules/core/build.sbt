name := "core"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.user.*", "model.exercise.*", "model.programming.*")

libraryDependencies ++= Seq(
  Common.jsonSchemaValidator,
  Common.jsonSchemaGenerator
)

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

EclipseKeys.classpathTransformerFactories := Seq(ClasspathentryTransformer)