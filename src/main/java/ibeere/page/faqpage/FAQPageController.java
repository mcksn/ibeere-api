package ibeere.page.faqpage;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ibeere.page.feedpage.FeedPageService;
import ibeere.page.common.ItemsPage;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@RequiredArgsConstructor
public class FAQPageController {

    private final FeedPageService feedPageService;
    private final FAQPageService faqPageService;
    private final FallbackFAQService fallbackFAQService;

    @GetMapping(path = "/page/faq")
    @PreAuthorize("permitAll()")
    public ItemsPage faqPage() {
        if (feedPageService.findFeedPage(null, 1).getItems().isEmpty()) {
            return fallbackFAQService.fetch();
        }

        return faqPageService.findFAQPage(null);
    }

    @GetMapping(path = "/page/faq/fallback/store")
    @PreAuthorize("permitAll()")
    public boolean storeFallback() {
        fallbackFAQService.storeFAQ();
        return true;
    }

    @GetMapping(path = "/page/faq/protected")
    @PreAuthorize("isAuthenticated()")
    public ItemsPage feedPage(Authentication authentication) {
        if (feedPageService.findFeedPage(null, 1).getItems().isEmpty()) {
            return fallbackFAQService.fetch();
        }
        return faqPageService.findFAQPage(userId(authentication));
    }
}
