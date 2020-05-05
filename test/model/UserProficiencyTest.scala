package model

import model.tools.programming.ProgrammingTopics
import org.scalatest.{FlatSpec, Matchers}

class UserProficiencyTest extends FlatSpec with Matchers {

  private val topic: Topic = ProgrammingTopics.Slicing

  "UserProficiency" should "get and remove values from a map" in {

    UserProficiency.getAndRemove(1, Map.empty) shouldBe (None, Map.empty)

    UserProficiency.getAndRemove(1, Map(1 -> 2, 2 -> 3, 3 -> 4)) shouldBe (Some(2), Map(2 -> 3, 3 -> 4))

    UserProficiency.getAndRemove(2, Map(1 -> 2, 2 -> 3, 3 -> 4)) shouldBe (Some(3), Map(1 -> 2, 3 -> 4))

    UserProficiency.getAndRemove(4, Map(1 -> 2, 2 -> 3, 3 -> 4)) shouldBe (None, Map(1 -> 2, 2 -> 3, 3 -> 4))
  }

  it should "calculate the right level" in {

    val up1 = UserProficiency("", topic, Map.empty)
    up1.getLevel shouldBe 1
    up1.explainLevel shouldBe Map(
      1 -> Map.empty,
      2 -> Map.empty,
      3 -> Map.empty
    )

    val up2 = UserProficiency("", topic, Map(1 -> 8))
    up2.getLevel shouldBe 1
    up2.explainLevel shouldBe Map(
      1 -> Map(1 -> 8),
      2 -> Map.empty,
      3 -> Map.empty
    )

    UserProficiency("", topic, Map(1 -> 11)).getLevel shouldBe 2

    // 10 points for lvl 1, 3 not counted
    UserProficiency("", topic, Map(1 -> 8, 2 -> 5)).getLevel shouldBe 2

    // 10 points for lvl 1, 9 not counted
    UserProficiency("", topic, Map(3 -> 19)).getLevel shouldBe 2

    // 10 for lvl 1, 20 for lvl 2, 2 not counted
    UserProficiency("", topic, Map(2 -> 32)).getLevel shouldBe 3

    UserProficiency("", topic, Map(3 -> 21)).getLevel shouldBe 3

  }

}
