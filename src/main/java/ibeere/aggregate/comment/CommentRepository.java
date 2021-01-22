package ibeere.aggregate.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ibeere.aggregate.question.QuestionId;
import ibeere.aggregate.question.answer.AnswerId;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, CommentId> {

    List<CommentEntity> findByAnswerIdAndQuestionId(AnswerId answerId, QuestionId questionId);
    long countByAnswerIdAndQuestionId(AnswerId answerId, QuestionId questionId);
}
