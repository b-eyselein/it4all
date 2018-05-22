package model.xml.dtd

import scala.language.postfixOps

case class DocTypeDef(lines: Seq[DocTypeDefLine]) {

  private def splitLines(lines: List[DocTypeDefLine]): (Seq[ElementDefinition], Seq[AttributeList]) = {

    @annotation.tailrec
    def go(lines: List[DocTypeDefLine], es: Seq[ElementDefinition], as: Seq[AttributeList]): (Seq[ElementDefinition], Seq[AttributeList]) = lines match {
      case Nil          => (es, as)
      case head :: tail => head match {
        case e: ElementDefinition => go(tail, es :+ e, as)
        case a: AttributeList     => go(tail, es, as :+ a)
      }
    }

    go(lines, Seq.empty, Seq.empty)
  }

  def asString: String = lines map (_.asString) mkString "\n"

  def asElementLines: Seq[ElementLine] = lines.groupBy(_.elementName) map {
    case (elementName, allLines) =>
      // FIXME: elementDefinition is missing!
      val (es, as) = splitLines(allLines toList)
      ElementLine(elementName, es.head, as)
  } toSeq

}

sealed abstract class DocTypeDefLine {

  val elementName: String

  def asString: String

}

case class ElementLine(elementName: String, elementDefinition: ElementDefinition, attributeDefinition: Seq[AttributeList])

case class AttributeList(elementName: String, attributeDefinitions: Seq[AttributeDefinition]) extends DocTypeDefLine {

  override def asString: String = attributeDefinitions match {
    case attr :: Nil => s"""<!ATTLIST $elementName ${attr.asString}>"""

    case _ :: _ =>
      s"""<!ATTLIST $elementName
         |${attributeDefinitions map ((" " * 4) + _.asString) mkString "\n"}
         |>""".stripMargin
  }

}

case class AttributeDefinition(attributeName: String, attributeType: AttributeType, attributeSpecification: AttributeSpecification) {

  def asString: String = attributeName + " " + attributeType + " " + attributeSpecification

}


case class ElementDefinition(elementName: String, content: ElementContent) extends DocTypeDefLine {

  def contentAsString: String = content.asString(false)

  override def asString: String = s"<!ELEMENT $elementName $contentAsString>"

}