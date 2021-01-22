package ibeere.questiondoc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ibeere.aggregate.question.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Long.MAX_VALUE;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

@Service
public class QuestionDocumentPopulater implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionDocumentPopulater.class);

    private Map<Integer, List<QuestionId>> preGeneratedPages = null;
    private final QuestionDocumentService questionDocumentService;
    private final QuestionService questionService;
    private final Long questionLimit;

    public QuestionDocumentPopulater(QuestionDocumentService questionDocumentService,
                                     QuestionService questionService,
                                     @Value("${question.limit:}") Long questionLimit) {
        this.questionDocumentService = questionDocumentService;
        this.questionService = questionService;
        this.questionLimit = questionLimit;
    }

    @Scheduled(fixedRate = 600000)
    public void populatePageCache() {
        LOG.info("Populating page cache");
        final List<QuestionId> all = questionService.findAll().stream()
                .limit(ofNullable(questionLimit).orElse(MAX_VALUE))
                .collect(toList());
        all.stream().forEach(q -> this.questionDocumentService.get(q));

        final List<Question> questions = all.stream()
                .map(q -> {
                    final Optional<Question> questionOpt = this.questionDocumentService.get(q);
                    if (questionOpt.isPresent()) {
                        return questionOpt;
                    } else {
                        LOG.info("Skipping ", q);
                        return Optional.<Question>empty();
                    } })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        preGeneratedPages = internalGeneratePages(questions);
    }

    public List<QuestionId> getPage(int index) {
        if (preGeneratedPages == null) {
            return EMPTY_LIST;
        }
        return preGeneratedPages.getOrDefault(index, EMPTY_LIST);
    }

    public boolean isThereAPageAfter(int index) {
        return ofNullable(preGeneratedPages)
                .map(f -> f.get(index + 1) != null)
                .orElse(false);
    }

    Map<Integer, List<QuestionId>> internalGeneratePages(@NonNull List<Question> list) {
        final Map<Score, Queue<Question>> collect = list.stream().collect(CollectorUtils.toShuffledStream())
                .collect(groupingBy(Question::getScore, toCollection(LinkedList::new)));
        List<List<QuestionId>> pages = new LinkedList<>();
        pages.add(new ArrayList<>());
        List<QuestionId> currentPage = pages.get(0);

        while (!(collect.entrySet().stream().allMatch(q -> ofNullable(q.getValue()).map(Collection::isEmpty).orElse(true)))) {
            for (Queue queue : collect.entrySet().stream()
                    .filter(e -> {
                        if (e.getKey().getCategory() == Score.ScoreCategory.MID)
                            return collect.entrySet().stream().filter(f -> f.getKey().getCategory() != Score.ScoreCategory.LOW && f.getKey().getCategory() != e.getKey().getCategory())
                                    .allMatch(q -> ofNullable(q.getValue())
                                            .map(Collection::isEmpty)
                                            .orElse(true));
                        else if (e.getKey().getCategory() == Score.ScoreCategory.LOW)
                            return collect.entrySet().stream().filter(f -> f.getKey().getCategory() != e.getKey().getCategory())
                                    .allMatch(q -> ofNullable(q.getValue())
                                            .map(Collection::isEmpty)
                                            .orElse(true));
                        else
                            return true;
                    })
                    .map(f -> f.getValue()).collect(toList())) {
                if (currentPage.size() > 4) {
                    currentPage = new ArrayList<>();
                    pages.add(currentPage);
                }
                internal(queue, currentPage);
            }
        }

        return IntStream.range(1, pages.size() + 1)
                .boxed()
                .collect(toMap(i -> i, i -> pages.get(i - 1)));
    }

    private void internal(Queue<Question> queue, List<QuestionId> page) {
        if (queue.isEmpty()) {
            return;
        } else {
            page.add(queue.poll().getQuestionId());
            return;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        this.populatePageCache();
    }

    static class CollectorUtils {

        public static <T> Collector<T, ?, Stream<T>> toShuffledStream() {
            return Collectors.collectingAndThen(toList(), collected -> {
                Collections.shuffle(collected);
                return collected.stream();
            });
        }

    }
}
