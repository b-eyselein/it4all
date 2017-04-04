package model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class QuestionRating extends Model {
  
  public static final Finder<QuestionRatingKey, QuestionRating> finder = new Finder<>(QuestionRating.class);
  
  @EmbeddedId
  public QuestionRatingKey key;
  
  @ManyToOne
  @JoinColumn(name = "username", insertable = false, updatable = false)
  public QuestionUser user;
  
  @ManyToOne
  @JoinColumn(name = "question_id", insertable = false, updatable = false)
  public Question question;
  
  public int rating; // NOSONAR
  
  public QuestionRating(QuestionRatingKey theKey) {
    key = theKey;
  }
  
}
