package model.xml.dtd

import model.core.CommonUtils
import play.api.Logger

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.{Try, Failure => TryFailure, Success => TrySuccess}


case class DTDParseException(msg: String, parsedLine: String) extends Exception(msg)

case class DTDParseResult(dtd: DocTypeDef, parseErrors: Seq[DTDParseException])


object DocTypeDefParser extends JavaTokenParsers {

  val SequenceSplitCharacter = ", "
  val AlternativeSplitChar   = "\\|"

  val repOperator  = "*"
  val rep1Operator = "+"
  val optOperator  = "?"

  private val lineRegex: Regex = "(?m)<[\\s\\S]*?>".r

  private val elemRegex    = "(?m)<!ELEMENT[\\s\\S]*?>".r
  private val attListRegex = "(?m)<!ATTLIST[\\s\\S]*?>".r

  //  val dtdLine: Parser[DocTypeDefLine] = elementDefinition | attList

  // Elements

  // FIXME: parse comments in dtd with <!-- ... -->

  private[dtd] def elementDefinition: Parser[ElementDefinition] = "<!ELEMENT" ~ anyName ~ "(" ~ sequenceContent ~ ")" ~ opt(unaryOperator) ~ ">" ^^ {
    case _ ~ elemName ~ _ ~ content ~ _ ~ repOper ~ _ => repOper match {
      case Some(operator) => ElementDefinition(elemName, instUnaryContent(operator, content))
      case None           => ElementDefinition(elemName, content)
    }
  }

  private[dtd] val anyName: Parser[String] = """\w+""".r

  private[dtd] def sequenceContent: Parser[ElementContent] = alternativeContents ~ rep("," ~ alternativeContents) ^^ {
    case first ~ Nil   => first
    case first ~ other => SequenceContent(first :: other.map(_._2))
  }

  private[dtd] def alternativeContents: Parser[ElementContent] = unaryOperatorContent ~ rep("|" ~ unaryOperatorContent) ^^ {
    case first ~ Nil   => first
    case first ~ other => AlternativeContent(first :: other.map(_._2))
  }

  private[dtd] def unaryOperatorContent: Parser[ElementContent] = singleChildContent ~ opt(unaryOperator) ^^ {
    case cc ~ None           => cc
    case cc ~ Some(operator) => instUnaryContent(operator, cc)
  }

  private[dtd] def singleChildContent: Parser[ElementContent] = pcDataType | emptyContent | anyContentType | childElement | ("(" ~ sequenceContent ~ ")") ^^ {
    case _ ~ ct ~ _ => ct
  }

  private[dtd] def childElement: Parser[ElementContent] = (anyName ~ opt(unaryOperator)) ^^ {
    case childElem ~ None           => ChildElementContent(childElem)
    case childElem ~ Some(operator) => instUnaryContent(operator, ChildElementContent(childElem))
  }

  private[dtd] def instUnaryContent(operator: String, childContent: ElementContent): UnaryOperatorElementContent = operator match {
    case DocTypeDefParser.repOperator  => RepElementContent(childContent)
    case DocTypeDefParser.rep1Operator => Rep1ElementContent(childContent)
    case DocTypeDefParser.optOperator  => OptElementContent(childContent)
  }

  private[dtd] val unaryOperator: Parser[String] = repOperator | optOperator | rep1Operator

  private[dtd] def pcDataType: Parser[ElementContent] = "#PCDATA" ^^ { _ => PCData }

  private[dtd] def emptyContent: Parser[ElementContent] = "EMPTY" ^^ { _ => EmptyContent }

  private[dtd] def anyContentType: Parser[ElementContent] = "ANY" ^^ { _ => AnyContent }

  // Attributes

  private[dtd] def attList: Parser[AttributeList] = "<!ATTLIST" ~ anyName ~ rep1(attributeDefinition) ^^ {
    case _ ~ elemName ~ attributeDefs => AttributeList(elemName, attributeDefs)
  }

