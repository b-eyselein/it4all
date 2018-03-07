package model.lti

object LTILaunchPresentation {

  def fromRequestData(data: Map[String, Seq[String]]): LTILaunchPresentation = new LTILaunchPresentation(
    documentTarget = data.getOrElse("launch_presentation_document_target", Seq.empty).mkString,
    locale = data.getOrElse("launch_presentation_locale", Seq.empty).mkString,
    returnURL = data.getOrElse("launch_presentation_return_url", Seq.empty).mkString
  )

}

case class LTILaunchPresentation(documentTarget: String, locale: String, returnURL: String)