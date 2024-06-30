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

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

/**
 * A container node for a log data writer
 *
 * @author Michael Adams
 * @date 12/5/21
 */
public class SuggestionsNode extends Node {

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
        Table output = Table.create("Suggestions to mitigate ").addColumns(StringColumn.create("Root Cause"),
                                        StringColumn.create("Suggestion"));
        
        Integer val = input.rowCount();
        Announcement.show(val.toString());
  
        for(int i=1;i<input.rowCount();i++){
            // input.stringColumn(0).get(i)
            String x = input.stringColumn(0).get(i);
            output.stringColumn(0).append(x);
            output.stringColumn(1).append(x);

        }
        // ((Suggestions) getPlugin()).write(input, getAuxiliaryInputs());
        setOutput(output);

        setState(NodeState.COMPLETED);
    }
    
}
