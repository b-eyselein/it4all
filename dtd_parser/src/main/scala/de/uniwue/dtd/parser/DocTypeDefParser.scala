package de.uniwue.dtd.parser

import de.uniwue.dtd.model._

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.{Try, Failure => TryFailure, Success => TrySuccess}

final case class DTDParseException(msg: String, parsedLine: String) extends Exception(msg)

final case class DTDParseResult(dtd: DocTypeDef, parseErrors: Seq[DTDParseException])

object DocTypeDefParser extends JavaTokenParsers {

  val repOperator  = "*"
  val rep1Operator = "+"
  val optOperator  = "?"

  private val lineRegex: Regex = "(?m)<[\\s\\S]*?>".r

  private val elemRegex    = "(?m)<!ELEMENT[\\s\\S]*?>".r
  private val attListRegex = "(?m)<!ATTLIST[\\s\\S]*?>".r

  // Elements

  // FIXME: parse comments in dtd with <!-- ... -->

  private val pcDataType: Parser[ElementContent] = "#PCDATA" ^^ { _ => PCData }

  private val emptyContent: Parser[ElementContent] = "EMPTY" ^^ { _ => EmptyContent }

  private val anyContentType: Parser[ElementContent] = "ANY" ^^ { _ => AnyContent }

  private val anyName: Parser[String] = """\w+""".r

  private val unaryOperator: Parser[String] = repOperator | optOperator | rep1Operator

  private val childElement: Parser[ElementContent] = (anyName ~ opt(unaryOperator)) ^^ {
    case childElem ~ None           => ChildElementContent(childElem)
    case childElem ~ Some(operator) => instUnaryContent(operator, ChildElementContent(childElem))
  }

  private val singleChildContent: Parser[ElementContent] = pcDataType | emptyContent | anyContentType | childElement | ("(" ~ sequenceContent ~ ")") ^^ {
    case _ ~ ct ~ _ => ct
  }

  private val unaryOperatorContent: Parser[ElementContent] = singleChildContent ~ opt(unaryOperator) ^^ {
    case cc ~ None           => cc
    case cc ~ Some(operator) => instUnaryContent(operator, cc)
  }

  private val alternativeContents: Parser[ElementContent] = unaryOperatorContent ~ rep("|" ~ unaryOperatorContent) ^^ {
    case first ~ Nil   => first
    case first ~ other => AlternativeContent(first :: other.map(_._2))
  }

  private lazy val sequenceContent: Parser[ElementContent] = alternativeContents ~ rep("," ~ alternativeContents) ^^ {
    case first ~ Nil   => first
    case first ~ other => SequenceContent(first :: other.map(_._2))
  }

  private val elementDefinition: Parser[ElementDefinition] = "<!ELEMENT" ~ anyName ~ "(" ~ sequenceContent ~ ")" ~ opt(unaryOperator) ~ ">" ^^ {
    case _ ~ elemName ~ _ ~ content ~ _ ~ Some(repOper) ~ _ => ElementDefinition(elemName, instUnaryContent(repOper, content))
    case _ ~ elemName ~ _ ~ content ~ _ ~ None ~ _          => ElementDefinition(elemName, content)
  }

  private def instUnaryContent(operator: String, childContent: ElementContent): UnaryOperatorElementContent = operator match {
    case DocTypeDefParser.repOperator  => RepElementContent(childContent)
    case DocTypeDefParser.rep1Operator => Rep1ElementContent(childContent)
    case DocTypeDefParser.optOperator  => OptElementContent(childContent)
  }

  // Parsers for attribute types

  private[dtd] val idAttrType: Parser[AttributeType] = "ID(\\b|\\z)".r ^^ { _ => IDAttributeType }

  private[dtd] val idRefAttrType: Parser[AttributeType] = "IDREF" ^^ { _ => IDRefAttributeType }

  private[dtd] val cDataAttrType: Parser[AttributeType] = "CDATA" ^^ { _ => CDataAttributeType }

  private[dtd] val enumAttrType: Parser[AttributeType] = "(" ~ anyName ~ rep1("|" ~ anyName) ~ ")" ^^ { case _ ~ value ~ otherValues ~ _ =>
    EnumAttributeType(value :: otherValues.map(_._2))
  }

