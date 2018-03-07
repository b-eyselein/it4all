package model.lti

object LTIToolConsumerInfo {

  def fromRequestData(data: Map[String, Seq[String]]): LTIToolConsumerInfo = new LTIToolConsumerInfo(
    productFamilyCode = data.getOrElse("tool_consumer_info_product_family_code", Seq.empty).mkString,
    version = data.getOrElse("tool_consumer_info_version", Seq.empty).mkString,
    instanceInfo = LTIToolConsumerInstanceInfo(
      description = data.getOrElse("tool_consumer_instance_description", Seq.empty).mkString,
      guid = data.getOrElse("tool_consumer_instance_guid", Seq.empty).mkString,
      name = data.getOrElse("tool_consumer_instance_name", Seq.empty).mkString
    )
  )

}

case class LTIToolConsumerInstanceInfo(description: String, guid: String, name: String)

case class LTIToolConsumerInfo(productFamilyCode: String, version: String, instanceInfo: LTIToolConsumerInstanceInfo)