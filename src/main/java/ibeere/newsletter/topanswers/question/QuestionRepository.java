package ibeere.newsletter.topanswers.question;

import ibeere.newsletter.newquestion.question.Question;
import ibeere.aggregate.question.QuestionId;
import ibeere.repository.CachingJpaRepository;
import org.springframework.stereotype.Repository;

@Repository("topanswers.QuestionRepository")
/** Repository of questions in a 'top answers newsletter' context */
public interface QuestionRepository extends CachingJpaRepository<Question, QuestionId> {
}