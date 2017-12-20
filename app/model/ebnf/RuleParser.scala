package model.ebnf

import model.ebnf.Replacement._

object RuleParser extends scala.util.parsing.combinator.JavaTokenParsers {

  private lazy val sequence: Parser[Replacement] = alternative ~ rep(opt(seqOperator) ~ alternative) ^^ {
    case seq1 ~ seqs => seqs.foldLeft(seq1)((replacement, seqNext) => replacement ~ seqNext._2)
  }

  private lazy val alternative = option ~ rep(altOperator ~ option) ^^ {
    case alt1 ~ alts => alts.foldLeft(alt1)((alt, altNext) => alt | altNext._2)
  }

  private lazy val option = subterm ~ opt(optOperator | repOperator | rep1Operator) ^^ {
    case rep ~ None                           => rep
    case rep ~ Some(Replacement.optOperator)  => rep.?
    case rep ~ Some(Replacement.repOperator)  => rep.*
    case rep ~ Some(Replacement.rep1Operator) => rep.+
    case _ ~ Some(t)                          => throw new Exception("Not awaited: " + t)
  }

  private lazy val subterm: Parser[Replacement] = variable | terminalsymbol | "(" ~ sequence ~ ")" ^^ { grouping => Grouping(grouping._1._2) }

  private lazy val variable: Parser[Replacement] = "\\w+".r ^^ { str => VariableReplacement(Variable(str)) }

  private lazy val terminalsymbol: Parser[Replacement] = "'" ~ "\\w+".r ~ "'" ^^ { terminal => TerminalReplacement(Terminal(terminal._1._2)) }

  def parseRules(toParse: String): Replacement = parseAll(sequence, toParse) match {
    case Success(result, _) => result
    case Failure(msg, _)    => throw new Exception("Fataler Fehler beim Parsen: " + msg)
    case Error(msg, _)      => throw new Exception("Fehler beim Parsen: " + msg)
  }


}