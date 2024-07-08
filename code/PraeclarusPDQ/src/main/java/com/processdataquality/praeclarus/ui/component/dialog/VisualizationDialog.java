package com.processdataquality.praeclarus.ui.component.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

import tech.tablesaw.api.Table;

import java.util.*;
import java.util.stream.Collectors;

public class VisualizationDialog extends Dialog {
    private final Button ok = new Button("OK", event -> close());
    private final Button cancel = new Button("Cancel", event -> close());

    public VisualizationDialog(Map<String, String> rootCausesMapResults, Map<String, List<String>> mitigationSuggestions, Map<String, Integer> rootCauseFrequencyMap) {
        super();
        setHeaderTitle("Mitigation Suggestions Visualization");
        setWidth("70%");
        // Add instruction
        addInstruction();

        // Create layout for tabs
        VerticalLayout layout = new VerticalLayout();
        add(layout);

        // Add tabs for tree and bar chart
        Tabs tabs = new Tabs();
        Tab treeTab = new Tab("Root Cause Tree");
        Tab barChartTab = new Tab("Pareto Chart");
        tabs.add(treeTab, barChartTab);

        VerticalLayout treeLayout = new VerticalLayout();
        VerticalLayout barChartLayout = new VerticalLayout();

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == treeTab) {
                layout.removeAll();
                layout.add(treeLayout);
            } else if (event.getSelectedTab() == barChartTab) {
                layout.removeAll();
                layout.add(barChartLayout);
            }
        });

        // Add tree visualization
        addTreeVisualization(treeLayout, rootCausesMapResults, mitigationSuggestions);

        // Add Pareto chart visualization
        addParetoChart(barChartLayout, rootCausesMapResults, rootCauseFrequencyMap);

        layout.add(tabs, treeLayout, barChartLayout);

        // Add buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(ok, cancel);
        layout.add(buttonLayout);
    }

    private void addInstruction() {
        add(new Label("Visualization of root causes and their mitigation suggestions."));
    }

    private void addTreeVisualization(VerticalLayout layout, Map<String, String> rootCausesMapResults, Map<String, List<String>> mitigationSuggestions) {
        TreeGrid<String> treeGrid = new TreeGrid<>();
        TreeData<String> treeData = new TreeData<>();


        for (Map.Entry<String, String> entry : rootCausesMapResults.entrySet()) {
            String rootCause = entry.getKey();
            String rootCauseName = entry.getValue();

            String rootCauseWithName = rootCause + " - " + rootCauseName;

            treeData.addItem(null, rootCauseWithName);

            List<String> suggestions = mitigationSuggestions.getOrDefault(rootCause, new ArrayList<>());
            for (String suggestion : suggestions) {
                treeData.addItem(rootCauseWithName, suggestion);
            }
        }

        TreeDataProvider<String> dataProvider = new TreeDataProvider<>(treeData);
        treeGrid.setDataProvider(dataProvider);
        treeGrid.addHierarchyColumn(String::toString).setHeader("Root Cause / Suggestion");

        layout.add(treeGrid);
    }


    private void addParetoChart(VerticalLayout layout, Map<String, String> rootCausesMapResults, Map<String, Integer> rootCauseFrequencyMap) {
        BarChart barChart = new BarChart(rootCauseFrequencyMap);
        layout.add(barChart);
    }
}

//class FrequencyCumulativePercentageChart extends VerticalLayout {
//    public FrequencyCumulativePercentageChart(Map<String, Integer> rootCauseFrequencyMap) {
//        // Create a grid to display the chart
//        Grid<FrequencyChartItem> grid = new Grid<>(FrequencyChartItem.class);
//        grid.addColumn(FrequencyChartItem::getRootCause).setHeader("Root Cause");
//        grid.addColumn(FrequencyChartItem::getFrequency).setHeader("Frequency");
//        grid.addColumn(FrequencyChartItem::getCumulativePercentage).setHeader("Cumulative %");
//
//        List<FrequencyChartItem> chartData = new ArrayList<>();
//
//        // Calculate the total frequency and sort the root causes by frequency
//        int totalFrequency = rootCauseFrequencyMap.values().stream().mapToInt(Integer::intValue).sum();
//        List<Map.Entry<String, Integer>> sortedEntries = rootCauseFrequencyMap.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                .collect(Collectors.toList());
//
//        // Populate the chart data
//        double cumulativeFrequency = 0.0;
//        for (Map.Entry<String, Integer> entry : sortedEntries) {
//            String rootCause = entry.getKey();
//            int frequency = entry.getValue();
//            cumulativeFrequency += frequency;
//            double cumulativePercentage = (cumulativeFrequency / totalFrequency) * 100;
//            chartData.add(new FrequencyChartItem(rootCause, frequency, cumulativePercentage));
//        }
//
//        grid.setItems(chartData);
//        add(grid);
//    }
//}



class BarChart extends VerticalLayout {
    public BarChart( Map<String, Integer> rootCauseFrequencyMap) {
        Grid<BarChartItem> grid = new Grid<>(BarChartItem.class);
        grid.addColumn(BarChartItem::getRootCause).setHeader("Root Cause");
        grid.addColumn(BarChartItem::getFrequency).setHeader("Frequency");

        List<BarChartItem> barChartData = new ArrayList<>();

        rootCauseFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    String rootCause = entry.getKey();
                    int frequency = entry.getValue();
                    barChartData.add(new BarChartItem(rootCause, frequency));
                });

        grid.setItems(barChartData);
        add(grid);
    }
}
