package ibeere.newsletter.newquestion.question;

import ibeere.aggregate.question.QuestionId;
import ibeere.repository.CachingJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("newquestion.QuestionRepository")
/** Repository of questions in a 'new questions newsletter' context */
public interface QuestionRepository extends CachingJpaRepository<Question, QuestionId> {

    List<Question> findBySentForNewQuestionsNot(Boolean sentForNewQuestions);
}