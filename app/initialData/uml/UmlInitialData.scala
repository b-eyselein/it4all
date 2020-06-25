package initialData.uml

import initialData.InitialData
import initialData.uml.coll_1.UmlColl1Ex1.umlColl1Ex1
import initialData.uml.coll_1.umlColl1Ex2.umlColl1Ex2
import model.ExerciseCollection
import model.tools.uml.UmlExerciseContent
import model.tools.uml.UmlTool.UmlExercise

object UmlInitialData extends InitialData[UmlExerciseContent] {

  override protected val toolId: String = "uml"

  private val umlColl01 = ExerciseCollection(1, toolId, title = "Uml Basics", authors = Seq("bje40dc"))

  private val umlColl01Exes = Seq(umlColl1Ex1, umlColl1Ex2)

  override val data: Seq[(ExerciseCollection, Seq[UmlExercise])] = Seq(
    (umlColl01, umlColl01Exes)
  )

}
