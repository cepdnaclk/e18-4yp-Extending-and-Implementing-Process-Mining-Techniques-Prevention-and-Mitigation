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

import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import com.processdataquality.praeclarus.suggestions.Suggestions;
import com.processdataquality.praeclarus.ui.component.announce.Announcement;

import com.processdataquality.praeclarus.ui.component.dialog.VisualizationDialog;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.*;

/**
 * A container node for a log data writer
 *
 * @author Michael Adams
 * @date 12/5/21
 */
public class SuggestionsNode extends Node {

    private static final Map<String, String> ROOTCAUSE_MAP = new HashMap<>();
    private static final Map<String, List<String>> MITIGATION_SUGGESTIONS = new HashMap<>();

    private final Map<String, String> RELEVANT_ROOTCAUSE_MAP = new HashMap<>();
    private final Map<String, Integer> ROOTCAUSE_FREQUENCY_MAP = new HashMap<>();


    static {
        ROOTCAUSE_MAP.put("RC5.2", "System designed so as to allow users to overwrite system generated label/value");
        ROOTCAUSE_MAP.put("RC5.4", "System design/implementation lacks suitable data validation to prompt/warn/prohibit user from entering incorrect value");
        ROOTCAUSE_MAP.put("RC10.2", "Different users (process participants) may adopt different conventions when recording data attributes");
        ROOTCAUSE_MAP.put("RC2.1", "Business requirement or traditional auditing requirements");
        ROOTCAUSE_MAP.put("RC3.1", "Different requirements for recording/reporting across different modules of the system, or different systems design (affecting datatype or recording level of date/time attributes)");
        ROOTCAUSE_MAP.put("RC7.1", "Record linkages between systems not established; or, individual systems are process aware but still have difficulty in linking across systems");

        MITIGATION_SUGGESTIONS.put("RC5.2", Arrays.asList(
                "Implement read-only fields for system-generated labels and values that cannot be overwritten by users",
                "Provide a clear warning message if a user attempts to modify a read-only field, explaining why it's not allowed",
                "Allow users to request changes to system-generated data through a separate process (e.g., submitting a change request form) that can be reviewed and approved by authorized personnel"
        ));

        MITIGATION_SUGGESTIONS.put("RC5.4", Arrays.asList(
                "Implement client-side and server-side data validation to check for invalid values and formats",
                "Display clear error messages when a user enters an incorrect value, explaining what's wrong and how to fix it",
                "Provide dropdown lists, calendars, or other input controls to limit user input to valid options",
                "Prohibit users from saving or submitting data until all required fields are filled out correctly"
        ));

        MITIGATION_SUGGESTIONS.put("RC10.2", Arrays.asList(
                "Provide clear guidelines and training on how to consistently record data attributes",
                "Implement data standardization rules in the system to automatically format and structure data attributes",
                "Offer auto-complete or suggestion features to help users select from a predefined list of standardized values",
                "Implement data quality checks to identify and flag inconsistencies in how data attributes are recorded",
                "Provide feedback and coaching to users who consistently record data attributes incorrectly"
        ));

        MITIGATION_SUGGESTIONS.put("RC2.1", Arrays.asList(
                "Review the business requirements and auditing needs to determine if the ability to record multiple values in a single field is truly necessary",
                "Redesign the data capture process to separate these values into distinct attributes, ensuring better data quality and consistency",
                "Implement data validation rules to prevent users from entering multiple values in a single field"
        ));

        MITIGATION_SUGGESTIONS.put("RC3.1", Arrays.asList(
                "Standardize the data recording and reporting requirements across different modules or systems",
                "Ensure that the system design and data types are consistent across all modules or systems",
                "Implement data transformation and integration processes to harmonize the data attributes and their recording levels",
                "Provide training and guidelines to users on the consistent use of date/time attributes across the system"
        ));

        MITIGATION_SUGGESTIONS.put("RC7.1", Arrays.asList(
                "Establish record linkages between the different systems to enable better data integration and process awareness",
                "Implement data matching and reconciliation algorithms to link related records across systems",
                "Ensure that the system design and data models are aligned to facilitate seamless data integration",
                "Provide training and guidelines to users on the importance of consistent data entry and record-keeping across systems"
        ));
    }


    public SuggestionsNode(AbstractPlugin plugin) {
        super(plugin);
    }


    /**
     * Gets incoming data from a predecessor node and writes it to a data 'sink' as
     * defined in this node's plugin
     */
    @Override
    public void run() throws Exception {
        setState(NodeState.EXECUTING);

        Table input = getInputs().get(0);     // a writer node has only one input
        Table output = Table.create("Suggestions to mitigate ").addColumns(
                StringColumn.create("Root Cause ID"),
                StringColumn.create("Root Cause Name"),
                StringColumn.create("Suggestion"));

        Integer val = input.rowCount();
        Announcement.show(val.toString());

        for (int i = 0; i < input.rowCount(); i++) {
            String rootCauseID = input.stringColumn(0).get(i);
            ROOTCAUSE_FREQUENCY_MAP.put(rootCauseID, ROOTCAUSE_FREQUENCY_MAP.getOrDefault(rootCauseID, 0) + 1);
        }

        for(int i=0;i<input.rowCount();i++){
            // input.stringColumn(0).get(i)
            String rootCauseID = input.stringColumn(0).get(i);
            String rootCauseName = ROOTCAUSE_MAP.getOrDefault(rootCauseID, "No suggestion available");
            RELEVANT_ROOTCAUSE_MAP.put(rootCauseID, rootCauseName);

            output.stringColumn(0).append(rootCauseID);
            output.stringColumn(1).append(rootCauseName);
            List<String> suggestions = MITIGATION_SUGGESTIONS.getOrDefault(rootCauseID, new ArrayList<>());
            String suggestionString = String.join(", ", suggestions);
            output.stringColumn(2).append(suggestionString);


        }

        VisualizationDialog visualizationDialog = new VisualizationDialog(RELEVANT_ROOTCAUSE_MAP, MITIGATION_SUGGESTIONS, ROOTCAUSE_FREQUENCY_MAP);
        visualizationDialog.open();
        // ((Suggestions) getPlugin()).write(input, getAuxiliaryInputs());
        setOutput(output);

        setState(NodeState.COMPLETED);
    }

}
