package ibeere.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ibeere.aggregate.question.QuestionId;

@Service
@RequiredArgsConstructor
public class  UserDocumentService {

    private final UserService userService;

    @Cacheable(value = "userById", cacheManager = "userCacheManager")
    public User get(UserId userId) {
        return this.userService.findById(userId);
    }

    @CachePut(value = "userById", cacheManager = "userCacheManager")
    public User rebuild(UserId userId) {
        return this.userService.findById(userId);
    }
    @CachePut(value = "userById", cacheManager = "userCacheManager", key = "#p0")
    public User rebuild(UserId userId, QuestionId recentQuestionIdAsked) {
        return this.userService.findById(userId, recentQuestionIdAsked);
    }
}
