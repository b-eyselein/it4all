package model.tools.web

import better.files.File
import fi.iki.elonen.{NanoHTTPD, SimpleWebServer}
import model.tools.web.sitespec._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, Suites}

import scala.util.{Failure, Success}

object WebTesterSuite {

  val basePath: File = File("src") / "test" / "resources" / "htdocs"

  val port = 6161

  val simpleWebServer: SimpleWebServer = new SimpleWebServer("localhost", port, basePath.toJava, false)

  def main(args: Array[String]): Unit = {
    simpleWebServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
  }

}

class WebTesterSuite extends Suites(new WebCorrectorSpec(WebTesterSuite.port)) with BeforeAndAfterAll {

  override def beforeAll(): Unit = WebTesterSuite.simpleWebServer.start()

  override def afterAll(): Unit = WebTesterSuite.simpleWebServer.stop()

}

class WebCorrectorSpec(port: Int) extends AnyFlatSpec with Matchers with WebCorrectorBasics {

  val baseUrl = s"http://localhost:$port"

  private val indexSiteSpec: SiteSpec = SiteSpec(
    "simpleTest/index.html",
    htmlTasks = Seq(
      HtmlTask(1, "testText", WebElementSpec("//h1", "h1")),
      HtmlTask(2, "testText", WebElementSpec("//p[@id='firstPar']", "p", awaitedTextContent = Some("Auf dieser Seite ist ein Par."))),
      HtmlTask(3, "_", WebElementSpec("//p[@id='secondPar']", "p", Some("Auf dieser Seite gibt es auch einen zweiten Par."), Map("class" -> "someClass")))
    )
  )

  private val carsListSiteSpec = {
    val carAttrs                        = Map("class" -> "list-group-item")
    val carLiSelector: String => String = name => s"/html/body//ul/li[contains(text(), '$name')]"

    SiteSpec(
      "carList/carList.html",
      htmlTasks = Seq(
        HtmlTask(1, "_", WebElementSpec("/html/head/link", "link", None, Map("href" -> "/assets/lib/bootstrap/dist/css/bootstrap.css", "rel" -> "stylesheet"))),
        HtmlTask(2, "_", WebElementSpec("/html/body//h1", "h1", Some("Autohersteller"))),
        HtmlTask(3, "_", WebElementSpec("/html/body//ul", "ul", None, Map("class" -> "list-group"))),
        HtmlTask(4, "_", WebElementSpec(carLiSelector("Audi"), "li", Some("Audi"), carAttrs)),
        HtmlTask(5, "_", WebElementSpec(carLiSelector("BMW"), "li", Some("BMW"), carAttrs)),
        HtmlTask(6, "_", WebElementSpec(carLiSelector("Mercedes-Benz"), "li", Some("Mercedes-Benz"), carAttrs)),
        HtmlTask(7, "_", WebElementSpec(carLiSelector("Volkswagen"), "li", Some("Volkswagen")))
      )
    )
  }

  private val clickCounterSiteSpec = SiteSpec(
    "clickCounter/clickCounter.html",
    htmlTasks = Seq(
      HtmlTask(1, "_", WebElementSpec("/html/body//button", "button", Some("Klick mich!"), Map("onclick" -> "increment()"))),
      HtmlTask(2, "_", WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("0"))),
      HtmlTask(3, "_", WebElementSpec("/html/head//script", "script", None, Map("src" -> "clickCounter.js")))
    ),
    jsTasks = Seq(
      JsTask(
        1,
        "_",
        preConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("0"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("1")))
      ),
      JsTask(
        2,
        "_",
        preConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("1"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("2")))
      ),
      JsTask(
        3,
        "_",
        preConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("2"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("3")))
      ),
      JsTask(
        4,
        "_",
        preConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("3"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(WebElementSpec("/html/body//span[@id='theSpan']", "span", Some("4")))
      )
    )
  )

  "The WebCorrector" should "evaluate a simple site" in {
    WebCorrector.evaluateSiteSpec(baseUrl, indexSiteSpec) match {
      case Failure(exception) => fail("Error while testing: " + exception.getMessage, exception)
      case Success(siteSpecResult) =>
        println("Evaluating results...")
        siteSpecResult.htmlResults.map(evaluateHtmlTaskResult)
        siteSpecResult.jsResults.map(evaluateJsTaskResult)
    }
  }

  it should "evaluate a site with a list" in {
    WebCorrector.evaluateSiteSpec(baseUrl, carsListSiteSpec) match {
      case Failure(exception) => fail("Error while testing: " + exception.getMessage, exception)
      case Success(siteSpecResult) =>
        println("Evaluating results...")
        siteSpecResult.htmlResults.map(evaluateHtmlTaskResult)
        siteSpecResult.jsResults.map(evaluateJsTaskResult)
    }
  }

  it should "evaluate a site with js" in {
    WebCorrector.evaluateSiteSpec(baseUrl, clickCounterSiteSpec) match {
      case Failure(exception) => fail("Error while testing: " + exception.getMessage, exception)
      case Success(siteSpecResult) =>
        println("Evaluating results...")
        siteSpecResult.htmlResults.map(evaluateHtmlTaskResult)
        siteSpecResult.jsResults.map(evaluateJsTaskResult)
    }
  }

}
