package model.programming

import model.MyYamlProtocol._
import model.core.FileUtils
import model.programming.ProgDataTypes.ProgDataType
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._

import scala.language.postfixOps
import scala.util.Try

case class ClassTest(className: String, description: String, constructorArguments: Seq[MemberDataType],
                     constructorTests: Seq[ConstructorTest], functionTests: Seq[FunctionTest])

case class MemberDataType(memberName: String, memberDataType: ProgDataType)

case class MemberValue(memberName: String, memberValue: Any)

case class ConstructorTest(args: Seq[MemberValue], result: Seq[MemberValue])

case class FunctionTest(functionName: String, description: String, arguments: Seq[MemberDataType], returnType: ProgDataType)

object NewProgYamlProtocol extends MyYamlProtocol with FileUtils {

  def testRead: Try[(String, ClassTest)] = readAll(ProgToolObject.exerciseResourcesFolder / "testdata.yaml") flatMap { fileContent =>
    ClassTestYamlFormat.read(fileContent.parseYaml) map (r => (fileContent, r))
  }

  implicit object ClassTestYamlFormat extends MyYamlObjectFormat[ClassTest] {

    override def readObject(yamlObject: YamlObject): Try[ClassTest] = for {
      className <- yamlObject.stringField("classname")
      description <- yamlObject.stringField("description")
      constructorArguments <- yamlObject.arrayField("init_args", MemberDataTypeYamlFormat.read)
      constructorTests <- yamlObject.arrayField("constructortests", ConstructorTestYamlFormat.read)
      functionTests <- yamlObject.arrayField("functiontests", FunctionTestYamlFormat.read)
    } yield ClassTest(className, description, constructorArguments._1, constructorTests._1, functionTests._1)

    override def write(obj: ClassTest): YamlValue = YamlObj(
      "classname" -> obj.className,
      "description" -> obj.description,
      "init_args" -> YamlArr(obj.constructorArguments map MemberDataTypeYamlFormat.write),
      "constructortests" -> YamlArr(obj.constructorTests map ConstructorTestYamlFormat.write),
      "functiontests" -> YamlArr(obj.functionTests map FunctionTestYamlFormat.write)
    )
  }

  object MemberDataTypeYamlFormat extends MyYamlObjectFormat[MemberDataType] {

    override def readObject(yamlObject: YamlObject): Try[MemberDataType] = for {
      argName <- yamlObject.stringField("name")
      argType <- yamlObject.enumField("dataType", str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield MemberDataType(argName, argType)

    override def write(t: MemberDataType): YamlValue = YamlObj(
      "name" -> t.memberName,
      "dataType" -> t.memberDataType.typeName
    )
  }

  object ConstructorTestYamlFormat extends MyYamlObjectFormat[ConstructorTest] {

    override def readObject(yamlObject: YamlObject): Try[ConstructorTest] = {
      for {

        // FIXME: implement better with case class...
        args <- yamlObject.someField("args").map(_.asYamlObject) map {
          _.fields map { case (yamlKey, yamlValue) => MemberValue(yamlKey.forgivingStr, yamlValue.forgivingStr) }
        }

        result <- yamlObject.someField("result").map(_.asYamlObject) map {
          _.fields map { case (yamlKey, yamlValue) => MemberValue(yamlKey.forgivingStr, yamlValue.forgivingStr) }
        }
      } yield ConstructorTest(args toSeq, result toSeq)
    }

    override def write(obj: ConstructorTest): YamlValue = YamlObj(
      "args" -> YamlObject(obj.args map (arg => arg.memberName.toYaml -> arg.memberValue.toString.toYaml) toMap),
      "result" -> YamlObject(obj.result map (res => res.memberName.toYaml -> res.memberValue.toString.toYaml) toMap)
    )


  }

  object FunctionTestYamlFormat extends MyYamlObjectFormat[FunctionTest] {

    override def readObject(yamlObject: YamlObject): Try[FunctionTest] = for {
      functionName <- yamlObject.stringField("functionname")
      description <- yamlObject.stringField("description")

      arguments <- yamlObject.arrayField("arguments", MemberDataTypeYamlFormat.read)
      returnType <- yamlObject.enumField("returntype", ProgDataTypes.byName(_) getOrElse ProgDataTypes.STRING)
    } yield FunctionTest(functionName, description, arguments._1, returnType)

    override def write(obj: FunctionTest): YamlValue = YamlObj(
      "functionname" -> obj.functionName,
      "description" -> obj.description,
      "arguments" -> YamlArr(obj.arguments map MemberDataTypeYamlFormat.write),
      "returntype" -> obj.returnType.typeName
    )

  }

}
