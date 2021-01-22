package ibeere.page.contentpage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContentPage {

    private final List<ContentItem> items;

    @Getter
    @RequiredArgsConstructor
    public static class ContentItem {
        private final ContentType type;
        private final  Object item;
    }
}
