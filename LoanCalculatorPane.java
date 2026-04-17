import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LoanCalculatorPane
 *
 * A self-contained GridPane that holds all loan-calculator UI:
 *   • Input fields  : loan amount, annual interest rate, loan term (years)
 *   • Calculate button
 *   • Result labels : monthly payment, total payment, total interest

 */
public class LoanCalculatorPane extends GridPane {

    // ── UI controls ────────────────────────────────────────────────────────
    private final TextField loanAmountField    = new TextField();
    private final TextField interestRateField  = new TextField();
    private final TextField loanTermField      = new TextField();
    private final Button    calculateButton    = new Button("Calculate");
    private final Button    clearButton        = new Button("Clear");

    private final Label monthlyPaymentLabel    = new Label("—");
    private final Label totalPaymentLabel      = new Label("—");
    private final Label totalInterestLabel     = new Label("—");
    private final Label statusLabel            = new Label("");

    // ── Constructor ────────────────────────────────────────────────────────
    public LoanCalculatorPane() {
        configureGrid();
        buildUI();
        wireEvents();
        applyStyles();
    }

    // ── Grid configuration ─────────────────────────────────────────────────
    private void configureGrid() {
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: white; "
               + "-fx-border-color: #bdc3c7; "
               + "-fx-border-radius: 6; "
               + "-fx-background-radius: 6;");

