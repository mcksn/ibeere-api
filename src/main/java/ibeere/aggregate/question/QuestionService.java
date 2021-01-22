package ibeere.aggregate.question;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.question.answer.vote.UpVoteRepository;
import ibeere.audience.Audience;
import ibeere.aggregate.comment.CommentService;
import ibeere.aggregate.credential.Credential;
import ibeere.aggregate.credential.CredentialService;
import ibeere.aggregate.question.follow.FollowEntity;
import ibeere.aggregate.question.follow.FollowRepository;
import ibeere.event.EventPublisher;
import ibeere.page.relevantpage.RelevantQuestion;
import ibeere.aggregate.credential.profile.CredentialProfileService;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.aggregate.profile.micro.MicroProfileService;
import ibeere.aggregate.question.answer.*;
import ibeere.aggregate.question.answer.vote.Votes;
import ibeere.support.ClockProvider;
import ibeere.user.UserId;
import ibeere.user.UserInputException;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;
import static java.time.Clock.system;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.domain.Pageable.unpaged;

/**
 * Service for maintaining the Question root
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository repository;
    private final AnswerRepository answerRepository;
    private final FollowRepository followRepository;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final CredentialProfileService credentialProfileService;
    private final CredentialService credentialService;
    private final MicroProfileService microProfileService;
    private final ClockProvider clockProvider;

    @Transactional
    public UUID follow(UserId userId, QuestionId questionId) throws UserInputException {

        if (repository.findIdByUserId(userId).stream()
                .anyMatch(q -> q.equals(questionId))) {
            throw new UserInputException("Attempting to follow their own question.");
        }

        if (followRepository.findByQuestionIdAndUserId(questionId, userId).isPresent()) {
            System.err.println("Attempting to follow when already following. " + questionId + " " + userId);
            throw new UserInputException("Attempting to follow when already following.");
        }

        final FollowEntity followEntity = new FollowEntity(randomUUID(), userId, questionId, now(clockProvider.standardClock()));
        this.followRepository.save(followEntity);
        return followEntity.getId();
    }

    public boolean unFollow(UserId userId, QuestionId questionId) {

        followRepository.findByQuestionIdAndUserId(questionId, userId)
                .ifPresent(e -> followRepository.delete(e));
        return true;
    }





    public Optional<Instant> lastFollowed(QuestionId questionId) {

        final Optional<FollowEntity> lastFollowed  = followRepository
                .findFirstByQuestionIdOrderBySubmitDateDesc(questionId);

        return lastFollowed.map(FollowEntity::getSubmitDate);
    }

    public List<UserId> findFollowersFor(QuestionId questionId) {
        return this.followRepository.findByQuestionId(questionId).stream()
                .map(FollowEntity::getUserId)
                .collect(Collectors.toList());
    }

    public List<QuestionId> findQuestionsFollowed(UserId userId) {
        return this.followRepository.findByUserId(userId).stream()
                .map(FollowEntity::getQuestionId)
                .collect(Collectors.toList());
    }

    public long countFollowsFor(QuestionId questionId) {
        return followRepository.countByQuestionId(questionId);
    }

    @Transactional
    public QuestionId submitQuestion(String questionText, String linkText, UserId userId, String userName, Audience audience, @Nullable MicroProfile profile) throws Exception {

        if (isBlank(questionText)) {
            throw new Exception("Question is empty");
        }

        QuestionEntity questionEntity = new QuestionEntity(QuestionId.of(UUID.randomUUID()),
                userId,
                userName,
                questionText,
                linkText,
                generatePath(questionText),
                now(system(ZoneId.of("UTC"))),
                audience,
                ofNullable(profile)
                        .map(MicroProfile::getUserId)
                        .orElse(null),
                null);

        repository.save(questionEntity);
        return questionEntity.getId();
    }

    public List<RelevantQuestion> findRelevantQuestion(String path) {
        return repository.findRelevant(path);
    }

    public Optional<QuestionId> findQuestion(String path) {
        return repository.findIdByPath(path);
    }

    public List<QuestionId> findQuestions(List<String> paths) {
        return repository.findIdsByPaths(paths);
    }

    public Optional<Question> findQuestion(QuestionId questionId) {
        return repository.findById(questionId).
                map(this::mapWithAnswers);
    }

    public List<QuestionId> findAll() {
        return this.repository.findIdsAll(unpaged()).stream().collect(toList());
    }

    public int count() {
        return toIntExact(this.repository.count());
    }

    public List<QuestionId> findIdByUserId(UserId userId) {
        return this.repository.findIdByUserId(userId);
    }

    public List<QuestionId> findIdByQandAUserId(UserId userId) {
        return this.repository.findIdByQandAUserId(userId);
    }

    private String generatePath(String questionText) {
        return questionText
                .trim()
                .replace(" ", "-")
                .replaceAll("[^a-zA-Z0-9^-]", "")
                .toLowerCase();
    }

    private Question mapWithAnswers(QuestionEntity entity) {

        long followingCount = countFollowsFor(entity.getId());

        final List<Answer> answers = answerRepository.findByQuestionId(entity.getId()).stream()
                .map(answerEntity -> mapWithQuestion(answerEntity, entity)).collect(toList());

        final Instant lastFollowed = lastFollowed(entity.getId()).orElse(null);

        final Question question = new Question(entity.getQuestionId(),
                entity.getQuestionText(),
                entity.getPath(),
                entity.getUserName(),
                entity.getUserId(),
                followingCount,
                entity.getSubmitDate(),
                answers,
                entity.getAudience());
        question.postInstance(lastFollowed);
        answers.forEach(d -> d.getQuestion().postInstance(lastFollowed));

        return question;
    }

    private Question mapWithoutAnswers(QuestionEntity entity) {

        long followingCount = countFollowsFor(entity.getId());

        return new Question(entity.getQuestionId(),
                entity.getQuestionText(),
                entity.getPath(),
                null,
                entity.getUserId(), followingCount,
                entity.getSubmitDate(),
                null,
                entity.getAudience());
    }

    private Answer mapWithQuestion(AnswerEntity entity, QuestionEntity questionEntity) {

        final Votes votes = answerService.voteAggregationCount(entity.getId());

        List<Credential> credentials = new ArrayList<>();
        credentials.addAll(credentialService.findBy(entity.getCredentials()));
        credentials.addAll(credentialProfileService.findById(entity.getUserId()).get().getCredentials(entity.getCredentials(), false));

        final boolean upVotedByAsker = answerService.upVotedByAsker(questionEntity.getUserId(), entity.getId());
        return new Answer(entity.getAnswerId(), mapWithoutAnswers(questionEntity), entity.getEditorState().replace("http://", "https://"),
                entity.getSubmitDate(),
                votes,
                entity.getPath(),
                false,
                false,
                upVotedByAsker,
                !upVotedByAsker && answerService.downVotedByAsker(questionEntity.getUserId(), entity.getId()),
                entity.getViewCount(),
                commentService.countComments(entity.answerQuestionRef()),
                entity.getAudience(),
                microProfileService.findMicroBy(entity.getUserId()).get(),
                credentials,
                entity.isEditable());
    }
}
