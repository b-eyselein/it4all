package model.sql

import model.sql.SqlConsts._
import model.{ExerciseState, SemanticVersionHelper}
import play.api.data.Form
import play.api.data.Forms._

object SqlFormMappings {

  def sqlCompleteExForm(collId: Int): Form[SqlCompleteEx] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> nonEmptyText,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      collIdName -> number,
      collSemVerName -> nonEmptyText,
      exerciseTypeName -> SqlExerciseType.formField,
      tagsName -> seq(text),
      hintName -> optional(text),
      samplesName -> seq(text)
    )(createCompleteSqlExercise)(unapplySqlCompleteEx)
  )

  private def createCompleteSqlExercise(exerciseId: Int, exSemVer: String, title: String, author: String, text: String, state: ExerciseState,
                                        collId: Int, collSemVer: String, exerciseType: SqlExerciseType,
                                        tags: Seq[String], hint: Option[String], sampleStrings: Seq[String]): SqlCompleteEx = {
    println(sampleStrings)

    val exerciseSemanticVersion = SemanticVersionHelper.parseFromString(exSemVer) getOrElse SemanticVersionHelper.DEFAULT
    val collectionSemanticVersion = SemanticVersionHelper.parseFromString(collSemVer) getOrElse SemanticVersionHelper.DEFAULT

    val samples: Seq[SqlSample] = sampleStrings.filter(_.nonEmpty).zipWithIndex map { case (sample, index) =>
      SqlSample(index, exerciseId, exerciseSemanticVersion, collId, collectionSemanticVersion, sample)
    }

    SqlCompleteEx(SqlExercise(exerciseId, exerciseSemanticVersion, title, author, text, state, collId, collectionSemanticVersion, exerciseType, tags.mkString("#"), hint), samples)
  }

  private def unapplySqlCompleteEx(ex: SqlCompleteEx): Option[(Int, String, String, String, String, ExerciseState, Int, String, SqlExerciseType, Seq[String], Option[String], Seq[String])] =
    Some((ex.ex.id, ex.ex.semanticVersion.asString, ex.ex.title, ex.ex.author, ex.ex.text, ex.ex.state, ex.ex.collectionId,
      ex.ex.collSemVer.asString, ex.ex.exerciseType, ex.tags.map(_.toString), ex.ex.hint, ex.samples.map(_.sample)))

}
