package model.mindmap

import java.nio.file.{Path, Paths}

import model.Consts

object MindmapConsts extends Consts {

  val BASE_PATH: Path = Paths.get("conf", "resources", "mindmap")

  val SOLUTION_PATH: Path = Paths.get(BASE_PATH.toString, "solution.xml")

  val INPUT_PATH: Path = Paths.get(BASE_PATH.toString, "input.xml")

  val META_PATH: Path = Paths.get(BASE_PATH.toString, "meta.xls")

  val RESULT_PATH: Path = Paths.get(BASE_PATH.toString, "result.xls")

  val TEMPLATE_PATH: Path = Paths.get(BASE_PATH.toString, "template.mmas")

  val ALT_INPUT_PATH: Path = Paths.get(BASE_PATH.toString, "als_input.xml")

  val ALT_SOLUTION_PATH: Path = Paths.get(BASE_PATH.toString, "alt_solution.xml")

}
