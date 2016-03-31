name := "sql"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.sql.*")

libraryDependencies ++= Seq(
  javaJdbc
)
