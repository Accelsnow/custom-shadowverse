/**
 * This enum contains all possible attack statuses for {@link Follower}. It is used to determine whether it is valid for
 * a follower to attack.
 *
 * @see Follower#attack(Follower)
 * @see Follower#attack(Leader)
 */
public enum AttackStatus {
    /**
     * The default status when a follower just entered the field. Follower is unable to attack under this status.
     */
    ENTRY("Entry"),
    /**
     * The default status that applies to all followers on field at the beginning of the round. Follower is able to
     * attack any available enemy target.
     */
    STORM("Storm"),
    /**
     * Follower is able to attack available enemy followers but not enemy leader.
     */
    RUSH("Rush"),
    /**
     * The status which the follower has already attacked this round. Follower is unable to attack under this status.
     */
    ATTACKED("Attacked"),
    /**
     * The status which the follower is banned from attacking by certain effects. Follower is unable to attack under
     * this status.
     */
    DISABLED("Disabled");

    private final String NAME;

    /**
     * The constructor sets the status' name
     *
     * @param name name of the status
     */
    AttackStatus(String name) {
        NAME = name;
    }

    /**
     * @return String name of the status
     */
    public String toString() {
        return NAME;
    }

    /**
     * @return true if followers with current status can attack enemy followers, false otherwise
     */
    public boolean canAttackFollower() {
        switch (this) {
            case STORM:
            case RUSH:
                return true;

            default:
                return false;
        }
    }

    /**
     * @return true if followers with current status can attack enemy leader, false otherwise
     */
    public boolean canAttackLeader() {
        switch (this) {
            case STORM:
                return true;

            default:
                return false;
        }
    }

}