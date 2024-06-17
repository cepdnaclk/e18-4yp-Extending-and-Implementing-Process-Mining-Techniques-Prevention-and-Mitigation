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
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Michael Adams
 * @date 27/5/21
 */
public class Questions extends AbstractDialog {

    public Questions(UploadDialogListener listener, List<List<String>> questionsList) {
        super("Questionnaire");
        MemoryBuffer buffer = new MemoryBuffer();
        Div div = new Div();
        // RadioButtonGroup button = new RadioButtonGroup<>();
        // button.setItems("True", "False");
        // H3 title;
        // H5 question;
        // RadioButtonGroup button;
        
        // Map <H5, RadioButtonGroup<String>> x = new HashMap<>();
        for (List<String> entry : questionsList){
            H4 title = new H4(entry.get(0));
            addComponent(title);

            for(String val : entry.subList(1, entry.size())){
                H5 question = new H5(val);
                addComponent(question);
                RadioButtonGroup<String> button = new RadioButtonGroup<>();
                button.setItems("True", "False");
                addComponent(button);
            }
            
               

        }

        
        // for (Map.Entry<H5, RadioButtonGroup<String>> entry : x.entrySet()) {
        //     if(entry.getValue()==null){
        //         addComponent(entry.getKey());

        //     }
        //     else{
        //         addComponent(entry.getKey());
        //         addComponent(entry.getValue());

        //     }
            

        // }

        // H5 Q2 = new H5(
        //         "Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?");
        // Span Q3 = new Span(
        //         "Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?");

        // TextField Q1 = new TextField(
        //         "Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?");

        // addComponent(Q1);
        // addComponent(Q2);
        // addComponent(button);
        // private TextField firstname;

        Button ok = new Button("OK", event -> {
            listener.dialogClosed(new UploadDialogCloseEvent(true,
                    buffer.getInputStream(), buffer.getFileName()));
            close();
        });
        ok.setEnabled(false);
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Upload upload = new Upload(buffer);
        Div outputMsg = new Div();

        // upload.setAcceptedFileTypes(mimeDescriptors);
        upload.addSucceededListener(event -> ok.setEnabled(true));

        // upload.getElement().addEventListener("file-remove",
        // event -> outputMsg.removeAll());

        // upload.addFileRejectedListener(event -> {
        // outputMsg.removeAll();
        // HtmlComponent p = new HtmlComponent(Tag.P);
        // p.getElement().setText(event.getErrorMessage());
        // outputMsg.add(p);
        // ok.setEnabled(false);
        // });

        Button cancel = new Button("Cancel", event -> {
            listener.dialogClosed(new UploadDialogCloseEvent(false, null, null));
            close();
        });

        // addComponent(upload, outputMsg);
        getButtonBar().add(cancel, ok);
    }

}
