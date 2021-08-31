package model.tools.ebnf.earley

import model.tools.ebnf.conversion.{BaseNormalizationConverter, EbnfToBnfConverter}
import model.tools.ebnf.grammar._

class EarleyParser(val grammar: BaseNormalizedFormGrammar) {

  def this(grammar: BackusNaurFormGrammar) = this(BaseNormalizationConverter.convert(grammar))

  def this(grammar: ExtendedBackusNaurFormGrammar) = this(EbnfToBnfConverter.convert(grammar))

  // this is the name of the special "gamma" rule added by the algorithm
  // (this is unicode for 'LATIN SMALL LETTER GAMMA')
  private val GAMMA_RULE = "\u0263" // "\u0194"

  /*
   * the Earley algorithm's core: add gamma rule, fill up table, and check if the gamma rule
   * spans from the first column to the last one. return the final gamma state, or null,
   * if the parse failed.
   */
  def parse(tokens: Seq[String], debug: Boolean = false): Option[TableState] = {

    val columns: Seq[TableColumn] = ("" +: tokens).zipWithIndex
      .map { case (token, index) => TableColumn(index, token) }

    val firstColumn: TableColumn = columns.head

    firstColumn.insertState(
      TableState(GAMMA_RULE, EarleyProductionSequence(Seq(grammar.startSymbol)), 0, firstColumn)
    )

    columns.zipWithIndex
      .foreach { case (column, colIndex) =>
        column.modifiableStateIterator().foreach { state =>
          state.getNextProductionTerm match {
            case None => complete(column, state)
            case Some(term) =>
              term match {
                case nt: Variable =>
                  // inserts new states into current columns!
                  predict(column, nt)
                case t: Terminal =>
                  if (colIndex + 1 < columns.size) {
                    scan(columns(colIndex + 1), state, token = t.value)
                  } else {
                    // No more columns, do nothing...
                    ()
                  }
              }
          }
        }

        if (debug) {
          // DEBUG -- print the table during parsing, column after column
          println(column.stringify())
        }
      }

    // find end state (return null if not found)
    columns.last.states.find { s => s.terminalName == GAMMA_RULE && s.isCompleted }
  }

  /*
   * Earley scan
   */
  private def scan(nextColumn: TableColumn, state: TableState, token: String): Unit = {
    if (token == nextColumn.token) {
      nextColumn.insertState(TableState(state.terminalName, state.production, state.dotIndex + 1, state.startColumn))
    }
  }

  /*
   * Earley predict.
   */
  private def predict(column: TableColumn, nt: Variable): Unit = grammar
    .alternativeForVariable(nt)
    .children
    .foreach { prod: BaseNormalizedFormSequence =>
      // Ignore all EmptyWords in grammar!
      val filteredProd = EarleyProductionSequence(prod.children.flatMap {
        case EmptyWord   => None
        case v: Variable => Some(v)
        case t: Terminal => Some(t)
      })

      val tableState = TableState(nt.value, filteredProd, dotIndex = 0, startColumn = column)

      column.insertState(tableState)

      tableState.getNextProductionTerm match {
        case Some(nextTerm: EarleyProductionSequenceContent) =>
          val producesEmptyWord = nextTerm match {
            case _: Terminal => false
            case v: Variable => v.producesEmptyWord(grammar, Seq.empty)
          }

          if (producesEmptyWord) {
            column.insertState(TableState(nt.value, filteredProd, dotIndex = 1, startColumn = column))
          }
        case _ => ()
      }
    }

  /*
   * Earley complete
   */
  private def complete(col: TableColumn, currentState: TableState): Unit = currentState.startColumn.states
    .foreach { state =>
      state.getNextProductionTerm match {
        case Some(nt: Variable) if nt.value == currentState.terminalName =>
          col.insertState(TableState(state.terminalName, state.production, state.dotIndex + 1, state.startColumn))
        case _ => false
      }
    }

}