  private[dtd] def attributeDefinition: Parser[AttributeDefinition] = anyName ~ attributeType ~ opt(attrSpec) ^^ {
    case attributeName ~ attrType ~ maybeAttrSpec => AttributeDefinition(attributeName, attrType, maybeAttrSpec getOrElse RequiredSpecification)
  }

  // Parsers for attribute types

  private[dtd] def attributeType: Parser[AttributeType] = idAttrType | idRefAttrType | cDataAttrType | enumAttrType

  private[dtd] def idAttrType: Parser[AttributeType] = "ID(\\b|\\z)".r ^^ { _ => IDAttributeType }

  private[dtd] def idRefAttrType: Parser[AttributeType] = "IDREF" ^^ { _ => IDRefAttributeType }

  private[dtd] def cDataAttrType: Parser[AttributeType] = "CDATA" ^^ { _ => CDataAttributeType }

  private[dtd] def enumAttrType: Parser[AttributeType] = "(" ~ anyName ~ rep1("|" ~ anyName) ~ ")" ^^ {
    case _ ~ value ~ otherValues ~ _ => EnumAttributeType(value :: otherValues.map(_._2))
  }

  // Parsers for attribute specifications

  private[dtd] def attrSpec: Parser[AttributeSpecification] = requiredSpec | impliedSpec | fixedValueSpec | defaultValueSpec

  private[dtd] def requiredSpec: Parser[AttributeSpecification] = "#REQUIRED" ^^ { _ => RequiredSpecification }

  private[dtd] def impliedSpec: Parser[AttributeSpecification] = "#IMPLIED" ^^ { _ => ImpliedSpecification }

  private[dtd] def fixedValueSpec: Parser[AttributeSpecification] = "#FIXED" ~ "\".*\"".r ^^ {
    case _ ~ value => FixedValueSpecification(value.replaceAll("\"", ""))
  }

  private[dtd] def defaultValueSpec: Parser[AttributeSpecification] = "\".*\"".r ^^ (value => DefaultValueSpecification(value.replaceAll("\"", "")))

  // Parsing function

  private[dtd] def parseDtdLine(str: String): Try[DocTypeDefLine] = {
    val tryParser: Try[Parser[DocTypeDefLine]] = str match {
      case elemRegex()    => TrySuccess(elementDefinition)
      case attListRegex() => TrySuccess(attList)
      case _              => TryFailure(DTDParseException(s"Line can not be identified as element or attlist!", str))
    }

    tryParser flatMap { parser =>
      parse(parser, str) match {
        case DocTypeDefParser.Success(res, _)   => TrySuccess(res)
        case DocTypeDefParser.NoSuccess(msg, _) => TryFailure(new Exception(msg))
      }
    }
  }

  def tryParseDTD(str: String): Try[DocTypeDef] = {
    val allLines: List[String] = lineRegex.findAllIn(str) toList

    val parseLinesTries: List[Try[DocTypeDefLine]] = allLines map parseDtdLine

    val (parseSuccesses, parseFails) = CommonUtils.splitTriesNew(parseLinesTries)

    parseFails match {
      case Nil    => TrySuccess(DocTypeDef(parseSuccesses))
      case errors =>
        errors.foreach(e => Logger.error("Failure while reading dtd from db: " + e.exception.getMessage))
        TryFailure(errors.head.exception)
    }
  }

  def parseDTD(str: String): DTDParseResult = {

    @annotation.tailrec
    def go(toParse: List[String], successes: Seq[DocTypeDefLine], failures: Seq[DTDParseException]): DTDParseResult = toParse match {
      case Nil          => DTDParseResult(DocTypeDef(successes), failures)
      case head :: tail => parseDtdLine(head) match {
        case TrySuccess(line)              => go(tail, successes :+ line, failures)
        case f: TryFailure[DocTypeDefLine] =>
          val dpe = f.exception match {
            case dpe: DTDParseException => dpe
            case e: Throwable           => DTDParseException(e.getMessage, "TODO!")
          }
          go(tail, successes, failures :+ dpe)
      }

    }

    go(lineRegex.findAllIn(str) toList, Seq.empty, Seq.empty)
  }

}
