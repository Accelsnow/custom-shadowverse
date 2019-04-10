import java.awt.*;

/**
 * This class represents a player's leader. It contains the leader's health status and its types.
 */
public final class Leader implements Attackable {
    private static final int MAX_HEALTH = 20;
    private int health;
    private LeaderType leaderType;
    /**
     * True if the leader has damage immune. Under damage immune status, no damage will be taken by the leader.
     *
     * @see LeaderEffects#DAMAGE_IMMUNE
     */
    private boolean hasDamageImmune;

    /**
     * The constructor sets current leader's type and initializes the fields with their default values.
     *
     * @param type the leader type to be created
     */
    Leader(String type) {
        leaderType = LeaderType.valueOf(type);
        hasDamageImmune = false;
        health = MAX_HEALTH;
    }

    /**
     * The leader takes damage from an enemy follower attack unless damage immune is active. The damage taken equals the
     * attacker follower's current attack value.
     *
     * @param attacker the attacker follower
     */
    @Override
    public void takeDamage(Follower attacker) {
        if (!hasDamageImmune)
            health -= attacker.getAttack();
    }

    /**
     * Heal the leader for a certain amount of health. The maximum health that can be reached is {@linkplain
     * #MAX_HEALTH}
     *
     * @param heal the amount of health to be healed
     * @see #hasDamageImmune
     */
    @Override
    public void heal(int heal) {
        if (health + heal <= MAX_HEALTH) {
            health += heal;
        } else {
            health = MAX_HEALTH;
        }
    }

    /**
     * @return the leader's current health
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * @return true if the follower is alive(health above 0), false otherwise
     */
    @Override
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * The leader takes a certain amount of damage unless damage immune is active.
     *
     * @param damage The amount of damage to be dealt
     * @see #hasDamageImmune
     */
    @Override
    public void takeDamage(int damage) {
        if (!hasDamageImmune)
            health -= damage;
    }

    /**
     * @return true if the leader has damage immune effect
     */
    @Override
    public boolean hasDamageImmune() {
        return hasDamageImmune;
    }

    /**
     * Grant the leader with a specified wimple effect
     *
     * @param effect the effect to be implemented
     */
    public void grantEffect(LeaderEffects effect) {
        switch (effect) {
            case DAMAGE_IMMUNE:
                hasDamageImmune = true;
                break;
        }
    }

    /**
     * Remove a specified simple effect
     *
     * @param effect the effect to be removed
     */
    public void revokeEffect(LeaderEffects effect) {
        switch (effect) {
            case DAMAGE_IMMUNE:
                hasDamageImmune = false;
                break;
        }
    }

    /**
     * @return leader's name
     */
    public String getName() {
        return leaderType.name;
    }

    /**
     * @return leader's image
     */
    public Image getImage() {
        return leaderType.image;
    }

    /**
     * @return leader's type
     */
    public LeaderType getLeaderType() {
        return leaderType;
    }

    /**
     * This enum contains all leader types and their correspond names (type name and leader name). NEUTRAL is only to be
     * used as a card's leader type, not a leader type.
     */
    public enum LeaderType {
        FORESTCRAFT("Forestcraft", "Arisa"), SWORDCRAFT("Swordcraft", "Erika"), RUNECRAFT("Runecraft", "Isabelle"),
        DRAGONCRAFT("Dragoncraft", "Rowen"), SHADOWCRAFT("Shadowcraft", "Luna"), BLOODCRAFT("Bloodcraft", "Urias"),
        HAVENCRAFT("Havencraft", "Eris"),
        /**
         * It is not to be used as a leader's type but only a card's leader type. There is no leader image for it.
         */
        NEUTRAL("Neutral", "Neutral");

        private String kind, name;
        private Image image;

        /**
         * Initializes type name and leader name
         *
         * @param knd type name
         * @param nme leader name
         */
        LeaderType(String knd, String nme) {
            kind = knd;
            name = nme;

            if (!kind.equals("Neutral"))
                image = ImageLib.getLeaderImage(kind);
        }

        /**
         * @return the type name
         */
        public String getKind() {
            return kind;
        }

        /**
         * @return leader's image
         */
        public Image getImage() {
            return image;
        }
    }

    /**
     * This enum contains all leader effects
     */
    public enum LeaderEffects {
        /**
         * Leader with damage immune effect receive no damage from any source
         */
        DAMAGE_IMMUNE}

}
