package model.tools.web

import model.Consts

object WebConsts extends Consts {

  val actionTypeName: String = "actiontype"

  val conditionsName: String = "conditions"

  val descriptionName: String = "description"

  val foundName: String = "found"

  val htmlResultsName: String = "htmlResults"
  val htmlSampleName          = "htmlSample"
  val htmlTasksName  : String = "htmlTasks"
  val htmlTextName   : String = "htmlText"

  val IS_PRECOND_NAME = "isPrecondition"

  val jsResultsName: String = "jsResults"
  val jsSampleName          = "jsSample"
  val jsTasksName  : String = "jsTasks"
  val jsTextName   : String = "jsText"

  val keysToSendName: String = "keysToSend"


  val textContentName: String = "textContent"

  val xpathQueryName: String = "xpathQuery"

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
