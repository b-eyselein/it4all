package model.nary

import model.nary.NumberBase._
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class NumberBaseTest {

  @Test
  def testGetBase(): Unit = {
    assertThat(BINARY.base, equalTo(2))
    assertThat(OCTAL.base, equalTo(8))
    assertThat(DECIMAL.base, equalTo(10))
    assertThat(HEXADECIMAL.base, equalTo(16))
  }

  @Test
  def testGetHtmlPattern(): Unit = {
    assertThat(BINARY.htmlPattern, equalTo("[\\s0-1]+"))
    assertThat(OCTAL.htmlPattern, equalTo("[\\s0-7]+"))
    assertThat(DECIMAL.htmlPattern, equalTo("[\\s0-9]+"))
    assertThat(HEXADECIMAL.htmlPattern, equalTo("[\\s0-9a-fA-F]+"))
  }

  @Test
  def testGetPluralName(): Unit = {
    assertThat(BINARY.pluralName, equalTo("Binärzahlen"))
    assertThat(OCTAL.pluralName, equalTo("Oktalzahlen"))
    assertThat(DECIMAL.pluralName, equalTo("Dezimalzahlen"))
    assertThat(HEXADECIMAL.pluralName, equalTo("Hexadezimalzahlen"))
  }

  @Test
  def testGetRegex(): Unit = {
    assertThat(BINARY.regex, equalTo("-?0b[0-1][0-1]*"))
    assertThat(OCTAL.regex, equalTo("-?0o[1-7][0-7]*"))
    assertThat(DECIMAL.regex, equalTo("-?[1-9][0-9]*"))
    assertThat(HEXADECIMAL.regex, equalTo("-?0x[1-9a-fA-F][0-9a-fA-F]*"))
  }

  @Test
  def testGetSingularName(): Unit = {
    assertThat(BINARY.singularName, equalTo("Binärzahl"))
    assertThat(OCTAL.singularName, equalTo("Oktalzahl"))
    assertThat(DECIMAL.singularName, equalTo("Dezimalzahl"))
    assertThat(HEXADECIMAL.singularName, equalTo("Hexadezimalzahl"))
  }

  @Test
  def testGetSystemName(): Unit = {
    assertThat(BINARY.systemName, equalTo("Binärsystem"))
    assertThat(OCTAL.systemName, equalTo("Oktalsystem"))
    assertThat(DECIMAL.systemName, equalTo("Dezimalsystem"))
    assertThat(HEXADECIMAL.systemName, equalTo("Hexadezimalsystem"))
  }

  @Test
  def testValueOf(): Unit = {
    assert(NumberBase.withNameInsensitive("BINARY") == BINARY)
    assert(NumberBase.withNameInsensitive("OCTAL") == OCTAL)
    assert(NumberBase.withNameInsensitive("DECIMAL") == DECIMAL)
    assert(NumberBase.withNameInsensitive("HEXADECIMAL") == HEXADECIMAL)
  }

  @Test(expected = classOf[NoSuchElementException])
  def testValueOfWrongValue(): Unit = {
    NumberBase.withNameInsensitive("TERNARY")
  }
}