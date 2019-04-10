/**
 * This class is the representation of one player's grave. It contains all cards on field and methods for grave-related
 * interactions (necromancy).
 */
public final class Grave extends CardList {
    /**
     * Necromancy keeps track of cards in the grave. It can be consumed to activate special/advanced effects for
     * {@linkplain Leader.LeaderType#SHADOWCRAFT} cards. Upon consumption, the grave's size will decrease simultaneously
     * as well.
     */
    private int necromancy = 0;

    /**
     * The constructor sets default grave size to default deck size. However, the actual amount of cards in the grave
     * may exceed the deck size, as tokens may be summoned and disposed into the grave.
     */
    Grave() {
        super(Deck.DEFAULT_DECK_SIZE);
    }

    /**
     * Add a card to the grave. It increases {@linkplain #necromancy} by 1.
     *
     * @param card the card to be sent to grave
     * @return true if the card is successfully added to the grave, false otherwise
     */
    public boolean add(Card card) {
        if (super.add(card)) {
            necromancy++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the current necromancy value
     */
    public int getNecromancy() {
        return necromancy;
    }

    /**
     * Consume a certain amount of necromancy point as well as actual cards in the grave.
     *
     * @param usage the amount to be consumed
     * @return true if the consumption is successful, false if necromancy point is insufficient
     */
    public boolean useNecromancy(int usage) {
        if (necromancy >= usage) {
            necromancy -= usage;
            for (int n = 0; n < usage && size() > 0; n++)
                remove(0);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Add a certain amount of necromancy point.
     *
     * @param amount the amount of necromancy point to be added
     */
    public void addNecromancy(int amount) {
        necromancy += amount;
    }
}
