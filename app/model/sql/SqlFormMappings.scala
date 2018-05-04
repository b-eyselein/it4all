package model.sql

import model.ExerciseState
import model.sql.SqlConsts._
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
      collIdName -> number,
      exerciseTypeName -> SqlExerciseType.formField,
      tagsName -> seq(text),
      hintName -> optional(text),
      samplesName -> seq(text)
    )(createCompleteSqlExercise)(unapplySqlCompleteEx)
  )

  private def createCompleteSqlExercise(exerciseId: Int, title: String, author: String, text: String, state: ExerciseState,
                                        collId: Int, exerciseType: SqlExerciseType, tags: Seq[String], hint: Option[String],
                                        sampleStrings: Seq[String]): SqlCompleteEx = {
    println(sampleStrings)

    val samples: Seq[SqlSample] = sampleStrings.filter(_.nonEmpty).zipWithIndex map { case (sample, index) =>
      SqlSample(index, exerciseId, collId, sample)
    }

    SqlCompleteEx(SqlExercise(exerciseId, title, author, text, state, collId, exerciseType, tags.mkString("#"), hint), samples)
  }

  private def unapplySqlCompleteEx(ex: SqlCompleteEx): Option[(Int, String, String, String, ExerciseState, Int, SqlExerciseType, Seq[String], Option[String], Seq[String])] =
    Some(ex.id, ex.title, ex.author, ex.text, ex.state, ex.ex.collectionId, ex.ex.exerciseType, ex.tags.map(_.toString), ex.ex.hint, ex.samples.map(_.sample))

}
