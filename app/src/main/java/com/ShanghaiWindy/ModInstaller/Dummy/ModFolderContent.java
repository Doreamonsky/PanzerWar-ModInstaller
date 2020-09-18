package com.ShanghaiWindy.ModInstaller.Dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModFolderContent {
    public static final List<ModFolderContent.ModFolderItem> ITEMS = new ArrayList<ModFolderContent.ModFolderItem>();
    public static final Map<String, ModFolderContent.ModFolderItem> ITEM_MAP = new HashMap<String, ModFolderContent.ModFolderItem>();

    public static void addItem(ModFolderContent.ModFolderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void remove(String id) {
        ITEMS.remove(id);
        ITEM_MAP.remove(id);
    }

    public static void removeAll() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static class ModFolderItem {
        public final String id;
        public final String displayFolder;
        public final String realFolder;

        public ModFolderItem(String id, String folder, String realFolder) {
            this.id = id;
            this.displayFolder = folder;
            this.realFolder = realFolder;
        }
    }
}
