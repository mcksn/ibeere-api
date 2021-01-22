package ibeere.framework;

import org.springframework.core.convert.converter.Converter;
import ibeere.aggregate.comment.CommentId;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.profile.ProfileId;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.QuestionId;
import ibeere.user.auth.twitter.TwitterUserId;
import ibeere.user.UserId;

import java.util.UUID;

public abstract class IdentifierConverter<T> implements Converter<String, T> {

    @Override
    public T convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
 
        return internalConvert(UUID.fromString(source));
    }

    abstract T internalConvert(UUID source);

    public static class UserIdConverter extends IdentifierConverter<UserId> {
        @Override
        UserId internalConvert(UUID source) {
            return UserId.of(source);
        }
    }

    public static class ProfileIdConverter extends IdentifierConverter<ProfileId> {
        @Override
        ProfileId internalConvert(UUID source) {
            return ProfileId.of(source);
        }
    }

    public static class CredentialIdConverter extends IdentifierConverter<CredentialId> {
        @Override
        CredentialId internalConvert(UUID source) {
            return CredentialId.of(source);
        }
    }

    public static class QuestionIdConverter extends IdentifierConverter<QuestionId> {
        @Override
        QuestionId internalConvert(UUID source) {
            return QuestionId.of(source);
        }
    }

    public static class AnswerIdConverter extends IdentifierConverter<AnswerId> {
        @Override
        AnswerId internalConvert(UUID source) {
            return AnswerId.of(source);
        }
    }

    public static class TwitterUserIdConverter implements Converter<Long, TwitterUserId> {
        @Override
        public TwitterUserId convert(Long source) {
                if (source == null) {
                    return null;
                }

                return TwitterUserId.of(Long.valueOf(source));
            }
    }

    public static class CommentIdConverter extends IdentifierConverter<CommentId> {
        @Override
        CommentId internalConvert(UUID source) {
            return CommentId.of(source);
        }
    }
}