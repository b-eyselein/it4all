package model.lti

object LTIContext {

  def fromRequestData(data: Map[String, Seq[String]]): LTIContext = new LTIContext(
    id = data.getOrElse("context_id", Seq.empty).mkString,
    label = data.getOrElse("context_label", Seq.empty).mkString,
    title = data.getOrElse("context_title", Seq.empty).mkString,
    aType = data.getOrElse("context_type", Seq.empty).mkString,
  )

}

case class LTIContext(id: String, label: String, title: String, aType: String)
