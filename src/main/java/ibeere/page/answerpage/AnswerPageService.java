package ibeere.page.answerpage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.AnswerViewCountService;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.User;
import ibeere.user.UserDocumentService;
import ibeere.user.UserId;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ibeere.aggregate.question.QuestionDto.toQuestionDto;
import static ibeere.aggregate.question.QuestionDto.toQuestionDtoWithNoAnswers;

@Service
@RequiredArgsConstructor
public class AnswerPageService {
    private final QuestionService questionService;
    private final QuestionDocumentService questionDocumentService;
    private final UserDocumentService userDocumentService;

    public List<QuestionDto> findAnswerPage(UserId userId) {

        final User user = userDocumentService.get(userId);

        final List<QuestionId> questionIds = this.questionService.findAll();
        return questionIds.stream().map(questionDocumentService::get).filter(Optional::isPresent).map(Optional::get)
                .map(q -> toQuestionDtoWithNoAnswers(q, user))
                .collect(toList());
    }
}
