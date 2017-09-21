package sample;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private WebView wv;
    private long position;
    private WebEngine engine;
    private boolean scroll;
    private int direction; // 1 -> down, -1 -> up
    private int speed;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = wv.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.load("http://facebook.pl");
        position = 0;
        scroll = true;
        speed = 90;


        wv.getEngine().getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> observable,
                 Worker.State oldValue,
                 Worker.State newValue) -> {
                    if( newValue != Worker.State.SUCCEEDED ) {
                        return;
                    }
                    engine.executeScript("var style = document.createElement(\"style\");\n" +
                            "style.innerHTML =\n" +
                            "'div{background-color: black !important; color: white !important;} \\n'+\n" +
                            "'textarea{background-color: black !important; color: white !important;} \\n'+\n" +
                            "'section{background-color: black !important; color: white !important;} \\n'+\n" +
                            "'article{background-color: black !important; color: white !important; border-style: solid !important; border-color: gray !important;} \\n'+\n" +
                            "'span{color: white !important;} \\n'+\n" +
                            "'h3{color: white !important;} \\n';\n" +
                            "document.body.appendChild(style);");
                    new Thread(() -> {
                        try {
                            while (scroll) {
                                Thread.sleep(speed);
                                Platform.runLater(
                                        () -> {
                                            switch (direction) {
                                                case -1:
                                                    if (position >= 1) {
                                                        position--;
                                                        scrollTo(0,position);
                                                        System.out.println("Scroll status: Up " + position);
                                                    }
                                                    break;
                                                case 0:
                                                    break;
                                                case 1:
                                                    if(position <= getVScrollValue() ) {
                                                        position++;
                                                        scrollTo(0,position);
                                                        System.out.println("Scroll status: Down " + position);
                                                    }
                                                    break;
                                                default:
                                                    System.out.println("Scroll status: Wrong value of direction");
                                                    break;
                                            }
                                        }
                                );
                            }
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                    }).start();
                } );
    }


    @FXML
    void scrollUpBtn_Clicked(ActionEvent event) {
        if (speed > 41) speed -= 40;
        this.direction = -1;
    }

    @FXML
    void scrollDownBtn_Clicked(ActionEvent event) {
        if (speed > 41) speed -= 40;
        this.direction = 1;
    }

    @FXML
    void stopScrollBtn_Clicked() {
        this.scroll = false;
    }

    @FXML
    void pauseScrollBtn_Clicked(ActionEvent event) {
        speed = 90;
        this.direction = 0;

    }

    public void scrollTo(double x, double y) {
        engine.executeScript("window.scrollTo(" + x + ", " + y + ")");
    }

    public int getVScrollValue() {
        return (Integer) engine.executeScript("document.body.scrollTop");
    }



}
