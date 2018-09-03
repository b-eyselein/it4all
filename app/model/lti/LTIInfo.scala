package model.lti

object LTIInfo {

  def fromRequestData(data: Map[String, Seq[String]]): LTIInfo = new LTIInfo(
    messageType = data.getOrElse("lti_message_type", Seq[String]()).mkString,
    version = data.getOrElse("lti_version", Seq[String]()).mkString
  )

}

final case class LTIInfo(messageType: String, version: String)
