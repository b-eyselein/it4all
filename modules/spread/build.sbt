name := "spread"

Common.settings

// Injected for non-static Routes
routesGenerator := InjectedRoutesGenerator

playEbeanModels in Compile := Seq("model.*")

// JaCoCo - siehe project/plugins.sbt
jacoco.settings

libraryDependencies ++= Seq(
  // Apache POI for Excel
  "org.apache.poi" % "poi" % "3.16",
  "org.apache.poi" % "poi-excelant" % "3.16",
  "org.apache.poi" % "poi-ooxml" % "3.16",
  "org.apache.poi" % "poi-ooxml-schemas" % "3.16",
  "org.apache.poi" % "poi-scratchpad" % "3.16",
  "org.apache.xmlbeans" % "xmlbeans" % "2.6.0",


  // ODF Toolkit for OpenOffice Calc
  "commons-validator" % "commons-validator" % "1.6",
  "net.rootdev" % "java-rdfa" % "0.4.2",
  "org.apache.jena" % "jena-arq" % "3.3.0",
  "org.apache.jena" % "jena-core" % "3.3.0",
  "org.apache.jena" % "jena-iri" % "3.3.0",
  "org.apache.odftoolkit" % "odfdom-java" % "0.8.10-incubating",
  "org.apache.odftoolkit" % "simple-odf" % "0.8.1-incubating",
  "xerces" % "xercesImpl" % "2.11.0",
  "xml-apis" % "xml-apis" % "1.4.01"
)