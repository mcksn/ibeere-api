package ibeere.page.relevantpage;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ibeere.page.common.ItemsPage;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@Transactional
@RequiredArgsConstructor
public class RelevantPageController {

    private final RelevantPageService relevantPageService;

    @GetMapping(path = "/question/{path}/relevant/protected")
    @PreAuthorize("isAuthenticated()")
    public ItemsPage getRelevantPages(Authentication authentication, @PathVariable("path") String path) {
        return relevantPageService.relevantPages(path, userId(authentication));
    }

    @GetMapping(path = "/question/{path}/relevant")
    public ItemsPage getRelevantPages(@PathVariable("path") String path) {
        return relevantPageService.relevantPages(path, null);
    }
}
