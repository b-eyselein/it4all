package model.ebnf

import scala.language.postfixOps

sealed abstract class Symbol

case class Variable(variable: String) extends Symbol {

  override def toString: String = variable

}

case class Terminal(name: String) extends Symbol {

  override def toString = s"'$name'"

}

object Grammar {

  val MaxDeriveDepth = 10

}

case class Grammar(terminals: Seq[Terminal], variables: Seq[Variable], startSymbol: Variable, rules: Map[Variable, Replacement]) {

  def deriveAll: Seq[String] = deriveNew(Nil, Seq(VariableReplacement(startSymbol)), 0)

  private def deriveNew(prefix: Seq[TerminalReplacement], suffix: Seq[Replacement], depth: Int): Seq[String] =
    if (depth >= Grammar.MaxDeriveDepth) Seq.empty
    else suffix match {
      case Nil          => Seq(prefix map (_.t.name) mkString)
      case head :: tail => head match {
        // case tr: replacement finished
        case tr: TerminalReplacement => deriveNew(prefix :+ tr, tail, depth)
        // case other: replacement can still be done
        case other =>
          val replacedWith: Seq[Seq[Replacement]] = other.getReplacements(rules)
          replacedWith flatMap (repl => deriveNew(prefix, repl ++ tail, depth + 1)) filter (_.nonEmpty)
      }
    }

}