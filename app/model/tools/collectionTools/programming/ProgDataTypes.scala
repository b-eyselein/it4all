package model.tools.collectionTools.programming

import enumeratum.{EnumEntry, PlayEnum}


sealed trait ProgDataType {

  def typeName: String

}


object ProgDataTypes {

  // Non Generic Data Types

  sealed abstract class NonGenericProgDataType(val typeName: String) extends ProgDataType with EnumEntry


  object NonGenericProgDataType extends PlayEnum[NonGenericProgDataType] {

    val values: IndexedSeq[NonGenericProgDataType] = findValues


    case object VOID extends NonGenericProgDataType("void")

    case object BOOLEAN extends NonGenericProgDataType("boolean")

    case object INTEGER extends NonGenericProgDataType("int")

    case object FLOAT extends NonGenericProgDataType("float")

    case object STRING extends NonGenericProgDataType("string")

  }

  // Generic Data Types

  sealed abstract class GenericProgDataType extends ProgDataType


  final case class LIST(subtype: ProgDataType) extends GenericProgDataType {

    override def typeName: String = s"List[${subtype.typeName}]"

  }

  final case class TUPLE(subTypes: Seq[ProgDataType]) extends GenericProgDataType {

    override def typeName: String = s"Tuple[${subTypes.map(_.typeName).mkString(", ")}]"

  }

  final case class DICTIONARY(keyType: ProgDataType, valueType: ProgDataType) extends GenericProgDataType {

    override def typeName: String = s"Dict[${keyType.typeName}, ${valueType.typeName}]"

  }

}
