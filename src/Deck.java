import java.util.Arrays;
import java.util.Collections;

/**
 * This class is the representation of the player's deck. It also contains preset decks that are available for the
 * players to use. Some players may not have a preset deck available to be used yet. Currently only Fairycraft and
 * Swordcraft have available decks.
 */
public final class Deck extends CardList {
    public static final int DEFAULT_DECK_SIZE = 40;
    /**
     * The apocalypse deck. Only used when Prince of Darkness is played.
     */
    private static final Card[] APOCALYPSE;
    /**
     * The preset deck for Swordcraft
     */
    private static final Card[] SPEED_SWORD;
    /**
     * The preset deck for Forestcraft
     */
    private static final Card[] PURGATORY_FOREST;

    static {
        APOCALYPSE = new Card[]{CardLib.getCard("Servant_of_Darkness"), CardLib.getCard("Servant_of_Darkness"),
                CardLib.getCard("Servant_of_Darkness"), CardLib.getCard("Silent_Rider"),
                CardLib.getCard("Silent_Rider"), CardLib.getCard("Silent_Rider"),
                CardLib.getCard("Dis's_Damnation"), CardLib.getCard("Dis's_Damnation"),
                CardLib.getCard("Dis's_Damnation"), CardLib.getCard("Astaroth's_Reckoning")};

        PURGATORY_FOREST = new Card[]{
                CardLib.getCard("Water_Fairy"), CardLib.getCard("Water_Fairy"), CardLib.getCard("Water_Fairy"),
                CardLib.getCard("Elf_Child_May"), CardLib.getCard("Elf_Child_May"), CardLib.getCard("Elf_Child_May"),
                CardLib.getCard("Nature's_Guidance"), CardLib.getCard("Nature's_Guidance"), CardLib.getCard("Nature's_Guidance"),
                CardLib.getCard("Fairy_Circle"), CardLib.getCard("Fairy_Circle"), CardLib.getCard("Fairy_Circle"),
                CardLib.getCard("Woodland_Refuge"), CardLib.getCard("Woodland_Refuge"),
                CardLib.getCard("Altered_Fate"), CardLib.getCard("Altered_Fate"), CardLib.getCard("Altered_Fate"),
                CardLib.getCard("Fairy_Whisperer"), CardLib.getCard("Fairy_Whisperer"), CardLib.getCard("Fairy_Whisperer"),
                CardLib.getCard("Rhinoceroach"), CardLib.getCard("Rhinoceroach"), CardLib.getCard("Rhinoceroach"),
                CardLib.getCard("Sylvan_Justice"), CardLib.getCard("Sylvan_Justice"), CardLib.getCard("Sylvan_Justice"),
                CardLib.getCard("Pixie_Mischief"), CardLib.getCard("Pixie_Mischief"), CardLib.getCard("Pixie_Mischief"),
                CardLib.getCard("Ancient_Elf"), CardLib.getCard("Ancient_Elf"), CardLib.getCard("Ancient_Elf"),
                CardLib.getCard("Path_to_Purgatory"), CardLib.getCard("Path_to_Purgatory"), CardLib.getCard("Path_to_Purgatory"),
                CardLib.getCard("Will_of_the_Forest"), CardLib.getCard("Will_of_the_Forest"), CardLib.getCard("Will_of_the_Forest"),
                CardLib.getCard("Homecoming"), CardLib.getCard("Homecoming")};

        SPEED_SWORD = new Card[]{
                CardLib.getCard("Quickblader"), CardLib.getCard("Quickblader"), CardLib.getCard("Quickblader"),
                CardLib.getCard("Ninja_Trainee"), CardLib.getCard("Ninja_Trainee"), CardLib.getCard("Ninja_Trainee"),
                CardLib.getCard("Vanguard"), CardLib.getCard("Vanguard"), CardLib.getCard("Vanguard"),
                CardLib.getCard("Keen_Enchantment"), CardLib.getCard("Keen_Enchantment"), CardLib.getCard("Keen_Enchantment"),
                CardLib.getCard("Oathless_Knight"), CardLib.getCard("Oathless_Knight"), CardLib.getCard("Oathless_Knight"),
                CardLib.getCard("Kunoichi_Trainee"), CardLib.getCard("Kunoichi_Trainee"), CardLib.getCard("Kunoichi_Trainee"),
                CardLib.getCard("Palace_Fencer"), CardLib.getCard("Palace_Fencer"), CardLib.getCard("Palace_Fencer"),
                CardLib.getCard("Centaur_Vanguard"), CardLib.getCard("Centaur_Vanguard"), CardLib.getCard("Centaur_Vanguard"),
                CardLib.getCard("Princess_Vanguard"), CardLib.getCard("Princess_Vanguard"), CardLib.getCard("Princess_Vanguard"),
                CardLib.getCard("Novice_Trooper"), CardLib.getCard("Novice_Trooper"), CardLib.getCard("Novice_Trooper"),
                CardLib.getCard("Demonic_Strike"), CardLib.getCard("Demonic_Strike"), CardLib.getCard("Demonic_Strike"),
                CardLib.getCard("White_General"), CardLib.getCard("White_General"), CardLib.getCard("White_General"),
                CardLib.getCard("Tsubaki"), CardLib.getCard("Tsubaki"), CardLib.getCard("Alwida's_Command"),
                CardLib.getCard("Alwida's_Command")};
    }

    /**
     * The constructor takes a leader reference and initializes the deck to the preset deck of that leader. The deck
     * will be shuffled after initialization.
     *
     * @param leader the leader to initialize from
     */
    Deck(Leader leader) {
        super(DEFAULT_DECK_SIZE);

        switch (leader.getLeaderType()) {
            case FORESTCRAFT:
                addAll(Arrays.asList(PURGATORY_FOREST));
                break;

            case SWORDCRAFT:
                addAll(Arrays.asList(SPEED_SWORD));
                break;

            default:
                throw new IllegalStateException("NO PRESET DECK FOUND");
        }

        Collections.shuffle(this);
    }

    /**
     * Add a card to the deck and then shuffles it
     *
     * @param card the card to be added
     * @return true if the card is successfully added
     */
    public boolean add(Card card) {
        super.add(card);
        Collections.shuffle(this);
        return true;
    }

    /**
     * Replace the deck with the deck of the given deck code and then shuffles it.
     *
     * @param deckCode the deck code of the designated deck
     * @throws IllegalArgumentException if the given deck code does not exist
     */
    public void replaceDeck(String deckCode) {
        clear();
        Card[] target;

        switch (deckCode) {
            case "APOCALYPSE":
                target = APOCALYPSE;
                break;

            default:
                throw new IllegalArgumentException("NO SUCH PRESET DECK FOUND");
        }

        addAll(Arrays.asList(target));
        Collections.shuffle(this);
    }

    /**
     * Draw a card from the deck.
     *
     * @return the card drawn from the deck. Null if there is no card left in the deck.
     */
    public Card drawCard() {
        if (size() > 0) {
            Card next = get(0);
            remove(0);
            return next;
        } else {
            return null;
        }
    }

}
