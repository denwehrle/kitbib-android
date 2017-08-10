package com.denwehrle.kitbib.data.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dennis Wehrle
 */
public enum BibSummaryItemState {

    MoreThanTenDays(0),
    LessThanTenDays(1),
    Overdue(2),
    Ordered(3),
    Error(4);

    private static final Map<Integer, BibSummaryItemState> lookup = new HashMap<>();

    static {
        for (BibSummaryItemState b : EnumSet.allOf(BibSummaryItemState.class))
            lookup.put(b.getCode(), b);
    }

    private int code;

    BibSummaryItemState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static BibSummaryItemState get(int code) {
        return lookup.get(code);
    }
}