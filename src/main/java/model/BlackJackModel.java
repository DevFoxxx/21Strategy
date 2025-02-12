package model;

import java.util.*;

/**
 * @brief Blackjack game model that handles the game logic and probability calculations.
 */
public class BlackJackModel {
    private boolean isMyTurn = false, isDealerTurn = false;
    private final List<Integer> playerHand = new ArrayList<>(), dealerHand = new ArrayList<>();

    /**
     * @brief Sets the player's turn.
     *
     * @param myTurn Boolean indicating if it's the player's turn.
     */
    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    /**
     * @brief Sets the dealer's turn.
     *
     * @param dealerTurn Boolean indicating if it's the dealer's turn.
     */
    public void setDealerTurn(boolean dealerTurn) {
        isDealerTurn = dealerTurn;
    }

    /**
     * @brief Returns true if it's the player's turn.
     *
     * @return True if it's the player's turn, false otherwise.
     */
    public boolean isMyTurn() {
        return isMyTurn;
    }

    /**
     * @brief Returns true if it's the dealer's turn.
     *
     * @return True if it's the dealer's turn, false otherwise.
     */
    public boolean isDealerTurn() {
        return isDealerTurn;
    }

    /**
     * @brief Adds a card to the player's hand.
     *
     * @param value The value of the card to be added.
     */
    public void addCard(int value) {
        if (isMyTurn) {
            playerHand.add(value);
        }
    }

    /**
     * @brief Adds a card to the dealer's hand.
     *
     * @param value The value of the card to be added.
     */
    public void addDealerCard(int value) {
        if (isDealerTurn) {
            dealerHand.add(value);
        }
    }

    /**
     * @brief Calculates the total value of the player's hand.
     *
     * @return The total value of the player's hand.
     */
    public int getPlayerHandValue() {
        return playerHand.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * @brief Calculates the total value of the dealer's hand.
     *
     * @return The total value of the dealer's hand.
     */
    public int getDealerHandValue() {
        return dealerHand.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * @brief Resets the player's hand.
     */
    public void resetPlayerHand() {
        playerHand.clear();
    }

    /**
     * @brief Resets the dealer's hand.
     */
    public void resetDealerHand() {
        dealerHand.clear();
    }

    /**
     * @brief Calculates the probability that the player will bust (go over 21).
     *
     * @param playerHandValue The current value of the player's hand.
     * @param cardCounts A map containing the counts of each remaining card.
     * @param totalCards The total number of remaining cards.
     *
     * @return The probability of busting.
     */
    public double probToBust(int playerHandValue, Map<Integer, Integer> cardCounts, int totalCards) {
        if (playerHandValue < 12) {
            return 0.0; // Impossible to bust
        }

        int bustThreshold = 22 - playerHandValue;
        int bustCardsCount = 0;

        for (int cardValue = bustThreshold; cardValue <= 10; cardValue++) {
            bustCardsCount += cardCounts.getOrDefault(cardValue, 0);
        }

        return (double) bustCardsCount / totalCards;
    }

    /**
     * @brief Calculates the probabilities for the player to achieve values from 12 to 21.
     *
     * @param cardCounts A map containing the counts of each remaining card.
     * @param totalCards The total number of remaining cards.
     *
     * @return A map containing the probabilities for each target value (from 12 to 21).
     */
    public Map<Integer, Double> calculateProbabilities(Map<Integer, Integer> cardCounts, int totalCards) {
        Map<Integer, Double> probabilities = new HashMap<>();
        int currentSum = getPlayerHandValue();

        for (int target = 12; target <= 21; target++) {
            probabilities.put(target, calculateProbabilityForTarget(currentSum, target, cardCounts, totalCards));
        }

        return probabilities;
    }

    /**
     * @brief Calculates the probability for the player to reach a specific target value.
     *
     * @param currentSum The current sum of the player's hand.
     * @param target The target value the player is trying to achieve.
     * @param cardCounts A map containing the counts of each remaining card.
     * @param totalCards The total number of remaining cards.
     *
     * @return The probability of achieving the target value.
     */
    private double calculateProbabilityForTarget(int currentSum, int target, Map<Integer, Integer> cardCounts, int totalCards) {
        if (currentSum >= target) return 0.0;

        int needed = target - currentSum;
        return cardCounts.getOrDefault(needed, 0) / (double) totalCards;
    }

    /**
     * @brief Calculates the probabilities for the dealer to achieve values between 17 and 21.
     *
     * @param cardCounts A map containing the counts of each remaining card.
     * @param totalCards The total number of remaining cards.
     * @param currentSum The current sum of the dealer's hand.
     *
     * @return A map containing the probabilities for the dealer to achieve values from 17 to 21.
     */
    public Map<Integer, Double> calculateDealerProbabilities(Map<Integer, Integer> cardCounts, int totalCards, int currentSum) {
        Map<Integer, Double> probabilities = new HashMap<>();

        // Initialize all probabilities to 0
        for (int i = 17; i <= 21; i++) {
            probabilities.put(i, 0.0);
        }

        // Recursively calculate the probabilities
        calculateProbabilityForDealer(cardCounts, totalCards, currentSum, 1.0, probabilities);

        // Normalize the probabilities
        double totalProbability = probabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalProbability > 0) {
            for (int i = 17; i <= 21; i++) {
                probabilities.put(i, probabilities.get(i) / totalProbability);
            }
        }

        return probabilities;
    }

    /**
     * @brief Recursively calculates the probability of the dealer achieving a value between 17 and 21.
     *
     * @param cardCounts A map containing the counts of each remaining card.
     * @param totalCards The total number of remaining cards.
     * @param currentSum The current sum of the dealer's hand.
     * @param currentProbability The current probability of the dealer achieving the target sum.
     * @param probabilities A map to store the final probabilities.
     */
    private void calculateProbabilityForDealer(Map<Integer, Integer> cardCounts, int totalCards, int currentSum, double currentProbability, Map<Integer, Double> probabilities) {
        // If the sum is between 17 and 21, add the current probability
        if (currentSum >= 17 && currentSum <= 21) {
            probabilities.put(currentSum, probabilities.getOrDefault(currentSum, 0.0) + currentProbability);
            return;
        }

        // If the sum is greater than 21, no further probabilities can be calculated
        if (currentSum > 21) {
            return;
        }

        // Iterate through all remaining cards in the deck
        for (Map.Entry<Integer, Integer> entry : cardCounts.entrySet()) {
            int cardValue = entry.getKey();
            int count = entry.getValue();

            if (count == 0) continue;  // Skip exhausted cards

            // Calculate the probability of drawing this card
            double cardProbability = (double) count / totalCards;

            // Calculate the new sum with the drawn card
            int newSum = currentSum + cardValue;

            // Temporarily remove the card from the deck
            cardCounts.put(cardValue, count - 1);
            totalCards--;

            // Recursively calculate the probability for the new sum
            calculateProbabilityForDealer(cardCounts, totalCards, newSum, currentProbability * cardProbability, probabilities);

            // Restore the card in the deck (backtracking)
            cardCounts.put(cardValue, count);
            totalCards++;
        }
    }
}
