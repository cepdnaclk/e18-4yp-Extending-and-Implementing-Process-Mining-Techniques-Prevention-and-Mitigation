/*
 * Copyright (c) 2022 Queensland University of Technology
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

package com.processdataquality.praeclarus.ui.component.dialog;

import com.processdataquality.praeclarus.ui.component.announce.Announcement;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H4;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 * @author Michael Adams
 * @date 27/5/21
 */
public class Questions extends AbstractDialog {
    private Boolean[] listOfResponses;
    private final CompletableFuture<Void> signal = new CompletableFuture<>();

    public Questions(QuestionDialogListener listener, List<List<String>> questionsList, int questionsCount) {

        super("Questionnaire");

        // Get the user response
        listOfResponses = new Boolean[questionsCount];

        // OK Button
        Button ok = new Button("OK", event -> {
            listener.dialogClosed(new QuestionDialogCloseEvent(true));

            close();
        });
        ok.setEnabled(false);
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        int mainIndex = 0;

        // Add question components to the dialog
        for (List<String> entry : questionsList) {
            // Title as h4 tag
            H4 title = new H4(entry.get(0));
            addComponent(title);

            // Loop through the all questions for that title
            for (String val : entry.subList(1, entry.size())) {

                // Question as h5 tag
                H5 question = new H5(val);
                addComponent(question);

                // Selection as True or false
                RadioButtonGroup<String> button = new RadioButtonGroup<>();
                button.setItems("True", "False");
                final Integer index = mainIndex;
                // Add button listener to get response
                button.addValueChangeListener(event -> {
                    // Get the selected value and record them
                    if (button.getValue() == "True") {
                        listOfResponses[index] = true;
                    } else if (button.getValue() == "False") {
                        listOfResponses[index] = false;
                    }
                    // Only if all the answers are given , then show up the ok button
                    if (!Arrays.asList(listOfResponses).contains(null)) {
                        ok.setEnabled(true);
                        signal.complete(null);
                    }
                    
                });

                mainIndex += 1;

                addComponent(button);
            }

        }

        Button cancel = new Button("Cancel", event -> {
            listener.dialogClosed(new QuestionDialogCloseEvent(false));
            close();
        });

        getButtonBar().add(cancel, ok);
    }

    public CompletableFuture<Void> getResults() {
        return signal;
    }

    public Boolean[] getWatchedVariable() {
        return listOfResponses;
    }

}
