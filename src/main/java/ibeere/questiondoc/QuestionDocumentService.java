package ibeere.questiondoc;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import ibeere.audience.Audience;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerQuestionRef;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static java.util.Optional.*;

/**
 * Question document refers to a denormalised question and graph of entities beyond the question aggregate.
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"questionById"})
public class QuestionDocumentService {

    private final InternalQuestionDocumentService internalQuestionDocumentService;

    public Optional<Question> get(QuestionId questionId) {
        return internalQuestionDocumentService.get(questionId);
    }

    public Map<Object, Object> getCacheAsMap() {
        return internalQuestionDocumentService.getCacheAsMap();
    }

    public Optional<Question> rebuild(QuestionId questionId) {
        return internalQuestionDocumentService.rebuild(questionId);
    }

    public Stream<Question> declareQuestionFollowedStream(List<QuestionId> questionIdsFollowed) {
        return questionIdsFollowed.stream()
                .map(internalQuestionDocumentService::get)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Stream<Question> declareMaybePrivatePublishedQuestionStream(List<QuestionId> publishedQueIds) {
        return publishedQueIds.stream()
                .map(internalQuestionDocumentService::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(d -> d.getAudience() != Audience.ANONYMOUS);
    }

    public Stream<Question> declarePublishedQuestionStream(List<QuestionId> publishedQueIds) {
        return publishedQueIds.stream()
                .map(internalQuestionDocumentService::get)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Stream<Answer> declarePublishAnswerStream(List<AnswerQuestionRef> publishedAnswerRefs) {
        return publishedAnswerRefs.stream()

                .map(ref -> internalQuestionDocumentService.get(ref.questionId())
                        .flatMap(q -> q.getAnswers().stream()
                                .filter(a -> a.getAnswerId().equals(ref.answerId()))
                                .findFirst()))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Stream<Answer> declareAllPublishAnswerStream(List<AnswerQuestionRef> publishedAnswerRefs) {
        return publishedAnswerRefs.stream()

                .map(ref -> internalQuestionDocumentService.get(ref.questionId())
                        .flatMap(q -> q.getAllAnswers().stream()
                                .filter(a -> a.getAnswerId().equals(ref.answerId()))
                                .findFirst()))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Service
    @RequiredArgsConstructor
    @CacheConfig(cacheNames = {"questionById"})
    static class InternalQuestionDocumentService {
        private static final Logger LOG = LoggerFactory.getLogger(QuestionDocumentService.class);

        private final QuestionService questionService;
        private final CacheManager cacheManager;

        @Cacheable
        public Optional<Question> get(QuestionId questionId) {
            LOG.info("bypassed cache. getting " + questionId);
            return findQuestion(questionId);
        }

        public Map<Object, Object> getCacheAsMap() {
            final @NonNull ConcurrentMap<Object, Object> map = ((CaffeineCache) cacheManager.getCache("questionById")).getNativeCache().asMap();
            return  map;
        }

        @CachePut
        public Optional<Question> rebuild(QuestionId questionId) {
            LOG.info("rebuild" + questionId);
            return findQuestion(questionId);
        }

        private Optional<Question> findQuestion(QuestionId questionId) {
            try {
                return this.questionService.findQuestion(questionId);
            } catch (Exception e ) {
                LOG.error(questionId.toString() ,e);
                return empty();
            }
        }
    }
}
