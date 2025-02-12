package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.BlackJackModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    private final Map<Button, Label> buttonLabelMap = new HashMap<>();
    private final Map<Label, Integer> labelValues = new HashMap<>();
    private final Map<Integer, Integer> cardCounts = new HashMap<>();
    private final BlackJackModel gameModel = new BlackJackModel();

    @FXML private TextField decksNum;
    @FXML private CheckBox myTurn, dealerTurn;
    @FXML private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    @FXML private Button start, reset, newTurn;
    @FXML private Label label1, label2, label3, label4, label5, label6, label7, label8, label9, label10;
    @FXML private Label probOf12, probOf13, probOf14, probOf15, probOf16, probOf17, probOf18, probOf19, probOf20, probOf21;
    @FXML private Label totalCards, playerHand, probToBust;
    @FXML private Label dealerHand;
    @FXML private Label dealerProbOf17, dealerProbOf18, dealerProbOf19, dealerProbOf20, dealerProbOf21, dealerProbOfBust;

    private List<Label> labels;

    @FXML
    public void initialize() {
        labels = List.of(label1, label2, label3, label4, label5, label6, label7, label8, label9, label10);

        buttonLabelMap.put(button1, label1);
        buttonLabelMap.put(button2, label2);
        buttonLabelMap.put(button3, label3);
        buttonLabelMap.put(button4, label4);
        buttonLabelMap.put(button5, label5);
        buttonLabelMap.put(button6, label6);
        buttonLabelMap.put(button7, label7);
        buttonLabelMap.put(button8, label8);
        buttonLabelMap.put(button9, label9);
        buttonLabelMap.put(button10, label10);

        setCardsNum(1);

        for (Map.Entry<Button, Label> entry : buttonLabelMap.entrySet()) {
            entry.getKey().setOnAction(event -> {
                try {
                    handleCardClick(entry.getValue(), entry.getKey());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        myTurn.setOnAction(event -> handleTurnChange());
        dealerTurn.setOnAction(event -> handleDealerTurnChange());
        start.setOnAction(event -> setCardsNum(getDecksNum()));
        newTurn.setOnAction(event -> {
            try {
                setNewTurn();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        reset.setOnAction(event -> {
            try {
                resetAll();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        updateLabels();
    }

    private int getDecksNum() {
        try {
            return Math.max(1, Integer.parseInt(decksNum.getText().trim()));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void handleCardClick(Label label, Button button) throws NoSuchFieldException, IllegalAccessException {
        if (!labelValues.containsKey(label)) return;

        if(Integer.parseInt(label.getText()) > 0) {
            int value = Integer.parseInt(button.getText().trim());
            decrementLabel(label, value);

            gameModel.addCard(value);
            updatePlayerHand();

            gameModel.addDealerCard(value);
            updateDealerHand();

            updateProbabilities();
            updateDealerProbabilities();
        }
    }

    private void handleTurnChange() {
        gameModel.setMyTurn(myTurn.isSelected());
    }

    private void handleDealerTurnChange() {
        System.out.println("Dealer turn changed");
        gameModel.setDealerTurn(dealerTurn.isSelected());
    }
    private void updatePlayerHand() {
        int playerValue = gameModel.getPlayerHandValue();
        playerHand.setText(String.valueOf(playerValue));

        playerHand.setStyle(playerValue > 21 ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    private void updateDealerHand() {
        int dealerValue = gameModel.getDealerHandValue();
        dealerHand.setText(String.valueOf(dealerValue));
    }

    private void decrementLabel(Label label, int cardValue) {
        if (!labelValues.containsKey(label)) return;

        int currentValue = labelValues.get(label);
        if (currentValue > 0) {
            labelValues.put(label, currentValue - 1);
            label.setText(String.valueOf(currentValue - 1));

            int total = Integer.parseInt(totalCards.getText());
            totalCards.setText(String.valueOf(Math.max(0, total - 1)));

            cardCounts.put(cardValue, cardCounts.getOrDefault(cardValue, 0) - 1);
        }
    }

    private void setCardsNum(int decks) {
        for (int i = 2; i <= 9; i++) {
            cardCounts.put(i, 4 * decks);
        }
        cardCounts.put(10, 16 * decks);
        cardCounts.put(1, 4 * decks);

        for (Label label : labels) {
            labelValues.put(label, 4 * decks);
        }
        labelValues.put(label10, 16 * decks);
        totalCards.setText(String.valueOf(52 * decks));

        updateLabels();
    }

    private void updateLabels() {
        for (Map.Entry<Label, Integer> entry : labelValues.entrySet()) {
            entry.getKey().setText(String.valueOf(entry.getValue()));
        }
    }

    private void updateProbabilities() throws NoSuchFieldException, IllegalAccessException {
        double probability = gameModel.probToBust(gameModel.getPlayerHandValue(), cardCounts, Integer.parseInt(totalCards.getText()));
        probToBust.setText(String.format("%.2f%%", probability * 100));

        updateProbLabels("probOf", gameModel.calculateProbabilities(cardCounts, Integer.parseInt(totalCards.getText())));
    }

    private void updateProbLabels(String prefix, Map<Integer, Double> probabilities) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 12; i <= 21; i++) {
            Label probLabel = (Label) getClass().getDeclaredField(prefix + i).get(this);
            double probability = probabilities.getOrDefault(i, 0.0);
            probLabel.setText(String.format("%.2f%%", probability * 100));
        }
    }

    private void updateDealerProbabilities() throws NoSuchFieldException, IllegalAccessException {
        // Ottieni le probabilità di bustare per il dealer
        // double probability = gameModel.dealerProbToBust(gameModel.getDealerHandValue(), cardCounts, Integer.parseInt(totalCards.getText()));
        // dealerProbToBust.setText(String.format("%.2f%%", probability * 100));

        // Aggiorna le probabilità specifiche per il dealer (da 17 a 21)
        updateDealerProbLabels("dealerProbOf", gameModel.calculateDealerProbabilities(cardCounts, Integer.parseInt(totalCards.getText()), gameModel.getDealerHandValue()));
    }

    private void updateDealerProbLabels(String prefix, Map<Integer, Double> probabilities) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 17; i <= 21; i++) {
            // Usa reflection per ottenere i label dinamicamente
            Label probLabel = (Label) getClass().getDeclaredField(prefix + i).get(this);
            System.out.println(prefix + i + " " + String.valueOf(probabilities.get(i)));
            double probability = probabilities.getOrDefault(i, 0.0);
            probLabel.setText(String.format("%.2f%%", probability));
        }
    }


    private void setNewTurn() throws NoSuchFieldException, IllegalAccessException {
        deleteGUIValue();
    }

    private void resetAll() throws NoSuchFieldException, IllegalAccessException {
        setCardsNum(1);
        deleteGUIValue();
    }

    private void deleteGUIValue() throws NoSuchFieldException, IllegalAccessException {
        gameModel.resetPlayerHand();
        gameModel.resetDealerHand();
        playerHand.setStyle("-fx-text-fill: black;");
        playerHand.setText("0");
        probToBust.setText("0.00%");
        dealerHand.setText("0");

        for (int i = 12; i <= 21; i++) {
            String prefix = "probOf";
            Label probLabel = (Label) getClass().getDeclaredField(prefix + i).get(this);
            probLabel.setText("0,00%");
        }

        dealerProbOfBust.setText("0.00%");
    }
}
