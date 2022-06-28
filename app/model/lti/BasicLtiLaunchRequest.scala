package model.lti

import play.api.data.Form
import play.api.data.Forms.{mapping, text}

final case class LTIExt(
  lms: String,
  username: String
)

final case class BasicLtiLaunchRequest(
  lms: String,
  username: String
)

object BasicLtiLaunchRequest {

  val form: Form[BasicLtiLaunchRequest] = Form(
    mapping(
      "ext_lms"           -> text,
      "ext_user_username" -> text
    )(BasicLtiLaunchRequest.apply)(BasicLtiLaunchRequest.unapply)
  )

  def fromRequest(data: Map[String, Seq[String]]): BasicLtiLaunchRequest = BasicLtiLaunchRequest(
    lms = data.getOrElse("ext_lms", Seq.empty).mkString,
    username = data.getOrElse("ext_user_username", Seq.empty).mkString
  )

}
