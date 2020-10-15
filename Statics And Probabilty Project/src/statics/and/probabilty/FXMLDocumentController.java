/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statics.and.probabilty;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * @author X550V
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    public TextField textfield_n;
    public TextField textfield_p;
    public TextField textfield_x;
    public TextField textfield_expectedValue;
    public TextField textfield_standartDeviation;
    public TextField textfield_varience;
    public TextField textfield_binomialCumulativePs;
    public CheckBox checkBox;
    public Button btn1;
    public BarChart barchart;
    public ComboBox<String> comboBox;
    ObservableList list = FXCollections.observableArrayList();

    double X_esittir_x = 0;
    double X_kucuktur_x = 0;
    double X_buyuktur_x = 0;

    double varience = 0;
    double standardDeviation = 0;
    double expectedValue = 0;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        //Kontrollerimiz yapıyoruz.
        if (textfield_p.getText().equalsIgnoreCase("") || textfield_n.getText().equalsIgnoreCase("") || textfield_x.getText().equalsIgnoreCase("")) {
            Alert alert3 = new Alert(AlertType.WARNING);
            alert3.setTitle("Dikkat");
            alert3.setHeaderText("Bütün girdileri girdiğinizden emin olun.");
            alert3.showAndWait();
        } else {
            if (Double.valueOf(textfield_p.getText()) > 1.0 || Double.valueOf(textfield_p.getText()) < 0) {
                Alert alert1 = new Alert(AlertType.WARNING);
                alert1.setTitle("Dikkat");
                alert1.setHeaderText("Geçersiz bir p değeri girdiniz.");
                alert1.setContentText("p değeri 1'den büyük veya 0'dan küçük olmamalı.!");
                alert1.showAndWait();
            } else {
                if (Integer.valueOf(textfield_x.getText()) > Integer.valueOf(textfield_n.getText())) {
                    Alert alert2 = new Alert(AlertType.WARNING);
                    alert2.setTitle("Dikkat");
                    alert2.setHeaderText("Geçersiz bir x değeri girdiniz.");
                    alert2.setContentText("x değeri n değerinden büyük olmamalı.!");
                    alert2.showAndWait();
                } else {
                    // Butona her basıldığında değerler sıfırlanıp baştan hesaplanıcak.
                    X_esittir_x = 0;
                    X_kucuktur_x = 0;
                    X_buyuktur_x = 0;

                    varience = 0;
                    standardDeviation = 0;
                    expectedValue = 0;
                    double q = 1.0 - Double.valueOf(textfield_p.getText());

                    barchart.getData().removeAll();
                    barchart.getData().clear();

                    // n ve p değerlerine göre normal yaklaşımı kullanılıp kullanılmadığını belirliyoruz.
                    if ((Integer.valueOf(textfield_n.getText()) * Double.valueOf(textfield_p.getText())) >= 10
                            && (Integer.valueOf(textfield_n.getText()) * (1.0 - Double.valueOf(textfield_p.getText()))) >= 10) {
                        checkBox.setSelected(true);
                    } else {
                        checkBox.setSelected(false);
                    }

                    // Beklenen değer varyans ve standart sapma hesaplamaları.
                    expectedValue = Double.valueOf(textfield_n.getText()) * Double.valueOf(textfield_p.getText());
                    varience = expectedValue * q;
                    standardDeviation = Math.sqrt(varience);

                    textfield_expectedValue.setText(String.valueOf(expectedValue));
                    textfield_standartDeviation.setText(String.valueOf(standardDeviation));
                    textfield_varience.setText(String.valueOf(varience));

                    // Yazdığımız fonksiyonu çağırıp grafiğimizi oluşturuyoruz.
                    barchart.getData().addAll(setDistributionTable());
                }
            }
        }
    }
// ComboBox da seçilen elemana göre TextField ' a cdf'i yazıyoruz.

    public void comboBoxChanged(ActionEvent event) {
        if (comboBox.getValue().equalsIgnoreCase("X = x")) {
            textfield_binomialCumulativePs.setText(String.valueOf(X_esittir_x));
        } else if (comboBox.getValue().equalsIgnoreCase("X > x")) {
            textfield_binomialCumulativePs.setText(String.valueOf(X_buyuktur_x));
        } else {
            textfield_binomialCumulativePs.setText(String.valueOf(X_kucuktur_x));
        }
    }

    public XYChart.Series setDistributionTable() {
        XYChart.Series set1 = new XYChart.Series<>();
        double q = 1.0 - Double.valueOf(textfield_p.getText());
        // n ve p değerlimizi girerek binom dağılımını oluşturuyoruz.
        BinomialDistribution bd = new BinomialDistribution(Integer.valueOf(textfield_n.getText()), Double.valueOf(textfield_p.getText()));
        for (int i = 0; i <= Integer.valueOf(textfield_n.getText()); i++) {

            // her x değeri için olasılığımızı hesaplayıp sütun grafiğine ekliyoruz.
            double probabilty = bd.probability(i);
            if (!(probabilty <= 1.00000000000000E-5)) {
                set1.getData().add(new XYChart.Data(String.valueOf(i), probabilty));
            }

        }
        // Cdf'lerimizi buluyoruz.
        X_kucuktur_x = bd.cumulativeProbability(Integer.valueOf(textfield_x.getText()));
        if ((X_kucuktur_x <= 1.00000000000000E-5)) {
            X_kucuktur_x = 0;
        }
        X_buyuktur_x = bd.cumulativeProbability(Integer.valueOf(textfield_x.getText()), Integer.valueOf(textfield_n.getText()));
        if ((X_buyuktur_x <= 1.00000000000000E-5)) {
            X_buyuktur_x = 0;
        }
        X_esittir_x = bd.cumulativeProbability(Integer.valueOf(textfield_x.getText()) - 1, Integer.valueOf(textfield_x.getText()));
        if ((X_esittir_x <= 1.00000000000000E-5)) {
            X_esittir_x = 0;
        }
        textfield_binomialCumulativePs.setText(String.valueOf(X_esittir_x));
        comboBox.getSelectionModel().select("X = x");
        return set1;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        checkBox.setMouseTransparent(true);
        list.removeAll(list);
        String a = "X = x";
        String b = "X > x";
        String c = "X < x";
        list.addAll(a, b, c);
        comboBox.getItems().addAll(list);
        textfield_expectedValue.setMouseTransparent(true);
        textfield_standartDeviation.setMouseTransparent(true);
        textfield_varience.setMouseTransparent(true);
        textfield_binomialCumulativePs.setMouseTransparent(true);

    }

}
