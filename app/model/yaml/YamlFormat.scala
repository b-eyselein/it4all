package model.yaml

import net.jcazevedo.moultingyaml.YamlValue

import scala.util.Try


trait MyYamlReader[A] {

  def read(yaml: YamlValue): Try[A]

}


trait MyYamlWriter[A] {

  def write(obj: A): YamlValue

}

trait MyYamlFormat[A] extends MyYamlReader[A] with MyYamlWriter[A]
