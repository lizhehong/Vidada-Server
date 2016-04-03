package com.elderbyte.vidada.tags;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class TagInfo {

    private String tag;
    private int usages;
    private boolean isMaster;
    private final Set<String> synonyms = new HashSet<>();

    protected TagInfo(){

    }

    public TagInfo(String tagName, int usages){
        this.tag = tagName;
        this.usages = usages;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getUsages() {
        return usages;
    }

    public void setUsages(int usages) {
        this.usages = usages;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }
}
