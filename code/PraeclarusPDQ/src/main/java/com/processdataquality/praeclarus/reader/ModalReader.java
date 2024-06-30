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

 import java.io.File;
 import java.io.IOException;
 
 import com.processdataquality.praeclarus.annotation.Plugin;
 import com.processdataquality.praeclarus.exception.InvalidOptionValueException;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.html.HtmlReadOptions;
import com.processdataquality.praeclarus.ui.component.announce.Announcement;
 
 /**
  * @author Michael Adams
  * @date 30/3/21
  */
 @Plugin(
         name = "Modal Reader",
         author = "Michael Adams",
         version = "1.0",
         synopsis = "Loads a log file formatted as HTML."
 )

 public class ModalReader extends AbstractDataReader {
 
     public ModalReader() {
         super();
         getOptions().addDefault("Table Index", 0);
     }
 
 
     protected HtmlReadOptions getReadOptions() throws InvalidOptionValueException {
        Announcement.show(getSource().toString());
         return HtmlReadOptions.builder(getSource())
                 .missingValueIndicator(getOptions().get("Missing Value").asString())
 //                .dateFormat(DateTimeFormatter.ofPattern((String) _options.get("Date Format")))
 //                .timeFormat(DateTimeFormatter.ofPattern((String) _options.get("Time Format")))
 //                .dateTimeFormat(DateTimeFormatter.ofPattern((String) _options.get("DateTime Format")))
                 .header(getOptions().get("Header").asBoolean())
                 .tableName(getOptions().get("Table Name").asString())
                 .sample(getOptions().get("Sample").asBoolean())
                 .tableIndex(getOptions().get("Table Index").asInt())
                 .build();
     }
 
     public static void main(String[] args) {
        // Announcement.show(getSour);
        XesDataReader reader = new XesDataReader();
        reader.setSource(new File("/Users/adamsmj/Documents/Git/contributions/praeclarus/sareh220209/updates220214/reviewing.xes"));
        try {
            Table t = reader.read();
            System.out.println(t.structure());
            System.out.println(t.summary());
            System.out.println();
            System.out.println(t.first(50));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
 }