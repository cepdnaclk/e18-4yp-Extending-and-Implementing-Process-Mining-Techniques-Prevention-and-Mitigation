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

import tech.tablesaw.api.Table;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

/**
 * A container node for a log data reader
 *
 * 
 */
public class RootCauseAnalyzerNode extends Node {

        private List<List<String>> questionsList = new ArrayList<>();
        private Table detected;

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
                //Get root cause plugin
                AbstractRootCause questions = (AbstractRootCause) getPlugin();

                try {
                        //Common Questions for each imperfection pattern
                        // Distorted Label
                        if (questions.getName().compareTo("Distorted Label") == 0) {
                                List<String> QList_DataEntry = new ArrayList<>();

                                QList_DataEntry.add("Data Entry Interface");
                                QList_DataEntry.add("Do your IT systems allow users to modify or overwrite automatically generated activity labels?");
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
                                QList_Manual.add("Is any activity label information entered manually during your data collection process?");
                                QList_Manual.add("Do different process participants use slightly different terminology or abbreviations for the same activity?");
                                QList_Manual.add("Do your data entry tools have any features to suggest or enforce a standardized vocabulary for activity labels?");
                                questionsList.add(QList_Manual);

                        }
                        // Polluted Label
                        else if (questions.getName().compareTo("Polluted Label") == 0) {
                                List<String> QList_DataField = new ArrayList<>();
                                QList_DataField.add( "Data Field Design");
                                QList_DataField.add(
                                                "Do your IT systems allow for recording multiple values within a single event attribute field?");
                                QList_DataField.add("How is this functionality justified?");
                                QList_DataField.add(
                                                "Are there any plans to redesign the data capture process to separate these values into distinct attributes?");
                                questionsList.add(QList_DataField);
                                                List<String> QList_Manual = new ArrayList<>();
                                QList_Manual.add( "Manual Data Entry ");
                                QList_Manual.add(
                                                "Is any information in the polluted attribute fields entered manually during data collection? "      );
                                QList_Manual.add(
                                                "Do different process participants use varying formats or conventions when entering data into these fields?");
                                QList_Manual.add(
                                                "Do your data entry tools have any features to guide users towards consistent formatting in these fields?");
                                questionsList.add(QList_Manual);

                        }
                        // Synonymous Label
                        else if (questions.getName().compareTo("Synonymous Label") == 0) {
                                List<String> QList_Multiple = new ArrayList<>();
                                QList_Multiple.add("Multiple Systems");
                                QList_Multiple.add(
                                                "Do you use different IT systems to capture data for the process you're analyzing?");
                                QList_Multiple.add(
                                                "Do these systems use the same terminology for process steps and attributes?"
                                                );
                                List<String> QList_Config = new ArrayList<>();
                                QList_Config.add( "System Configuration");
                                QList_Config.add(
                                                "Do your IT systems allow users to customize how they record data (e.g., labels, attributes)?"
                                                );
                                QList_Config.add(
                                                "Are there any departmental or user-level configurations that might lead to variations in how the same data is recorded?"
                                                );
                                List<String> QList_Data = new ArrayList<>();
                                QList_Data.add( "Data Transformation");
                                QList_Data.add(
                                                "Do you have any data transformation processes (ETL) before using the data for process mining?"
                                               );
                                QList_Data.add(
                                                "Do these data transformation tools have functionalities to modify or standardize labels or attribute values?"
                                               );
                                QList_Data.add(
                                                "Are these tools configured to handle synonymous labels?");
                                QList_Data.add("How do they handle them (e.g., mapping, merging)?");
                                QList_Data.add(
                                                "Are data curators or analysts involved in overseeing or configuring these transformations?"
                                               );
                                QList_Data.add(
                                                "What training or guidelines are in place to ensure consistent handling of synonymous labels?"
                                                );

                        }

                        Announcement.show("Please answer to the below questions to proceed !!!");
                        
                        // setState(NodeState.COMPLETED);
                } catch (Exception e) {

                        // see if file was loaded previously, then stored
                        String tableID = getTableID();
                        if (!(tableID == null || tableID.isEmpty())) {
                                loadOutput(tableID); // load from repo

                                // setState not used here because loadOutput() above sets it to completed
                                announceStateChange();
                        } else
                                throw new ReaderException(e.getMessage(), e.getCause());
                }
                if (getState() == NodeState.UNSTARTED) {

                        // load plugin with all incoming plugins' aux datasets
                        questions.getAuxiliaryDatasets().putAll(getAuxiliaryInputs());

                        setState(NodeState.EXECUTING);

                        //Build the Questions dialog and pass the listener and questions list
                        Questions dialog = new Questions(e -> {
                                if (e.successful) {}
                        }, questionsList);

                        //Open the dialog 
                        dialog.open();

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
