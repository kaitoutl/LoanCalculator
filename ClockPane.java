import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ClockPane

 */
public class ClockPane extends VBox {

    private static final int SIZE    = 110; // clock diameter in px
    private static final int CX     = SIZE / 2;
    private static final int CY     = SIZE / 2;
    private static final int RADIUS = SIZE / 2 - 6;

    private final Canvas       canvas      = new Canvas(SIZE, SIZE);
    private final Label        digitalLabel = new Label();
    private final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("hh:mm:ss a");

    public ClockPane() {
        super(6);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(8));
        setStyle("-fx-background-color: #2c3e50; "
               + "-fx-border-color: #bdc3c7; "
               + "-fx-border-radius: 6; "
               + "-fx-background-radius: 6;");

        Label heading = new Label("Live Clock");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        heading.setTextFill(Color.web("#ecf0f1"));

        digitalLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        digitalLabel.setTextFill(Color.web("#2ecc71"));

        getChildren().addAll(heading, canvas, digitalLabel);

        // Draw immediately, then every second
        drawClock();
        Timeline clock = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> drawClock()));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    // ── Drawing ────────────────────────────────────────────────────────────
    private void drawClock() {
        LocalTime now = LocalTime.now();
        int h = now.getHour() % 12;
        int m = now.getMinute();
        int s = now.getSecond();

        digitalLabel.setText(now.format(fmt));

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, SIZE, SIZE);

        // Clock face
        gc.setFill(Color.web("#34495e"));
        gc.fillOval(0, 0, SIZE, SIZE);
        gc.setStroke(Color.web("#ecf0f1"));
        gc.setLineWidth(2);
        gc.strokeOval(2, 2, SIZE - 4, SIZE - 4);

        // Hour ticks
        gc.setStroke(Color.web("#bdc3c7"));
        gc.setLineWidth(1.5);
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            double x1 = CX + Math.cos(angle) * (RADIUS - 4);
            double y1 = CY + Math.sin(angle) * (RADIUS - 4);
            double x2 = CX + Math.cos(angle) * RADIUS;
            double y2 = CY + Math.sin(angle) * RADIUS;
            gc.strokeLine(x1, y1, x2, y2);
        }

        // Hour hand
        drawHand(gc, Color.web("#ecf0f1"),
                (h * 30 + m * 0.5), RADIUS * 0.55, 3.5);
        // Minute hand
        drawHand(gc, Color.web("#3498db"),
                (m * 6 + s * 0.1), RADIUS * 0.80, 2.5);
        // Second hand
        drawHand(gc, Color.TOMATO, s * 6, RADIUS * 0.90, 1.5);

        // Center dot
        gc.setFill(Color.web("#ecf0f1"));
        gc.fillOval(CX - 4, CY - 4, 8, 8);
    }

    private void drawHand(GraphicsContext gc, Color color,
                          double angleDeg, double length, double width) {
        double angle = Math.toRadians(angleDeg - 90);
        gc.setStroke(color);
        gc.setLineWidth(width);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.strokeLine(CX, CY,
                CX + Math.cos(angle) * length,
                CY + Math.sin(angle) * length);
    }
}