        // Column constraints: label col | field col
        ColumnConstraints col1 = new ColumnConstraints(140);
        ColumnConstraints col2 = new ColumnConstraints(180);
        getColumnConstraints().addAll(col1, col2);
    }

    // ── Build UI ───────────────────────────────────────────────────────────
    private void buildUI() {
        // Section heading
        Label heading = new Label("Loan Details");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        heading.setTextFill(Color.web("#2c3e50"));
        add(heading, 0, 0, 2, 1);

        // Decorative separator line (demonstrates Line shape)
        Line separator = new Line(0, 0, 310, 0);
        separator.setStroke(Color.web("#bdc3c7"));
        separator.setStrokeWidth(1.5);
        add(separator, 0, 1, 2, 1);

        // ── Input rows ──────────────────────────────────────────────
        addRow(2, makeLabel("Loan Amount ($):"),   loanAmountField);
        addRow(3, makeLabel("Annual Interest (%):"), interestRateField);
        addRow(4, makeLabel("Loan Term (years):"), loanTermField);

        // Prompts
        loanAmountField.setPromptText("e.g. 200000");
        interestRateField.setPromptText("e.g. 5.5");
        loanTermField.setPromptText("e.g. 30");

        // ── Buttons ─────────────────────────────────────────────────
        HBox buttons = new HBox(10, calculateButton, clearButton);
        buttons.setAlignment(Pos.CENTER_LEFT);
        add(buttons, 0, 5, 2, 1);

        // Status (validation messages)
        add(statusLabel, 0, 6, 2, 1);

        // ── Results section ─────────────────────────────────────────
        Label resultsHeading = new Label("Results");
        resultsHeading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        resultsHeading.setTextFill(Color.web("#2c3e50"));
        add(resultsHeading, 0, 7, 2, 1);

        Line sep2 = new Line(0, 0, 310, 0);
        sep2.setStroke(Color.web("#bdc3c7"));
        add(sep2, 0, 8, 2, 1);

        addRow(9,  makeLabel("Monthly Payment:"),  monthlyPaymentLabel);
        addRow(10, makeLabel("Total Payment:"),    totalPaymentLabel);
        addRow(11, makeLabel("Total Interest:"),   totalInterestLabel);
    }

    // ── Event wiring (all lambdas) ─────────────────────────────────────────
    private void wireEvents() {
        // Calculate on button press
        calculateButton.setOnAction(e -> calculate());

        // Also calculate when Enter is pressed in any field
        loanAmountField.setOnAction(e   -> calculate());
        interestRateField.setOnAction(e -> calculate());
        loanTermField.setOnAction(e     -> calculate());

        // Clear button
        clearButton.setOnAction(e -> {
            loanAmountField.clear();
            interestRateField.clear();
            loanTermField.clear();
            monthlyPaymentLabel.setText("—");
            totalPaymentLabel.setText("—");
            totalInterestLabel.setText("—");
            statusLabel.setText("");
            loanAmountField.requestFocus();
        });

        // Observable property binding: bind the prompt text colour to whether
        // the field is empty (simple demonstration of change listeners)
        loanAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                loanAmountField.setStyle("-fx-border-color: #27ae60;");
            } else {
                loanAmountField.setStyle("");
            }
        });
    }

    // ── Calculation logic ──────────────────────────────────────────────────
    private void calculate() {
        statusLabel.setTextFill(Color.web("#e74c3c"));
        try {
            double principal    = Double.parseDouble(loanAmountField.getText().trim());
            double annualRate   = Double.parseDouble(interestRateField.getText().trim());
            int    termYears    = Integer.parseInt(loanTermField.getText().trim());

            if (principal <= 0 || annualRate <= 0 || termYears <= 0) {
                statusLabel.setText("⚠ All values must be greater than zero.");
                return;
            }

            double monthlyRate  = annualRate / 100.0 / 12.0;
            int    termMonths   = termYears * 12;

            double monthlyPayment;
            if (monthlyRate == 0) {
                monthlyPayment = principal / termMonths;
            } else {
                monthlyPayment = (principal * monthlyRate)
                        / (1 - Math.pow(1 + monthlyRate, -termMonths));
            }

            double totalPayment  = monthlyPayment * termMonths;
            double totalInterest = totalPayment - principal;

            monthlyPaymentLabel.setText(String.format("$%,.2f", monthlyPayment));
            totalPaymentLabel.setText(String.format("$%,.2f", totalPayment));
            totalInterestLabel.setText(String.format("$%,.2f", totalInterest));

            statusLabel.setTextFill(Color.web("#27ae60"));
            statusLabel.setText("✔ Calculated successfully.");

        } catch (NumberFormatException ex) {
            statusLabel.setText("⚠ Please enter valid numeric values.");
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    /** Creates a right-aligned label using the project Font style. */
    private Label makeLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        lbl.setTextFill(Color.web("#34495e"));
        lbl.setAlignment(Pos.CENTER_RIGHT);
        lbl.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHalignment(lbl, javafx.geometry.HPos.RIGHT);
        return lbl;
    }

    private void applyStyles() {
        // Calculate button – blue
        calculateButton.setStyle(
                "-fx-background-color: #2980b9; "
              + "-fx-text-fill: white; "
              + "-fx-font-weight: bold; "
              + "-fx-padding: 6 18; "
              + "-fx-background-radius: 4;");
        calculateButton.setOnMouseEntered(e ->
                calculateButton.setStyle(
                        "-fx-background-color: #3498db; "
                      + "-fx-text-fill: white; "
                      + "-fx-font-weight: bold; "
                      + "-fx-padding: 6 18; "
                      + "-fx-background-radius: 4;"));
        calculateButton.setOnMouseExited(e ->
                calculateButton.setStyle(
                        "-fx-background-color: #2980b9; "
                      + "-fx-text-fill: white; "
                      + "-fx-font-weight: bold; "
                      + "-fx-padding: 6 18; "
                      + "-fx-background-radius: 4;"));

        // Clear button – grey
        clearButton.setStyle(
                "-fx-background-color: #95a5a6; "
              + "-fx-text-fill: white; "
              + "-fx-padding: 6 14; "
              + "-fx-background-radius: 4;");

        // Result labels – larger, coloured font
        Font resultFont = Font.font("Arial", FontWeight.BOLD, 14);
        for (Label lbl : new Label[]{monthlyPaymentLabel, totalPaymentLabel, totalInterestLabel}) {
            lbl.setFont(resultFont);
            lbl.setTextFill(Color.web("#16a085"));
        }

        statusLabel.setFont(Font.font("Arial", 12));
    }
}