package model.lti

object LTIToolConsumerInfo {

  def fromRequestData(data: Map[String, Seq[String]]): LTIToolConsumerInfo = new LTIToolConsumerInfo(
    productFamilyCode = data.getOrElse("tool_consumer_info_product_family_code", Seq[String]()).mkString,
    version = data.getOrElse("tool_consumer_info_version", Seq[String]()).mkString,
    instanceInfo = LTIToolConsumerInstanceInfo(
      description = data.getOrElse("tool_consumer_instance_description", Seq[String]()).mkString,
      guid = data.getOrElse("tool_consumer_instance_guid", Seq[String]()).mkString,
      name = data.getOrElse("tool_consumer_instance_name", Seq[String]()).mkString
    )
  )

}

final case class LTIToolConsumerInstanceInfo(description: String, guid: String, name: String)

final case class LTIToolConsumerInfo(productFamilyCode: String, version: String, instanceInfo: LTIToolConsumerInstanceInfo)