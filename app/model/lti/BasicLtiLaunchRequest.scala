package model.lti


case class BasicLtiLaunchRequest(
                             ltiContext: LTIContext,
                             ltiExt: LTIExt,
                             ltiLaunchPresentation: LTILaunchPresentation,
                             lisCourseSectionSourcedId: String,
                             lisOutcomeServiceUrl: String,
                             lisPersonData: LTILisPersonData,
                             lisResultSourcedId: String,
                             ltiInfo: LTIInfo,
                             oAuthCallback: String,
                             resourceLink: LTIResourceLink,
                             roles: Seq[String],
                             ltiToolConsumerInfo: LTIToolConsumerInfo,
                             userId: String,
                           )

object BasicLtiLaunchRequest {

  def fromRequest(data: Map[String, Seq[String]]): BasicLtiLaunchRequest = new BasicLtiLaunchRequest(
    ltiContext = LTIContext.fromRequestData(data),
    ltiExt = LTIExt.fromRequestData(data),
    ltiLaunchPresentation = LTILaunchPresentation.fromRequestData(data),
    lisCourseSectionSourcedId = data.getOrElse("lis_course_section_sourcedid", Seq.empty).mkString,
    lisOutcomeServiceUrl = data.getOrElse("lis_outcome_service_url", Seq.empty).mkString,
    lisPersonData = LTILisPersonData.fromRequestData(data),
    lisResultSourcedId = data.getOrElse("lis_result_sourcedid", Seq.empty).mkString,
    ltiInfo = LTIInfo.fromRequestData(data),
    oAuthCallback = data.getOrElse("oauth_callback", Seq.empty).mkString,
    resourceLink = LTIResourceLink.fromRequestData(data),
    roles = data.getOrElse("roles", Seq.empty),
    ltiToolConsumerInfo = LTIToolConsumerInfo.fromRequestData(data),
    userId = data.getOrElse("user_id", Seq.empty).mkString
  )

}