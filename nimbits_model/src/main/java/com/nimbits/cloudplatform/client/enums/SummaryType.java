package com.nimbits.cloudplatform.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:54 AM
 */
public enum SummaryType {
    average(0, "Average"),
    standardDeviation(1, "Standard Deviation"),
    skewness(2, "Skewness"),
    sum(3, "Sum"),
    variance(4, "Variance"),
    max(6, "Max"),
    min(7, "Min"),
    delta(8, "Delta")


    ;

    private static final Map<Integer, SummaryType> lookup = new HashMap<Integer, SummaryType>(8);

    static {
        for (SummaryType s : EnumSet.allOf(SummaryType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private SummaryType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public static SummaryType get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }
}
