name := "core"

playEbeanModels in Compile := Seq("model.*")

libraryDependencies ++= Seq(
  javaJdbc
)