package model.web

import model.Consts

object WebConsts extends Consts {

  val actionTypeName = "actiontype"

  val conditionsName = "conditions"

  val descriptionName = "description"

  val foundName = "found"

  val htmlResultsName = "htmlResults"
  val htmlSampleName  = "htmlSample"
  val htmlTasksName   = "htmlTasks"
  val htmlTextName    = "htmlText"

  val IS_PRECOND_NAME = "isPrecondition"

  val jsResultsName = "jsResults"
  val jsSampleName  = "jsSample"
  val jsTasksName   = "jsTasks"
  val jsTextName    = "jsText"

  val keysToSendName = "keysToSend"

  val partName      = "part"
  val phpSampleName = "phpSample"
  val phpTasksName  = "phpTasks"
  val phpTextName   = "phpText"

  val textContentName = "textContent"

  val xpathQueryName = "xpathQuery"

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