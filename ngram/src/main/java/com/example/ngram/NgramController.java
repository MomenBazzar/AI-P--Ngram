package com.example.ngram;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NgramController {

    @FXML
    public Label labelAnswer;
    public TextField textInput;
    public HBox wordsHBox;

    HashMap<String, Integer> countTable = new HashMap<>();
    ArrayList<String> sentence = new ArrayList<>();
    ArrayList<Text> textNodes = new ArrayList<>();


    public NgramController() {

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\1ME\\Berzeit\\AI\\P3\\countTable.csv"));
            while ((line = br.readLine()) != null) {
                String[] token = line.split(",");
                countTable.putIfAbsent(token[0], Integer.parseInt(token[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Run() {
        String text = textInput.getText();
        if (text.isBlank())
            return;

        while (textNodes.size() != 0) {
            wordsHBox.getChildren().remove(textNodes.get(0));
            textNodes.remove(0);
            sentence.remove(0);
        }

        String[] words = text.split(" ");
        for (String word : words) {
            Text text1 = new Text(word);
            wordsHBox.getChildren().add(text1);
            sentence.add(word);
            textNodes.add(text1);
        }

    }

    @FXML
    public void wordClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        if (node instanceof Text) {
            String word = ((Text) node).getText();
            int index = wordsHBox.getChildren().indexOf(node);
            System.out.println(chainRule(index));

        }
    }

    public float chainRule(int index) {
        String fullWord = "", wordBefore = "";

        if (index >= 2) {
            wordBefore = sentence.get(index - 2) + ' ' + sentence.get(index - 1);
            fullWord = wordBefore + ' ' + sentence.get(index);
            float probability = probability(fullWord, wordBefore);
            if (probability > 0) {
                labelAnswer.setText(probability + "\t" + fullWord);
                return probability;
            }
        }

        if (index >= 1) {
            wordBefore = sentence.get(index - 1);
            fullWord = wordBefore + ' ' + sentence.get(index);
            float probability = probability(fullWord, wordBefore);
            if (probability > 0) {
                labelAnswer.setText(probability + "\t" + fullWord);
                return probability;
            }
        }

        return 0;
    }

    private float probability(String fullWord, String wordBefore) {
        if (countTable.containsKey(fullWord) && countTable.containsKey(wordBefore)) {
            int count1 = countTable.get(fullWord);
            int count2 = countTable.get(wordBefore);
            return (float) count1 / count2;
        }
        return 0;
    }
}