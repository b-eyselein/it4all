package model.tools.web

import better.files.File
import model.tools.web.sitespec.{HtmlElementSpec, JsAction, JsActionType, HtmlElementSpec, JsTask, SiteSpec}
import fi.iki.elonen.{NanoHTTPD, SimpleWebServer}
import org.scalatest.{BeforeAndAfterAll, Suites}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

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

  override def beforeAll: Unit = WebTesterSuite.simpleWebServer.start()

  override def afterAll: Unit = WebTesterSuite.simpleWebServer.stop()

}

class WebCorrectorSpec(port: Int) extends AnyFlatSpec with Matchers with WebCorrectorBasics {

  val baseUrl = s"http://localhost:$port"

  private val indexSiteSpec: SiteSpec = SiteSpec(
    "simpleTest/index.html",
    htmlTasks = Seq[HtmlElementSpec](
      HtmlElementSpec(1, "testText", "//h1", "h1"),
      HtmlElementSpec(2, "testText", "//p[@id='firstPar']", "p", awaitedTextContent = Some("Auf dieser Seite ist ein Par.")),
      HtmlElementSpec(
        3,
        "_",
        "//p[@id='secondPar']",
        "p",
        Some("Auf dieser Seite gibt es auch einen zweiten Par."),
        Map("class" -> "someClass")
      )
    )
  )

  private val carsListSiteSpec = {
    val carAttrs                        = Map("class" -> "list-group-item")
    val carLiSelector: String => String = name => s"/html/body//ul/li[contains(text(), '$name')]"

    SiteSpec(
      "carList/carList.html",
      htmlTasks = Seq[HtmlElementSpec](
        HtmlElementSpec(
          1,
          "_",
          "/html/head/link",
          "link",
          None,
          Map("href" -> "/assets/lib/bootstrap/dist/css/bootstrap.css", "rel" -> "stylesheet")
        ),
        HtmlElementSpec(2, "_", "/html/body//h1", "h1", Some("Autohersteller")),
        HtmlElementSpec(3, "_", "/html/body//ul", "ul", None, Map("class" -> "list-group")),
        HtmlElementSpec(4, "_", carLiSelector("Audi"), "li", Some("Audi"), carAttrs),
        HtmlElementSpec(5, "_", carLiSelector("BMW"), "li", Some("BMW"), carAttrs),
        HtmlElementSpec(6, "_", carLiSelector("Mercedes-Benz"), "li", Some("Mercedes-Benz"), carAttrs),
        HtmlElementSpec(7, "_", carLiSelector("Volkswagen"), "li", Some("Volkswagen"))
      )
    )
  }

  private val clickCounterSiteSpec = SiteSpec(
    "clickCounter/clickCounter.html",
    htmlTasks = Seq[HtmlElementSpec](
      HtmlElementSpec(1, "_", "/html/body//button", "button", Some("Klick mich!"), Map("onclick" -> "increment()")),
      HtmlElementSpec(2, "_", "/html/body//span[@id='theSpan']", "span", Some("0")),
      HtmlElementSpec(3, "_", "/html/head//script", "script", None, Map("src" -> "clickCounter.js"))
    ),
    jsTasks = Seq[JsTask](
      JsTask(
        1,
        "_",
        preConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("0"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("1")))
      ),
      JsTask(
        2,
        "_",
        preConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("1"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("2")))
      ),
      JsTask(
        3,
        "_",
        preConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("2"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("3")))
      ),
      JsTask(
        4,
        "_",
        preConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("3"))),
        action = JsAction("/html/body//button", JsActionType.Click, keysToSend = None),
        postConditions = Seq(HtmlElementSpec(1, "/html/body//span[@id='theSpan']", "span", Some("4")))
      )
    )
  )

  "The WebCorrector" should "evaluate a simple site" in {
    WebCorrector.evaluateSiteSpec(baseUrl, indexSiteSpec) match {
      case Failure(exception) => fail("Error while testing: " + exception.getMessage, exception)
      case Success(siteSpecResult) =>
        println("Evaluating results...")
        siteSpecResult.htmlResults.map(evaluateHtmlElementSpecResult)
        siteSpecResult.jsResults.map(evaluateJsTaskResult)
    }
  }

  it should "evaluate a site with a list" in {
    WebCorrector.evaluateSiteSpec(baseUrl, carsListSiteSpec) match {
      case Failure(exception) => fail("Error while testing: " + exception.getMessage, exception)
      case Success(siteSpecResult) =>
        println("Evaluating results...")
        siteSpecResult.htmlResults.map(evaluateHtmlElementSpecResult)
        siteSpecResult.jsResults.map(evaluateJsTaskResult)
    }
  }

  it should "evaluate a site with js" in {
    WebCorrector.evaluateSiteSpec(baseUrl, clickCounterSiteSpec) match {
      case Failure(exception) => fail("Error while testing: " + exception.getMessage, exception)
      case Success(siteSpecResult) =>
        println("Evaluating results...")
        siteSpecResult.htmlResults.map(evaluateHtmlElementSpecResult)
        siteSpecResult.jsResults.map(evaluateJsTaskResult)
    }
  }

}
