package model

import java.nio.file.Files
import java.nio.file.Path
import java.io.IOException
import play.Logger

import scala.collection.JavaConverters._

object CommonUtils {

  def createDirectory(directory: Path) = try {
    Files.createDirectories(directory);
    true;
  } catch {
    case e: IOException =>
      Logger.error("Error while creating sample file directory \"" + directory.toString() + "\"", e);
      false;
  }

  def readFile(path: Path) = try {
    Files.readAllLines(path).asScala.mkString("\n")
  } catch {
    case e: IOException =>
      Logger.error(s"Error while reading file $path", e);
      "";
  }

}