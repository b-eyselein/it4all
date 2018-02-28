package model

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.core.CommonUtils
import model.core.CoreConsts._
import model.yaml.MyYamlFormat
import net.jcazevedo.moultingyaml._

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

    private def optField[T](fieldName: String, f: YamlValue => T): Option[T] = yamlObject.fields get fieldName map f


    def boolField(fieldName: String): Try[Boolean] = someField(fieldName) flatMap (_.asBool)

    def intField(fieldName: String): Try[Int] = someField(fieldName) flatMap (_.asInt)

    def stringField(fieldName: String): Try[String] = someField(fieldName) flatMap (_.asStr)

    def forgivingStringField(fieldName: String): Try[String] = someField(fieldName) map (_.forgivingStr)

    def optStringField(fieldName: String): Try[Option[String]] = someField(fieldName) match {
      case Failure(_)     => Success(None)
      case Success(field) => field.asStr map Some.apply
    }

    def optForgivingStringField(fieldName: String): Try[Option[String]] = Try(optField(fieldName, _.forgivingStr))

    def arrayField[T](fieldName: String, mapping: YamlValue => Try[T]): Try[(Seq[T], Seq[Failure[T]])] = someField(fieldName) flatMap (_.asArray(mapping))

    def optArrayField[T](fieldName: String, mapping: YamlValue => Try[T]): Try[(Seq[T], Seq[Failure[T]])] = yamlObject.fields get fieldName match {
      case None        => Success((Seq.empty, Seq.empty))
      case Some(field) => field.asArray(mapping)
    }

    def enumField[T](fieldName: String, valueOf: String => T): Try[T] = stringField(fieldName) map valueOf

  }

}

abstract class MyYamlProtocol extends DefaultYamlProtocol {

  protected def writeBaseValues(hasBaseValues: HasBaseValues): Map[YamlValue, YamlValue] = Map(
    YamlString(ID_NAME) -> hasBaseValues.id,
    YamlString(TITLE_NAME) -> hasBaseValues.title,
    YamlString(AUTHOR_NAME) -> hasBaseValues.author,
    YamlString(TEXT_NAME) -> hasBaseValues.text,
    YamlString(STATE_NAME) -> hasBaseValues.state.name
  )

  protected def readBaseValues(yamlObject: YamlObject): Try[(Int, String, String, String, ExerciseState)] = for {
    id <- yamlObject.intField(ID_NAME)
    title <- yamlObject.stringField(TITLE_NAME)
    author <- yamlObject.stringField(AUTHOR_NAME)
    text <- yamlObject.stringField(TEXT_NAME)
    state = yamlObject.enumField(STATE_NAME, ExerciseState.valueOf) getOrElse ExerciseState.CREATED
  } yield (id, title, author, text, state)

  abstract class HasBaseValuesYamlFormat[E <: HasBaseValues] extends MyYamlObjectFormat[E] {

    override def readObject(yamlObject: YamlObject): Try[E] = readBaseValues(yamlObject) flatMap (readRest(yamlObject, _))

    protected def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[E]

    override def write(completeEx: E): YamlObject = YamlObject(writeBaseValues(completeEx) ++ writeRest(completeEx))

    protected def writeRest(completeEx: E): Map[YamlValue, YamlValue]

  }

  abstract class MyYamlObjectFormat[T] extends MyYamlFormat[T] {

    override def read(yaml: YamlValue): Try[T] = yaml match {
      case yamlObj: YamlObject => readObject(yamlObj)
      case other               => deserializationError(s"Awaited an yaml object, instead got ${other.getClass}")
    }

    protected def readObject(yamlObject: YamlObject): Try[T]

  }

}