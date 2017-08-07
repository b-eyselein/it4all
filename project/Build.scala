import sbt._
import Keys._
import scalaz.Scalaz._
import com.typesafe.sbteclipse.core._
import com.typesafe.sbteclipse.core.EclipsePlugin._
import scala.xml.transform._

object ClasspathentryRewriteRule extends RewriteRule {
  import scala.xml._
  override def transform(parent: Node): Seq[Node] = {

    val ivyHome = System.getProperty("user.home") + "/.ivy2"

    parent match {
      case entry @ <classpathentry/> if (
        ((entry \ "@kind").toString() == "lib")
        && ((entry \ "@path").toString() startsWith ivyHome)
        && ((entry \ "@sourcepath").toString() startsWith ivyHome)) => {
        <classpathentry kind="var" path={ (entry \ "@path").toString.replaceAll(ivyHome, "IVY_HOME") }
        sourcepath={ (entry \ "@sourcepath").toString.replaceAll(ivyHome, "IVY_HOME") }/>
      }

      case entry @ <classpathentry>{ attributes }</classpathentry> if (
        ((entry \ "@kind").toString() == "lib")
        && ((entry \ "@path").toString() startsWith ivyHome)
        && ((entry \ "@sourcepath").toString() startsWith ivyHome)) => {
        <classpathentry kind="var" path={ (entry \ "@path").toString().replaceAll(ivyHome, "IVY_HOME") }
        sourcepath={ (entry \ "@sourcepath").toString.replaceAll(ivyHome, "IVY_HOME") }>@attributes</classpathentry>
      }

      case entry @ <classpathentry/> if (
        ((entry \ "@kind").toString() == "lib")
        && ((entry \ "@path").toString() startsWith ivyHome)) => {
        <classpathentry kind="var" path={ (entry \ "@path").toString().replaceAll(ivyHome, "IVY_HOME") }/>
      }

      case other => other
    }
  }
}

object ClasspathentryTransformer extends EclipseTransformerFactory[RewriteRule] {
  override def createTransformer(ref: ProjectRef, state: State): Validation[RewriteRule] = {
    ClasspathentryRewriteRule.success
  }
}