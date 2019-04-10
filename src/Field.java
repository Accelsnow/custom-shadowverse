/**
 * This class is the representation of one player's field. It contains all cards on field and methods needed for field
 * processes.
 */
public final class Field extends CardList {
    public static final int MAXIMUM_FIELD_SIZE = 5;

    Field() {
        super(MAXIMUM_FIELD_SIZE);
    }

    /**
     * This method adds a card to the field if there is space left on field.
     *
     * @param card the card to be added
     * @return true if the card is successfully added, false if there is no space for the card
     * @throws IllegalArgumentException if the card is not a valid type of card to be on the field (only {@linkplain
     *                                  Follower} and {@linkplain Amulet} are allowed to be on the field)
     */
    public boolean add(Card card) {
        if (size() < MAXIMUM_FIELD_SIZE) {
            if (card instanceof Follower || card instanceof Amulet) {
                super.add(card);
                return true;
            } else {
                throw new IllegalArgumentException("Not a valid type of card to be on the field");
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if current field has no follower with WARD effect
     *
     * @return true if there is no follower with WARD effect, false otherwise
     * @see Follower.SimpleEffects#WARD
     */
    public boolean haveNoWard() {
        for (Card c : this) {
            if (c instanceof Follower && ((Follower) c).hasWard()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Counts the number of followers on current field
     *
     * @return the number of followers on current field
     */
    public int followerCount() {
        int count = 0;

        for (Card c : this) {
            if (c instanceof Follower && c.isAlive()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts the number of amulets on current field
     *
     * @return the number of amulets on current field
     */
    public int amuletCount() {
        int count = 0;

        for (Card c : this) {
            if (c instanceof Amulet) {
                count++;
            }
        }

        return count;
    }

    /**
     * Checks if there is targetable follower to be selected as an effect or attack target on current field
     *
     * @return true if there is targetable follower on current field, false otherwise
     * @see Follower#canBeTargeted()
     */
    public boolean hasTargetableFollower() {
        for (Card c : this) {
            if (c instanceof Follower) {
                Follower follower = (Follower) c;

                if (follower.canBeTargeted()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if there is any amulet with countdown effect on current field
     *
     * @return true if there is countdown amulet on current field, false otherwise
     */
    public boolean hasCountdownAmulet() {
        for (Card c : this)
            if (c instanceof Amulet && ((Amulet) c).isCountDown())
                return true;
        return false;
    }

    /**
     * Checks if there is any follower with specified swordcraft trait
     *
     * @param targetTrait the trait to be checked
     * @return true if there is follower with the given trait, false otherwise
     * @see Follower.SwordCraftTrait
     */
    public boolean hasSwordTraitFollower(Trait.SwordCraftTrait targetTrait) {
        for (Card c : this) {
            if (c instanceof Follower && ((Follower) c).getSwordCraftTrait() == targetTrait)
                return true;
        }

        return false;
    }

    /**
     * Checks if there is any follower with a health value less than or equal to the given value
     *
     * @param hpLimit the health limit
     * @return true if there is follower with a health value less than or equal to the given value, false otherwise
     */
    public boolean hasTargetableFollowerBelowHpLimit(int hpLimit) {
        for (Card c : this)
            if (c instanceof Follower && ((Follower) c).getHealth() <= hpLimit && ((Follower) c).canBeTargeted())
                return true;
        return false;
    }

    /**
     * Checks if there is any follower with an attack value greater than or equal to the given value
     *
     * @param atkLimit the attack limit
     * @return true if there is follower with an attack value greater than or equal to the given value, false otherwise
     */
    public boolean hasTargetableFollowerAboveAtkLimit(int atkLimit) {
        for (Card c : this)
            if (c instanceof Follower && ((Follower) c).getAttack() >= atkLimit && ((Follower) c).canBeTargeted())
                return true;
        return false;
    }

}
