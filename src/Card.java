import java.awt.*;

/**
 * This class is the parent class of all card types {@link Amulet}, {@link Follower}, {@link Spell}. It contains
 * mandatory variables representing the card's current status and original status, getters and setters.
 *
 * @see Leader
 */
public abstract class Card {
    private static final int MAX_COST = 20, MIN_COST = 0;
    private final String NAME;
    private final int ORIGINAL_COST;
    private final boolean IS_SPELL_BOOST, BOOST_COST;
    private int cost;
    private Leader.LeaderType leader;
    private Rarity rarity;
    private Type type;
    private Image handImage, fieldImage;
    private String fanfare = null, lastword = null, whenOtherEnter = null, whenEntranceEnd = null,
            whenAttack = null, whenFight = null, whenStart = null, whenEnd = null, whenEvolve = null;
    private int boostAmount = 0;
    /**
     * This variable tells whether if the current round is the card's first round on field (entrance round). It has a
     * default value of true, and will turn false at the end of the round. This variable shall not be switch back to
     * true in any cases.
     *
     * <p>The variable is used as trigger to some effects that are only active in the entrance round.</p>
     */
    private boolean isEntranceRound;
    /**
     * This variable indicates whether certain effects have been triggered already or not. Its default value is true,
     * representing that certain effects can be triggered. It turns false when the effects are triggers, and will only
     * switch back to true when {@linkplain #resetEffectToggle()} is triggered.
     */
    private boolean effectToggle;

    /**
     * This top-level constructor initializes the card object's basic information
     *
     * @param typ           string representation of the type of the card. It must match with one of the values in
     *                      {@linkplain Type}
     * @param nme           name of the card
     * @param lead          string representation of the leader of the card. It must match with one of the values in
     *                      {@linkplain Leader.LeaderType}
     * @param cst           cost of the card
     * @param rar           string representation of the rarity of the card. It must match with one of the values in
     *                      {@linkplain Rarity}
     * @param handImg       image of the card when the card is in hand
     * @param fieldImg      image of the card when the card is on field
     * @param efx           effects of the card. It may contain multiple effects. They are interpreted by {@linkplain
     *                      EffectLib} later
     * @param hasSpellBoost true if the card is one of the spell boost type, false otherwise
     */
    Card(String typ, String nme, String lead, int cst, String rar, Image handImg, Image fieldImg, String efx,
         boolean hasSpellBoost) {
        type = Type.valueOf(typ);
        NAME = nme;
        leader = Leader.LeaderType.valueOf(lead);
        rarity = Rarity.valueOf(rar);
        cost = cst;
        ORIGINAL_COST = cst;
        handImage = handImg;
        fieldImage = fieldImg;
        isEntranceRound = true;
        effectToggle = true;

        String[] effects = efx.split("\\+");

        for (String effect : effects) {
            String[] efxInfo = effect.split("=");

            switch (efxInfo[0]) {
                case "FANFARE":
                    fanfare = efxInfo[1];
                    break;

                case "LSTWRD":
                    lastword = efxInfo[1];
                    break;

                case "WETR":
                    whenOtherEnter = efxInfo[1];
                    break;

                case "WATK":
                    whenAttack = efxInfo[1];
                    break;

                case "WFIG":
                    whenFight = efxInfo[1];
                    break;

                case "WSTA":
                    whenStart = efxInfo[1];
                    break;

                case "WEND":
                    whenEnd = efxInfo[1];
                    break;

                case "WEVO":
                    whenEvolve = efxInfo[1];
                    break;

                case "WENTEND":
                    whenEntranceEnd = efxInfo[1];
                    break;
            }
        }

        if (hasSpellBoost) {
            IS_SPELL_BOOST = true;

            BOOST_COST = efx.contains("BSTCST");
        } else {
            IS_SPELL_BOOST = false;
            BOOST_COST = false;
        }
    }

    /**
     * Triggered when some special effects are activated on the card. It sets {@linkplain #effectToggle} to false to
     * prevent unwanted multiple executions of the effects.
     */
    public void effectActivated() {
        effectToggle = false;
    }

    /**
     * @return the current {@linkplain #effectToggle} status.
     */
    public boolean getEffectToggle() {
        return effectToggle;
    }

    /**
     * Reset {@linkplain #effectToggle} to true. Triggered for some once-a-round effects.
     */
    public void resetEffectToggle() {
        effectToggle = true;
    }

    /**
     * @return the card's effect that is triggered when any other follower enters the field.
     */
    public String getWhenOtherEnter() {
        return whenOtherEnter;
    }

    /**
     * @return the card's effect that is triggered when it is evolved. It is only valid for {@linkplain Follower}, as
     * only followers can evolve.
     */
    public String getWhenEvolve() {
        return whenEvolve;
    }

    /**
     * @return the original, unchanged cost of the card
     */
    public int getOrgCost() {
        return ORIGINAL_COST;
    }

    /**
     * @return the card's effect that is triggered upon usage. This is the only possible effect for {@linkplain Spell}.
     */
    public String getFanfare() {
        return fanfare;
    }

    /**
     * @return the card's effect that is triggered when the card is dead/destroyed.
     */
    public String getLastword() {
        return lastword;
    }

    /**
     * @return the card's effect that is triggered when it attacks. It is only valid for {@linkplain Follower}, as only
     * followers can attack.
     */
    public String getWhenAttack() {
        return whenAttack;
    }

