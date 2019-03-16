package com.example.a12260.szh.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 12260
 * 过滤规则结构体，保存在shared preferences
 */
public class FilterRule {
    public static FilterRule getDefaultRule() {
        FilterRule filterRule = new FilterRule();
        filterRule.setSelectPartOrAll(true);
        filterRule.setPackages(new HashSet<>());
        return filterRule;
    }

    public boolean isSelectPartOrAll() {
        return selectPartOrAll;
    }

    public FilterRule setSelectPartOrAll(boolean selectPartOrAll) {
        this.selectPartOrAll = selectPartOrAll;
        return this;
    }

    public Set<String> getPackages() {
        return packages;
    }

    public FilterRule setPackages(Set<String> packages) {
        this.packages = packages;
        return this;
    }

    /**
     * 是全选还是全不选
     */
    private boolean selectPartOrAll;
    private Set<String> packages;

    @Override
    public String toString() {
        return "FilterRule{" +
                "selectPartOrAll=" + selectPartOrAll +
                ", packages=" + packages +
                '}';
    }
}
