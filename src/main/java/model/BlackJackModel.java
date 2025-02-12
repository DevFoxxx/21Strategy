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
        System.out.println(dealerTurn);
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public boolean isDealerTurn() {
        return isDealerTurn;
    }

    public void addCard(int value) {
        if (isMyTurn) {
            playerHand.add(value);
        }
    }

    public void addDealerCard(int value) {
        if (isDealerTurn) {
            dealerHand.add(value);
        }
    }

    public int getPlayerHandValue() {
        return playerHand.stream().mapToInt(Integer::intValue).sum();
    }

    public int getDealerHandValue() {
        return dealerHand.stream().mapToInt(Integer::intValue).sum();
    }

    public void resetPlayerHand() {
        playerHand.clear();
    }

    public void resetDealerHand() {
        dealerHand.clear();
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

    public Map<Integer, Double> calculateDealerProbabilities(Map<Integer, Integer> cardCounts, int totalCards, int currentSum) {
        Map<Integer, Double> probabilities = new HashMap<>();

        // Inizializza tutte le probabilità a 0
        for (int i = 17; i <= 21; i++) {
            probabilities.put(i, 0.0);
        }

        // Calcola la probabilità ricorsivamente
        calculateProbabilityForDealer(cardCounts, totalCards, currentSum, 1.0, probabilities);

        // Normalizza le probabilità
        double totalProbability = probabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalProbability > 0) {
            for (int i = 17; i <= 21; i++) {
                probabilities.put(i, probabilities.get(i) / totalProbability);
            }
        }

        return probabilities;
    }

    private void calculateProbabilityForDealer(Map<Integer, Integer> cardCounts, int totalCards, int currentSum, double currentProbability, Map<Integer, Double> probabilities) {
        // Se la somma è tra 17 e 21, aggiungi la probabilità corrente
        if (currentSum >= 17 && currentSum <= 21) {
            probabilities.put(currentSum, probabilities.getOrDefault(currentSum, 0.0) + currentProbability);
            return;
        }

        // Se la somma è maggiore di 21, non calcolare ulteriori probabilità
        if (currentSum > 21) {
            return;
        }

        // Itera su tutte le carte rimanenti nel mazzo
        for (Map.Entry<Integer, Integer> entry : cardCounts.entrySet()) {
            int cardValue = entry.getKey();
            int count = entry.getValue();

            if (count == 0) continue;  // Salta le carte esaurite

            // Calcola la probabilità di pescare questa carta
            double cardProbability = (double) count / totalCards;

            // Calcola la nuova somma con la carta estratta
            int newSum = currentSum + cardValue;

            // Rimuovi temporaneamente la carta dal mazzo
            cardCounts.put(cardValue, count - 1);
            totalCards--;

            // Calcola ricorsivamente la probabilità per la nuova somma
            calculateProbabilityForDealer(cardCounts, totalCards, newSum, currentProbability * cardProbability, probabilities);

            // Ripristina la carta nel mazzo (backtracking)
            cardCounts.put(cardValue, count);
            totalCards++;
        }
    }
}
