package ibeere.page.profilepage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.credential.*;
import ibeere.aggregate.credential.profile.CredentialProfile;
import ibeere.aggregate.credential.profile.CredentialProfileService;
import ibeere.aggregate.credential.profile.TemplateCredential;
import ibeere.aggregate.profile.bio.BioProfile;
import ibeere.aggregate.profile.bio.BioProfileService;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.aggregate.profile.micro.MicroProfileService;
import ibeere.page.common.LastUpdatedContent;
import ibeere.aggregate.credential.profile.CredentialDto;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.*;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.User;
import ibeere.user.UserDocumentService;
import ibeere.user.UserId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static ibeere.aggregate.question.QuestionDto.toQuestionDtoWithNoAnswers;
import static ibeere.aggregate.question.answer.AnswerDto.toAnswerDto;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProfilePageService {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final QuestionDocumentService questionDocumentService;
    private final UserDocumentService userDocumentService;
    private final AnswerViewCountService answerViewCountService;
    private final CredentialProfileService credentialProfileService;
    private final MicroProfileService microProfileService;
    private final CredentialService credentialService;
    private final MicroProfileService uncachedMicroProfileService;
    private final CredentialProfileService uncachedCredentialProfileService;
    private final BioProfileService bioProfileService;

    public List<CredentialDto> findCredentialOptions(UserId userId, UserId requesterId) {

        CredentialProfile credentialProfile = credentialProfileService.findById(userId).get();

        return credentialProfile.getCredentialOptionsForAnAnswer()
                .stream()
                .map(CredentialDto::new)
                .collect(toList());
    }

    public FullProfileDto findFullByPathUncached(String path, UserId requesterId) {

        MicroProfile microProfile = uncachedMicroProfileService.findMicroByPath(path).get();
        CredentialProfile credentialProfile = uncachedCredentialProfileService.findById(microProfile.getUserId()).get();
        BioProfile bioProfile = bioProfileService.findById(microProfile.getUserId()).get();

        if (microProfile.isDoingAQandA()) {
            return new QandAProfileDto(microProfile, credentialProfile, bioProfile, requesterId);
        } else {
            return new FullProfileDto(microProfile, credentialProfile, bioProfile, requesterId);
        }
    }

    public FullProfileDto findProfilePage(String path) {

        MicroProfile microProfile = microProfileService.findMicroByPath(path).get();
        CredentialProfile credentialProfile = credentialProfileService.findById(microProfile.getUserId()).get();
        BioProfile bioProfile = bioProfileService.findById(microProfile.getUserId()).get();

        if (microProfile.isDoingAQandA()) {
            return new QandAProfileDto(microProfile, credentialProfile, bioProfile, null);
        } else {
            return new FullProfileDto(microProfile, credentialProfile, bioProfile, null);
        }
    }

    public AnswerCredentialPreviewDto findBy(UserId userId, Set<CredentialId> credentialIds) {

        MicroProfile microProfile = microProfileService.findMicroBy(userId).get();
        CredentialProfile credentialProfile = credentialProfileService.findById(userId).get();
        List<TemplateCredential> templateCredentials = credentialProfile.getCredentials(credentialIds);
        List<ImmutableCredential> immutableCredentials = credentialService.findBy(credentialIds);

        List<Credential> credentials = new ArrayList<>();
        credentials.addAll(templateCredentials);
        credentials.addAll(immutableCredentials);
        return new AnswerCredentialPreviewDto(microProfile, credentials);
    }
    public List<AnswerDto> findProfileAnswers(UserId userId) {

        final User user = userDocumentService.get(userId);
        final List<AnswerQuestionRef> publishedAnswers = answerService.findPublishedAnswersByDeviceId(user.getId());
        final List<LastUpdatedContent> contentList = questionDocumentService.declarePublishAnswerStream(publishedAnswers)
                .collect(toList());

        return contentList.stream()
                .sorted(Comparator.comparing(LastUpdatedContent::getUpdated))
                .map(c ->
                        toAnswerDto((Answer) c,
                                user,
                                this.answerViewCountService.showViewCount(AnswerQuestionRef.of(((Answer) c).getAnswerId(),
                                        ((Answer) c).getQuestion().getQuestionId()))))
                .collect(toList());
    }

    public List<QuestionDto> findProfileQandAQuestions(UserId userId) {

        final User user = userDocumentService.get(userId);
        final List<QuestionId> questionIds = questionService.findIdByQandAUserId(user.getId());
        final List<LastUpdatedContent> contentList = questionDocumentService.declarePublishedQuestionStream(questionIds)
                .collect(toList());

        return contentList.stream()
                .sorted(Comparator.comparing(LastUpdatedContent::getUpdated))
                .map(c -> toQuestionDtoWithNoAnswers((Question) c, user))
                .collect(toList());
    }

    public List<QuestionDto> findProfileQuestions(UserId userId) {

        final User user = userDocumentService.get(userId);
        final List<QuestionId> questionIds = questionService.findIdByUserId(user.getId());
        final List<LastUpdatedContent> contentList = questionDocumentService.declareMaybePrivatePublishedQuestionStream(questionIds)
                .collect(toList());

        return contentList.stream()
                .sorted(Comparator.comparing(LastUpdatedContent::getUpdated))
                .map(c -> toQuestionDtoWithNoAnswers((Question) c, user))
                .collect(toList());
    }
}
