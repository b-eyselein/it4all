package model.web

import model.Consts

object WebConsts extends Consts {

  val ACTION_TYPE_NAME   = "actiontype"
  val AWAITED_VALUE_NAME = "awaitedValue"

  val BASE_URL = "http://localhost:9000"

  val CONDITIONS_NAME = "conditions"

  val HTML_TYPE       = "html"
  val HTML_TASKS_NAME = "htmlTasks"
  val HTML_TEXT_NAME  = "htmlText"

  val IS_PRECOND_NAME = "isPrecondition"

  val JS_TYPE       = "js"
  val JS_TASKS_NAME = "jsTasks"
  val JS_TEXT_NAME  = "jsText"

  val KEYS_TO_SEND_NAME = "keysToSend"

  val TEXT_CONTENT_NAME = "textContent"

  val XPATH_NAME = "xpathQuery"

  val STANDARD_HTML: String =
    """<!doctype html>
      |<html>
      |<head>
      |  <!-- Hier: CSS und Javascript -->
      |</head>
      |<body>
      |  <!-- Html-Elemente -->
      |</body>
      |</html>""".stripMargin

  val STANDARD_HTML_PLAYGROUND: String =
    """<!doctype html>
      |<html>
      |<head>
      |  <style>
      |    /* Css-Anweisungen */
      |  </style>
      |  <script type="text/javascript">
      |    // Javascript-Code
      |  </script>
      |</head>
      |<body>
      |  <!-- Html-Elemente -->
      |</body>
      |</html>""".stripMargin

}