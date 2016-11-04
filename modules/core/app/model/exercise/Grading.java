package model.exercise;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.user.User;

@Entity
public class Grading extends Model {

  @Embeddable
  public static class GradingKey implements Serializable {

    private static final long serialVersionUID = -1474660743836662259L;

    public String userName;

    public int exerciseId;

    public GradingKey(String theUserName, int theExerciseId) {
      userName = theUserName;
      exerciseId = theExerciseId;
    }

    @Override
    public boolean equals(Object obj) {
      if(obj == null || !(obj instanceof GradingKey))
        return false;

      GradingKey otherKey = (GradingKey) obj;
      return otherKey.userName.equals(userName) && otherKey.exerciseId == exerciseId;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + exerciseId;
      result = prime * result + ((userName == null) ? 0 : userName.hashCode());
      return result;
    }

  }

  public static final Finder<GradingKey, Grading> finder = new Finder<>(Grading.class);

  @EmbeddedId
  public GradingKey key;

  @ManyToOne
  @JoinColumn(name = "user_name", insertable = false, updatable = false)
  public User user;
  
  // @ManyToOne
  // @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  // public Exercise exercise;

  public int points;

  public Grading(GradingKey theKey) {
    key = theKey;
  }

  public static boolean otherPartCompleted(int exerciseId, User user) {
    List<Grading> gradings = Grading.finder.where().eq("user_name", user.name).eq("exercise_id", exerciseId).findList();
    return gradings.size() == 1 && gradings.get(0).hasAllPoints();
  }

  public boolean hasAllPoints() {
    return false;
    // return points == exercise.getMaxPoints();
  }

  public void setPoints(int thePoints) {
    points = thePoints;
  }

}
