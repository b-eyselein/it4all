package initialData.rose

import initialData.InitialData
import initialData.rose.coll_1.RoseColl1Ex1.roseColl1Ex1
import initialData.rose.coll_1.RoseColl1Ex2.roseColl1Ex2
import model.ExerciseCollection
import model.tools.rose.RoseExerciseContent
import model.tools.rose.RoseTool.RoseExercise

object RoseInitialData extends InitialData[RoseExerciseContent] {

  override protected val toolId: String = "rose"

  private val roseColl01 = ExerciseCollection(1, toolId, title = "Rose Basics", authors = Seq("bje40dc"))

  private val roseColl01Exes = Seq(roseColl1Ex1, roseColl1Ex2)

  override val data: Seq[(ExerciseCollection, Seq[RoseExercise])] = Seq(
    (roseColl01, roseColl01Exes)
  )

}
