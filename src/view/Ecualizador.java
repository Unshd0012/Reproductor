package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Ecualizador extends HBox {

    private static final int NUM_BARS = 128;  // Número de barras en el visualizador
    private static final int BAR_WIDTH = 10; // Ancho de cada barra
    private static final int MAX_HEIGHT = 200; // Altura máxima de las barras

    private Rectangle[] bars = new Rectangle[NUM_BARS];
    private double[] targetHeights = new double[NUM_BARS]; // Guarda la altura objetivo para cada barra
    Timeline smoothingTimeline;

    public Ecualizador() {
        // Configuracion inicial del HBox y las barras
        this.setSpacing(2);
        this.setStyle("-fx-background-color: black;");
        this.setAlignment(Pos.CENTER);
        this.setMinHeight(200);

        for (int i = 0; i < NUM_BARS; i++) {
            Rectangle bar = new Rectangle(BAR_WIDTH, 0); // Ancho inicial, altura 0
            bar.setFill(Color.LIMEGREEN);

            // Alineamos la barra en la parte inferior del StackPane
            StackPane stackPane = new StackPane(bar);
            stackPane.setMinHeight(MAX_HEIGHT);
            stackPane.setAlignment(Pos.CENTER);

            bars[i] = bar;
            this.getChildren().addAll(stackPane);
        }

       
        smoothingTimeline = new Timeline(new KeyFrame(Duration.millis(40), e -> smoothUpdate()));
        smoothingTimeline.setCycleCount(Timeline.INDEFINITE);
        smoothingTimeline.play();

    }

    
    public void updateBars(float[] magnitudes) {
        for (int i = 0; i < NUM_BARS && i < magnitudes.length; i++) {
            double magnitude = (magnitudes[i]);
            targetHeights[i] = (magnitude + 60) * (MAX_HEIGHT / 40); // Escala la magnitud a la altura
        }
    }

    
    private void smoothUpdate() {
        for (int i = 0; i < NUM_BARS; i++) {
            double currentHeight = bars[i].getHeight();
            double targetHeight = targetHeights[i];
            double smoothedHeight = currentHeight + 0.1 * (targetHeight - currentHeight); 
            bars[i].setHeight(smoothedHeight);
        }
    }

    public void reload() {
        limpiar();
        for (int i = 0; i < NUM_BARS; i++) {
            Rectangle bar = new Rectangle(BAR_WIDTH, 0);
            bar.setFill(Color.LIMEGREEN);
            StackPane stackPane = new StackPane(bar);
            stackPane.setMinHeight(MAX_HEIGHT);
            stackPane.setAlignment(Pos.CENTER);

            bars[i] = bar;
            this.getChildren().addAll(stackPane);
        }
    }

    public void limpiar() {
        this.getChildren().clear();
    }
}
