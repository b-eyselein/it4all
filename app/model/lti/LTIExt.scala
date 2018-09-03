package model.lti

object LTIExt {

  def fromRequestData(data: Map[String, Seq[String]]): LTIExt = new LTIExt(
    lms = data.getOrElse("ext_lms", Seq[String]()).mkString,
    username = data.getOrElse("ext_user_username", Seq[String]()).mkString
  )

}

final case class LTIExt(lms: String, username: String)
