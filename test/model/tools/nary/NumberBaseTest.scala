package model.tools.nary

import model.tools.nary.NumberBase._
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class NumberBaseTest {

  @Test
  def testGetBase(): Unit = {
    assertThat(Binary.base, equalTo(2))
    assertThat(Octal.base, equalTo(8))
    assertThat(Decimal.base, equalTo(10))
    assertThat(HexaDecimal.base, equalTo(16))
  }

  @Test
  def testGetHtmlPattern(): Unit = {
    assertThat(Binary.htmlPattern, equalTo("[\\s0-1]+"))
    assertThat(Octal.htmlPattern, equalTo("[\\s0-7]+"))
    assertThat(Decimal.htmlPattern, equalTo("[\\s0-9]+"))
    assertThat(HexaDecimal.htmlPattern, equalTo("[\\s0-9a-fA-F]+"))
  }

  @Test
  def testGetPluralName(): Unit = {
    assertThat(Binary.pluralName, equalTo("Binärzahlen"))
    assertThat(Octal.pluralName, equalTo("Oktalzahlen"))
    assertThat(Decimal.pluralName, equalTo("Dezimalzahlen"))
    assertThat(HexaDecimal.pluralName, equalTo("Hexadezimalzahlen"))
  }

  @Test
  def testGetRegex(): Unit = {
    assertThat(Binary.regex, equalTo("-?0b[0-1][0-1]*"))
    assertThat(Octal.regex, equalTo("-?0o[1-7][0-7]*"))
    assertThat(Decimal.regex, equalTo("-?[1-9][0-9]*"))
    assertThat(HexaDecimal.regex, equalTo("-?0x[1-9a-fA-F][0-9a-fA-F]*"))
  }

  @Test
  def testGetSingularName(): Unit = {
    assertThat(Binary.singularName, equalTo("Binärzahl"))
    assertThat(Octal.singularName, equalTo("Oktalzahl"))
    assertThat(Decimal.singularName, equalTo("Dezimalzahl"))
    assertThat(HexaDecimal.singularName, equalTo("Hexadezimalzahl"))
  }

  @Test
  def testGetSystemName(): Unit = {
    assertThat(Binary.systemName, equalTo("Binärsystem"))
    assertThat(Octal.systemName, equalTo("Oktalsystem"))
    assertThat(Decimal.systemName, equalTo("Dezimalsystem"))
    assertThat(HexaDecimal.systemName, equalTo("Hexadezimalsystem"))
  }

  @Test
  def testValueOf(): Unit = {
    assert(NumberBase.withNameInsensitive("BINARY") == Binary)
    assert(NumberBase.withNameInsensitive("OCTAL") == Octal)
    assert(NumberBase.withNameInsensitive("DECIMAL") == Decimal)
    assert(NumberBase.withNameInsensitive("HEXADECIMAL") == HexaDecimal)
  }

  @Test(expected = classOf[NoSuchElementException])
  def testValueOfWrongValue(): Unit = {
    NumberBase.withNameInsensitive("TERNARY")
  }
}
