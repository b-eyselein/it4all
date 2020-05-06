package initialData.rose

import initialData.InitialData
import initialData.rose.coll_1.RoseColl1Ex1.roseColl1Ex1
import initialData.rose.coll_1.RoseColl1Ex2.roseColl1Ex2
import model.ExerciseCollection
import model.tools.rose.RoseExerciseContent
import model.tools.rose.RoseTool.RoseExercise

object RoseData extends InitialData[String, RoseExerciseContent] {

  override protected val toolId: String = "rose"

  override val data: Seq[(ExerciseCollection, Seq[RoseExercise])] = Seq(
    (
      ExerciseCollection(
        collectionId = 1,
        toolId,
        title = "Rose Basics",
        authors = Seq("bje40dc"),
        text = "Einfache Aufgaben um den Umgang mit diesem Tool zu lernen"
      ),
      Seq(roseColl1Ex1, roseColl1Ex2)
    )
  )

}
