package ibeere.page.questionpage;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.answer.AnswerViewCountService;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.User;
import ibeere.user.UserDocumentService;
import ibeere.user.UserId;

import java.util.*;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static ibeere.aggregate.question.answer.AnswerDto.toAnswerDto;
import static ibeere.aggregate.question.QuestionDto.toQuestionDto;
import static ibeere.aggregate.question.QuestionDto.toQuestionDtoWithNoAnswers;

@Service
@RequiredArgsConstructor
public class QuestionPageService {
    private final QuestionService questionService;
    private final QuestionDocumentService questionDocumentService;
    private final UserDocumentService userDocumentService;
    private final AnswerViewCountService answerViewCountService;

    public Optional<QuestionDto> findQuestionPage(String path, @Nullable UserId userId) {
        final Optional<User> user = ofNullable(userId).map(userDocumentService::get);

        return questionService.findQuestion(path)
                .flatMap(questionDocumentService::get)
                .map(q -> {
                    final Map<AnswerId, Long> viewCountToAnswerId = q.getAnswers().stream()
                            .map(Answer::getAnswerId)
                            .collect(toMap(identity(), aId -> this.answerViewCountService.incAndShowViewCount(AnswerQuestionRef.of(aId, q.getQuestionId()))));
                    return user.map(u -> toQuestionDto(q, u, viewCountToAnswerId)).orElse(toQuestionDto(q, viewCountToAnswerId));
                });
    }
}
