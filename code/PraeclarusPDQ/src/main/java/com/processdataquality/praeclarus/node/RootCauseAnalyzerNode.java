/*
 * Copyright (c) 2021-2022 Queensland University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.processdataquality.praeclarus.node;

import com.processdataquality.praeclarus.exception.ReaderException;
import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import com.processdataquality.praeclarus.plugin.uitemplate.PluginUI;
import com.processdataquality.praeclarus.rootCause.AbstractRootCause;
import com.processdataquality.praeclarus.ui.component.announce.Announcement;
import com.processdataquality.praeclarus.ui.component.dialog.Questions;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;

/**
 * A container node for a log data reader
 *
 * 
 */
public class RootCauseAnalyzerNode extends Node {

        private List<List<String>> questionsList = new ArrayList<>();
        private Table detected;
        private int questionsCount = 0;

        public RootCauseAnalyzerNode(AbstractPlugin plugin) {
                super(plugin);
        }

        /**
         * Reads log data from the plugin contained by this node and sets the data as
         * the
         * node's output
         */
        @Override
        public void run() throws Exception {
                AbstractRootCause questions = (AbstractRootCause) getPlugin();
                questions.getInputs().addAll(getInputs());
                Table master = getInputs().get(0);

                Announcement.success(master.getString(1, 0));

                Announcement.success(questions.getName());

                Integer[] RootCauseMatrix = new Integer[9];

                int[] mask = new int[9];
                String[] RC = new String[9];

                // Distorted
                // [Q1 Q2 Q3 Q4 Q5 Q6 Q7 Q8 Q9]
                // RC5.2 [1 1 N N N N N N N]
                // RC5.4 [1 N 0 N N N N N N]
                // RC3.1 [N N N 1 0 0 N N N]
                // RC10.2[N N N N N N 1 1 N]
                // RC5.2 [N N N N N N 1 1 N]
                // RC5.4 [N N N N N N 1 N 0]

                if (questions.getName().compareTo("Distorted Label") == 0) {
                        List<String> QList_DataEntry = new ArrayList<>();
                        QList_DataEntry.add("Data Entry Interface");
                        QList_DataEntry.add(
                                        "Do your IT systems allow users to modify or overwrite automatically generated activity labels?");
                        QList_DataEntry.add("How is this functionality justified?");
                        QList_DataEntry.add(
                                        "Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?");
                        questionsList.add(QList_DataEntry);

                        List<String> QList_Config = new ArrayList<>();
                        QList_Config.add("System Configuration");
                        QList_Config.add("Do you have different IT systems in your process?");
                        QList_Config.add(
                                        "Do these systems use a standardized vocabulary or controlled list for activity labels?");
                        QList_Config.add(
                                        "How are inconsistencies between different systems managed during data integration?");
                        questionsList.add(QList_Config);

                        List<String> QList_Manual = new ArrayList<>();

                        QList_Manual.add("Manual Data Entry");
                        QList_Manual.add(
                                        "Is any activity label information entered manually during your data collection process?");
                        QList_Manual.add(
                                        "Do different process participants use slightly different terminology or abbreviations for the same activity?");
                        QList_Manual.add(
                                        "Do your data entry tools have any features to suggest or enforce a standardized vocabulary for activity labels?");
                        questionsList.add(QList_Manual);

                        questionsCount = 9;
                        RootCauseMatrix[0] = 0b110000000;
                        RootCauseMatrix[1] = 0b101000000;
                        RootCauseMatrix[2] = 0b000111000;
                        RootCauseMatrix[3] = 0b000000110;
                        RootCauseMatrix[4] = 0b000000110;
                        RootCauseMatrix[5] = 0b000000101;
                        mask[1] = 0b1000000;
                        mask[2] = 0b11000;
                        mask[5] = 0b1;
                        RC[0] = "RC5.2";
                        RC[1] = "RC5.4";
                        RC[2] = "RC3.1";
                        RC[3] = "RC10.2";
                        RC[4] = "RC5.2";
                        RC[5] = "RC5.4";

                }

                else if (questions.getName().compareTo("Polluted Label") == 0) {

                        List<String> QList_DataField = new ArrayList<>();
                        QList_DataField.add("Data Field Design");
                        QList_DataField.add(
                                        "Do your IT systems allow for recording multiple values within a single event attribute field?");
                        QList_DataField.add("How is this functionality justified?");
                        QList_DataField.add(
                                        "Are there any plans to redesign the data capture process to separate these values into distinct attributes?");
                        questionsList.add(QList_DataField);

                        List<String> QList_Manual = new ArrayList<>();
                        QList_Manual.add("Manual Data Entry ");
                        QList_Manual.add(
                                        "Is any information in the polluted attribute fields entered manually during data collection? ");
                        QList_Manual.add(
                                        "Do different process participants use varying formats or conventions when entering data into these fields?");
                        QList_Manual.add(
                                        "Do your data entry tools have any features to guide users towards consistent formatting in these fields?");
                        questionsList.add(QList_Manual);
                        questionsCount = 6;

                        RootCauseMatrix[0] = 0b101000; // RC2.1
                        RootCauseMatrix[1] = 0b000111; // RC10.2

                        mask[0] = 0b001000;
                        mask[1] = 0b000001;
                        RC[0] = "RC2.1";
                        RC[1] = "RC10.2";

                }

                else if (questions.getName().compareTo("Synonymous Label") == 0) {

                        List<String> QList_Multiple = new ArrayList<>();
                        QList_Multiple.add("Multiple Systems");
                        QList_Multiple.add(
                                        "Do you use different IT systems to capture data for the process you're analyzing?");
                        QList_Multiple.add(
                                        "Do these systems use the same terminology for process steps and attributes?");
                        questionsList.add(QList_Multiple);

                        List<String> QList_Config = new ArrayList<>();
                        QList_Config.add("System Configuration");
                        QList_Config.add(
                                        "Do your IT systems allow users to customize how they record data (e.g., labels, attributes)?");
                        QList_Config.add(
                                        "Are there any departmental or user-level configurations that might lead to variations in how the same data is recorded?");
                        questionsList.add(QList_Config);

                        List<String> QList_Data = new ArrayList<>();
                        QList_Data.add("Data Transformation");
                        QList_Data.add(
                                        "Do you have any data transformation processes (ETL) before using the data for process mining?");
                        QList_Data.add(
                                        "Do these data transformation tools have functionalities to modify or standardize labels or attribute values?");
                        QList_Data.add(
                                        "Are these tools configured to handle synonymous labels?");
                        QList_Data.add("How do they handle them (e.g., mapping, merging)?");
                        QList_Data.add(
                                        "Are data curators or analysts involved in overseeing or configuring these transformations?");
                        QList_Data.add(
                                        "What training or guidelines are in place to ensure consistent handling of synonymous labels?");
                        questionsList.add(QList_Data);
                        questionsCount = 10;

                        RootCauseMatrix[0] = 0b1111000000; // RC3.1
                        RootCauseMatrix[1] = 0b0000111010; // RC7.1

                        mask[0] = 0b0100000000;

                        RC[0] = "RC3.1";
                        RC[1] = "RC7.1";

                }

                Announcement.success("Please answer to the below questions to proceed !!!");
                
                if (getState() == NodeState.UNSTARTED) {

                        // load plugin with all incoming plugins' aux datasets
                        questions.getAuxiliaryDatasets().putAll(getAuxiliaryInputs());

                        setState(NodeState.EXECUTING);

                        Announcement.show("Please answer to the below questions to proceed !!!");

                        // Build the Questions dialog and pass the listener and questions list
                        Questions dialog = new Questions(e -> {
                                if (e.successful) {

                                }
                        }, questionsList, questionsCount);

                        // Open the dialog
                        dialog.open();

                        detected = Table.create("Questions").addColumns(StringColumn.create("Category"),
                                        StringColumn.create("Questions"), BooleanColumn.create("Answers"));
                        StringBuilder binaryString = new StringBuilder();
                        
                        Thread waiterThread = new Thread(() -> {
                                try {
                                        System.out.println("Waiting for variable to change...");
                                        dialog.getResults().thenRun(() -> {
                                                System.out.println("Variable changed! New value: "
                                                                + dialog.getWatchedVariable());

                                                for (List<String> list : questionsList) {

                                                        for (String var : list.subList(1, list.size())) {
                                                                detected.stringColumn(0).append(list.get(0));
                                                                detected.stringColumn(1).append(var);

                                                        }

                                                }
                                                int index = 0;
                                                for (Boolean answer : dialog.getWatchedVariable()) {
                                                        binaryString.append(answer ? 1 : 0);
                                                        detected.booleanColumn(2).append(answer);
                                                }
                                                for (Integer entry : RootCauseMatrix) {

                                                        Integer binaryNumber = Integer.parseInt(binaryString.toString(),
                                                                        2);
                                                        binaryNumber = binaryNumber ^ mask[index];
                                                        Integer result = (binaryNumber & entry);

                                                        if (result.equals(entry)) {

                                                                Announcement.show(
                                                                                "Root Cause Detected!! - " + RC[index]);

                                                                // MessageDialog msg = new MessageDialog("Detected Root
                                                                // Cause!! - "+RC[index]);
                                                                // msg.addConfirmButton("OK");
                                                                // msg.open();

                                                        }
                                                        index++;

                                                }

                                        }).get(); // Block until the variable changes
                                        setOutput(detected);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        });
                        waiterThread.start();

                        setOutput(detected);

                        setState(NodeState.COMPLETED);

                }

        }

        @Override
        public Table getOutput() {
                if (getState() == NodeState.PAUSED) {
                        return detected;
                }

                return detected;
        }

        public void updateUI(PluginUI ui) {
                ((AbstractRootCause) getPlugin()).setUI(ui);

        }

}