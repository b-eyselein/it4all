name := "core"

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  javaJdbc
)

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator