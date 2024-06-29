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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

/**
 * @author Michael Adams
 * @date 27/5/21
 */
public class ModelDialog extends Dialog {

//    private final Button ok = new Button("OK", event -> close());
//    private final Button cancel = new Button("Cancel", event -> close());
private final CompletableFuture<Void> signal = new CompletableFuture<>();

    private final Button ok = new Button("OK", event -> {
    signal.complete(null);
    close();
});
    private final Button cancel = new Button("Cancel", event -> {
        signal.completeExceptionally(new RuntimeException("Dialog cancelled"));
        close();
    });

    private File uploadedFile;


    public ModelDialog() {
        super();
        setHeaderTitle("Follow PROM tool and export HTML file by importing your data set there.");
        addInstruction();
        addFileUpload();
        addButtons();
    }

    public Button getOKButton() {
        return ok;
    }

    public Button getCancelButton() {
        return cancel;
    }

    public File getUploadedFile() {
        return uploadedFile;
    }

    private void addInstruction() {
        Label instruction = new Label("Please follow the PROM tool to export an HTML file by importing your data set there. Then upload that exported HTML file here.");
        add(instruction);
    }

    public String getUploadedFilePath() {
        return (uploadedFile != null) ? uploadedFile.getAbsolutePath() : null;
    }


    private void addFileUpload() {
        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes("text/html");
        upload.addSucceededListener(event -> {
            try {
                File tempFile = File.createTempFile("uploaded-", ".html");
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    os.write(fileBuffer.getInputStream().readAllBytes());
                    uploadedFile = tempFile;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        add(upload);
    }

    private void addButtons() {
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(cancel, ok);
    }

    public CompletableFuture<Void> getResults() {
        return signal;
    }
//
//    public Boolean[] getWatchedVariable() {
//        return listOfResponses;
//    }

}