    /**
     * @return the card's effect that is triggered when it attacks an enemy follower. It is only valid for {@linkplain
     * Follower}, as only followers can attack.
     */
    public String getWhenFight() {
        return whenFight;
    }

    /**
     * @return the card's effect that is triggered at the beginning of a new round.
     */
    public String getWhenStart() {
        return whenStart;
    }

    /**
     * @return the card's effect that is triggered at the end of its entrance round.
     */
    public String getWhenEntranceEnd() {
        return whenEntranceEnd;
    }

    /**
     * This method is specifically designed to changes effects for only certain cards, as effects that changes effects
     * are very rare in game.
     *
     * @throws IllegalArgumentException when the current card is unable to change its own effect.
     */
    public void changeEffect() {
        switch (NAME) {
            case "Lucifer":
                whenEnd = "DMGENELED@4";
                break;

            default:
                throw new IllegalArgumentException("ONLY CERTAIN CARDS ALLOW EFFECT CHANGE");
        }
    }

    /**
     * @return the card's effect that is triggered at the end of a round.
     */
    public String getWhenEnd() {
        return whenEnd;
    }

    /**
     * @return name of the card
     */
    public String getName() {
        return NAME;
    }

    /**
     * @return cost of the card
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return rarity of the card
     */
    public Rarity getRarity() {
        return rarity;
    }

    /**
     * @return the leader type of the card
     */
    public Leader.LeaderType getLeader() {
        return leader;
    }

    /**
     * @return the type of the card
     */
    public Type getType() {
        return type;
    }

    /**
     * Reduces the card's cost by a specific amount. The lowest possible cost is {@linkplain #MIN_COST}
     *
     * @param change the amount of cost to be reduced
     */
    private void reduceCost(int change) {
        cost -= change;
        if (cost < MIN_COST) cost = MIN_COST;
    }

    /**
     * Change the card's cost to a specific number
     *
     * @param target_cost the target cost
     * @throws IllegalArgumentException when the target cost is out of range
     * @see #MIN_COST
     * @see #MAX_COST
     */
    public void changeCost(int target_cost) {
        if (target_cost <= MAX_COST && target_cost >= MIN_COST)
            cost = target_cost;
        else
            throw new IllegalArgumentException("COST EXCEEDED MAXIMUM RANGE");
    }

    /**
     * @return image of the card when it is in hand
     */
    public Image getHandImage() {
        return handImage;
    }

    /**
     * @return image of the card when it is on field
     */
    public Image getFieldImage() {
        return fieldImage;
    }

    /**
     * The card's cost will reduce by 1 if it is boost cost type, otherwise boost amount will increase by 1. This method
     * is only valid for cards with spell-boost effects.
     *
     * @throws IllegalStateException if the card is not spell-boost type
     */
    public void boost() {
        if (IS_SPELL_BOOST) {

            if (BOOST_COST)
                reduceCost(1);
            else
                boostAmount++;

        } else {
            throw new IllegalStateException("YOU CAN ONLY BOOST SPELLBOOST CARDS");
        }
    }

    /**
     * @return true if the card is spell-boost type, false otherwise
     */
    public boolean isSpellBoost() {
        return IS_SPELL_BOOST;
    }

    /**
     * @return the boost amount of the card. This method is only valid for spell-boost cards that are not boost-cost
     * type
     * @throws IllegalStateException if the card is boost-cost type or the card is not spell-boost type
     */
    public int getBoostAmount() {
        if (IS_SPELL_BOOST && !BOOST_COST)
            return boostAmount;
        else
            throw new IllegalStateException("Only non-cost boost spellboost cards are allowed to have boost amount");
    }

    /**
     * Invoked at the end of the entrance round to indicate that it is no longer the entrance round.
     *
     * @see #isEntranceRound
     */
    public void passEntranceRound() {
        isEntranceRound = false;
    }

    /**
     * @return true if current round is this card's entrance round to the field, false otherwise
     * @see #isEntranceRound
     */
    public boolean isEntranceRound() {
        return isEntranceRound;
    }

    /**
     * This method is to be implemented by all kinds of cards to indicate whether the card is alive or not.
     *
     * @return true if the card is alive (maintain on field), false otherwise
     */
    abstract boolean isAlive();

    /**
     * This enum contains all possible rarities for a card. It also provides the string name for each rarity.
     */
    public enum Rarity {
        BRONZE("bronze"), SILVER("silver"), GOLD("gold"), LEGENDARY("legendary");

        private final String NAME;

        /**
         * The constructor sets the string name for the corresponding rarity.
         *
         * @param name the name for the enum value to be initialized
         */
        Rarity(String name) {
            NAME = name;
        }

        /**
         * @return the string name for the current rarity value
         */
        public String toString() {
            return NAME;
        }
    }

    /**
     * This enum contains all three possible types of a card and their string names.
     */
    public enum Type {
        FOLLOWER("follower"), SPELL("spell"), AMULET("amulet");

        private final String NAME;

        /**
         * The constructor sets the string name for the corresponding card type.
         *
         * @param name the name for the enum value to be initialized
         */
        Type(String name) {
            NAME = name;
        }

        /**
         * @return the string name for the current card type
         */
        public String toString() {
            return NAME;
        }
    }
}
