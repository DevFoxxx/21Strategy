package model;

import java.util.*;

public class BlackJackModel {
    private boolean isMyTurn = false, isDealerTurn = false;
    private final List<Integer> playerHand = new ArrayList<>(), dealerHand = new ArrayList<>();

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public void setDealerTurn(boolean dealerTurn) {
        isDealerTurn = dealerTurn;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void addCard(int value) {
        if (isMyTurn) {
            playerHand.add(value);
        }
    }

    public int getPlayerHandValue() {
        return playerHand.stream().mapToInt(Integer::intValue).sum();
    }

    public void resetPlayerHand() {
        playerHand.clear();
    }

    public double probToBust(int playerHandValue, Map<Integer, Integer> cardCounts, int totalCards) {
        if (playerHandValue < 12) {
            return 0.0; // Impossibile sballare
        }

        int bustThreshold = 22 - playerHandValue;
        int bustCardsCount = 0;

        for (int cardValue = bustThreshold; cardValue <= 10; cardValue++) {
            bustCardsCount += cardCounts.getOrDefault(cardValue, 0);
        }

        return (double) bustCardsCount / totalCards;
    }

    public Map<Integer, Double> calculateProbabilities(Map<Integer, Integer> cardCounts, int totalCards) {
        Map<Integer, Double> probabilities = new HashMap<>();
        int currentSum = getPlayerHandValue();

        for (int target = 12; target <= 21; target++) {
            probabilities.put(target, calculateProbabilityForTarget(currentSum, target, cardCounts, totalCards));
        }

        return probabilities;
    }

    private double calculateProbabilityForTarget(int currentSum, int target, Map<Integer, Integer> cardCounts, int totalCards) {
        if (currentSum >= target) return 0.0;

        int needed = target - currentSum;
        return cardCounts.getOrDefault(needed, 0) / (double) totalCards;
    }
}
