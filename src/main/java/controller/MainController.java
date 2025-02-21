package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.BlackJackModel;

import javax.smartcardio.Card;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    private final Map<Button, Label> buttonLabelMap = new HashMap<>();
    private final Map<Label, Integer> labelValues = new HashMap<>();
    private final Map<Integer, Integer> cardCounts = new HashMap<>();
    private final BlackJackModel gameModel = new BlackJackModel();

    // FXML components
    @FXML private TextField decksNum;
    @FXML private CheckBox myTurn, dealerTurn;
    @FXML private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10; // card buttons
    @FXML private Button start, reset, newTurn;
    @FXML private Label label1, label2, label3, label4, label5, label6, label7, label8, label9, label10; // total deck cards by value
    @FXML private Label probNum1, probNum2, probNum3, probNum4, probNum5, probNum6, probNum7, probNum8, probNum9, probNum10; // probability to draw card
    @FXML private Label probOf12, probOf13, probOf14, probOf15, probOf16, probOf17, probOf18, probOf19, probOf20, probOf21; // player probability
    @FXML private Label totalCards, playerHand, probToBust, dealerHand, dealerProbOfBust, bestChoice, handCards;
    @FXML private Label dealerProbOf17, dealerProbOf18, dealerProbOf19, dealerProbOf20, dealerProbOf21; // dealer probability

    private List<Label> labels;

    /**
     * @brief Initializes the controller by setting up the buttons and labels.
     */
    @FXML
    public void initialize() throws NoSuchFieldException, IllegalAccessException {
        labels = List.of(label1, label2, label3, label4, label5, label6, label7, label8, label9, label10);

        // Map buttons to corresponding labels
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

        // Initialize card count to 1 deck and prob to draw cards
        setCardsNum(1);
        setProbToDrawCards();

        // Set up button actions
        for (Map.Entry<Button, Label> entry : buttonLabelMap.entrySet()) {
            entry.getKey().setOnAction(event -> {
                try {
                    handleCardClick(entry.getValue(), entry.getKey());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // Set up checkboxes actions
        myTurn.setOnAction(event -> {
            if (myTurn.isSelected()) {
                dealerTurn.setSelected(false);
                handleDealerTurnChange();
            }
            handleTurnChange();
        });
        dealerTurn.setOnAction(event -> {
            if (dealerTurn.isSelected()) {
                myTurn.setSelected(false);
                handleTurnChange();
            }
            handleDealerTurnChange();
        });

        // Set up buttons for start, new turn, and reset
        start.setOnAction(event -> {
            try {
                setCardsNum(getDecksNum());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
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

        // Update labels after initialization
        updateLabels();
    }

    /**
     * @brief Retrieves the number of decks entered by the user.
     *
     * @return The number of decks (default is 1 if input is invalid).
     */
    private int getDecksNum() {
        try {
            return Math.max(1, Integer.parseInt(decksNum.getText().trim()));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * @brief Handles the action of clicking a card button.
     *
     * @param label The corresponding label for the clicked card.
     * @param button The button that was clicked.
     * @throws NoSuchFieldException If reflection fails to access fields.
     * @throws IllegalAccessException If reflection access is denied.
     */
    private void handleCardClick(Label label, Button button) throws NoSuchFieldException, IllegalAccessException {
        if (!labelValues.containsKey(label)) return;

        if(Integer.parseInt(label.getText()) > 0) {
            int value = Integer.parseInt(button.getText().trim());
            decrementLabel(label, value);

            // Add the card to the player's hand and update the hand probabilities for the player
            if(Integer.parseInt(playerHand.getText()) < 21 && Integer.parseInt(totalCards.getText())!=0) {
                gameModel.addCard(value);
                updatePlayerHand(button.getText());
                updateProbabilities();
            }

            // Add the card to the dealer's hand and update the dealer's hand and probabilities for the dealer
            if(Integer.parseInt(dealerHand.getText()) < 17 && Integer.parseInt(totalCards.getText())!=0) {
                gameModel.addDealerCard(value);
                updateDealerHand();
                updateDealerProbabilities();
            }
        }

        bestChoice.setText(gameModel.bestChoice(
                Integer.parseInt(playerHand.getText()),
                Integer.parseInt(dealerHand.getText()),
                Double.parseDouble(probToBust.getText().replace("%", "").replace(",", ".")) / 100,
                Double.parseDouble(dealerProbOfBust.getText().replace("%", "").replace(",", ".")) / 100,
                getPlayerProbabilities(), getDealerProbabilities()
        ));
    }

    /**
     * @brief Handles the change in the player's turn (checkbox action).
     */
    private void handleTurnChange() {
        gameModel.setMyTurn(myTurn.isSelected());
    }

    /**
     * @brief Handles the change in the dealer's turn (checkbox action).
     */
    private void handleDealerTurnChange() {
        gameModel.setDealerTurn(dealerTurn.isSelected());
    }

    /**
     * @brief Updates the player's hand value on the UI.
     */
    private void updatePlayerHand(String cardValue) {
        int playerValue = gameModel.getPlayerHandValue();
        playerHand.setText(String.valueOf(playerValue));

        if(gameModel.isMyTurn()) {
            String currentText = handCards.getText();
            handCards.setText(currentText + " " + cardValue);
        }

        // Disable myTurn if the player's hand value > 21
        if (playerValue >= 21) {
            myTurn.setSelected(false);
            myTurn.setDisable(true);
            handleTurnChange();
        } else {
            myTurn.setDisable(false);
        }

        // Set text color to red if the player busts (hand value > 21)
        playerHand.setStyle(playerValue > 21 ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
    }

    /**
     * @brief Updates the dealer's hand value on the UI and disables dealerTurn if the hand value is between 17 and 21.
     */
    private void updateDealerHand() {
        int dealerValue = gameModel.getDealerHandValue();
        dealerHand.setText(String.valueOf(dealerValue));

        // Disable dealerTurn if the dealer's hand value is between 17 and 21
        if (dealerValue >= 17 && dealerValue <= 21) {
            dealerTurn.setSelected(false);
            dealerTurn.setDisable(true);
            handleDealerTurnChange();
        } else {
            dealerTurn.setDisable(false);
        }
    }

    /**
     * @brief Decrements the value of the selected label by the card value.
     *
     * @param label The label corresponding to the card.
     * @param cardValue The value of the card.
     */
    private void decrementLabel(Label label, int cardValue) throws NoSuchFieldException, IllegalAccessException {
        if (!labelValues.containsKey(label)) return;

        int currentValue = labelValues.get(label);
        if (currentValue > 0) {
            labelValues.put(label, currentValue - 1);
            label.setText(String.valueOf(currentValue - 1));

            // Decrease total card count
            int total = Integer.parseInt(totalCards.getText());
            totalCards.setText(String.valueOf(Math.max(0, total - 1)));

            // Update the card count for the card value
            cardCounts.put(cardValue, cardCounts.getOrDefault(cardValue, 0) - 1);

            setProbToDrawCards();
        }
    }

    /**
     * @brief Probability to draw all different type of card
     */
    private void setProbToDrawCards() throws NoSuchFieldException, IllegalAccessException {
        for(int i=1; i<=10; i++) {
            Label tmpLab = (Label) getClass().getDeclaredField("label" + i).get(this);
            double probability = gameModel.probDrawCard(Integer.parseInt(tmpLab.getText()), Integer.parseInt(totalCards.getText()));
            Label drawNum = (Label) getClass().getDeclaredField("probNum" + i).get(this);
            drawNum.setText(String.format("%.2f%%", probability * 100));
        }
    }

    /**
     * @brief Sets the number of decks in play.
     *
     * @param decks The number of decks to use.
     */
    private void setCardsNum(int decks) throws NoSuchFieldException, IllegalAccessException {
        deleteGUIValue();
        for (int i = 2; i <= 9; i++) {
            cardCounts.put(i, 4 * decks);
        }
        cardCounts.put(10, 16 * decks);
        cardCounts.put(1, 4 * decks);

        // Set the label values based on the number of decks
        for (Label label : labels) {
            labelValues.put(label, 4 * decks);
        }
        labelValues.put(label10, 16 * decks);
        totalCards.setText(String.valueOf(52 * decks));

        updateLabels();
    }

    /**
     * @brief Updates the labels for the remaining cards.
     */
    private void updateLabels() {
        for (Map.Entry<Label, Integer> entry : labelValues.entrySet()) {
            entry.getKey().setText(String.valueOf(entry.getValue()));
        }
    }

    /**
     * @brief Updates the probabilities for the player's hand.
     *
     * @throws NoSuchFieldException If reflection fails to access fields.
     * @throws IllegalAccessException If reflection access is denied.
     */
    private void updateProbabilities() throws NoSuchFieldException, IllegalAccessException {
        double probability = gameModel.probToBust(gameModel.getPlayerHandValue(), cardCounts, Integer.parseInt(totalCards.getText()));
        probToBust.setText(String.format("%.2f%%", probability * 100));

        // Update probabilities for the player's hand values (12 to 21)
        updateProbLabels("probOf", gameModel.calculateProbabilities(cardCounts, Integer.parseInt(totalCards.getText())));
    }

    /**
     * @brief Updates the probability labels for values from 12 to 21.
     *
     * @param prefix The prefix for the probability labels (e.g., "probOf").
     * @param probabilities The map containing probabilities for each value.
     * @throws NoSuchFieldException If reflection fails to access fields.
     * @throws IllegalAccessException If reflection access is denied.
     */
    private void updateProbLabels(String prefix, Map<Integer, Double> probabilities) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 12; i <= 21; i++) {
            Label probLabel = (Label) getClass().getDeclaredField(prefix + i).get(this);
            double probability = probabilities.getOrDefault(i, 0.0);
            probLabel.setText(String.format("%.2f%%", probability * 100));
        }
    }

    /**
     * @brief Updates the dealer's probabilities for busting and specific hand values.
     *
     * @throws NoSuchFieldException If reflection fails to access fields.
     * @throws IllegalAccessException If reflection access is denied.
     */
    private void updateDealerProbabilities() throws NoSuchFieldException, IllegalAccessException {
        double probability = gameModel.probToBust(gameModel.getDealerHandValue(), cardCounts, Integer.parseInt(totalCards.getText()));
        dealerProbOfBust.setText(String.format("%.2f%%", probability * 100));
        // Update dealer's probabilities for hand values 17 to 21
        updateDealerProbLabels("dealerProbOf", gameModel.calculateDealerProbabilities(cardCounts, Integer.parseInt(totalCards.getText()), gameModel.getDealerHandValue()));
    }

    /**
     * @brief Updates the dealer's probability labels for values from 17 to 21.
     *
     * @param prefix The prefix for the probability labels (e.g., "dealerProbOf").
     * @param probabilities The map containing probabilities for each value.
     * @throws NoSuchFieldException If reflection fails to access fields.
     * @throws IllegalAccessException If reflection access is denied.
     */
    private void updateDealerProbLabels(String prefix, Map<Integer, Double> probabilities) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 17; i <= 21; i++) {
            // Use reflection to access the correct label dynamically
            Label probLabel = (Label) getClass().getDeclaredField(prefix + i).get(this);
            double probability = probabilities.getOrDefault(i, 0.0);
            probLabel.setText(String.format("%.2f%%", probability * 100));
        }
    }

    /**
     * @brief Starts a new turn by resetting the necessary values.
     *
     * @throws NoSuchFieldException If reflection fails to access fields.
     * @throws IllegalAccessException If reflection access is denied.
     */
    private void setNewTurn() throws NoSuchFieldException, IllegalAccessException {
        deleteGUIValue();
    }

    /**
     * @brief Resets all values to their initial state.
     *
     * @throws NoSuchFieldException If reflection fails to access fields.
     * @throws IllegalAccessException If reflection access is denied.
     */
    private void resetAll() throws NoSuchFieldException, IllegalAccessException {
        setCardsNum(1);
        decksNum.setText("1");
        deleteGUIValue();
    }

    /**
     * @brief Resets the GUI values, including hands and probabilities.
     */
    private void deleteGUIValue() throws NoSuchFieldException, IllegalAccessException {
        gameModel.resetPlayerHand();
        gameModel.resetDealerHand();
        playerHand.setStyle("-fx-text-fill: black;");
        playerHand.setText("0");
        probToBust.setText("0.00%");
        dealerHand.setText("0");
        bestChoice.setText("Hit");
        dealerProbOfBust.setText("0.00%");
        handCards.setText("");

        for (int i = 12; i <= 21; i++) {
            String prefix = "probOf";
            Label probLabel = (Label) getClass().getDeclaredField(prefix + i).get(this);
            probLabel.setText("0,00%");
        }

        for (int i = 17; i <= 21; i++) {
            String prefix = "dealerProbOf";
            Label probLabel = (Label) getClass().getDeclaredField(prefix + i).get(this);
            probLabel.setText("0,00%");
        }

        dealerTurn.setDisable(false);
        myTurn.setDisable(false);
        setProbToDrawCards();
    }

    /**
     * @brief Retrieves the player's probabilities to achieve hand values from 12 to 21.
     *
     * @throws NoSuchFieldException If the field corresponding to the label is not found.
     * @throws IllegalAccessException If the field is not accessible.
     *
     * @return A map where the keys are hand values from 12 to 21, and the values are the probabilities
     *         (between 0 and 1) of the player achieving those values.
     */
    private Map<Integer, Double> getPlayerProbabilities() throws NoSuchFieldException, IllegalAccessException {
        Map<Integer, Double> probabilities = new HashMap<>();
        for (int i = 12; i <= 21; i++) {
            Label probLabel = (Label) getClass().getDeclaredField("probOf" + i).get(this);
            String probText = probLabel.getText().replace("%", "").replace(",", ".");
            probabilities.put(i, Double.parseDouble(probText) / 100);
        }
        return probabilities;
    }

    /**
     * @brief Retrieves the dealer's probabilities to achieve hand values from 17 to 21.
     *
     * @throws NoSuchFieldException If the field corresponding to the label is not found.
     * @throws IllegalAccessException If the field is not accessible.
     *
     * @return A map where the keys are hand values from 17 to 21, and the values are the probabilities
     *         (between 0 and 1) of the dealer achieving those values.
     */
    private Map<Integer, Double> getDealerProbabilities() throws NoSuchFieldException, IllegalAccessException {
        Map<Integer, Double> probabilities = new HashMap<>();
        for (int i = 17; i <= 21; i++) {
            Label probLabel = (Label) getClass().getDeclaredField("dealerProbOf" + i).get(this);
            String probText = probLabel.getText().replace("%", "").replace(",", ".");
            probabilities.put(i, Double.parseDouble(probText) / 100);
        }
        return probabilities;
    }
}
