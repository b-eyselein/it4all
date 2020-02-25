package model.adaption

import enumeratum.{Enum, EnumEntry}

sealed abstract class BloomTaxonomy(level: Int) extends EnumEntry

case object BloomTaxonomy extends Enum[BloomTaxonomy] {

  val values: IndexedSeq[BloomTaxonomy] = findValues

  case object Remember extends BloomTaxonomy(1)

  case object Understand extends BloomTaxonomy(2)

  case object Apply extends BloomTaxonomy(3)

  case object Analyse extends BloomTaxonomy(4)

  case object Evaluate extends BloomTaxonomy(5)

  case object Create extends BloomTaxonomy(6)

}
