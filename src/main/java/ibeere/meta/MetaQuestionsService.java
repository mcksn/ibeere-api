package ibeere.meta;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import ibeere.aggregate.question.QuestionId;
import ibeere.aggregate.question.QuestionService;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
@RequiredArgsConstructor
public class MetaQuestionsService implements ApplicationListener<ContextRefreshedEvent> {

    private final QuestionService questionService;
    private List<QuestionId> contentPageMetaQuestionIds = new ArrayList<>();

    public List<QuestionId> fetchContentPageMetaQuestionIds() {
        return contentPageMetaQuestionIds;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        contentPageMetaQuestionIds = this.questionService.findQuestions(newArrayList("how-do-i-log-out-of-ibeere.com",
                "what-is-ibeere.com",
                "how-do-i-know-if-someone-has-commented-on-my-answer",
                "how-do-i-know-if-someone-has-answered-my-question",
                "can-i-ask-a-question-anonymously",
                "what-is-the-content-page-on-ibeere.com-for",
                "how-do-i-contact-the-ibeere.com-team"
        ));
    }
}
