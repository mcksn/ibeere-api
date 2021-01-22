package ibeere.page.feedpage;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ibeere.page.common.ItemsPage;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@Transactional
@RequiredArgsConstructor
public class FeedPageController {

    private final FeedPageService feedPageService;
    private final FallbackFeedService fallbackFeedService;

    @GetMapping(path = "/page/feed")
    @PreAuthorize("permitAll()")
    public ItemsPage feedPage(HttpServletResponse httpServletResponse, @Param("pageNo") int pageNo) {
        final ItemsPage itemsPage = feedPageService.findFeedPage(null, pageNo);
        if (itemsPage.getItems().isEmpty()) {
            return fallbackFeedService.fetch(pageNo);
        }
        return itemsPage;
    }

    @GetMapping(path = "/page/feed/protected")
    @PreAuthorize("isAuthenticated()")
    public ItemsPage feedPage(Authentication authentication, @Param("pageNo") int pageNo) {
        final ItemsPage itemsPage = feedPageService.findFeedPage(userId(authentication), pageNo);
        if (itemsPage.getItems().isEmpty()) {
            return fallbackFeedService.fetch(pageNo);
        }
        return itemsPage;
    }

    @GetMapping(path = "/page/feed/ready/percentage")
    @PreAuthorize("permitAll()")
    public int feedReadyPercentage() {
        return feedPageService.countFeedReadyPercentage();
    }

    @GetMapping(path = "/page/feed/fallback/store")
    @PreAuthorize("permitAll()")
    public boolean storeFallback() throws IOException {
        fallbackFeedService.storeFeed();
        return true;
    }
}
