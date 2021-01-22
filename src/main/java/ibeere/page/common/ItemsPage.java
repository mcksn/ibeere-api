package ibeere.page.common;

import java.util.List;

public class ItemsPage {

    private final List<Item> items;
    private final boolean hasMore;

    public ItemsPage(List<Item> items, boolean hasMore) {
        this.items = items;
        this.hasMore = hasMore;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private final ItemType type;
        private final  Object item;

        public Item(ItemType type, Object item) {
            this.type = type;
            this.item = item;
        }

        public ItemType getType() {
            return type;
        }

        public Object getItem() {
            return item;
        }
    }

    public boolean getHasMore() {
        return hasMore;
    }
}
