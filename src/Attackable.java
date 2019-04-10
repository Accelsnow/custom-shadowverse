/**
 * The interface provides necessary method structure for targets that can be attacked. In this project, it is
 * implemented by {@link Follower} and {@link Leader}.
 */
interface Attackable {
    /**
     * Object takes attack damage from a follower. The damage dealt is equal to the follower's current attack
     *
     * @param attacker the attacker follower
     */
    void takeDamage(Follower attacker);

    /**
     * Heals the object for a certain amount of health
     *
     * @param heal the amount of health to be healed
     */
    void heal(int heal);

    /**
     * @return the object's current health
     */
    int getHealth();

    /**
     * @return true if the object is alive, false otherwise
     */
    boolean isAlive();

    /**
     * Deal a specific amount of damage to the object
     *
     * @param damage The amount of damage to be dealt
     */
    void takeDamage(int damage);

    /**
     * @return true if the target is damage immune, false otherwise
     */
    boolean hasDamageImmune();
}
