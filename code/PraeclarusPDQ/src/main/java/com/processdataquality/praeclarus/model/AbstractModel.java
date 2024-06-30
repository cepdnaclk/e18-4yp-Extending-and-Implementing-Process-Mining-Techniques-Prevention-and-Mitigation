package com.processdataquality.praeclarus.model;

import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import com.processdataquality.praeclarus.plugin.uitemplate.PluginUI;

public abstract class AbstractModel extends AbstractPlugin implements Model {
    protected PluginUI _ui;
    protected AbstractModel(){
        super();
    }
    @Override
    public PluginUI getUI() {
        return _ui;
    }
    @Override
    public void setUI(PluginUI ui) {
        _ui = ui;
    }
}
