package model.bool

import scala.util.parsing.combinator._

object ScalaNodeParser extends JavaTokenParsers {

  private lazy val b_expression: Parser[ScalaNode] = b_term ~ rep("or" ~ b_term) ^^ {
    case f1 ~ fs => (f1 /: fs) ((f1, fs) => f1 or fs._2)
  }

  private lazy val b_term: Parser[ScalaNode] = (b_other_term ~ rep("and" ~ b_other_term)) ^^ {
    case f1 ~ fs => (f1 /: fs) ((f1, fs) => f1 and fs._2)
  }

  private lazy val b_other_term: Parser[ScalaNode] = (b_not_factor ~ rep(b_other_operator ~ b_not_factor)) ^^ {
    case f1 ~ fs => (f1 /: fs) ((f1, fs) => fs._1 match {
      case "nand"  => f1 nand fs._2
      case "nor"   => f1 nor fs._2
      case "xor"   => f1 xor fs._2
      case "equiv" => f1 equiv fs._2
      case _       => f1 impl fs._2 // Only impl possible
    })
  }

  private lazy val b_not_factor: Parser[ScalaNode] = opt("not") ~ b_factor ^^ {
    case Some(_) ~ f => ScalaNode.not(f);
    case None ~ f    => f
  }

  private lazy val b_factor: Parser[ScalaNode] = b_literal | b_variable | ("(" ~ b_expression ~ ")" ^^ {
    case "(" ~ exp ~ ")" => exp
    case other           => other._1._2
  })

  private lazy val b_other_operator: Parser[String] = "nand" | "nor" | "xor" | "equiv" | "impl"

  private lazy val b_literal: Parser[ScalaNode] = ("1" | "true" | "TRUE") ^^ (_ => TRUE) | ("0" | "false" | "FALSE") ^^ (_ => FALSE)

  private lazy val b_variable: Parser[Variable] = "[a-zA-Z]".r ^^ (str => Variable(str.charAt(0).toLower))

  def parse(toParse: String): Option[ScalaNode] = parseAll(b_expression, toParse) match {
    case Success(result, _)    => Some(result)
    case NoSuccess(msg, input) => throw new IllegalArgumentException(msg + " :: " + input)
    case Error(_, _)           => null
    case Failure(_, _)         => null
  }
}
