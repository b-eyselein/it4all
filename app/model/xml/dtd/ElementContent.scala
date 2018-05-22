package model.xml.dtd

sealed trait ElementContent {
  def asString(needsParentheses: Boolean): String
}

// Fixed content types

abstract class StaticElementContent(name: String) extends ElementContent {
  override def asString(needsParentheses: Boolean): String = if (needsParentheses) "(" + name + ")" else name
}

case object PCData extends StaticElementContent("#PCDATA")

case object EmptyContent extends StaticElementContent("EMTPY")

case object AnyContent extends StaticElementContent("ANY")

// Single element types

sealed trait UnaryOperatorElementContent extends ElementContent {
  val operator: String

  val childContent: ElementContent

  override def asString(needsParentheses: Boolean): String = {
    val childrenNeedParentheses: Boolean = !needsParentheses && (childContent match {
      case _: MultiElementContent => true
      case _                      => false
    })

    val childContentStr = childContent.asString(childrenNeedParentheses)

    (if (needsParentheses) "(" + childContentStr + ")" else childContentStr) + operator
  }
}

case class RepElementContent(childContent: ElementContent) extends UnaryOperatorElementContent {
  override val operator: String = DocTypeDefParser.repOperator
}

case class Rep1ElementContent(childContent: ElementContent) extends UnaryOperatorElementContent {
  override val operator: String = DocTypeDefParser.rep1Operator
}

case class OptElementContent(childContent: ElementContent) extends UnaryOperatorElementContent {
  override val operator: String = DocTypeDefParser.optOperator
}

case class ChildElementContent(childName: String) extends ElementContent {
  override def asString(needsParentheses: Boolean): String = if (needsParentheses) "(" + childName + ")" else childName
}

// Multi element types

sealed trait MultiElementContent extends ElementContent {
  val children: Seq[ElementContent]

  val joinChar: String

  override def asString(needsParentheses: Boolean): String = if (needsParentheses) "(" + childrenAsStrings(false) + ")" else childrenAsStrings(false)

  private def childrenAsStrings(needParentheses: Boolean): String = children map (_.asString(needParentheses)) mkString joinChar
}

case class SequenceContent(children: Seq[ElementContent]) extends MultiElementContent {
  override val joinChar: String = DocTypeDefParser.SequenceSplitCharacter
}

case class AlternativeContent(children: Seq[ElementContent]) extends MultiElementContent {
  override val joinChar: String = " | "
}