package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NumberBaseTest {

  @Test
  public void testGetBase() {
    assertThat(NumberBase.BINARY.getBase(), equalTo(2));
    assertThat(NumberBase.OCTAL.getBase(), equalTo(8));
    assertThat(NumberBase.DECIMAL.getBase(), equalTo(10));
    assertThat(NumberBase.HEXADECIMAL.getBase(), equalTo(16));
  }

  @Test
  public void testGetHtmlPattern() {
    assertThat(NumberBase.BINARY.getHtmlPattern(), equalTo("[\\s0-1][\\s0-1]*"));
    assertThat(NumberBase.OCTAL.getHtmlPattern(), equalTo("[\\s1-7][\\s0-7]*"));
    assertThat(NumberBase.DECIMAL.getHtmlPattern(), equalTo("[\\s1-9][\\s0-9]*"));
    assertThat(NumberBase.HEXADECIMAL.getHtmlPattern(), equalTo("[\\s1-9a-fA-F][\\s0-9a-fA-F]*"));
  }

  @Test
  public void testGetPluralName() {
    assertThat(NumberBase.BINARY.getPluralName(), equalTo("Binärzahlen"));
    assertThat(NumberBase.OCTAL.getPluralName(), equalTo("Oktalzahlen"));
    assertThat(NumberBase.DECIMAL.getPluralName(), equalTo("Dezimalzahlen"));
    assertThat(NumberBase.HEXADECIMAL.getPluralName(), equalTo("Hexadezimalzahlen"));
  }

  @Test
  public void testGetRegex() {
    assertThat(NumberBase.BINARY.getRegex(), equalTo("-?0b[0-1][0-1]*"));
    assertThat(NumberBase.OCTAL.getRegex(), equalTo("-?0o[1-7][0-7]*"));
    assertThat(NumberBase.DECIMAL.getRegex(), equalTo("-?[1-9][0-9]*"));
    assertThat(NumberBase.HEXADECIMAL.getRegex(), equalTo("-?0x[1-9a-fA-F][0-9a-fA-F]*"));
  }

  @Test
  public void testGetSingularName() {
    assertThat(NumberBase.BINARY.getSingularName(), equalTo("Binärzahl"));
    assertThat(NumberBase.OCTAL.getSingularName(), equalTo("Oktalzahl"));
    assertThat(NumberBase.DECIMAL.getSingularName(), equalTo("Dezimalzahl"));
    assertThat(NumberBase.HEXADECIMAL.getSingularName(), equalTo("Hexadezimalzahl"));
  }

  @Test
  public void testGetSystemName() {
    assertThat(NumberBase.BINARY.toString(), equalTo("Binärsystem"));
    assertThat(NumberBase.OCTAL.toString(), equalTo("Oktalsystem"));
    assertThat(NumberBase.DECIMAL.toString(), equalTo("Dezimalsystem"));
    assertThat(NumberBase.HEXADECIMAL.toString(), equalTo("Hexadezimalsystem"));
  }

  @Test
  public void testValueOf() {
    assertThat(NumberBase.valueOf("BINARY"), equalTo(NumberBase.BINARY));
    assertThat(NumberBase.valueOf("OCTAL"), equalTo(NumberBase.OCTAL));
    assertThat(NumberBase.valueOf("DECIMAL"), equalTo(NumberBase.DECIMAL));
    assertThat(NumberBase.valueOf("HEXADECIMAL"), equalTo(NumberBase.HEXADECIMAL));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueOfWrongValue() {
    NumberBase.valueOf("TERNARY");
  }
}
