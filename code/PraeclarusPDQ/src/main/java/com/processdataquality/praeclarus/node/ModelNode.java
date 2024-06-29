package com.processdataquality.praeclarus.node;

import com.processdataquality.praeclarus.model.AbstractModel;
import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import com.processdataquality.praeclarus.rootCause.AbstractRootCause;
import com.processdataquality.praeclarus.service.HtmlParserService;
import com.processdataquality.praeclarus.ui.component.dialog.ModelDialog;
import com.processdataquality.praeclarus.ui.component.dialog.Questions;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModelNode extends Node {

    private Table modelData;
    public ModelNode(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() throws Exception {

        AbstractModel model = (AbstractModel) getPlugin();


        if (getState() == NodeState.UNSTARTED) {
            // load plugin with all incoming plugins' aux datasets
            model.getAuxiliaryDatasets().putAll(getAuxiliaryInputs());

            setState(NodeState.EXECUTING);

            ModelDialog modelDialog = new ModelDialog();
            modelDialog.open();

            CompletableFuture<Void> dialogFuture = modelDialog.getResults();

            SecurityContext context = SecurityContextHolder.getContext();

            modelData = Table.create("Model Data").addColumns(StringColumn.create("Class Name"),
                    StringColumn.create("Occurrences"));

            Thread waiterThread = new Thread(() -> {
                SecurityContextHolder.setContext(context);

                try {
                    System.out.println("Waiting for variable to change...");

                    dialogFuture.thenRun(() -> {
                        String filePath = modelDialog.getUploadedFilePath();
                        if (filePath != null) {
                            HtmlParserService parserService = new HtmlParserService();

                            try {
                                List<String> classNames = parserService.extractClassNames(filePath);

                                for (String className : classNames) {
                                    modelData.stringColumn(0).append(className);
                                    modelData.stringColumn(1).append("count");
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        } else {
                            System.out.println("No file was uploaded.");
                        }
                        setOutput(modelData);
                        try {
                            setState(NodeState.COMPLETED);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).get(); // Block until the dialog completes

                    setOutput(modelData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            waiterThread.start();

            setOutput(modelData);
            setState(NodeState.COMPLETED);

        }
    }

    @Override
    public Table getOutput() {
        if (getState() == NodeState.PAUSED) {
            return modelData;
        }

        return modelData;
    }

}
