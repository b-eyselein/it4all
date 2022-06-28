package model

import play.api.data.Form
import play.api.data.Forms.{mapping, text}

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

}
