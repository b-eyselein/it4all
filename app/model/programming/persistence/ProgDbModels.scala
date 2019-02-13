package model.programming.persistence

import model.persistence.DbModels
import model.programming._
import model.{ExerciseState, HasBaseValues, SemanticVersion}
import play.api.libs.json.JsValue

object ProgDbModels extends DbModels {

  override type Exercise = ProgExercise

  override type DbExercise = DbProgExercise

  def dbExerciseFromExercise(ex: ProgExercise): DbProgExercise =
    DbProgExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state,
      ex.folderIdentifier, ex.functionName, ex.outputType, ex.baseData)

  def exerciseFromDbValues(dbProgEx: DbProgExercise,
                               inputTypes: Seq[ProgInput],
                               sampleSolutions: Seq[ProgSampleSolution],
                               sampleTestData: Seq[SampleTestData],
                               maybeClassDiagramPart: Option[UmlClassDiagPart]): ProgExercise =
    ProgExercise(
      dbProgEx.id, dbProgEx.semanticVersion, dbProgEx.title, dbProgEx.author, dbProgEx.text, dbProgEx.state,
      dbProgEx.folderIdentifier, dbProgEx.functionname, dbProgEx.outputType, dbProgEx.baseData,
      inputTypes, sampleSolutions, sampleTestData, maybeClassDiagramPart
    )

}

final case class DbProgExercise(id: Int,
                                semanticVersion: SemanticVersion,
                                title: String,
                                author: String,
                                text: String,
                                state: ExerciseState,
                                folderIdentifier: String,
                                functionname: String,
                                outputType: ProgDataType,
                                baseData: Option[JsValue]) extends HasBaseValues