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
import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import com.processdataquality.praeclarus.plugin.uitemplate.PluginUI;
import com.processdataquality.praeclarus.rootCause.AbstractRootCause;
import com.processdataquality.praeclarus.ui.component.announce.Announcement;
import com.processdataquality.praeclarus.ui.component.dialog.Questions;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import java.io.File;
import java.io.IOException;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.*;
import java.util.*;

/**
 * A container node for a log data reader
 *
 * 
 */
public class RootCauseAnalyzerNode extends Node {

        private List<List<String>> questionsList = new ArrayList<>();
        private Table detected;
        private int questionsCount = 0;
        private static Word2Vec word2Vec;

        public RootCauseAnalyzerNode(AbstractPlugin plugin) {
                super(plugin);
        }

        private static StanfordCoreNLP pipeline;

        static {
                // Initialize Stanford CoreNLP pipeline
                Properties props = new Properties();
                props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse");
                pipeline = new StanfordCoreNLP(props);
        }

        public static double calculateSimilarity(String sentence1, String sentence2) {
                // Dummy similarity calculation - can be replaced with more sophisticated logic
                return sentence1.equals(sentence2) ? 1.0 : 0.0;
        }

        public static String enhanceQuestion(String question, String keyword) {
                Annotation doc = new Annotation(question);
                pipeline.annotate(doc);
                List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
                if (sentences == null || sentences.isEmpty()) {
                        return question;
                }
                CoreMap sentence = sentences.get(0);
                int insertPosition = question.length();
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                        if (token.tag().startsWith("NN") || token.tag().startsWith("VB")) {
                                insertPosition = token.endPosition();
                                break;
                        }
                }
                String enhancedQuestion;
                if (question.endsWith(".") || question.endsWith("?")) {
                        enhancedQuestion = question.substring(0, insertPosition) + " with regard to " + keyword
                                        + question.substring(insertPosition);
                } else {
                        enhancedQuestion = question + " with regard to " + keyword;
                }
                return enhancedQuestion;
        }

        public static List<Object> enhanceQuestionsChain(List<Object> questionnaireChain, List<String> keywords) {
                List<Object> enhancedChain = new ArrayList<>();

                for (Object mainQuestionObj : questionnaireChain) {
                        List<Object> mainQuestion = (List<Object>) mainQuestionObj;
                        String mainQuestionText = (String) mainQuestion.get(0);
                        String answerType = (String) mainQuestion.get(1);
                        List<Object> subQuestions = mainQuestion.size() > 2 ? (List<Object>) mainQuestion.get(2) : null;

                        String bestKeyword = null;
                        double bestScore = -1;

                        for (String keyword : keywords) {
                                double similarityScore = calculateSimilarity(mainQuestionText, keyword);
                                if (similarityScore > bestScore) {
                                        bestScore = similarityScore;
                                        bestKeyword = keyword;
                                }
                        }

                        String enhancedMainQuestion = bestKeyword != null
                                        ? enhanceQuestion(mainQuestionText, bestKeyword)
                                        : mainQuestionText;

                        List<Object> enhancedMainQuestionObj = new ArrayList<>();
                        enhancedMainQuestionObj.add(enhancedMainQuestion);
                        enhancedMainQuestionObj.add(answerType);
                        if (subQuestions != null) {
                                enhancedMainQuestionObj.add(enhanceSubQuestions(subQuestions, keywords));
                        }
                        enhancedChain.add(enhancedMainQuestionObj);
                }
                return enhancedChain;
        }

        public static List<Object> enhanceSubQuestions(List<Object> subQuestions, List<String> keywords) {
                List<Object> enhancedSubQuestions = new ArrayList<>();
                for (Object subQuestionObj : subQuestions) {
                        List<Object> subQuestion = (List<Object>) subQuestionObj;
                        String subQuestionText = (String) subQuestion.get(0);
                        String subAnswerType = (String) subQuestion.get(1);
                        List<Object> nestedSubQuestions = subQuestion.size() > 2 ? (List<Object>) subQuestion.get(2)
                                        : null;

                        String bestSubKeyword = null;
                        double bestSubScore = -1;

                        for (String keyword : keywords) {
                                double similarityScore = calculateSimilarity(subQuestionText, keyword);
                                if (similarityScore > bestSubScore) {
                                        bestSubScore = similarityScore;
                                        bestSubKeyword = keyword;
                                }
                        }

                        String enhancedSubQuestionText = bestSubKeyword != null
                                        ? enhanceQuestion(subQuestionText, bestSubKeyword)
                                        : subQuestionText;

                        List<Object> enhancedSubQuestionObj = new ArrayList<>();
                        enhancedSubQuestionObj.add(enhancedSubQuestionText);
                        enhancedSubQuestionObj.add(subAnswerType);
                        if (nestedSubQuestions != null) {
                                enhancedSubQuestionObj.add(enhanceSubQuestions(nestedSubQuestions, keywords));
                        }
                        enhancedSubQuestions.add(enhancedSubQuestionObj);
                }
                return enhancedSubQuestions;
        }

        /**
         * Reads log data from the plugin contained by this node and sets the data as
         * the
         * node's output
         */
        @Override
        public void run() throws Exception {
                AbstractRootCause questions = (AbstractRootCause) getPlugin();
                questions.getInputs().addAll(getInputs());
                Table master = getInputs().get(0);

                Announcement.success(master.getString(1, 0));

                Announcement.success(questions.getName());

                Integer[] RootCauseMatrix = new Integer[9];

                int[] mask = new int[9];
                String[] RC = new String[9];

                // Distorted
                // [Q1 Q2 Q3 Q4 Q5 Q6 Q7 Q8 Q9]
                // RC5.2 [1 1 N N N N N N N]
                // RC5.4 [1 N 0 N N N N N N]
                // RC3.1 [N N N 1 0 0 N N N]
                // RC10.2[N N N N N N 1 1 N]
                // RC5.2 [N N N N N N 1 1 N]
                // RC5.4 [N N N N N N 1 N 0]

                if (questions.getName().compareTo("Distorted Label") == 0) {
                        List<String> QList_DataEntry = new ArrayList<>();
                        QList_DataEntry.add("Data Entry Interface");
                        QList_DataEntry.add(
                                        "Do your IT systems allow users to modify or overwrite automatically generated activity labels?");
                        QList_DataEntry.add("How is this functionality justified?");
                        QList_DataEntry.add(
                                        "Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?");
                        questionsList.add(QList_DataEntry);

                        List<String> QList_Config = new ArrayList<>();
                        QList_Config.add("System Configuration");
                        QList_Config.add("Do you have different IT systems in your process?");
                        QList_Config.add(
                                        "Do these systems use a standardized vocabulary or controlled list for activity labels?");
                        QList_Config.add(
                                        "How are inconsistencies between different systems managed during data integration?");
                        questionsList.add(QList_Config);

                        List<String> QList_Manual = new ArrayList<>();

                        QList_Manual.add("Manual Data Entry");
                        QList_Manual.add(
                                        "Is any activity label information entered manually during your data collection process?");
                        QList_Manual.add(
                                        "Do different process participants use slightly different terminology or abbreviations for the same activity?");
                        QList_Manual.add(
                                        "Do your data entry tools have any features to suggest or enforce a standardized vocabulary for activity labels?");
                        questionsList.add(QList_Manual);

                        questionsCount = 9;
                        RootCauseMatrix[0] = 0b110000000;
                        RootCauseMatrix[1] = 0b101000000;
                        RootCauseMatrix[2] = 0b000111000;
                        RootCauseMatrix[3] = 0b000000110;
                        RootCauseMatrix[4] = 0b000000110;
                        RootCauseMatrix[5] = 0b000000101;
                        mask[1] = 0b1000000;
                        mask[2] = 0b11000;
                        mask[5] = 0b1;
                        RC[0] = "RC5.2";
                        RC[1] = "RC5.4";
                        RC[2] = "RC3.1";
                        RC[3] = "RC10.2";
                        RC[4] = "RC5.2";
                        RC[5] = "RC5.4";

                }

                else if (questions.getName().compareTo("Polluted Label") == 0) {

                        List<String> QList_DataField = new ArrayList<>();
                        QList_DataField.add("Data Field Design");
                        QList_DataField.add(
                                        "Do your IT systems allow for recording multiple values within a single event attribute field?");
                        QList_DataField.add("How is this functionality justified?");
                        QList_DataField.add(
                                        "Are there any plans to redesign the data capture process to separate these values into distinct attributes?");
                        questionsList.add(QList_DataField);

                        List<String> QList_Manual = new ArrayList<>();
                        QList_Manual.add("Manual Data Entry ");
                        QList_Manual.add(
                                        "Is any information in the polluted attribute fields entered manually during data collection? ");
                        QList_Manual.add(
                                        "Do different process participants use varying formats or conventions when entering data into these fields?");
                        QList_Manual.add(
                                        "Do your data entry tools have any features to guide users towards consistent formatting in these fields?");
                        questionsList.add(QList_Manual);
                        questionsCount = 6;

                        RootCauseMatrix[0] = 0b101000; // RC2.1
                        RootCauseMatrix[1] = 0b000111; // RC10.2

                        mask[0] = 0b001000;
                        mask[1] = 0b000001;
                        RC[0] = "RC2.1";
                        RC[1] = "RC10.2";

                }

                else if (questions.getName().compareTo("Synonymous Label") == 0) {

                        List<String> QList_Multiple = new ArrayList<>();
                        QList_Multiple.add("Multiple Systems");
                        QList_Multiple.add(
                                        "Do you use different IT systems to capture data for the process you're analyzing?");
                        QList_Multiple.add(
                                        "Do these systems use the same terminology for process steps and attributes?");
                        questionsList.add(QList_Multiple);

                        List<String> QList_Config = new ArrayList<>();
                        QList_Config.add("System Configuration");
                        QList_Config.add(
                                        "Do your IT systems allow users to customize how they record data (e.g., labels, attributes)?");
                        QList_Config.add(
                                        "Are there any departmental or user-level configurations that might lead to variations in how the same data is recorded?");
                        questionsList.add(QList_Config);

                        List<String> QList_Data = new ArrayList<>();
                        QList_Data.add("Data Transformation");
                        QList_Data.add(
                                        "Do you have any data transformation processes (ETL) before using the data for process mining?");
                        QList_Data.add(
                                        "Do these data transformation tools have functionalities to modify or standardize labels or attribute values?");
                        QList_Data.add(
                                        "Are these tools configured to handle synonymous labels?");
                        QList_Data.add("How do they handle them (e.g., mapping, merging)?");
                        QList_Data.add(
                                        "Are data curators or analysts involved in overseeing or configuring these transformations?");
                        QList_Data.add(
                                        "What training or guidelines are in place to ensure consistent handling of synonymous labels?");
                        questionsList.add(QList_Data);
                        questionsCount = 10;

                        RootCauseMatrix[0] = 0b1111000000; // RC3.1
                        RootCauseMatrix[1] = 0b0000111010; // RC7.1

                        mask[0] = 0b0100000000;

                        RC[0] = "RC3.1";
                        RC[1] = "RC7.1";

                }

                Announcement.success("Please answer to the below questions to proceed !!!");

                if (getState() == NodeState.UNSTARTED) {

                        // load plugin with all incoming plugins' aux datasets
                        questions.getAuxiliaryDatasets().putAll(getAuxiliaryInputs());

                        setState(NodeState.EXECUTING);

                        Announcement.show("Please answer to the below questions to proceed !!!");

                        // Build the Questions dialog and pass the listener and questions list
                        Questions dialog = new Questions(e -> {
                                if (e.successful) {

                                }
                        }, questionsList, questionsCount);

                        // Open the dialog
                        dialog.open();

                        // detected = Table.create("Questions").addColumns(StringColumn.create("Category"),
                                        // StringColumn.create("Questions"), BooleanColumn.create("Answers"));
                        detected = Table.create("Detected_Root_Causes").addColumns(StringColumn.create("Root_Cause"));
                        
                        StringBuilder binaryString = new StringBuilder();
                        
                        Thread waiterThread = new Thread(() -> {
                                try {
                                        System.out.println("Waiting for variable to change...");
                                        dialog.getResults().thenRun(() -> {
                                                System.out.println("Variable changed! New value: "
                                                                + dialog.getWatchedVariable());

                                                // for (List<String> list : questionsList) {

                                                //         for (String var : list.subList(1, list.size())) {
                                                //                 detected.stringColumn(0).append(list.get(0));
                                                //                 detected.stringColumn(1).append(var);

                                                //         }

                                                // }
                                                int index = 0;
                                                for (Boolean answer : dialog.getWatchedVariable()) {
                                                        binaryString.append(answer ? 1 : 0);
                                                        // detected.booleanColumn(2).append(answer);
                                                }
                                                for (Integer entry : RootCauseMatrix) {

                                                        Integer binaryNumber = Integer.parseInt(binaryString.toString(),
                                                                        2);
                                                        binaryNumber = binaryNumber ^ mask[index];
                                                        Integer result = (binaryNumber & entry);

                                                        if (result.equals(entry)) {

                                                                Announcement.show(
                                                                                "Root Cause Detected!! - " + RC[index]);
                                                                detected.stringColumn(0).append(RC[index]);

                                                                // MessageDialog msg = new MessageDialog("Detected Root
                                                                // Cause!! - "+RC[index]);
                                                                // msg.addConfirmButton("OK");
                                                                // msg.open();

                                                        }
                                                        index++;

                                                }

                                        }).get(); // Block until the variable changes
                                        setOutput(detected);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        });
                        waiterThread.start();

                        List<String> keywords = Arrays.asList("Validate application", "Call after offers",
                                        "Complete application",
                                        "Handle leads", "Create Offer", "Sent (mail and online)", "Validating",
                                        "Create Application",
                                        "Accepted", "Cancelled", "Refused", "Denied", "Assess potential fraud",
                                        "Shortened completion",
                                        "Personal Loan collection");

                        List<Object> questionnaireChain = Arrays.asList(
                                        Arrays.asList(
                                                        "Do your IT systems allow users to modify or overwrite automatically generated activity labels?",
                                                        "Yes/No",
                                                        Arrays.asList(
                                                                        Arrays.asList("How is this functionality justified? (e.g., flexibility, user needs)",
                                                                                        ""),
                                                                        Arrays.asList(
                                                                                        "Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?",
                                                                                        "Yes/No"))),
                                        Arrays.asList("Do you have different IT systems in your process?", "Yes/No",
                                                        Arrays.asList(Arrays.asList(
                                                                        "Do these systems use a standardized vocabulary or controlled list for activity labels?",
                                                                        "Yes/No"),
                                                                        Arrays.asList(
                                                                                        "How are inconsistencies between different systems managed during data integration? (e.g., mapping, normalization)",
                                                                                        ""))),
                                        Arrays.asList("Is any activity label information entered manually during your data collection process?",
                                                        "Yes/No",
                                                        Arrays.asList(Arrays.asList(
                                                                        "Do different process participants use slightly different terminology or abbreviations for the same activity?",
                                                                        "Yes/No",
                                                                        Arrays.asList(Arrays.asList(
                                                                                        "Provide examples of such variations.",
                                                                                        ""))),
                                                                        Arrays.asList(
                                                                                        "Do your data entry tools have any features to suggest or enforce a standardized vocabulary for activity labels?",
                                                                                        "Yes/No"))));

                        List<Object> enhancedChain = enhanceQuestionsChain(questionnaireChain, keywords);

                        for (Object mainQuestionObj : enhancedChain) {
                                List<Object> mainQuestion = (List<Object>) mainQuestionObj;
                                Announcement.show(mainQuestion.get(0) + " " + mainQuestion.get(1));
                                if (mainQuestion.size() > 2) {
                                        List<Object> subQuestions = (List<Object>) mainQuestion.get(2);
                                        for (Object subQuestionObj : subQuestions) {
                                                List<Object> subQuestion = (List<Object>) subQuestionObj;
                                                Announcement.show(
                                                                "  " + subQuestion.get(0) + " " + subQuestion.get(1));
                                                if (subQuestion.size() > 2) {
                                                        List<Object> nestedSubQuestions = (List<Object>) subQuestion
                                                                        .get(2);
                                                        for (Object nestedSubQuestionObj : nestedSubQuestions) {
                                                                List<Object> nestedSubQuestion = (List<Object>) nestedSubQuestionObj;
                                                                Announcement.show("    " + nestedSubQuestion.get(0)
                                                                                + " " + nestedSubQuestion.get(1));
                                                        }
                                                }
                                        }
                                }
                        }

                        setOutput(detected);

                        setState(NodeState.COMPLETED);

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