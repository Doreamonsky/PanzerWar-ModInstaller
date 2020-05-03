package com.ShanghaiWindy.ModInstaller.dummy;


import com.ShanghaiWindy.ModInstaller.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkContent {
    public static final List<LinkItem> ITEMS = new ArrayList<LinkItem>();
    public static final Map<String, LinkItem> ITEM_MAP = new HashMap<String, LinkItem>();

    public static void addItem(LinkItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void removeAll() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static class LinkItem {
        public final String id;
        public final String displayName;
        public final Util.FileState fileState;
        public final int installStateText;
        public final String details;
        public final String link;
        public final String fileName;

        public LinkItem(String id, Util.FileState fileState, int installState, String displayName, String details, String link, String fileName) {
            this.id = id;
            this.displayName = displayName;
            this.fileState = fileState;
            this.installStateText = installState;
            this.details = details;
            this.link = link;
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
