package model.lti

object LTIResourceLink {

  def fromRequestData(data: Map[String, Seq[String]]): LTIResourceLink = new LTIResourceLink(
    linkId = data.getOrElse("resource_link_id", Seq.empty).mkString,
    linkTitle = data.getOrElse("resource_link_title", Seq.empty).mkString,
    linkDescription = data.getOrElse("resource_link_description", Seq.empty).mkString
  )

}

case class LTIResourceLink(linkId: String, linkTitle: String, linkDescription: String)