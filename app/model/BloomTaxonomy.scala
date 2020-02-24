package model

import enumeratum.{Enum, EnumEntry}

sealed trait BloomTaxonomy extends EnumEntry

case object BloomTaxonomy extends Enum[BloomTaxonomy] {

  val values: IndexedSeq[BloomTaxonomy] = findValues

  case object Remember extends BloomTaxonomy

  case object Understand extends BloomTaxonomy

  case object Apply extends BloomTaxonomy

  case object Analyse extends BloomTaxonomy

  case object Evaluate extends BloomTaxonomy

  case object Create extends BloomTaxonomy

}
