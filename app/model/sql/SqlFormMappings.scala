package model.sql

import model.sql.SqlConsts._
import model.{ExerciseState, SemanticVersionHelper}
import play.api.data.Form
import play.api.data.Forms._

object SqlFormMappings {

  def sqlExerciseForm(collId: Int): Form[SqlExercise] = Form(
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
    )(createSqlEx)(unapplySqlEx)
  )

  private def createSqlEx(exerciseId: Int, exSemVer: String, title: String, author: String, text: String, state: ExerciseState,
                                        collId: Int, collSemVer: String, exerciseType: SqlExerciseType,
                                        tags: Seq[String], hint: Option[String], sampleStrings: Seq[String]): SqlExercise = {
    println(sampleStrings)

    val exerciseSemanticVersion = SemanticVersionHelper.parseFromString(exSemVer) getOrElse SemanticVersionHelper.DEFAULT
    val collectionSemanticVersion = SemanticVersionHelper.parseFromString(collSemVer) getOrElse SemanticVersionHelper.DEFAULT

    val samples: Seq[SqlSample] = sampleStrings.filter(_.nonEmpty).zipWithIndex map { case (sample, index) =>
      SqlSample(index, exerciseId, exerciseSemanticVersion, collId, collectionSemanticVersion, sample)
    }

    SqlExercise(exerciseId, exerciseSemanticVersion, title, author, text, state, collId, collectionSemanticVersion, exerciseType, tags.map(SqlExTag.withNameInsensitive), hint, samples)
  }

  private def unapplySqlEx(ex: SqlExercise): Option[(Int, String, String, String, String, ExerciseState, Int, String, SqlExerciseType, Seq[String], Option[String], Seq[String])] =
    Some((ex.id, ex.semanticVersion.asString, ex.title, ex.author, ex.text, ex.state, ex.collectionId,
      ex.collSemVer.asString, ex.exerciseType, ex.tags.map(_.toString), ex.hint, ex.samples.map(_.sample)))

}
