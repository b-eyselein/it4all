package model

final case class Topic(
  abbreviation: String,
  toolId: String,
  title: String,
  maxLevel: Level = Level.Expert
)

final case class TopicWithLevel(
  topic: Topic,
  level: Level
)
