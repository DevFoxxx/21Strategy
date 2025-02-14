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
     * @brief Calculate the probability of drawing a card and format the result.
     *
     * @param cardCount The number of cards remaining of a given value.
     * @param totalCards The total number of cards remaining in the deck.
     * @return The probability
     */
    public double probDrawCard(int cardCount, int totalCards) {
        if (totalCards > 0) {
            return (double) cardCount / totalCards;
        }
        return 0;
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
     * @brief Calculates the total value of the player's hand, considering the flexible value of aces.
     *
     * @return The total value of the player's hand.
     */
    public int getPlayerHandValue() {
        return calculateHandValue(playerHand);
    }

    /**
     * @brief Calculates the total value of the dealer's hand, considering the flexible value of aces.
     *
     * @return The total value of the dealer's hand.
     */
    public int getDealerHandValue() {
        return calculateHandValue(dealerHand);
    }

    /**
     * @brief Calculates the total value of a hand, considering the flexible value of aces.
     *
     * @param hand The hand to calculate the value for.
     * @return The total value of the hand.
     */
    private int calculateHandValue(List<Integer> hand) {
        int sum = 0;
        int aceCount = 0;

        // Sum all cards, counting aces as 11
        for (int card : hand) {
            if (card == 1) {
                sum += 11;
                aceCount++;
            } else {
                sum += card;
            }
        }

        // Adjust for aces if the sum is over 21
        while (sum > 21 && aceCount > 0) {
            sum -= 10; // Change an ace from 11 to 1
            aceCount--;
        }

        return sum;
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
     * @brief Calculates the probability for the player to reach a specific target value, considering the flexible value of aces.
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
        double probability = 0.0;

        // Consider the case where the needed card is an ace (1 or 11)
        if (needed == 1) {
            // Ace can be 1 or 11
            probability += cardCounts.getOrDefault(1, 0) / (double) totalCards;
        } else if (needed == 11) {
            // Ace can be 11
            probability += cardCounts.getOrDefault(1, 0) / (double) totalCards;
        } else {
            // Regular card
            probability += cardCounts.getOrDefault(needed, 0) / (double) totalCards;
        }

        return probability;
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
     * @brief Recursively calculates the probability of the dealer achieving a value between 17 and 21, considering the flexible value of aces.
     *
     * @param cardCounts A map containing the counts of each remaining card.
     * @param totalCards The total number of remaining cards.
     * @param currentSum The current sum of the dealer's hand.
     * @param currentProbability The current probability of the dealer achieving the target sum.
     * @param probabilities A map to store the final probabilities.
     */
    private void calculateProbabilityForDealer(Map<Integer, Integer> cardCounts, int totalCards, int currentSum, double currentProbability, Map<Integer, Double> probabilities) {
        // Calculate the current hand value considering aces
        int currentHandValue = calculateHandValue(dealerHand);

        // If the sum is between 17 and 21, add the current probability
        if (currentHandValue >= 17 && currentHandValue <= 21) {
            probabilities.put(currentHandValue, probabilities.getOrDefault(currentHandValue, 0.0) + currentProbability);
            return;
        }

        // If the sum is greater than 21, no further probabilities can be calculated
        if (currentHandValue > 21) {
            return;
        }

        // Iterate through all remaining cards in the deck
        for (Map.Entry<Integer, Integer> entry : cardCounts.entrySet()) {
            int cardValue = entry.getKey();
            int count = entry.getValue();

            if (count == 0) continue;  // Skip exhausted cards

            // Calculate the probability of drawing this card
            double cardProbability = (double) count / totalCards;

            // Temporarily remove the card from the deck
            cardCounts.put(cardValue, count - 1);
            totalCards--;

            // Add the card to the dealer's hand
            dealerHand.add(cardValue);

            // Recursively calculate the probability for the new sum
            calculateProbabilityForDealer(cardCounts, totalCards, currentSum + cardValue, currentProbability * cardProbability, probabilities);

            // Restore the card in the deck (backtracking)
            cardCounts.put(cardValue, count);
            totalCards++;
            dealerHand.remove(dealerHand.size() - 1); // Remove the last card added
        }
    }

    /**
     * @brief Determines the best strategy for the player based on probabilities.
     *
     * @param playerHandValue The current value of the player's hand.
     * @param dealerHandValue The current value of the dealer's hand.
     * @param probToBust The probability that the player will bust.
     * @param probToBustDealer The probability that the dealer will bust.
     * @param playerProbabilities The probabilities for the player to achieve values from 12 to 21.
     * @param dealerProbabilities The probabilities for the dealer to achieve values from 17 to 21.
     * @return A string indicating the best strategy
     */
    public String bestChoice(int playerHandValue, int dealerHandValue, double probToBust, double probToBustDealer,
                             Map<Integer, Double> playerProbabilities, Map<Integer, Double> dealerProbabilities) {
        if (playerHandValue == 21) {
            return "Stand";
        }

        if (probToBust > 0.51 && probToBustDealer < 0.69) {
            return "Stand";
        }

        if (probToBustDealer > 0.70) {
            boolean shouldHit = shouldPlayerHit(playerProbabilities, dealerProbabilities);
            if (shouldHit) {
                return "Hit";
            }
        }

        double winProbability = 1 - probToBust;
        if (winProbability > 0.75 && playerHandValue >= 9 && playerHandValue <= 11) {
            return "Double Down";
        }

        return "Hit";
    }

    /**
     * @brief Determines whether the player should hit based on probability comparisons.
     *
     * @param playerProbabilities A map containing the player's probability of reaching each hand value.
     * @param dealerProbabilities A map containing the dealer's probability of holding each hand value.
     * @return true if the player should hit, false otherwise.
     */

    private boolean shouldPlayerHit(Map<Integer, Double> playerProbabilities, Map<Integer, Double> dealerProbabilities) {
        int[] handValues = {17, 18, 19, 20, 21};

        for (int value : handValues) {
            double playerProb = playerProbabilities.getOrDefault(value, 0.0);
            double dealerProb = dealerProbabilities.getOrDefault(value, 0.0);

            if (playerProb < dealerProb) {
                return true;
            }
        }

        return false;
    }
}