package model.sql

import model.ExerciseState
import model.core.ExerciseFormMappings
import model.sql.SqlConsts._
import model.sql.SqlEnums.SqlExerciseType
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}

object SqlFormMappings extends ExerciseFormMappings {

  private implicit object SqlExerciseTypeFormatter extends Formatter[SqlExerciseType] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], SqlExerciseType] = data.get(key) match {
      case None        => Left(Seq(FormError(key, s"There is no key $key in this form!")))
      case Some(value) => SqlExerciseType.byString(value) match {
        case None                  => Left(Seq(FormError(key, s"The value $value is no legal value for an SqlExerciseType")))
        case Some(sqlExerciseType) => Right(sqlExerciseType)
      }
    }

    override def unbind(key: String, value: SqlExerciseType): Map[String, String] = Map(key -> value.name)

  }

  def sqlCompleteExForm(collId: Int): Form[SqlCompleteEx] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      stateName -> of[ExerciseState],
      collIdName -> number,
      exerciseTypeName -> of[SqlExerciseType],
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
