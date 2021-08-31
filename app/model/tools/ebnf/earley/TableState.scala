package model.tools.ebnf.earley

import model.tools.ebnf.grammar._

final case class Node(private val value: TableState, private val children: Seq[Node]) {

  def stringify(level: Int = 0): String =
    " ".repeat(level) + value.stringify + "\n" + children.map(_.stringify(level + 1)).mkString

}



final case class TableState(
  terminalName: String,
  production: EarleyProductionSequence,
  dotIndex: Int,
  startColumn: TableColumn
) {

  import TableState.buildTrees

  var endColumn: Option[TableColumn] = None

  def isCompleted: Boolean = dotIndex >= production.children.size

  def getNextProductionTerm: Option[EarleyProductionSequenceContent] =
    if (isCompleted) None else Some(production.children(dotIndex))

  def stringify: String = {
    val (left, right) = production.children.map(_.toString).splitAt(dotIndex)

    String.format(
      s"%-6s -> %-20s [${startColumn.index}-${endColumn.get.index}]",
      terminalName,
      left.mkString(" ") + "\u00B7" + right.mkString(" ")
    )
  }

  def trees(grammar: BaseNormalizedFormGrammar): Seq[Node] = buildTrees(this, grammar)

}

object TableState {

  private def getRulesForTerminalsInProduction(
    state: TableState,
    grammar: BaseNormalizedFormGrammar
  ): Seq[(Variable, BaseNormalizedFormTopLevelElement)] = state.production.variables
    .map { variable => (variable, grammar.rules(variable)) }

  def buildTrees(state: TableState, grammar: BaseNormalizedFormGrammar): Seq[Node] = buildTreesHelper(
    Seq.empty,
    state,
    getRulesForTerminalsInProduction(state, grammar).size - 1,
    state.endColumn.get,
    grammar
  )

  private def buildTreesHelper(
    children: Seq[Node],
    state: TableState,
    ruleIndex: Int,
    endCol: TableColumn,
    grammar: BaseNormalizedFormGrammar
  ): Seq[Node] = if (ruleIndex < 0) {
    // this is the base-case for the recursion (we matched the entire rule)
    Seq(Node(state, children))
  } else {
    val startCol = if (ruleIndex == 0) {
      // if this is the first rule
      Some(state.startColumn)
    } else {
      None
    }

    val (variable, _) = getRulesForTerminalsInProduction(state, grammar)(ruleIndex)

    endCol.states
      .takeWhile {
        // this prevents an endless recursion: since the states are filled in order of completion, we know that
        // X cannot depend on state Y that comes after it X in chronological order
        _ ne state
      }
      .filter { s =>
        // this state is out of the question -- either not completed or does not match the name
        s.isCompleted && s.terminalName == variable.value
      }
      .filter { s =>
        // if startCol isn't null, this state must span from startCol to endCol
        startCol.isEmpty || (startCol.get == s.startColumn)
      }
      .flatMap { st =>
        // okay, so `st` matches -- now we need to create a tree for every possible sub-match
        buildTrees(st, grammar).flatMap { subTree =>
          // now try all options
          buildTreesHelper(subTree +: children, state, ruleIndex - 1, st.startColumn, grammar)
        }
      }
      .toSeq
  }
}
