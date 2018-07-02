package model.sql

import model.sql.SqlConsts._
import model.{ExerciseState, SemanticVersion}
import play.api.data.Form
import play.api.data.Forms._

object SqlFormMappings {

  def sqlCompleteExForm(collId: Int): Form[SqlCompleteEx] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      stateName -> ExerciseState.formField,
      semanticVersionName -> ???,
      collIdName -> number,
      exerciseTypeName -> SqlExerciseType.formField,
      tagsName -> seq(text),
      hintName -> optional(text),
      samplesName -> seq(text)
    )(createCompleteSqlExercise)(unapplySqlCompleteEx)
  )

  private def createCompleteSqlExercise(exerciseId: Int, title: String, author: String, text: String, state: ExerciseState,
                                        semanticVersion: SemanticVersion, collId: Int, exerciseType: SqlExerciseType,
                                        tags: Seq[String], hint: Option[String], sampleStrings: Seq[String]): SqlCompleteEx = {
    println(sampleStrings)

    val samples: Seq[SqlSample] = sampleStrings.filter(_.nonEmpty).zipWithIndex map { case (sample, index) =>
      SqlSample(index, exerciseId, collId, sample)
    }

    SqlCompleteEx(SqlExercise(exerciseId, title, author, text, state, semanticVersion, collId, exerciseType, tags.mkString("#"), hint), samples)
  }

  private def unapplySqlCompleteEx(ex: SqlCompleteEx): Option[(Int, String, String, String, ExerciseState,
    SemanticVersion, Int, SqlExerciseType, Seq[String], Option[String], Seq[String])] =
    Some(ex.ex.id, ex.ex.title, ex.ex.author, ex.ex.text, ex.ex.state, ex.ex.semanticVersion, ex.ex.collectionId,
      ex.ex.exerciseType, ex.tags.map(_.toString), ex.ex.hint, ex.samples.map(_.sample))

}
