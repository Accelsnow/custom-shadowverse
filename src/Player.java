/**
 * This class represents a game player. It contains all necessary resources for a player (deck, hand, grave, etc.).
 * There are also interaction methods that allocates between resources.
 *
 * <p>One game should have and only have 2 player instances.</p>
 */
public final class Player {
    private static final int MAX_COST_POSSIBLE = 10;
    private final RoundOrder ROUND_ORDER;
    private final Deck DECK;
    private final Hand HAND;
    private final Grave GRAVE;
    private final Leader LEADER;
    private final Field FIELD;
    private boolean hasEvolved = false;
    private int maxCost, costLeft, evolvePts;
    private int usedCardCount = 0, roundNumber = 0;

    /**
     * The constructor initializes all resources and attributes.
     *
     * @param leader_kind the leader type player has chosen
     * @param roundOdr    the round order of this player
     */
    Player(String leader_kind, RoundOrder roundOdr) {
        LEADER = new Leader(leader_kind.toUpperCase());
        DECK = new Deck(LEADER);
        HAND = new Hand();
        GRAVE = new Grave();
        FIELD = new Field();
        ROUND_ORDER = roundOdr;

        switch (ROUND_ORDER) {
            case OFFENSIVE:
                evolvePts = 2;
                break;

            case DEFENSIVE:
                evolvePts = 3;
        }
    }

    /**
     * @return this player's field instance
     */
    public Field getField() {
        return FIELD;
    }

    /**
     * @return this player's leader instance
     */
    public Leader getLeader() {
        return LEADER;
    }

    /**
     * @return this player's deck instance
     */
    public Deck getDeck() {
        return DECK;
    }

    /**
     * @return this player's deck instance
     */
    public Hand getHand() {
        return HAND;
    }

    /**
     * @return this player's grave instance
     */
    public Grave getGrave() {
        return GRAVE;
    }

    /**
     * @return the round past since the beginning of the game
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * @return player's round order
     */
    public RoundOrder getRoundOrder() {
        return ROUND_ORDER;
    }

    /**
     * @return this player's current maximum cost (maximum cost usable this round, not maximum cost overall)
     */
    public int getMaxCost() {
        return maxCost;
    }

    /**
     * @return this player's current usable cost left
     */
    public int getCostLeft() {
        return costLeft;
    }

    /**
     * @return the number of cards played this round
     */
    public int getUsedCardCount() {
        return usedCardCount;
    }

    /**
     * @return the number of evolve points left
     */
    public int getEvolvePts() {
        return evolvePts;
    }

    /**
     * @return true if the player has evolved this round already, false otherwise
     */
    public boolean hasEvolved() {
        return hasEvolved;
    }

    /**
     * Initializes a new round. It increases round number and maximum cost, reset usable cost and evolve status and
     * grant all followers on field with {@linkplain Follower.SimpleEffects#STORM} effect. Finally, the player will draw
     * a card from the deck and the round begins (draw 1 more if the player has a round order of {@linkplain
     * RoundOrder#DEFENSIVE} and current round is the first round)
     *
     * @return true if the round has initialized successfully, false if there is no card left in deck and the game will
     * end.
     */
    public boolean iniNewRound() {
        roundNumber++;
        increaseMaxCost();
        costLeft = maxCost;
        hasEvolved = false;

        for (Card c : FIELD) {
            if (c instanceof Follower)
                ((Follower) c).grantSimpleEffect(Follower.SimpleEffects.STORM);

            if (c instanceof Amulet && ((Amulet) c).isCountDown())
                ((Amulet) c).decreaseCountDown(1);

            c.resetEffectToggle();
        }

        if (ROUND_ORDER == RoundOrder.DEFENSIVE && roundNumber == 1) {
            return drawCards(2);
        } else {
            return drawCards(1);
        }
    }

    /**
     * End current round. It resets used card count and reset all player's attack status to {@linkplain
     * AttackStatus#ATTACKED}
     */
    public void endRound() {
        usedCardCount = 0;

        for (Card c : FIELD) {
            if (c instanceof Follower) {
                ((Follower) c).revokeSimpleEffect(Follower.SimpleEffects.DISABLED);
                ((Follower) c).grantSimpleEffect(Follower.SimpleEffects.ATTACKED);
            }
        }

        hasEvolved = false;
    }

    /**
     * Use a card in hand
     *
     * @param card the card to be used. It must be present in hand.
     * @throws IllegalArgumentException if the card given does not exist in hand
     */
    public void useCard(Card card) {
        if (HAND.contains(card)) {
            HAND.remove(card);
        } else {
            throw new IllegalArgumentException("NO SUCH CARD FOUND IN HAND");
        }
        usedCardCount++;
    }

    /**
     * Use a certain amount of cost. The usage must be less than or equal to current cost left.
     *
     * @param usage the amount of cost to be used
     * @throws IllegalArgumentException if there is not enough cost left
     */
    public void useCost(int usage) {
        if (usage > costLeft) {
            throw new IllegalArgumentException("Not enough cost!");
        } else {
            costLeft -= usage;
        }
    }

    /**
     * Increase the maximum cost available by 1 unless it hits the {@linkplain #MAX_COST_POSSIBLE}
     */
    public void increaseMaxCost() {
        if (maxCost < MAX_COST_POSSIBLE) {
            maxCost++;
        }
    }

    /**
     * This method checks for special leader status for {@linkplain Leader.LeaderType#DRAGONCRAFT}
     *
     * @return true if the leader is under OVERFLOW status
     * @throws IllegalStateException if the leader is not type {@linkplain Leader.LeaderType#DRAGONCRAFT}
     */
    public boolean isOverflow() {
        if (LEADER.getLeaderType() == Leader.LeaderType.DRAGONCRAFT)
            return maxCost >= 7;
        else
            throw new IllegalStateException("ONLY DRAGONCRAFT HAS OVERFLOW");
    }

    /**
     * This method checks for special leader status for {@linkplain Leader.LeaderType#BLOODCRAFT}
     *
     * @return true if the leader is under VENGEANCE status
     * @throws IllegalStateException if the leader is not type {@linkplain Leader.LeaderType#BLOODCRAFT}
     */
    public boolean isVengeance() {
        if (LEADER.getLeaderType() == Leader.LeaderType.BLOODCRAFT)
            return LEADER.getHealth() <= 10;
        else
            throw new IllegalStateException("ONLY BLOODCRAFT HAS VENGEANCE");
    }

    /**
     * Evolve a specified follower. Evolve points will be needed and only one evolution is allowed per round.
     *
     * @param follower the follower to be evolved
     * @return true if the evolution is successful, false otherwise
     */
    public boolean evolve(Follower follower) {
        if (evolvePts > 0 && !hasEvolved) {
            hasEvolved = true;

            if (follower != null) {
                evolvePts--;
                follower.evolve();
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Draw a certain amount of cards. If there is no card left in deck, the player will lose the game.
     *
     * @param numOfCards the number of cards to be drawn
     * @return true if the card is successfully drawn, false if there is no card left in deck
     */
    public boolean drawCards(int numOfCards) {
        for (int n = 0; n < numOfCards; n++) {
            Card drawnCard = DECK.drawCard();

            if (drawnCard != null) {

                if (!HAND.add(drawnCard))
                    GRAVE.add(drawnCard);

            } else {
                return false;
            }
        }

        return true;
    }


    /**
     * This enum contains all possible round order values
     */
    public enum RoundOrder {
        /**
         * The player plays first with 2 evolve points
         */
        OFFENSIVE,
        /**
         * The player plays second with an extra card drawn in the first round and 3 evolve points
         */
        DEFENSIVE}

}
