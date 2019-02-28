package model.tools.xml

import model.tools.xml.XmlExYamlProtocol.XmlSampleYamlFormat
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import net.jcazevedo.moultingyaml._
import org.scalatest._

import scala.util.{Failure, Success}

class XmlExYamlProtocolSpec extends FlatSpec with Matchers {


  "A XmlSampleYamlFormat" should "read and write grammars and documents correctly" in {

    val valuesToTest = Seq(
      (1,
        """id: 1
          |grammar: <!ELEMENT root (#PCDATA)>
          |document: <root></root>
          |""".stripMargin,
        "<!ELEMENT root (#PCDATA)>",
        "<root></root>"),

      (73,
        """id: 73
          |grammar: |-
          |  <!ELEMENT x (mytest)>
          |  <!ELEMENT mytest (#PCDATA)>
          |document: |
          |  <x>
          |    <mytest>
          |    </mytest>
          |  </x>
          |""".stripMargin,
        """<!ELEMENT x (mytest)>
          |<!ELEMENT mytest (#PCDATA)>""".stripMargin,
        """<x>
          |  <mytest>
          |  </mytest>
          |</x>
          |""".stripMargin),

      (537,
        """id: 537
          |grammar: |-
          |  <!ELEMENT found (name, date)>
          |  <!ELEMENT name (#PCDATA)>
          |  <!ELEMENT date (#PCDATA)>
          |document: |
          |  <found>
          |    <name>test</name>
          |    <date>2018-01-01</date>
          |  </found>
          |""".stripMargin,
        """<!ELEMENT found (name, date)>
          |<!ELEMENT name (#PCDATA)>
          |<!ELEMENT date (#PCDATA)>""".stripMargin,
        """<found>
          |  <name>test</name>
          |  <date>2018-01-01</date>
          |</found>
          |""".stripMargin)
    )

    val xmlSampleYamlFormat = XmlSampleYamlFormat(1, SemanticVersionHelper.DEFAULT)

    for ((docId, toRead, grammarContent, docContent) <- valuesToTest) {

      //      val dtd = DocTypeDefParser.tryParseDTD(grammarContent) match {
      //        case Failure(error)   =>
      //          Logger.error("Could not parse dtd", error)
      //          fail(error.getMessage)
      //        case Success(someDtd) => someDtd
      //      }

      xmlSampleYamlFormat.read(toRead.parseYaml) match {
        case Failure(error)     => fail("Could not read yaml: " + error.getMessage)
        case Success(xmlSample) =>
          xmlSample shouldBe XmlSample(docId, grammarContent, docContent)
          xmlSampleYamlFormat.write(xmlSample).prettyPrint shouldBe toRead
      }
    }
  }


  "A XmlExYamlFormat" should "read and write xml exercises correctly" in {

    val xmlExYamlFormat = XmlExYamlProtocol.XmlExYamlFormat

    val toRead =
      """id: 1
        |title: Party
        |author: bje40dc
        |text: Erstellen Sie zu dieser DTD ein passendes Xml-Dokument
        |status: APPROVED
        |semanticVersion: 1.0.0
        |
        |rootNode: party
        |grammarDescription: >
        |  In dieser XML-Datei soll eine Party [party] beschrieben werden. Diese hat ein Datum [datum] und eine Liste von Gästen [gast] (mindestens ein Gast war
        |  anwesend). Für jeden Gast sollen der Vor- [vorname] und Nachname [nachname] und die Getränke [getraenk], die er getrunken hat, notiert werden. Jeder Gast
        |  hat mindestens ein Getränk getrunken. Außerdem soll für jeden Gast gespeichert werden, ob er nüchtern [nuechtern] und ob er ledig [ledig] ist.
        |samples:
        |- id: 1
        |  document: |
        |    <root>
        |    </root>
        |  grammar: |
        |    <!ELEMENT root (#PCDATA)>""".stripMargin

    xmlExYamlFormat.read(toRead.parseYaml) match {
      case Failure(error)           => fail("Could not read yaml: " + error.getMessage)
      case Success(readXmlExercise) =>

        val grammarContent = "<!ELEMENT root (#PCDATA)>"

        //        val awaitedDTD = DocTypeDefParser.tryParseDTD(grammarContent) match {
        //          case Failure(error)   =>
        //            Logger.error("Error while parsing dtd", error)
        //            fail("Error while parsing dtd..." + error.getMessage)
        //          case Success(someDTD) => someDTD
        //        }

        readXmlExercise shouldBe
          XmlExercise(
            1, SemanticVersion(1, 0, 0), "Party", "bje40dc", "Erstellen Sie zu dieser DTD ein passendes Xml-Dokument", ExerciseState.APPROVED,
            grammarDescription = "In dieser XML-Datei soll eine Party [party] beschrieben werden. Diese hat ein Datum [datum] und eine Liste von Gästen [gast] (mindestens ein Gast war anwesend). " +
              "Für jeden Gast sollen der Vor- [vorname] und Nachname [nachname] und die Getränke [getraenk], die er getrunken hat, notiert werden. Jeder Gast hat mindestens ein " +
              "Getränk getrunken. Außerdem soll für jeden Gast gespeichert werden, ob er nüchtern [nuechtern] und ob er ledig [ledig] ist.\n",
            rootNode = "party",
            Seq(XmlSample(1, grammarContent,
              """<root>
                |</root>
                |""".stripMargin)))


        // Write and read again => ordering is not important
        xmlExYamlFormat.readObject(xmlExYamlFormat.write(readXmlExercise)) shouldBe Success(readXmlExercise)
    }

  }

}
