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

package com.processdataquality.praeclarus.reader;

import com.processdataquality.praeclarus.exception.InvalidOptionValueException;
import com.processdataquality.praeclarus.option.FileOption;
import com.processdataquality.praeclarus.plugin.AbstractPlugin;
import org.apache.commons.io.input.ReaderInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.Source;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 29/4/21
 */
public abstract class AbstractModalReader extends AbstractPlugin implements DataReader {

    protected Source _source;             // the data input source
    private Table modalData;

    protected AbstractModalReader() {
        super();
        addDefaultOptions();
    }


    // each sub-class will have unique read options for data format etc.
//    protected abstract ReadOptions getReadOptions();
    protected ReadOptions getReadOptions() {             // N/A
        return null;
    }

    /**
     * Reads data from an input source into a Table
     * @return a Table containing the input data
     * @throws IOException if there's a problem reading
     */
    @Override
    public Table read() throws IOException {
        return Table.read().usingOptions(getReadOptions());
//        return Table.read().usingOptions();
//        modalData = Table.create("Modal Data").addColumns(StringColumn.create("Class Name"),
//                StringColumn.create("Occurrences"));
//        return modalData;
    }


    @Override
    public void setSource(Source source) {
        _source = source;
    }


    @Override
    public Source getSource() {
        if (_source == null) {
            throw new InvalidOptionValueException("Parameter 'Source' requires a value");
        }
        return _source;
    }


    /**
     * Extracts an InputStream from a Source (if possible)
     * @return A source's InputStream
     * @throws IOException if the source is null or an InputStream cannot be extracted
     */
    public InputStream getSourceAsInputStream() throws IOException {
        Source source = getSource();
        if (source != null) {
            if (source.inputStream() != null) {
                return source.inputStream(); 
            }
            if (source.file() != null) {
                return new FileInputStream(source.file());
            }
            if (source.reader() != null) {
                return new ReaderInputStream(source.reader(), StandardCharsets.UTF_8);
            }
        }
        throw new IOException("Unable to get InputStream from Source");
    }


    // thw methods below set the Source object from various supported sources

    public void setSource(File file) {
        setSource(file, Charset.defaultCharset());
    }


    public void setSource(File file, Charset charset) {
        setSource(new Source(file, charset));
    }


    public void setSource(InputStreamReader reader) {
        setSource(new Source(reader));
    }


    public void setSource(Reader reader) {
        setSource(new Source(reader));
    }


    public void setSource(InputStream inputStream) {
        setSource(inputStream, Charset.defaultCharset());
    }


    public void setSource(InputStream inputStream, Charset charset) {
        setSource(new Source(inputStream, charset));
    }


    public void setSource(String pathOrURL) {
        Source source;
        try {
            source = Source.fromUrl(pathOrURL);     // try URL first
        }
        catch (IOException e) {
            source = Source.fromString(pathOrURL);  // ok, must be a file path
        }
        setSource(source);
    }


    protected void addDefaultOptions() {
        getOptions().addDefaults(new CommonReadOptions().toMap());
        getOptions().addDefault(new FileOption("Source", ""));
    }

    private List<Element> parseInput() throws IOException {
        try {
            InputStream is = getSourceAsInputStream(); // Assuming you have a method to get InputStream for HTML source

            if (is == null) {
                throw new IOException("Failed to read: No HTML input source specified");
            }

            // Parse HTML using JSoup or any other HTML parsing library
            Document doc = Jsoup.parse(is, null, ""); // Replace with appropriate JSoup usage

            // Example: Extract all <div> elements
            Elements divElements = doc.getElementsByTag("div");

            // Convert Elements to List<Element> if needed
            List<Element> elements = new ArrayList<>();
            elements.addAll(divElements);

            return elements;
        } catch (Exception e) {
            throw new IOException("Failed to load HTML file", e);
        }
    }



    public void parseAndPrintClassNames() {
        try {
//            String filePath = getSource().getAbsolutePath();
            String filePath = getFilePath();
            List<String> classNames = extractClassNames(filePath);
            classNames.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getFilePath() throws IOException {
        if (getSource().file() != null) {
            return getSource().file().getAbsolutePath();
//            return getSourceAsInputStream();
        }
        else {
            throw new IllegalStateException("Source is not a file");
        }
    }

    public List<String> extractClassNames(String filePath) throws IOException {
        List<String> classNames = new ArrayList<>();
        File input = new File(filePath);
        Document doc = Jsoup.parse(input, "UTF-8");

        Element totalClassesElement = doc.selectFirst("font:contains(Total number of classes:)");
        int totalClasses = 0;
        if (totalClassesElement != null) {
            String totalClassesText = totalClassesElement.text();
            totalClasses = Integer.parseInt(totalClassesText.replaceAll("[^0-9]", ""));
        }

        Element table = doc.select("table").select("tbody").first();

        if (table != null && totalClasses > 0) {
            Elements rows = table.select("tr");
            for (int i = 3; i < 3 + totalClasses && i < rows.size(); i++) {
                Element row = rows.get(i);
                Element classNameElement = row.select("td").first();
                if (classNameElement != null) {
                    String className = classNameElement.text().trim();
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }
}
