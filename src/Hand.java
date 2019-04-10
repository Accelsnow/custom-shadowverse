/**
 * This class is the representation of one player's hand. It contains all cards in hand.
 */
public final class Hand extends CardList {
    private static final int MAXIMUM_NUM_OF_CARDS = 9;

    /**
     * The constructor initializes the hand with a default size of {@linkplain #MAXIMUM_NUM_OF_CARDS}
     */
    Hand() {
        super(MAXIMUM_NUM_OF_CARDS);
    }

    /**
     * Add a card to the hand. The hand's size must not exceed {@linkplain #MAXIMUM_NUM_OF_CARDS} after addition.
     *
     * @param card the card to be added
     * @return true if the card is successfully added, false otherwise
     */
    public boolean add(Card card) {
        if (card != null && size() < MAXIMUM_NUM_OF_CARDS) {
            super.add(card);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add multiple cards to the hand. The hand's size must not exceed {@linkplain #MAXIMUM_NUM_OF_CARDS} after
     * addition.
     *
     * @param cards the array of cards to be added
     * @return true if the cards are all successfully added, false if any one of them failed to be added
     */
    public boolean add(Card[] cards) {
        for (Card c : cards)
            if (!add(c))
                return false;
        return true;
    }

}
