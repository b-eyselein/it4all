package model

import model.MyYamlProtocol._
import model.core.CommonUtils
import model.core.CoreConsts._
import model.yaml.MyYamlFormat
import net.jcazevedo.moultingyaml.{YamlNull, _}
import play.api.libs.json._

import scala.language.{implicitConversions, postfixOps}
import scala.util.{Failure, Success, Try}

case class WrongFieldTypeException(fieldtype: String) extends Exception


object YamlObj {

  // FIXME: remove cast ...
  def apply(fields: (String, YamlValue)*): YamlObject = YamlObject(fields map (f => (YamlString(f._1).asInstanceOf[YamlValue], f._2)) toMap)

}

object YamlArr {

  def apply(objects: Seq[YamlValue]): YamlArray = YamlArray(objects toVector)

}

object MyYamlProtocol {

  implicit def string2YamlString(str: String): YamlString = YamlString(str)

  implicit def int2YamlNumber(num: Int): YamlNumber = YamlNumber(num)

  implicit def bool2YamlBoolean(bool: Boolean): YamlBoolean = YamlBoolean(bool)

  implicit class PimpedYamlValue(yaml: YamlValue) {

    def asBool: Try[Boolean] = yaml match {
      case YamlBoolean(bool) => Success(bool)
      case other             => Failure(WrongFieldTypeException(other.getClass.toString))
    }

    def asInt: Try[Int] = yaml match {
      case YamlNumber(bigDec) => Success(bigDec.intValue)
      case other              => Failure(WrongFieldTypeException(other.getClass.toString))
    }

    def asStringEnum[T](func: String => T): Try[T] = yaml match {
      case YamlString(str) => Try(func(str))
      case other           => Failure(WrongFieldTypeException(other.getClass.toString))
    }

    def asStr: Try[String] = yaml match {
      case YamlString(str) => Success(str)
      case other           => Failure(WrongFieldTypeException(other.getClass.toString))
    }

    def forgivingStr: String = yaml match {
      case YamlString(str)   => str
      case YamlNumber(num)   => num.toString
      case YamlBoolean(bool) => bool.toString
      case YamlNull          => "null"
      case other             => other.toString
    }

    def asArray[T](mapping: YamlValue => Try[T]): Try[(Seq[T], Seq[Failure[T]])] = yaml match {
      case YamlArray(vector) => Success(CommonUtils.splitTries(vector map mapping))
      case other             => Failure(WrongFieldTypeException(other.getClass.toString))
    }

  }

  implicit class PimpedYamlObject(yamlObject: YamlObject) {

    def someField(fieldName: String): Try[YamlValue] = yamlObject.fields get fieldName match {
      case Some(field) => Try(field)
      case None        => Failure(new NoSuchFieldException(fieldName))
    }

    def optField[T](fieldName: String, f: YamlValue => Try[T]): Try[Option[T]] = yamlObject.fields get fieldName match {
      case None            => Success(None)
      case Some(yamlValue) => f(yamlValue) map Some.apply
    }

    def boolField(fieldName: String): Try[Boolean] = someField(fieldName) flatMap (_.asBool)

    def optBoolField(fieldName: String): Try[Option[Boolean]] = yamlObject.fields get fieldName match {
      case None        => Success(None)
      case Some(field) => field.asBool map Some.apply
    }

    def intField(fieldName: String): Try[Int] = someField(fieldName) flatMap (_.asInt)

    def stringField(fieldName: String): Try[String] = someField(fieldName) flatMap (_.asStr)

    def forgivingStringField(fieldName: String): Try[String] = someField(fieldName) map (_.forgivingStr)

    def optStringField(fieldName: String): Try[Option[String]] = someField(fieldName) match {
      case Failure(_)     => Success(None)
      case Success(field) => field.asStr map Some.apply
    }

    def objField[A](fieldName: String, f: YamlObject => A): Try[A] = someField(fieldName) map (_.asYamlObject) map f

    def arrayField[T](fieldName: String, mapping: YamlValue => Try[T]): Try[(Seq[T], Seq[Failure[T]])] = someField(fieldName) flatMap (_.asArray(mapping))

    def optArrayField[T](fieldName: String, mapping: YamlValue => Try[T]): Try[(Seq[T], Seq[Failure[T]])] = yamlObject.fields get fieldName match {
      case None        => Success((Seq.empty, Seq.empty))
      case Some(field) => field.asArray(mapping)
    }

    def enumField[T](fieldName: String, valueOf: String => T): Try[T] = stringField(fieldName) map valueOf

    def enumFieldOption[T](fieldName: String, valueOf: String => Option[T]): Try[Option[T]] = stringField(fieldName) map valueOf

    def jsonField(fieldName: String): Try[JsValue] = someField(fieldName) map mapToJson

    def optJsonField(fieldName: String): Try[Option[JsValue]] = optField(fieldName, yamlValue => Try(mapToJson(yamlValue)))

    def mapToJson(yamlValue: YamlValue): JsValue = yamlValue match {
      case YamlArray(arrayValues) => JsArray(arrayValues map mapToJson)
      case YamlSet(content)       => JsArray(content.toSeq map mapToJson)

      case YamlObject(yamlFields) => JsObject(yamlFields map {
        case (key, value) => key.forgivingStr -> mapToJson(value)
      })

      case YamlDate(date) => JsString(date.toLocalDate.toString)

      case YamlNaN | YamlNegativeInf | YamlPositiveInf => JsNull

      case YamlString(str)        => JsString(str)
      case YamlBoolean(bool)      => JsBoolean(bool)
      case YamlNull               => JsNull
      case YamlNumber(bigDecimal) => JsNumber(bigDecimal)

      // TODO: other yamlValues such as YamlPosInf, YamlNegInf, ...
      //      case other => other.toString
    }
  }

}

abstract class MyYamlProtocol extends DefaultYamlProtocol {

  protected def writeBaseValues(hasBaseValues: HasBaseValues): Map[YamlValue, YamlValue] = Map(
    YamlString(idName) -> hasBaseValues.id,
    YamlString(titleName) -> hasBaseValues.title,
    YamlString(authorName) -> hasBaseValues.author,
    YamlString(textName) -> hasBaseValues.text,
    YamlString(stateName) -> hasBaseValues.state.entryName,
    YamlString(semanticVersionName) -> hasBaseValues.semanticVersion.asString
  )

  protected def readBaseValues(yamlObject: YamlObject): Try[(Int, String, String, String, ExerciseState, SemanticVersion)] = for {
    id <- yamlObject.intField(idName)
    title <- yamlObject.stringField(titleName)
    author <- yamlObject.stringField(authorName)
    text <- yamlObject.stringField(textName)
    state: ExerciseState <- yamlObject.enumField(stateName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
    semanticVersion <- yamlObject.stringField(semanticVersionName) flatMap SemanticVersionHelper.tryFromString
  } yield (id, title, author, text, state, semanticVersion)

  abstract class MyYamlObjectFormat[T] extends MyYamlFormat[T] {

    override def read(yaml: YamlValue): Try[T] = yaml match {
      case yamlObj: YamlObject => readObject(yamlObj)
      case other               => deserializationError(s"Awaited an yaml object, instead got ${other.getClass}")
    }

    protected def readObject(yamlObject: YamlObject): Try[T]

  }

}