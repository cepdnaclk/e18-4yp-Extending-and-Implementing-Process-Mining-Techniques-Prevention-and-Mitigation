package com.processdataquality.praeclarus.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HtmlParserService {

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

