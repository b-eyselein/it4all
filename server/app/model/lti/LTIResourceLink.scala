package model.lti

object LTIResourceLink {

  def fromRequestData(data: Map[String, Seq[String]]): LTIResourceLink = new LTIResourceLink(
    linkId = data.getOrElse("resource_link_id", Seq[String]()).mkString,
    linkTitle = data.getOrElse("resource_link_title", Seq[String]()).mkString,
    linkDescription = data.getOrElse("resource_link_description", Seq[String]()).mkString
  )

}

final case class LTIResourceLink(linkId: String, linkTitle: String, linkDescription: String)