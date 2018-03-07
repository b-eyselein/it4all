package model.lti

object LTIExt {

  def fromRequestData(data: Map[String, Seq[String]]): LTIExt = new LTIExt(
    lms = data.getOrElse("ext_lms", Seq.empty).mkString,
    username = data.getOrElse("ext_user_username", Seq.empty).mkString
  )

}

case class LTIExt(lms: String, username: String)
