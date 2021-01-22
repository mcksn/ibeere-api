package ibeere.aggregate.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.comment.pagination.CommentPaginationPage;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.aggregate.profile.micro.MicroProfileService;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.support.ClockProvider;
import ibeere.user.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final MicroProfileService microProfileService;
    private final ClockProvider clockProvider;

    @Transactional(readOnly = true)
    public CommentPaginationPage findComments(AnswerQuestionRef answerQuestionRef, int skipFirstCount) {

        final List<CommentEntity> commentEntities = repository.findByAnswerIdAndQuestionId(answerQuestionRef.answerId(), answerQuestionRef.questionId());

        List<CommentEntity> skippedEntities = commentEntities.stream()
                .sorted(comparing(CommentEntity::getSubmitDate).reversed())
                .skip(skipFirstCount)
                .collect(toList());

        List<CommentEntity> limitedEntities = skippedEntities.stream()
                .limit(3)
                .collect(toList());

        final Set<UserId> authorIds = limitedEntities.stream()
                .map(CommentEntity::getAuthorId)
                .collect(toSet());

        final Map<UserId, MicroProfile> microProfilesMap = microProfileService.findMicroBy(authorIds).stream()
                .collect(toMap(MicroProfile::getUserId, identity()));

        return new CommentPaginationPage(limitedEntities.stream()
                .map(c -> new Comment(c.getCommentId(), c.getAnswerId(), c.getQuestionId(), microProfilesMap.get(c.getAuthorId()), c.getContent(), c.getSubmitDate()))
                .collect(toList()), skippedEntities.size() > limitedEntities.size());
    }

    public Comment addComment(NewComment newComment) {

        final CommentEntity entity = new CommentEntity(CommentId.of(UUID.randomUUID()), newComment.getAnswerIdQuestionRef().answerId(),
                newComment.getAnswerIdQuestionRef().questionId(), newComment.getUserId(), newComment.getContent(),
                Instant.now(clockProvider.standardClock()));

        repository.save(entity);

        return new Comment(entity.getCommentId(), entity.getAnswerId(), entity.getQuestionId(), microProfileService.findMicroBy(entity.getAuthorId()).get(), entity.getContent(), entity.getSubmitDate());
    }

    public boolean deleteComment(CommentId commentId, AnswerQuestionRef answerQuestionRef, UserId requesterId) {
        return repository.findById(commentId)
                .filter(foundComment -> foundComment.getAuthorId().equals(requesterId))
                .map(foundComment -> {
                     repository.delete(foundComment);
                     return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public long countComments(AnswerQuestionRef answerQuestionRef) {
        return repository.countByAnswerIdAndQuestionId(answerQuestionRef.answerId(), answerQuestionRef.questionId());
    }
}
