package model.tools.web

import model.Consts

object WebConsts extends Consts {

  val actionName            : String = "action"
  val actionTypeName        : String = "actiontype"
  val attributeResultsName  : String = "attributeResults"
  val awaitedTagName        : String = "awaitedTag"
  val awaitedTextContentName: String = "awaitedTextContent"

  val conditionsName: String = "conditions"

  val descriptionName: String = "description"

  val elementFoundName: String = "elementFound"
  val elementSpecName : String = "elementSpec"

  val fileNameName: String = "fileName"
  val foundName   : String = "found"

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

  val preConditionsName : String = "preConditions"
  val postConditionsName: String = "postConditions"

  val siteSpecName: String = "siteSpec"

  val textContentName      : String = "textContent"
  val textContentResultName: String = "textContentResult"

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
