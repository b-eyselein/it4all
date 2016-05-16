name := "core"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.user.*", "model.exercise.*")

libraryDependencies ++= Seq(
  javaJdbc
)
