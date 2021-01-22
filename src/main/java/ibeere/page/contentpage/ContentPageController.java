package ibeere.page.contentpage;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@Transactional
@RequiredArgsConstructor
public class ContentPageController {

    private final ContentPageService contentPageService;

    @GetMapping(path = "/page/content")
    @PreAuthorize("isAuthenticated()")
    public ContentPage getContentPage(Authentication authentication) {
        return contentPageService.getContentPage(userId(authentication));
    }
}
