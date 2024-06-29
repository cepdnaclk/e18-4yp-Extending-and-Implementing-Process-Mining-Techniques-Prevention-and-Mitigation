package com.processdataquality.praeclarus.node;

import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import tech.tablesaw.api.Table;

public class ModelNode extends Node {
    public ModelNode(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() throws Exception {

    }

//    @Override
//    public Table getOutput() {
//        if (getState() == NodeState.PAUSED) {
//            return detected;
//        }
//
//        return detected;
//    }
}
