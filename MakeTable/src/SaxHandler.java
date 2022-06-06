import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class SaxHandler extends DefaultHandler {

    private final StringBuilder currentValue = new StringBuilder();
    HashMap<String, Integer> countTable = new HashMap<>();

    @Override
    public void endDocument() {
        File file = new File("countTable.csv");
        try {
            FileWriter fileWriter = new FileWriter("countTable.csv");
            for (Entry<String, Integer> entry : countTable.entrySet()) {
                if (entry.getValue() > 1)
                    fileWriter.write(entry.getKey() + ',' + entry.getValue() + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes) {

        // reset the tag value
        currentValue.setLength(0);
    }

    @Override
    public void endElement(String uri,
                           String localName,
                           String qName) {


        if (qName.equals("Headline") || qName.equals("Text")) {
            parseString(currentValue.toString());
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        currentValue.append(ch, start, length);
    }

    private void parseString(String input) {
        String[] words = input.split("[ \"\\.\u200F؛;'”:\\(\\),،\u0006/-]");

        boolean last1Valid = true, last2Valid = true, last3Valid;
        for (int i = 0; i < words.length; i++) {

            // === 1 word ====
            String word = words[i];
            last3Valid = last2Valid;
            last2Valid = last1Valid;
            last1Valid = true;

            if (isNotValid(word)){
                last1Valid = false;
                continue;
            }

            putInTable(word);
            // === 1 word ===

            // === 2 words ===
            if (i > 0) {
                if (!last2Valid)
                    continue;

                putInTable(words[i-1] + ' ' + words[i]);
            }

            // === 3 words ===
            if (i > 1) {
                if (!last3Valid)
                    continue;

                putInTable( words[i-2] + ' ' + words[i-1] + ' ' + words[i]);
            }
            // === 3 words ===
        }
    }

    private void putInTable(String word) {
        if (!countTable.containsKey(word))
            countTable.put(word, 0);

        countTable.replace(word, countTable.get(word) + 1);
    }

    public static boolean isNotValid(String strNum) {
        if (strNum.isBlank()) {
            return true;
        }

        if (strNum.matches("\\d*ر\\d*"))
            return true;

        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}