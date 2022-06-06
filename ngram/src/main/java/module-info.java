module com.example.ngram {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ngram to javafx.fxml;
    exports com.example.ngram;
}