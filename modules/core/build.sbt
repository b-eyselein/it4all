name := "core"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.user.*", "model.exercise.*")

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

libraryDependencies ++= Seq(
  javaJdbc,
  
  Common.mockitoDep,
  
  // JSON Schema Validator
  Common.jsonSchemaValidator
)

