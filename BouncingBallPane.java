import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * BouncingBallPane

 */
public class BouncingBallPane extends VBox {

    // ── Ball ───────────────────────────────────────────────────────────────
    private final Circle ball = new Circle(18, Color.TOMATO);

    // ── Animation objects ─────────────────────────────────────────────────
    private final TranslateTransition bounceTransition;
    private final Timeline            colorTimeline;

    // ── Color cycling state ────────────────────────────────────────────────
    private static final Color[] COLORS = {
        Color.TOMATO, Color.DODGERBLUE, Color.MEDIUMSEAGREEN,
        Color.GOLDENROD, Color.MEDIUMPURPLE, Color.DEEPPINK
    };
    private int colorIndex = 0;

    // ── Constructor ────────────────────────────────────────────────────────
    public BouncingBallPane() {
        super(8);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #1a1a2e; "
               + "-fx-border-color: #bdc3c7; "
               + "-fx-border-radius: 6; "
               + "-fx-background-radius: 6;");

        // ── Canvas pane that holds the ball ───────────────────────
        Pane canvas = new Pane();
        canvas.setPrefSize(220, 200);
        canvas.setStyle("-fx-background-color: #16213e; -fx-background-radius: 4;");

        // Ball shadow (Rectangle behind ball for depth)
        Rectangle shadow = new Rectangle(36, 8, Color.color(0, 0, 0, 0.3));
        shadow.setArcWidth(36);
        shadow.setArcHeight(8);
        shadow.setTranslateX(80);
        shadow.setTranslateY(185);

        // Position ball
        ball.setCenterX(110);
        ball.setCenterY(160);
        ball.setEffect(new javafx.scene.effect.DropShadow(8, Color.color(0,0,0,0.5)));

        canvas.getChildren().addAll(shadow, ball);

        // ── Bounce transition ─────────────────────────────────────
        bounceTransition = new TranslateTransition(Duration.millis(700), ball);
        bounceTransition.setFromY(0);
        bounceTransition.setToY(-130);
        bounceTransition.setCycleCount(Animation.INDEFINITE);
        bounceTransition.setAutoReverse(true);
        bounceTransition.setInterpolator(Interpolator.SPLINE(0.5, 0, 0.84, 0));

        // Shadow shrinks as ball rises (parallel animation)
        ScaleTransition shadowScale = new ScaleTransition(Duration.millis(700), shadow);
        shadowScale.setFromX(1.0);
        shadowScale.setToX(0.3);
        shadowScale.setFromY(1.0);
        shadowScale.setToY(0.3);
        shadowScale.setCycleCount(Animation.INDEFINITE);
        shadowScale.setAutoReverse(true);

        // ── Color cycling via Timeline ────────────────────────────
        colorTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1.4), e -> {
                colorIndex = (colorIndex + 1) % COLORS.length;
                ball.setFill(COLORS[colorIndex]);
            })
        );
        colorTimeline.setCycleCount(Animation.INDEFINITE);

        // ── Start all animations ──────────────────────────────────
        bounceTransition.play();
        shadowScale.play();
        colorTimeline.play();

        // ── Mouse: click ball to randomise color ──────────────────
        ball.setOnMouseClicked(e -> {
            Color random = Color.color(Math.random(), Math.random(), Math.random());
            ball.setFill(random);
        });
        ball.setOnMouseEntered(e -> ball.setCursor(javafx.scene.Cursor.HAND));

        // ── Controls ──────────────────────────────────────────────
        Button pauseBtn  = makeControlButton("⏸ Pause",  "#e67e22");
        Button resumeBtn = makeControlButton("▶ Resume", "#27ae60");
        Button fastBtn   = makeControlButton("⏩ Fast",   "#8e44ad");

        pauseBtn.setOnAction(e  -> { bounceTransition.pause(); colorTimeline.pause(); });
        resumeBtn.setOnAction(e -> { bounceTransition.play();  colorTimeline.play();  });
        fastBtn.setOnAction(e   -> {
            double rate = bounceTransition.getRate() == 1.0 ? 2.5 : 1.0;
            bounceTransition.setRate(rate);
        });

        HBox controls = new HBox(6, pauseBtn, resumeBtn, fastBtn);

        // ── Heading ───────────────────────────────────────────────
        Label heading = new Label("Bouncing Ball");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        heading.setTextFill(Color.web("#ecf0f1"));

        Label hint = new Label("Click ball to change color");
        hint.setFont(Font.font("Arial", 10));
        hint.setTextFill(Color.web("#95a5a6"));

        getChildren().addAll(heading, canvas, controls, hint);
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private Button makeControlButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; "
              + "-fx-text-fill: white; "
              + "-fx-font-size: 11; "
              + "-fx-padding: 4 8; "
              + "-fx-background-radius: 4;", color));
        return btn;
    }
}