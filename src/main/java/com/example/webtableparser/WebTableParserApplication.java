package com.example.webtableparser;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WebTableParserApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebTableParserApplication.class, args);
    }

    @Override
    public void run(String... args) {
        String url = "https://qbit.dots.org.ua/standings?cid=101";
        /*
        Пояснюю, де брати цей URL (по суті, де брати число, якому рівний cid).
        Під ВИКЛАДАЧЕМ, ОДНОКРАТНО:
         1) залогінитися;
         2) зайти в потрібний турнір;
         3) взяти число cid з рядка адреси (перенабрати/скопіпастити вручну)
         4) запам'ятати його в налаштуваннях, що саме хочемо синхронізувати.
         */
        List<List<String>> tableData = fetchTableData(url);

        for (List<String> row : tableData) {
            System.out.println(row);
        }
    }

    private List<List<String>> fetchTableData(String url) {
        List<List<String>> tableData = new ArrayList<>();
        List<List<String>> tableDataSelected = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();
//            System.out.println(doc);
            Element table = doc.select("#standingstable").first(); // Search specifically for results specifically at qbit.dots.org.ua

            if (table != null) {
                Elements rows = table.select("tr");
                for (Element row : rows) {
                    Elements cols = row.select("th, td");
                    List<String> rowData = new ArrayList<>();
                    for (Element col : cols) {
                        rowData.add(col.text());
                    }
                    tableData.add(rowData);
                    if(rowData.get(0).matches("\\d+(-\\d+)?\\.")) { // Skip top, bottom and maybe other rows which are not actual students' results
                        List<String> mainResOnly = new ArrayList<>();
                        mainResOnly.add(rowData.get(0));
                        mainResOnly.add(rowData.get(1));
                        mainResOnly.add(rowData.get(rowData.size() - 1));
                        tableDataSelected.add(mainResOnly);
                    }
                }
            } else {
                System.out.println("Failed to find table ``standingstable''.");
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch webpage: " + e.getMessage());
        }

//        return tableData;         // for whole data
        return tableDataSelected;   // for name and sum only
    }

}
