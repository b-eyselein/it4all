package initialData.uml

import initialData.InitialData
import initialData.uml.coll_1.UmlColl1Ex1.umlColl1Ex1
import initialData.uml.coll_1.umlColl1Ex2.umlColl1Ex2
import model.ExerciseCollection
import model.tools.uml.UmlTool.UmlExercise

object UmlData extends InitialData {

  override protected val toolId: String = "uml"

  val uml_collections_and_exes: Seq[(ExerciseCollection, Seq[UmlExercise])] = Seq(
    (
      ExerciseCollection(
        1,
        toolId,
        title = "Uml Basics",
        authors = Seq("bje40dc"),
        text = "Aufgaben um die Grundlagen von UML-Klassendiagrammen zu erlernen."
      ),
      Seq(umlColl1Ex1, umlColl1Ex2)
    )
  )

}
