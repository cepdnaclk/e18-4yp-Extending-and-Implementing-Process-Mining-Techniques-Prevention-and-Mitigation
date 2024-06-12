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
import com.processdataquality.praeclarus.pattern.AbstractDataPattern;
import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import com.processdataquality.praeclarus.plugin.uitemplate.PluginUI;
import com.processdataquality.praeclarus.plugin.uitemplate.UIContainer;
import com.processdataquality.praeclarus.plugin.uitemplate.UIForm;
import com.processdataquality.praeclarus.plugin.uitemplate.UITable;
import com.processdataquality.praeclarus.reader.DataReader;
import com.processdataquality.praeclarus.rootCause.AbstractRootCause;
import com.processdataquality.praeclarus.ui.component.announce.Announcement;
import com.processdataquality.praeclarus.ui.component.plugin.PluginUIDialog;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.processdataquality.praeclarus.ui.component.OutputPanel;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.RollingColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.swing.JOptionPane;

/**
 * A container node for a log data reader
 *
 * 
 */
public class RootCauseAnalyzerNode extends Node {

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
        
        AbstractRootCause questions = (AbstractRootCause) getPlugin();

        try {
           
            Announcement.success(questions.getName());
            
            if(questions.getName().compareTo("Distorted Label")==0){
                detected = Table.create("Questions").addColumns(StringColumn.create("Category"),
                        StringColumn.create("Questions"),
                        BooleanColumn.create("Answers"));

                detected.stringColumn(0).append("Data Entry Interface");
                detected.stringColumn(1).append(
                        "Do your IT systems allow users to modify or overwrite automatically generated activity labels?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append("How is this functionality justified?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?");
                detected.booleanColumn(2).append(false);

                detected.stringColumn(0).append("System Configuration");
                detected.stringColumn(1).append("Do you have different IT systems in your process?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Do these systems use a standardized vocabulary or controlled list for activity labels?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "How are inconsistencies between different systems managed during data integration?");
                detected.booleanColumn(2).append(false);

                detected.stringColumn(0).append("Manual Data Entry");
                detected.stringColumn(1).append(
                        "Is any activity label information entered manually during your data collection process?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1)
                        .append("Do different process participants use slightly different terminology or abbreviations for the same activity?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Do your data entry tools have any features to suggest or enforce a standardized vocabulary for activity labels?");
                detected.booleanColumn(2).append(false);
            }

            else if (questions.getName().compareTo("Polluted Label")==0 ) {
                detected = Table.create("Questions").addColumns(StringColumn.create("Category"),
                        StringColumn.create("Questions"),
                        BooleanColumn.create("Answers"));

                detected.stringColumn(0).append("Data Field Design");
                detected.stringColumn(1).append(
                        "Do your IT systems allow for recording multiple values within a single event attribute field?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append("How is this functionality justified?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Are there any plans to redesign the data capture process to separate these values into distinct attributes?");
                detected.booleanColumn(2).append(false);

                detected.stringColumn(0).append("Manual Data Entry ");
                detected.stringColumn(1).append("Is any information in the polluted attribute fields entered manually during data collection? ");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Do different process participants use varying formats or conventions when entering data into these fields?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Do your data entry tools have any features to guide users towards consistent formatting in these fields?");
                detected.booleanColumn(2).append(false);

                
            }

            else if (questions.getName().compareTo("Synonymous Label")==0 ) {
                detected = Table.create("Questions").addColumns(StringColumn.create("Category"),
                        StringColumn.create("Questions"),
                        BooleanColumn.create("Answers"));

                detected.stringColumn(0).append("Multiple Systems");
                detected.stringColumn(1).append(
                        "Do you use different IT systems to capture data for the process you're analyzing?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append("Do these systems use the same terminology for process steps and attributes?");
                detected.booleanColumn(2).append(false);
               

                detected.stringColumn(0).append("System Configuration");
                detected.stringColumn(1).append("Do your IT systems allow users to customize how they record data (e.g., labels, attributes)?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Are there any departmental or user-level configurations that might lead to variations in how the same data is recorded?");
                detected.booleanColumn(2).append(false);
                

                detected.stringColumn(0).append("Data Transformation");
                detected.stringColumn(1).append(
                        "Do you have any data transformation processes (ETL) before using the data for process mining?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1)
                        .append("Do these data transformation tools have functionalities to modify or standardize labels or attribute values?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Are these tools configured to handle synonymous labels?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "How do they handle them (e.g., mapping, merging)?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "Are data curators or analysts involved in overseeing or configuring these transformations?");
                detected.booleanColumn(2).append(false);
                detected.stringColumn(0).append("");
                detected.stringColumn(1).append(
                        "What training or guidelines are in place to ensure consistent handling of synonymous labels?");
                detected.booleanColumn(2).append(false);
            }
            
            
            
            Announcement.success("Please answer to the below questions to proceed !!!");
            // FormItem item = new FormItem();
            // item.add("item1");
            // UIForm form = new UIForm(item);
            // // UIForm table = new UIForm(detected);
            // setState(NodeState.PAUSED);
            // UIContainer tableLayout = new UIContainer();
            // PluginUIDialog x = new PluginUIDialog(ui, null);
            // x.setVisible(true);
            // tableLayout.add(form);
            // ui.add(tableLayout);
            // updateUI(ui);
            // // ui.add(x);
            // questions.setUI(ui);

            setOutput(detected);
            setState(NodeState.COMPLETED);
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
