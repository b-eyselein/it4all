package de.uniwue.dtd.model

import de.uniwue.dtd.model.TestHelperValues._
import org.scalatest.flatspec.AnyFlatSpec

class AttributeSpecificationTest extends AnyFlatSpec {

  "A #REQUIRED attribute specification" should "have a toString representation of" in {
    assert(RequiredSpecification.toString == "#REQUIRED")
  }

  "A #IMPLIED attribute specification" should "have a toString representation of" in {
    assert(ImpliedSpecification.toString == "#IMPLIED")
  }

  "A Default Value attribute specification" should "have a value and a toString representation of" in {
    val spec = DefaultValueSpecification(DefaultValue)

    assert(spec.toString == "\"" + DefaultValue + "\"")
    assert(spec.default == DefaultValue)
  }

  "A #FIXED value attribute specification" should "have a value and a toString representation of" in {
    val spec = FixedValueSpecification(FixedValue)

    assert(spec.toString == "#FIXED \"" + FixedValue + "\"")
    assert(spec.value == FixedValue)
  }

}
