package model.nary

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat

import org.junit.Test

class NumberBaseTest {

  @Test
  def testGetBase() {
    assertThat(BINARY.base, equalTo(2))
    assertThat(OCTAL.base, equalTo(8))
    assertThat(DECIMAL.base, equalTo(10))
    assertThat(HEXADECIMAL.base, equalTo(16))
  }

  @Test
  def testGetHtmlPattern() {
    assertThat(BINARY.htmlPattern, equalTo("[\\s0-1][\\s0-1]*"))
    assertThat(OCTAL.htmlPattern, equalTo("[\\s1-7][\\s0-7]*"))
    assertThat(DECIMAL.htmlPattern, equalTo("[\\s1-9][\\s0-9]*"))
    assertThat(HEXADECIMAL.htmlPattern, equalTo("[\\s1-9a-fA-F][\\s0-9a-fA-F]*"))
  }

  @Test
  def testGetPluralName() {
    assertThat(BINARY.pluralName, equalTo("Binaerzahlen"))
    assertThat(OCTAL.pluralName, equalTo("Oktalzahlen"))
    assertThat(DECIMAL.pluralName, equalTo("Dezimalzahlen"))
    assertThat(HEXADECIMAL.pluralName, equalTo("Hexadezimalzahlen"))
  }

  @Test
  def testGetRegex() {
    assertThat(BINARY.regex, equalTo("-?0b[0-1][0-1]*"))
    assertThat(OCTAL.regex, equalTo("-?0o[1-7][0-7]*"))
    assertThat(DECIMAL.regex, equalTo("-?[1-9][0-9]*"))
    assertThat(HEXADECIMAL.regex, equalTo("-?0x[1-9a-fA-F][0-9a-fA-F]*"))
  }

  @Test
  def testGetSingularName() {
    assertThat(BINARY.singularName, equalTo("Binaerzahl"))
    assertThat(OCTAL.singularName, equalTo("Oktalzahl"))
    assertThat(DECIMAL.singularName, equalTo("Dezimalzahl"))
    assertThat(HEXADECIMAL.singularName, equalTo("Hexadezimalzahl"))
  }

  @Test
  def testGetSystemName() {
    assertThat(BINARY.systemName, equalTo("Binaersystem"))
    assertThat(OCTAL.systemName, equalTo("Oktalsystem"))
    assertThat(DECIMAL.systemName, equalTo("Dezimalsystem"))
    assertThat(HEXADECIMAL.systemName, equalTo("Hexadezimalsystem"))
  }

  @Test
  def testValueOf() {
    assert(NumberBase.valueOf("BINARY") == BINARY)
    assert(NumberBase.valueOf("OCTAL") == OCTAL)
    assert(NumberBase.valueOf("DECIMAL") == DECIMAL)
    assert(NumberBase.valueOf("HEXADECIMAL") == HEXADECIMAL)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testValueOfWrongValue() {
    NumberBase.valueOf("TERNARY")
  }
}