package model.essentials

import scala.util.parsing.combinator.JavaTokenParsers

object BoolNodeParser extends JavaTokenParsers {

  private lazy val boolExpression: Parser[ScalaNode] = boolTerm ~ rep("or" ~ boolTerm) ^^ {
    case f1 ~ fs => (f1 /: fs) ((f1, fs) => f1 or fs._2)
  }

  private lazy val boolTerm: Parser[ScalaNode] = (boolOtherTerm ~ rep("and" ~ boolOtherTerm)) ^^ {
    case f1 ~ fs => (f1 /: fs) ((f1, fs) => f1 and fs._2)
  }

  private lazy val boolOtherTerm: Parser[ScalaNode] = (boolNotFactor ~ rep(boolOtherOperator ~ boolNotFactor)) ^^ {
    case f1 ~ fs => (f1 /: fs) ((f1, fs) => fs._1 match {
      case "nand"  => f1 nand fs._2
      case "nor"   => f1 nor fs._2
      case "xor"   => f1 xor fs._2
      case "equiv" => f1 equiv fs._2
      case _       => f1 impl fs._2 // Only impl possible
    })
  }

  private lazy val boolNotFactor: Parser[ScalaNode] = opt("not") ~ boolFactor ^^ {
    case Some(_) ~ f => ScalaNode.not(f);
    case None ~ f    => f
  }

  private lazy val boolFactor: Parser[ScalaNode] = boolLiteral | boolVariable | ("(" ~ boolExpression ~ ")" ^^ {
    case "(" ~ exp ~ ")" => exp
    case other           => other._1._2
  })

  private lazy val boolOtherOperator: Parser[String] = "nand" | "nor" | "xor" | "equiv" | "impl"

  private lazy val boolLiteral: Parser[ScalaNode] = ("1" | "true" | "TRUE") ^^ (_ => TRUE) | ("0" | "false" | "FALSE") ^^ (_ => FALSE)

  private lazy val boolVariable: Parser[Variable] = "[a-zA-Z]".r ^^ (str => Variable(str.charAt(0).toLower))

  def parseBoolFormula(toParse: String): Option[ScalaNode] = parseAll(boolExpression, toParse) match {
    case Success(result, _)    => Some(result)
    case NoSuccess(msg, input) => throw new IllegalArgumentException(msg + " :: " + input)
    case Error(_, _)           => null
    case Failure(_, _)         => null
  }
}
