package ibeere.aggregate.question.answer;

import lombok.Getter;
import org.jsoup.Jsoup;
import ibeere.audience.Audience;
import ibeere.ddd.ImmutableEntity;
import ibeere.page.common.LastUpdatedContent;
import ibeere.aggregate.question.*;
import ibeere.support.Utils;
import ibeere.aggregate.credential.Credential;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.aggregate.question.answer.vote.Votes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.time.Period.between;
import static ibeere.support.Utils.getActualDateHuman;
import static ibeere.support.Utils.getDaysAgoToHuman;
import static ibeere.aggregate.question.answer.FirstImageFinder.findFirstImage;
import static ibeere.aggregate.question.Score.*;

@Getter
public class Answer implements LastUpdatedContent, ImmutableEntity {
    private static final Random RANDOM = new Random(0);
    private final String contentPreview;
    private final AnswerId answerId;
    private final Question question;
    private final String editorState;
    private final Instant submitDate;
    private final String answeredHumanDate;
    private final String answeredActualHumanDate;
    private final long upVoteCounts;
    private final String path;
    private final boolean upVoted;
    private final boolean downVoted;
    private final boolean upVotedByAsker;
    private final boolean downVotedByAsker;
    private final long viewCount;
    private final long downVotes;
    private final String answerText;
    private final long randomValue = RANDOM.nextInt(5);
    private final Image firstImage;
    private final AnswerProfile profile;
    private final long commentCount;
    private final boolean editable;

    public Answer(AnswerId answerId, Question question, String editorState, Instant submitDate,
                  Votes votes, String path,
                  boolean upVoted,
                  boolean downVoted,
                  boolean upVotedByAsker,
                  boolean downVotedByAsker,
                  long viewCount,
                  long commentCount,
                  Audience audience,
                  MicroProfile microProfile,
                  List<Credential> credentials,
                  boolean editable) {
        this.answerId = answerId;
        this.question = question;
        // replacing for reverse proxy TODO make cleaner and re-useable
        this.editorState = editorState.replaceAll("res.cloudinary.com", "ibeere.com");
        this.answerText = Jsoup.parse(editorState).text();
        this.submitDate = submitDate;
        this.path = path;
        this.upVoted = upVoted;
        this.downVoted = downVoted;
        this.answeredHumanDate = getDaysAgoToHuman(
                submitDate);
        this.answeredActualHumanDate = getActualDateHuman(
                submitDate);
        this.upVoteCounts = votes.aggregated;
        this.downVotes = votes.down;
        this.upVotedByAsker = upVotedByAsker;
        this.downVotedByAsker = downVotedByAsker;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.profile = new AnswerProfile(microProfile, credentials, audience);

        this.contentPreview = ContentPreviewGenerator.generate(this.editorState); //after replace is done

        Optional<Image> image = findFirstImage(editorState);

        this.firstImage = image.orElse(null);

        this.editable = editable;

    }

    public String getHumanViewCount(long viewCount) {
        return Utils.getCountsToHuman(viewCount);
    }


    public AnswerQuestionRef answerQuestionRef() {
        return AnswerQuestionRef.of(answerId, question.getQuestionId());
    }

    public String getTag() {
        return answerId.getId().toString().substring(0, 8);
    }

    @Override
    public Instant getUpdated() {
        return submitDate;
    }

    public String microContentPreview() {
        return Jsoup.parse(editorState).text().substring(0, 500);
    }

    public boolean removed() {
        return this.downVotes > 1;
    }

    public Score getScore() {

        if (this.isReallyNewAnswer())
            return editorState.length() >= 200 ? REALLY_NEW_ANSWER : MID_REALLY_NEW_ANSWER_CONTENT;
        if (this.isNewAnswer()) return editorState.length() >= 200 ? NEW_ANSWER : MID_NEW_ANSWER_CONTENT;
        else if (this.upVoteCounts > 2) {
            return randomValue % 2 == 0 ? HIGH_RANDOM_VOTED_ANSWER : LOW_RANDOM_VOTED_ANSWER;
        } else if (this.upVoteCounts > 0)
            return MID;
        else {
            return LOW;
        }
    }

    public boolean isNewAnswer() {
        return between(submitDate.atZone(ZoneId.of("UTC+1")).toLocalDate(), LocalDate.now(ZoneId.of("UTC+1"))).getDays() <= 10;
    }

    public boolean isReallyNewAnswer() {
        return between(submitDate.atZone(ZoneId.of("UTC+1")).toLocalDate(), LocalDate.now(ZoneId.of("UTC+1"))).getDays() <= 2;
    }
}
