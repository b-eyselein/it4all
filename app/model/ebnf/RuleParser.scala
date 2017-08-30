package model.ebnf

import scala.util.parsing.combinator.JavaTokenParsers
import model.ebnf.Replacement._

object RuleParser extends JavaTokenParsers {

  private lazy val variable: Parser[Variable] = "\\w+".r ^^ { new Variable(_) }

  private lazy val terminalsymbol: Parser[TerminalSymbol] = "'" ~ "\\w+".r ~ "'" ^^ { terminal => new TerminalSymbol(terminal._1._2) }

  // Rule

  private lazy val ebnfRule: Parser[Rule] = variable ~ "=" ~ sequence ^^ { r => new Rule(r._1._1, r._2) }

  // Replacements

  private lazy val sequence: Parser[Replacement] = alternative ~ rep(seqOperator ~ alternative) ^^ {
    case seq ~ Nil => seq
    case seq1 ~ seqs => new Sequence(seq1 :: seqs.map(_._2))
  }

  private lazy val alternative: Parser[Replacement] = option ~ rep(altOperator ~ option) ^^ {
    case alt ~ Nil => alt
    case alt1 ~ alts => new Alternative(alt1 :: alts.map(_._2))
  }

  private lazy val option: Parser[Replacement] = repetition ~ opt(optOperator) ^^ {
    case rep ~ None => rep
    case rep ~ Some(_) => new Option(rep)
  }

  private lazy val repetition: Parser[Replacement] = repetition1 ~ opt(repOperator) ^^ {
    case rep ~ None => rep
    case rep ~ Some(_) => new Repetition(rep)
  }

  private lazy val repetition1: Parser[Replacement] = subterm ~ opt(rep1Operator) ^^ {
    case rep ~ None => rep
    case rep ~ Some(_) => new Min1Repetition(rep)
  }

  private lazy val subterm: Parser[Replacement] = varRepl | terminalRepl | ("(" ~ sequence ~ ")" ^^ { grouping => new Grouping(grouping._1._2) })

  private lazy val terminalRepl: Parser[Replacement] = terminalsymbol ^^ { termin => new TerminalReplacement(termin) }

  private lazy val varRepl: Parser[Replacement] = variable ^^ { variab => new VarReplacement(variab) }

  def parse(toParse: String) = parseAll(ebnfRule, toParse) match {
    case Success(result, input) => result
    case NoSuccess(msg, input) => throw new Exception(msg)
    case Error(msg, input) => throw new Exception(msg)
    case Failure(msg, input) => throw new Exception(msg)
  }

  def parseRules(rules: List[String]) = rules.map(parse(_))

}