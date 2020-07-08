package model.lti

object LTIContext {

  def fromRequestData(data: Map[String, Seq[String]]): LTIContext =
    new LTIContext(
      id = data.getOrElse("context_id", Seq[String]()).mkString,
      label = data.getOrElse("context_label", Seq[String]()).mkString,
      title = data.getOrElse("context_title", Seq[String]()).mkString,
      aType = data.getOrElse("context_type", Seq[String]()).mkString
    )

}

final case class LTIContext(id: String, label: String, title: String, aType: String)
