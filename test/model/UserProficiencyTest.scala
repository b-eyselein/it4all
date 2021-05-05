package model

import model.tools.programming.ProgrammingTopics
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UserProficiencyTest extends AnyFlatSpec with Matchers {

  private val topic: Topic = ProgrammingTopics.Slicing

  it should "calculate the right level" in {

    val up1 = UserProficiency("", topic)
    up1.getPoints shouldBe 0
    up1.getLevel shouldBe Level.Beginner

    val up2 = up1.copy(pointsForExercises = Set(LevelForExercise(1, 1, Level.Beginner))) // 1 points
    up2.getPoints shouldBe 1
    up2.getLevel shouldBe Level.Beginner

    val up3 = up2.copy(pointsForExercises =
      Set(
        LevelForExercise(1, 1, Level.Beginner),     // 1 points
        LevelForExercise(2, 2, Level.Intermediate), // 2 points
        LevelForExercise(3, 3, Level.Intermediate), // 2 points
        LevelForExercise(4, 4, Level.Intermediate), // 2 points
        LevelForExercise(5, 5, Level.Advanced)      // 4 points
      )
    )
    up3.getPoints shouldBe 11
    up3.getLevel shouldBe Level.Intermediate

    val up4 = up3.copy(pointsForExercises =
      up3.pointsForExercises ++ Set(
        LevelForExercise(4, 4, Level.Intermediate), // 2 points
        LevelForExercise(5, 5, Level.Advanced)      // 4 points
      )
    )
    up4 shouldBe up3

    // 10 points for lvl 1, 3 not counted
    val up5 = up4.copy(pointsForExercises =
      up4.pointsForExercises ++ Set(
        LevelForExercise(6, 6, Level.Expert),   // 8 points
        LevelForExercise(1, 2, Level.Beginner), // 1 points
        LevelForExercise(7, 7, Level.Expert),   // 8 points
        LevelForExercise(8, 8, Level.Expert)    // 8 points
      )
    )
    up5.getPoints shouldBe 36
    up5.getLevel shouldBe Level.Advanced

  }

}
