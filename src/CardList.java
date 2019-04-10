import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is the parent class of all classes that contains a list of cards. It provides default initializer and a
 * card search method.
 */
public abstract class CardList extends ArrayList<Card> {

    /**
     * This constructor takes the max capacity of the list and sets default size of the ArrayList to the designated
     * size.
     *
     * @param maxCapacity the max capacity of the card list
     */
    CardList(int maxCapacity) {
        super(maxCapacity);
    }

    /**
     * This constructor takes a prepared card list as the default values of the current card list
     *
     * @param initialCards the initial values of the card list
     */
    CardList(ArrayList<Card> initialCards) {
        super(initialCards);
    }

    /**
     * This methods fetches a random selected card from all possible cards in the current card list that meets all the
     * requirements given
     *
     * @param leader  the leader of the wanted card, set to null if not required
     * @param trait   the trait of the card, set to null if not required
     * @param type    type of the wanted card, set to null if not required
     * @param maxCost maximum cost of the card(inclusive), set to {@linkplain Card#MAX_COST} if not required
     * @param minCost minimum cost of the card(inclusive), set to {@linkplain Card#MIN_COST} if not required
     * @param remove  true if the acquired card has to be removed from the list, false otherwise
     * @return a random selected card from all possible cards in the current card list that meets all the requirements
     * given
     */
    public Card getRandomRequestedCard(Leader.LeaderType leader, Trait.SwordCraftTrait trait, Card.Type type, int maxCost, int minCost, boolean remove) {
        Collections.shuffle(this);

        for (Card c : this) {
            if ((type == null || c.getType() == type) && (leader == null || leader == c.getLeader()) &&
                    (trait == null || (c instanceof Follower && ((Follower) c).getSwordCraftTrait() == trait)) &&
                    c.getCost() >= minCost && c.getCost() <= maxCost) {
                if (remove)
                    remove(c);
                return c;
            }
        }

        return null;
    }

}
