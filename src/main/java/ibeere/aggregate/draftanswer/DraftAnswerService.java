package ibeere.aggregate.draftanswer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.audience.Audience;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.credential.CredentialService;
import ibeere.aggregate.credential.profile.CredentialProfileService;
import ibeere.aggregate.credential.profile.TemplateCredential;
import ibeere.aggregate.question.QuestionEntity;
import ibeere.aggregate.question.QuestionId;
import ibeere.aggregate.question.QuestionRepository;
import ibeere.aggregate.question.answer.AnswerEntity;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.answer.AnswerRepository;
import ibeere.support.ClockProvider;
import ibeere.user.UserId;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

import static java.time.Clock.system;
import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional
@RequiredArgsConstructor
public class DraftAnswerService {

    private final AnswerRepository answerRepository;
    private final DraftAnswerRepository draftAnswerRepository;
    private final CredentialProfileService credentialProfileService;
    private final CredentialService credentialService;
    private final ClockProvider clockProvider;

    @Transactional
    public UUID updateDraftAnswer(QuestionId questionId, String editorState, UserId userId, String userName, String userBio, Audience audience) {

        final Optional<DraftAnswerEntity> byQuestionAndUser = draftAnswerRepository.findByQuestionIdAndUserId(questionId, userId);

        if (!isBlank(editorState)) {
            if (byQuestionAndUser.isPresent()) {

                byQuestionAndUser.get().updateEditorState(editorState, audience);

                draftAnswerRepository.save(byQuestionAndUser.get());
                return byQuestionAndUser.get().getId();
            } else {
                return createDraftAnswer(questionId, editorState, userId, userName, userBio, audience);
            }
        }
        return null;
    }

    public List<DraftAnswer> findDraftAnswers(UserId userId) {

        final List<DraftAnswerEntity> draftAnswerEntities = draftAnswerRepository.findByUserId(userId);

        return draftAnswerEntities.stream().map(this::map)
                .collect(toList());
    }

    public Optional<DraftAnswerEntity> findDraftAnswer(QuestionId questionId, UserId userId) {

        return draftAnswerRepository.findByQuestionIdAndUserId(questionId, userId);
    }

    /**
     * TODO: Refactor so that the question and draft answer aggregates do not invade on one another's privacy.
     * Maybe need a higher transactional layer.
     */
    @Transactional
    public QuestionId submitDraft(QuestionId questionId, Set<CredentialId> credentialIds, UserId userId, String userName,
                                  String userBio,
                                  Audience audience) {
        DraftAnswerEntity draft = draftAnswerRepository.findByQuestionIdAndUserId(questionId, userId).get();

        final AnswerId answerId = AnswerId.of(draft.getId());

        //TODO credential applicability validation on the list of ids

        List<TemplateCredential> userGenTemplates = credentialProfileService.findById(userId)
                .get().getCredentials(credentialIds, true);

        List<TemplateCredential> nonUserGenTemplates = credentialProfileService.findById(userId)
                .get().getCredentials(credentialIds, false);

        List<CredentialId> newCredentialIds = credentialService.addFromTemplates(userId, answerId, userGenTemplates);

        List<CredentialId> nonUserGeneratedCredentialIds = nonUserGenTemplates.stream()
                .map(cred -> cred.getCredentialId())
                .collect(toList());

        List<CredentialId> credentialIdsToIncludeInAnswer = new ArrayList<>();

        credentialIdsToIncludeInAnswer.addAll(newCredentialIds);
        credentialIdsToIncludeInAnswer.addAll(nonUserGeneratedCredentialIds);

        AnswerEntity answerEntity = new AnswerEntity(answerId,
                questionId,
                userId,
                audience,
                credentialIdsToIncludeInAnswer,
                draft.getEditorState(),
                Instant.now(clockProvider.standardClock()),
                0, "fix me",
                userBio,
                false);

        answerRepository.save(answerEntity);
        draftAnswerRepository.delete(draft);
        return answerEntity.getQuestionId();
    }

    public boolean deleteDraftAnswer(QuestionId questionId, UserId userId) {
        final DraftAnswerEntity draft = draftAnswerRepository.findByQuestionIdAndUserId(questionId, userId).get();
            draftAnswerRepository.delete(draft);
            return true;
    }

    private UUID createDraftAnswer(QuestionId questionId, String editorState, UserId userId, String userName, String userBio, Audience audience) {

        if (isBlank(editorState)) {
            return null;
        }

        final Instant now = now(system(ZoneId.of("UTC")));
        DraftAnswerEntity draftAnswerEntity = new DraftAnswerEntity(UUID.randomUUID(),
                userId,
                userName, editorState,
                questionId,
                now, now, null, null, userBio, audience);

        draftAnswerRepository.save(draftAnswerEntity);
        return draftAnswerEntity.getId();
    }

    private DraftAnswer map(DraftAnswerEntity entity) {
        return new DraftAnswer(entity.getQuestionId(), entity.getEditorState(), entity.getUpdated());
    }
}
