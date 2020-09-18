package com.ShanghaiWindy.ModInstaller.Dummy;


import com.ShanghaiWindy.ModInstaller.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModLinkContent {
    public static final List<ModLinkItem> ITEMS = new ArrayList<ModLinkItem>();
    public static final Map<String, ModLinkItem> ITEM_MAP = new HashMap<String, ModLinkItem>();

    public static void addItem(ModLinkItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void removeAll() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static class ModLinkItem {
        public final String id;

        public final String displayName;
        public final Util.FileState fileState;
        public final int installStateText;
        public final String description;
        public final String link;
        public final String fileName;
        public final String size;
        public final String author;
        public final String editTime;

        public ModLinkItem(String id, Util.FileState fileState, int installState, String displayName, String details, String link, String fileName, String size, String author, String editTime) {
            this.id = id;
            this.displayName = displayName;
            this.fileState = fileState;
            this.installStateText = installState;
            this.description = details;
            this.link = link;
            this.fileName = fileName;
            this.size = size;
            this.author = author;
            this.editTime = editTime;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
