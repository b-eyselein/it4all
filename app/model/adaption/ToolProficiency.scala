package model.adaption

sealed trait Proficiency {
  val username: String
  val toolId: String
  val points: Int
}

final case class ToolProficiency(
  username: String,
  toolId: String,
  points: Int
) extends Proficiency

final case class TopicProficiency(
  username: String,
  toolId: String,
  topicId: Int,
  points: Int
) extends Proficiency

final case class Proficiencies(
  toolProf: ToolProficiency,
  topicProfs: Seq[TopicProficiency]
)
