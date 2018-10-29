package model.ideTest

import play.api.libs.json.{JsValue, Json}

final case class IdeFile(name: String, content: String, codeMirrorFileType: String) {

  def htmlId: String = name.replace(".", "_")

  def toJson: JsValue = Json.obj(
    "name" -> name,
    "content" -> content,
    "filetype" -> codeMirrorFileType
  )
}

object IdeFilesTest {

  val files = Seq(
    IdeFile("app.py",
      """from flask import Flask
        |
        |app = Flask(__name__)
        |
        |
        |@app.route("/")
        |def route_index():
        |    return "TODO!"
      """.stripMargin, "python"),

    IdeFile("base.html",
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

    IdeFile("index.html",
      """{% extends "base.html" %}
        |
        |{% block content %}
        |    <p>Todo...</p>
        |{% endblock content %}
      """.stripMargin, "jinja2")
  )

}
