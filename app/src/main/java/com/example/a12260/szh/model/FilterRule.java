package com.example.a12260.szh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 12260
 * 过滤规则结构体，保存在shared preferences
 */
public class FilterRule {
    public static FilterRule getDefaultRule() {
        FilterRule filterRule = new FilterRule();
        filterRule.setSelectPartOrAll(true);
        filterRule.setPackages(new ArrayList<>());
        return filterRule;
    }

    public boolean isSelectPartOrAll() {
        return selectPartOrAll;
    }

    public FilterRule setSelectPartOrAll(boolean selectPartOrAll) {
        this.selectPartOrAll = selectPartOrAll;
        return this;
    }

    public List<String> getPackages() {
        return packages;
    }

    public FilterRule setPackages(List<String> packages) {
        this.packages = packages;
        return this;
    }

    /**
     * 是全选还是全不选
     */
    private boolean selectPartOrAll;
    private List<String> packages;

}
