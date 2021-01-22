package ibeere.framework;

import com.fasterxml.jackson.databind.util.StdConverter;
import ibeere.ddd.Identifier;

import java.util.UUID;

public class IdentifierDeConverter {

    public static class UserId extends Identifier.IdentifierDeConverter<ibeere.user.UserId> {
        @Override
        public ibeere.user.UserId convert(String value) {
            return ibeere.user.UserId.of(UUID.fromString(value));
        }
    }

    public static class ProfileId extends Identifier.IdentifierDeConverter<ibeere.aggregate.profile.ProfileId> {
        @Override
        public ibeere.aggregate.profile.ProfileId convert(String value) {
            return ibeere.aggregate.profile.ProfileId.of(UUID.fromString(value));
        }
    }

    public static class QuestionId extends Identifier.IdentifierDeConverter<ibeere.aggregate.question.QuestionId> {
        @Override
        public ibeere.aggregate.question.QuestionId convert(String value) {
            return ibeere.aggregate.question.QuestionId.of(UUID.fromString(value));
        }
    }

    public static class CredentialId extends Identifier.IdentifierDeConverter<ibeere.aggregate.credential.CredentialId> {
        @Override
        public ibeere.aggregate.credential.CredentialId convert(String value) {
            return ibeere.aggregate.credential.CredentialId.of(UUID.fromString(value));
        }
    }

    public static class AnswerId extends Identifier.IdentifierDeConverter<ibeere.aggregate.question.answer.AnswerId> {
        @Override
        public ibeere.aggregate.question.answer.AnswerId convert(String value) {
            return ibeere.aggregate.question.answer.AnswerId.of(UUID.fromString(value));
        }
    }

    public static class TwitterUserId extends StdConverter<String, ibeere.user.auth.twitter.TwitterUserId> {
        @Override
        public ibeere.user.auth.twitter.TwitterUserId convert(String value) {
            return ibeere.user.auth.twitter.TwitterUserId.of(Long.valueOf(value));
        }
    }

    public static class CommentId extends Identifier.IdentifierDeConverter<ibeere.aggregate.comment.CommentId> {
        @Override
        public ibeere.aggregate.comment.CommentId convert(String value) {
            return ibeere.aggregate.comment.CommentId.of(UUID.fromString(value));
        }
    }
}