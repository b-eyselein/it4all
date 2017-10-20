package model.ebnf

import model.CorrectionException
import model.ebnf.Replacement._

import scala.util.parsing.combinator.JavaTokenParsers

object RuleParser extends JavaTokenParsers {

  private lazy val sequence: Parser[Replacement] = alternative ~ rep(opt(seqOperator) ~ alternative) ^^ {
    case seq1 ~ seqs => seqs.foldLeft(seq1)((repl, seqNext) => repl ~ seqNext._2)
  }

  private lazy val alternative = option ~ rep(altOperator ~ option) ^^ {
    case alt1 ~ alts => alts.foldLeft(alt1)((alt, altNext) => alt | altNext._2)
  }

  private lazy val option = subterm ~ opt(optOperator | repOperator | rep1Operator) ^^ {
    case rep ~ None => rep
    case rep ~ Some(Replacement.optOperator) => rep.?
    case rep ~ Some(Replacement.repOperator) => rep.*
    case rep ~ Some(Replacement.rep1Operator) => rep.+
    case rep ~ Some(t) => throw new CorrectionException("", s"Not awaited: $t")
  }

  private lazy val subterm = /*variable | terminalsymbol |*/ ("(" ~ sequence ~ ")" ^^ { grouping => Grouping(grouping._1._2) })

  private lazy val variable = "\\w+".r ^^ {
    Variable
  }

  private lazy val terminalsymbol = "'" ~ "\\w+".r ~ "'" ^^ { terminal => Terminal(terminal._1._2) }

  def parseRules(repl: String) = parseAll(sequence, repl) match {
    case Success(result, input) => result
    case _ => throw new Exception("Fehler beim Parsen!")
  }

}