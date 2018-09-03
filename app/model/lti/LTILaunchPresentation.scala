package model.lti

object LTILaunchPresentation {

  def fromRequestData(data: Map[String, Seq[String]]): LTILaunchPresentation = new LTILaunchPresentation(
    documentTarget = data.getOrElse("launch_presentation_document_target", Seq[String]()).mkString,
    locale = data.getOrElse("launch_presentation_locale", Seq[String]()).mkString,
    returnURL = data.getOrElse("launch_presentation_return_url", Seq[String]()).mkString
  )

}

final case class LTILaunchPresentation(documentTarget: String, locale: String, returnURL: String)