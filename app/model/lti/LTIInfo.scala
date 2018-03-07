package model.lti

object LTIInfo {

  def fromRequestData(data: Map[String, Seq[String]]): LTIInfo = new LTIInfo(
    messageType = data.getOrElse("lti_message_type", Seq.empty).mkString,
    version = data.getOrElse("lti_version", Seq.empty).mkString
  )

}

case class LTIInfo(messageType: String, version: String)
