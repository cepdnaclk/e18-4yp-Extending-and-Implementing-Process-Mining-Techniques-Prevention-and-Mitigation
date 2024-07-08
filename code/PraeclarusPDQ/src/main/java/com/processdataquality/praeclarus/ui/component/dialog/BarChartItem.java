package com.processdataquality.praeclarus.ui.component.dialog;

public class BarChartItem {
    private final String rootCause;
    private final int frequency;


    public BarChartItem(String rootCause, int frequency) {
        this.rootCause = rootCause;
        this.frequency = frequency;

    }

    public String getRootCause() {
        return rootCause;
    }

    public int getFrequency() {
        return frequency;
    }

}
