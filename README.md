# 21Strategy - README

![img.png](img.png)

## Introduction 🃏
21Strategy is a software designed to calculate probabilities in the game of blackjack. By utilizing advanced probabilistic algorithms, the program suggests the optimal strategy to follow, taking into account the cards already played and the possible future values, both for the player and the dealer. A great tool for anyone looking to improve their technique and understand the dynamics of the game better.

## Disclaimer ⚠️
**This software is developed exclusively for educational and study purposes.** It is recommended to comply with local laws and regulations regarding gambling in your country or state. **The software should not be used for malicious or illegal purposes.** The author takes no responsibility for any misuse of the program. Remember: always gamble responsibly!

## Features ✨
- **Graphical User Interface (GUI):** A simple and intuitive interface that allows users to input data such as the number of decks in play, cards already dealt, the dealer's visible card, and when it’s the player’s turn. Not pretty but easy to use.
- **Probability Calculation:** The software calculates and shows:
    - The probability of the player and the dealer going bust.
    - The possible values the dealer could have (from 17 to 21).
    - The future values for the player based on their current hand (from hand+1 up to 21).
- **Strategic Suggestions:** Provides the optimal strategy for the player based on the entered data, enhancing the chances of winning.
- **And more...**

## Code Accuracy & Strategy 📊

## 🔍 How It Works
**21Strategy** calculates probabilities in blackjack by considering the current hand state and remaining deck composition, providing insights for both the player and dealer.

### **Player Probability Calculation** 🃏
- Determines the chance of drawing a value from **hand+1 to 21**.
- Considers only **one draw at a time**, but updates dynamically with each new card, ensuring real-time accuracy.
- Efficient and quick calculations using **direct probability extraction from the deck composition**.
- Does **not limit the number of draws**, as the strategy updates with each new card.

### **Dealer Probability Calculation** 🎲
- Uses **recursion** to simulate all possible dealer hands from **17 to 21**.
- Accounts for **multiple draws**, following the **blackjack rules** (dealer must hit until at least 17).
- Ensures a **precise probability distribution** by exploring all possible card sequences.

### **Additional Considerations** 📊
- The software continuously updates its calculations **as cards are drawn**, maintaining an accurate probability model.
- Designed to be **lightweight and efficient**, with future improvements aimed at optimizing **computation times and memory usage**.

### ⚡ **Strengths & Benefits**
✅ **Efficient:** Optimized calculations for real-time probability analysis.  
✅ **Accurate Dealer Simulation:** Considers all possible dealer outcomes.  
✅ **Dynamic Updates:** Always reflects the latest game state.  
✅ **Strategic Insights:** Helps users make better, **mathematically sound decisions**.

### 🔧 **Future Improvements**
- **Advanced Strategy Implementation:** Incorporating Monte Carlo simulations for deeper insights.
- **Better GUI Feedback:** More intuitive probability visualizations.

This makes 21Strategy a **powerful tool** for understanding blackjack probabilities and improving gameplay strategy. 🚀

## Technologies Used 💻
- **Java:** The main programming language.
- **JavaFX:** For creating a responsive and modern graphical user interface.
- **SceneBuilder:** To design and visually preview the user interface.
- **CSS:** For styling the GUI, making the user experience even more enjoyable.

## Contributions 🤝
Contribute to this project:
- **Bug Fixing:** Help fix any issues in the software.
- **Feature Additions:** Implement new features and optimize existing ones.
- **Algorithm Optimization:** Improve **computation times** and **memory efficiency** for faster and smoother performance.
- **Advanced Strategy Implementation:** Introduce **Monte Carlo simulations** for more in-depth probability analysis.
- **Better GUI Feedback:** Improve **probability visualization** for a more intuitive user experience.

All contributions are welcome! We’re always looking for new ideas and solutions. 
## License 📜
This project is licensed under the **MIT License**. You are free to use, modify, and distribute it as long as the license is included. Open source is for everyone! 

