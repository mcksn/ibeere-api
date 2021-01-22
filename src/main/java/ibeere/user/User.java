package ibeere.user;

import lombok.RequiredArgsConstructor;
import ibeere.aggregate.question.follow.FollowEntity;
import ibeere.aggregate.profile.micro.FullName;
import ibeere.aggregate.question.QuestionId;
import ibeere.aggregate.question.answer.vote.DownVoteEntity;
import ibeere.aggregate.question.answer.vote.UpVoteEntity;
import ibeere.ddd.ImmutableEntity;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class User extends UserEntity implements ImmutableEntity {

    private final QuestionId recentQuestionId;

    User(UserId id, FullName fullName, String bio, String email, String password, List<FollowEntity> follows,
         List<UpVoteEntity> upVotes, List<DownVoteEntity> downVotes, Instant signUpDate, QuestionId recentQuestionId) {
        super(id, fullName, bio, email, password, null, follows, upVotes, downVotes, null, null, signUpDate);
        this.recentQuestionId = recentQuestionId;
    }

    public QuestionId getRecentQuestionId() {
        return recentQuestionId;
    }
}
