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

package com.processdataquality.praeclarus.plugin;

import com.processdataquality.praeclarus.action.AbstractAction;
import com.processdataquality.praeclarus.model.AbstractModel;
import com.processdataquality.praeclarus.pattern.AbstractDataPattern;
import com.processdataquality.praeclarus.reader.AbstractDataReader;
import com.processdataquality.praeclarus.writer.AbstractDataWriter;
import com.processdataquality.praeclarus.rootCause.AbstractRootCause;
import com.processdataquality.praeclarus.suggestions.AbstractSuggestions;

/**
 * @author Michael Adams
 * @date 29/4/21
 */
public class PluginService {

    private static final PluginFactory<AbstractDataReader> READER_FACTORY = new PluginFactory<>(
            AbstractDataReader.class);
    private static final PluginFactory<AbstractModel> MODEL_FACTORY = new PluginFactory<>(
            AbstractModel.class);
    private static final PluginFactory<AbstractDataWriter> WRITER_FACTORY = new PluginFactory<>(
            AbstractDataWriter.class);
    private static final PluginFactory<AbstractDataPattern> PATTERN_FACTORY = new PluginFactory<>(
            AbstractDataPattern.class);
    private static final PluginFactory<AbstractAction> ACTION_FACTORY = new PluginFactory<>(AbstractAction.class);
    private static final PluginFactory<AbstractRootCause> ROOT_FACTORY = new PluginFactory<>(AbstractRootCause.class);
    private static final PluginFactory<AbstractSuggestions> SUGGESTIONS_FACTORY = new PluginFactory<>(
            AbstractSuggestions.class);

    public static PluginFactory<AbstractDataReader> readers() {
        return READER_FACTORY;
    }
    public static PluginFactory<AbstractModel> modelAnalyzers() {
        return MODEL_FACTORY;
    }
    public static PluginFactory<AbstractDataWriter> writers() {
        return WRITER_FACTORY;
    }

    public static PluginFactory<AbstractDataPattern> patterns() {
        return PATTERN_FACTORY;
    }

    public static PluginFactory<AbstractAction> actions() {
        return ACTION_FACTORY;
    }

    public static PluginFactory<AbstractRootCause> rootCauseAnalyzers() {
        return ROOT_FACTORY;
    }
    
    public static PluginFactory<AbstractSuggestions> suggestionsWriters() {
        return SUGGESTIONS_FACTORY;
    }

    public static PluginFactory<? extends AbstractPlugin> factory(Class<? extends AbstractPlugin> clazz) {
        if (AbstractDataReader.class.isAssignableFrom(clazz)) {
            return READER_FACTORY;
        }
        if (AbstractModel.class.isAssignableFrom(clazz)) {
            return MODEL_FACTORY;
        }
        if (AbstractDataWriter.class.isAssignableFrom(clazz)) {
            return WRITER_FACTORY;
        }
        if (AbstractDataPattern.class.isAssignableFrom(clazz)) {
            return PATTERN_FACTORY;
        }
        if (AbstractAction.class.isAssignableFrom(clazz)) {
            return ACTION_FACTORY;
        }
        if (AbstractRootCause.class.isAssignableFrom(clazz)) {
            return ROOT_FACTORY;
        }
        if (AbstractSuggestions.class.isAssignableFrom(clazz)) {
            return SUGGESTIONS_FACTORY;
        }
        return null;
    }
}
