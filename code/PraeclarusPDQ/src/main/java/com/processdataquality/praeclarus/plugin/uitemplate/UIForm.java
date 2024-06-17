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

package com.processdataquality.praeclarus.plugin.uitemplate;

import com.vaadin.flow.component.formlayout.FormLayout.FormItem;

import tech.tablesaw.api.Table;

/**
 * @author Michael Adams
 * @date 1/11/21
 */
public class UIForm implements UIComponent {
    private final FormItem form;

    private final FormItem _originalTable;
    private FormItem _updatedTable = null;
    private FormItem _selectedRows = null;
    private boolean _multiSelect = false;

    public UIForm(FormItem table) {
        this.form = new FormItem();
        _originalTable = table;
    }

    public FormItem getTable() { return _originalTable; }


    public void setUpdatedTable(FormItem table) { _updatedTable = table; }

    public FormItem getUpdatedTable() {
        return _updatedTable != null ? _updatedTable : new FormItem();
    }


    public void setSelectedRows(FormItem table) { _selectedRows = table; }

    public FormItem getSelectedRows() {
        return _selectedRows != null ? _selectedRows : new FormItem();
    }


    public boolean isMultiSelect() { return _multiSelect; }

    public void setMultiSelect(boolean b) { _multiSelect = b; }
}
