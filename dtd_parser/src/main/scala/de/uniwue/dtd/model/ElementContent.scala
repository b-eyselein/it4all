package de.uniwue.dtd.model

import de.uniwue.dtd.parser.DocTypeDefParser

sealed trait ElementContent {
  def asString(needsParentheses: Boolean): String
}

// Fixed content types

sealed abstract class StaticElementContent(name: String) extends ElementContent {
  override def asString(needsParentheses: Boolean): String = "(" + name + ")"
}

case object PCData extends StaticElementContent("#PCDATA")

case object EmptyContent extends StaticElementContent("EMPTY")

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

final case class RepElementContent(childContent: ElementContent) extends UnaryOperatorElementContent {
  override val operator: String = DocTypeDefParser.repOperator
}

final case class Rep1ElementContent(childContent: ElementContent) extends UnaryOperatorElementContent {
  override val operator: String = DocTypeDefParser.rep1Operator
}

final case class OptElementContent(childContent: ElementContent) extends UnaryOperatorElementContent {
  override val operator: String = DocTypeDefParser.optOperator
}

final case class ChildElementContent(childName: String) extends ElementContent {
  override def asString(needsParentheses: Boolean): String = if (needsParentheses) "(" + childName + ")" else childName
}

// Multi element types

sealed trait MultiElementContent extends ElementContent {
  val children: Seq[ElementContent]

  val joinChar: String

  override def asString(needsParentheses: Boolean): String = if (needsParentheses) "(" + childrenAsStrings + ")" else childrenAsStrings

  private def childrenAsStrings: String = children.map(_.asString(false)).mkString(joinChar)
}

final case class SequenceContent(children: Seq[ElementContent]) extends MultiElementContent {
  override val joinChar: String = ","
}

final case class AlternativeContent(children: Seq[ElementContent]) extends MultiElementContent {
  override val joinChar: String = " | "
}
