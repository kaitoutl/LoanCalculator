import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *   - LoanCalculatorPane.java
 *   - BouncingBallPane-animated bouncing ball)
 *   - ClockPane-live clock
 */
public class LoanCalculator extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Loan Calculator with Bouncing Ball");

        // ── Title bar ──────────────────────────────────────────────
        Label titleLabel = new Label("Loan Calculator");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        HBox titleBar = new HBox(titleLabel);
        titleBar.setAlignment(Pos.CENTER);
        titleBar.setPadding(new Insets(12, 0, 8, 0));
        titleBar.setStyle("-fx-background-color: #ecf0f1;");

        // ── Sub-panes ──────────────────────────────────────────────
        LoanCalculatorPane loanPane   = new LoanCalculatorPane();
        BouncingBallPane   ballPane   = new BouncingBallPane();
        ClockPane          clockPane  = new ClockPane();

        // Wrap ball + clock in a VBox (right column)
        VBox rightColumn = new VBox(15, ballPane, clockPane);
        rightColumn.setAlignment(Pos.TOP_CENTER);
        rightColumn.setPadding(new Insets(10));

        // ── Root layout ────────────────────────────────────────────
        HBox centerArea = new HBox(20, loanPane, rightColumn);
        centerArea.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(titleBar);
        root.setCenter(centerArea);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Scene scene = new Scene(root, 780, 520);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}