package ibeere.aggregate.question.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ibeere.questiondoc.QuestionDocumentService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for counting answer views. Not used in such a way that yield accurate results but for no gives rough estimate.
 */
@Service
@RequiredArgsConstructor
public class AnswerViewCountService {

    private final QuestionDocumentService questionDocumentService;
    private final AnswerService answerService;
    private final Map<AnswerId, Long> viewCounts = new ConcurrentHashMap();

    public Long incAndShowViewCount(AnswerQuestionRef answerQuestionRef) {
        computeIfAbsent(answerQuestionRef);
        return viewCounts.computeIfPresent(answerQuestionRef.answerId(), (answerIdToComputeWith, value) -> value + 1);
    }

    @Scheduled(fixedRate = 60000)
    public void updateAnswerCounts() {
        this.answerService.updateAnswerViewCounts(viewCounts);
    }

    public Long showViewCount(AnswerQuestionRef answerQuestionRef) {
        return computeIfAbsent(answerQuestionRef);
    }

    private Long computeIfAbsent(AnswerQuestionRef answerQuestionRef) {
        return viewCounts.computeIfAbsent(answerQuestionRef.answerId(),
                answerIdToComputeWith -> this.questionDocumentService.get(answerQuestionRef.questionId()).get().getAllAnswers().stream()
                        .filter(answer -> answer.getAnswerId().equals(answerQuestionRef.answerId())).findAny().get().getViewCount());
    }
}
