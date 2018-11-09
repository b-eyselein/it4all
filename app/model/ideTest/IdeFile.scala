package model.ideTest

import better.files._
import model.User

final case class IdeWorkspace(ideFiles: Seq[IdeFile], numFiles: Int)

final case class IdeFile(name: String, content: String, codeMirrorFileType: String) {

  def htmlId: String = name.replace(".", "_")

}

object IdeFilesTest {

  def saveWorkspace(user: User, ideWorkspace: IdeWorkspace): Unit = {

    val path = File.currentWorkingDirectory / "data" / "ideTest" / user.username
    println(path)


    ideWorkspace.ideFiles.foreach { ideFile =>
      val targetPath = path / ideFile.name

      println("TODO: save " + ideFile.name)
      println("TO: " + targetPath)
      println()

      targetPath
        .createIfNotExists(createParents = true)
        .overwrite(ideFile.content)

    }


    ???
  }

  val files: Map[String, IdeFile] = Map(
    "app.py" -> IdeFile("app.py",
      """from flask import Flask
        |
        |app = Flask(__name__)
        |
        |
        |@app.route("/")
        |def route_index():
        |    return "TODO!"
      """.stripMargin, "python"),

    "templates/base.html" -> IdeFile("templates/base.html",
      """<!doctype html>
        |<html>
        |<head>
        |    <title>{% block title %}{% endblock title %}</title>
        |</head>
        |<body>
        |    {% block content %}
        |    {% endblock content %}
        |</body>
        |</html>
      """.stripMargin, "jinja2"),

    "templates/index.html" -> IdeFile("templates/index.html",
      """{% extends "base.html" %}
        |
        |{% block content %}
        |    <p>Todo...</p>
        |{% endblock content %}
      """.stripMargin, "jinja2")
  )

}
