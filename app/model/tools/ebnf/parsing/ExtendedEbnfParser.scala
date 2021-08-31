package model.tools.ebnf.parsing

import model.tools.ebnf.grammar._

import scala.util.Try
import scala.util.matching.Regex
import scala.util.parsing.combinator._

object ExtendedEbnfParser extends RegexParsers {

  protected val terminalRegex: Regex = """'\w'""".r

  protected val variableRegex: Regex = """[A-Z][1-9]?\d*""".r

  def variable: Parser[Variable] = variableRegex ^^ { str =>
    Variable(value = str.substring(0, 1), index = Try(str.substring(1).toInt).toOption)
  }

  def terminal: Parser[Terminal] = terminalRegex ^^ { str => Terminal(str.trim.substring(1, str.length - 1)) }

  def childElement: Parser[ExtendedBackusNaurFormElement] = variable | terminal // | TODO: ???

  // unary elements

  def optional: Parser[EbnfOptional] = childElement ~ "?" ^^ { case child ~ _ => EbnfOptional(child) }

  def repAny: Parser[EbnfRepetitionAny] = childElement ~ "*" ^^ { case child ~ _ => EbnfRepetitionAny(child) }

  def repOne: Parser[EbnfRepetitionOne] = childElement ~ "+" ^^ { case child ~ _ => EbnfRepetitionOne(child) }

  // collection elements

  def alternative: Parser[ExtendedBackusNaurFormElement] = childElement ~ rep("|" ~ childElement) ^^ {
    case c ~ Seq() => c
    case c1 ~ rest => EbnfAlternative(c1 +: rest.map(_._2))
  }

  def sequence: Parser[ExtendedBackusNaurFormElement] = alternative ~ rep(alternative) ^^ {
    case c ~ Seq() => c
    case c1 ~ rest => EbnfSequence(c1 +: rest)
  }

  def grammar: Parser[Map[Variable, ExtendedBackusNaurFormElement]] = ???

  def parseRule(input: String): ParseResult[ExtendedBackusNaurFormElement] = parse(sequence, input)

}
