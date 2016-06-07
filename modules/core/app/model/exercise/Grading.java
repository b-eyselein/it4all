package model.exercise;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.user.User;

@Entity
public class Grading extends Model {
  
  public static final Finder<GradingKey, Grading> finder = new Finder<GradingKey, Grading>(Grading.class);

  public static boolean otherPartCompleted(int exerciseId, User user) {
    List<Grading> gradings = Grading.finder.where().eq("user_name", user.name).eq("exercise_id", exerciseId).findList();
    return gradings.size() == 1 && gradings.get(0).hasAllPoints();
  }

  @EmbeddedId
  public GradingKey key;

  @ManyToOne
  @JoinColumn(name = "user_name", insertable = false, updatable = false)
  public User user;

  @ManyToOne
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  public Exercise exercise;

  public int points;

  public Grading(GradingKey theKey) {
    key = theKey;
  }

  public boolean hasAllPoints() {
    return points == exercise.getMaxPoints();
  }

}
