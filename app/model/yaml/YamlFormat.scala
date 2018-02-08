package model.yaml

import net.jcazevedo.moultingyaml.{YamlReader, YamlValue, YamlWriter}

import scala.annotation.implicitNotFound
import scala.language.implicitConversions
import scala.util.Try


/**
  * Provides the YAML deserialization for type A.
  */
@implicitNotFound(msg =
  "Cannot find YamlReader or YamlFormat type class for ${A}")
trait MyYamlReader[A] {
  def read(yaml: YamlValue): Try[A]
}

object MyYamlReader {
  implicit def func2Reader[A](f: YamlValue => A): YamlReader[A] = (yaml: YamlValue) => f(yaml)
}

/**
  * Provides the YAML serialization for type A.
  */
@implicitNotFound(msg =
  "Cannot find YamlWriter or YamlFormat type class for ${A}")
trait MyYamlWriter[A] {
  def write(obj: A): YamlValue
}

object MyYamlWriter {
  implicit def func2Writer[A](f: A => YamlValue): YamlWriter[A] = (obj: A) => f(obj)
}

/**
  * Provides the YAML deserialization and serialization for type A.
  */
trait MyYamlFormat[A] extends MyYamlReader[A] with MyYamlWriter[A] with YamlReader[Try[A]] with YamlWriter[A]