  private[dtd] val attributeType: Parser[AttributeType] = idAttrType | idRefAttrType | cDataAttrType | enumAttrType

  // Parsers for attribute specifications

  private[dtd] val requiredSpec: Parser[AttributeSpecification] = "#REQUIRED" ^^ { _ => RequiredSpecification }

  private[dtd] val impliedSpec: Parser[AttributeSpecification] = "#IMPLIED" ^^ { _ => ImpliedSpecification }

  private[dtd] val fixedValueSpec: Parser[AttributeSpecification] = "#FIXED" ~ "\".*\"".r ^^ { case _ ~ value =>
    FixedValueSpecification(value.replaceAll("\"", ""))
  }

  private[dtd] val defaultValueSpec: Parser[AttributeSpecification] = "\".*\"".r ^^ (value => DefaultValueSpecification(value.replaceAll("\"", "")))

  private[dtd] val attrSpec: Parser[AttributeSpecification] = requiredSpec | impliedSpec | fixedValueSpec | defaultValueSpec

  // Attributes

  private val attributeDefinition: Parser[AttributeDefinition] = anyName ~ attributeType ~ opt(attrSpec) ^^ { case attributeName ~ attrType ~ maybeAttrSpec =>
    AttributeDefinition(attributeName, attrType, maybeAttrSpec getOrElse RequiredSpecification)
  }

  private val attList: Parser[AttributeList] = "<!ATTLIST" ~ anyName ~ rep1(attributeDefinition) ^^ { case _ ~ elemName ~ attributeDefs =>
    AttributeList(elemName, attributeDefs)
  }

  // Parsing function

  private[dtd] def parseDtdLine(str: String): Try[DocTypeDefLine] = for {
    parser <- str match {
      case elemRegex()    => TrySuccess(elementDefinition)
      case attListRegex() => TrySuccess(attList)
      case _              => TryFailure[Parser[DocTypeDefLine]](DTDParseException(s"Line can not be identified as element or attlist!", str))
    }

    result <- parse(parser, str) match {
      case DocTypeDefParser.Success(res, _)   => TrySuccess(res)
      case DocTypeDefParser.NoSuccess(msg, _) => TryFailure[DocTypeDefLine](new Exception(msg))
    }
  } yield result

  def tryParseDTD(str: String): Try[DocTypeDef] = {
    val allLines = lineRegex
      .findAllIn(str)
      .map(parseDtdLine)

    val (parseSuccesses, parseFails) = splitTries(allLines.toList)

    parseFails match {
      case Nil    => TrySuccess(DocTypeDef(parseSuccesses))
      case errors => TryFailure[DocTypeDef](errors.headOption.getOrElse(???).exception)
    }
  }

  def parseDTD(str: String): DTDParseResult = {

    @annotation.tailrec
    def go(toParse: List[String], successes: Seq[DocTypeDefLine], failures: Seq[DTDParseException]): DTDParseResult = toParse match {
      case Nil => DTDParseResult(DocTypeDef(successes), failures)
      case head :: tail =>
        parseDtdLine(head) match {
          case TrySuccess(line) => go(tail, successes :+ line, failures)
          case TryFailure(exception) =>
            val dpe = exception match {
              case dpe: DTDParseException => dpe
              case e: Throwable           => DTDParseException(e.getMessage, "TODO!")
            }
            go(tail, successes, failures :+ dpe)
        }

    }

    go(lineRegex.findAllIn(str).toList, Seq[DocTypeDefLine](), Seq[DTDParseException]())
  }

  private def splitTries[A](tries: Seq[Try[A]]): (Seq[A], Seq[TryFailure[A]]) = {

    @annotation.tailrec
    def go(ts: List[Try[A]], successes: Seq[A], failures: Seq[TryFailure[A]]): (Seq[A], Seq[TryFailure[A]]) = ts match {
      case Nil => (successes, failures)
      case head :: tail =>
        head match {
          case TrySuccess(a)    => go(tail, successes :+ a, failures)
          case f: TryFailure[A] => go(tail, successes, failures :+ f)
        }

    }

    go(tries.toList, Seq.empty, Seq.empty)
  }

}
