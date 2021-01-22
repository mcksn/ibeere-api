package ibeere.user;

import com.google.common.base.Objects;
import ibeere.ddd.RootEntity;
import lombok.Getter;
import org.hibernate.Hibernate;
import ibeere.aggregate.question.follow.FollowEntity;
import ibeere.aggregate.profile.micro.FullName;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.QuestionId;
import ibeere.ddd.Entity;
import ibeere.aggregate.question.answer.vote.DownVoteEntity;
import ibeere.aggregate.question.answer.vote.UpVoteEntity;
import ibeere.user.auth.AuthType;
import ibeere.user.auth.google.GoogleUserId;
import ibeere.user.auth.twitter.TwitterUserId;

import javax.persistence.*;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static java.util.Optional.ofNullable;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

/**
 * @see UserService
 * TODO upVotes + downVotes + follows should be removed to avoid same data shared between aggregates
 */
@javax.persistence.Entity
@Getter
public class UserEntity implements RootEntity<UserId> {

    public static final String DEFAULT_PASSWORD = "password-tmp";

    @EmbeddedId
    private UserId id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String lastName;
    private String bio;
    private String password;
    private Instant lastNonAuthEmailSent;
    private TwitterUserId twitterUserId;
    private GoogleUserId googleUserId;

    @Column(unique = true)
    private String email;

    @OneToMany(fetch = EAGER, cascade = REMOVE)
    @JoinColumn(name = "user_id")
    private List<UpVoteEntity> upVotes;

    @OneToMany(fetch = LAZY, cascade = REMOVE)
    @JoinColumn(name = "user_id")
    private List<DownVoteEntity> downVotes;

    @OneToMany(fetch = LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<FollowEntity> follows;

    private Instant signUpDate;

    public UserEntity(UserId id,
                      FullName fullName,
                      String bio,
                      String email,
                      String password,
                      Instant lastNonAuthEmailSent,
                      List<FollowEntity> follows,
                      List<UpVoteEntity> upVotes,
                      List<DownVoteEntity> downVotes,
                      TwitterUserId twitterUserId,
                      GoogleUserId googleUserId,
                      Instant signUpDate) {
        this.id = id;
        this.name = ofNullable(fullName)
                .map(FullName::first)
                .orElse(null);
        this.lastName = ofNullable(fullName)
                .map(FullName::last)
                .orElse(null);
        this.bio = bio;
        this.email = email;
        this.lastNonAuthEmailSent = lastNonAuthEmailSent;
        this.follows = follows;
        this.password = password;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.twitterUserId = twitterUserId;
        this.googleUserId = googleUserId;
        this.signUpDate = signUpDate;
    }

    public UserEntity(UserId id,
                      FullName fullName,
                      String bio,
                      String email,
                      String password,
                      Instant lastNonAuthEmailSent,
                      TwitterUserId twitterUserId,
                      GoogleUserId googleUserId,
                      Instant signUpDate) {
        this.id = id;
        this.name = ofNullable(fullName).map(FullName::first).orElse(null);
        this.lastName = ofNullable(fullName).map(FullName::last).orElse(null);
        this.bio = bio;
        this.email = email;
        this.lastNonAuthEmailSent = lastNonAuthEmailSent;
        this.password = password;
        this.twitterUserId = twitterUserId;
        this.googleUserId = googleUserId;
        this.signUpDate = signUpDate;
    }

    // hibernate
    public UserEntity() {
    }

    public AuthType authType() {
        if (twitterUserId != null) {
            return AuthType.TWITTER;
        } else if (googleUserId != null) {
            return AuthType.GOOGLE;
        } else {
            return AuthType.EMAIL;
        }
    }

    public FullName getFullName() {
        return FullName.of(name, lastName);
    }

    public void recordEmailSent(Clock clock) {
        this.lastNonAuthEmailSent = clock.instant();
    }

    public boolean hasUpVoted(AnswerId answerId) {
        return upVotes.stream()
                .anyMatch(u -> u.getAnswerId().equals(answerId));
    }

    public boolean hasDownVoted(AnswerId answerId) {
        return downVotes.stream()
                .anyMatch(u -> u.getAnswerId().equals(answerId));
    }

    public boolean isFollowing(QuestionId questionId) {
        return follows.stream()
                .anyMatch(u -> u.getQuestionId().equals(questionId));
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void initLazyCollections() {
        Hibernate.initialize(this.getUpVotes());
        Hibernate.initialize(this.getFollows());
        Hibernate.initialize(this.getDownVotes());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("lastName", lastName)
                .add("bio", bio)
                .add("lastNonAuthEmailSent", lastNonAuthEmailSent)
                .add("twitterUserId", twitterUserId)
                .add("googleUserId", googleUserId)
                .add("email", email)
                .toString();
    }
}
