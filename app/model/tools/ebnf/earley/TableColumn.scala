package model.tools.ebnf.earley

import scala.collection.mutable.{Buffer => MutableBuffer}

final case class TableColumn(index: Int, token: String) {

  val states: MutableBuffer[TableState] = MutableBuffer.empty

  def insertState(state: TableState): Unit = if (!states.contains(state)) {
    states += state
    state.endColumn = Some(this)
  }

  class ModifiableIterator extends Iterator[TableState] {

    private var i = 0

    override def hasNext: Boolean = i < states.size

    override def next(): TableState = {
      val state = states(i)
      i += 1
      state
    }
  }

  def modifiableStateIterator() = new ModifiableIterator()

  def stringify(showUncompleted: Boolean = true): String =
    s"""[$index] '$token'
       |=======================================
       |${this.states.filter { showUncompleted || _.isCompleted }.map(_.stringify).mkString("\n")}
       |""".stripMargin
}
