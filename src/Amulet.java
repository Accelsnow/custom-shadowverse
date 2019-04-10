import java.awt.*;

/**
 * This class represent an individual card of type Amulet. An amulet is a card that exists on the field. There are two
 * types of amulets - ones with a countdown and ones without. Amulets with a countdown will have its countdown decreases
 * by one at the beginning of every round, and it will self-destroy when the countdown reaches zero. Amulets without a
 * countdown will stay on field unless it is destroyed or returned by other card's effects. Usually an amulet would have
 * effects that are triggered by other events.
 *
 * <p>An amulet instance contains its countdown(if it has one) and the boolean variable {@linkplain #isAlive} indicating
 * if the card is destroyed or not.</p>
 */
final class Amulet extends Card {
    private final int ORIGINAL_COUNT_DOWN;
    private int countDown;
    private boolean isCountDown, isAlive;

    /**
     * The constructor runs the {@linkplain Card}'s constructor first and then initializes the countdown statuses.
     *
     * @param nme      amulet's name
     * @param lead     amulet's leader
     * @param cst      amulet's cost
     * @param rar      amulet's rarity
     * @param handImg  amulet's hand image
     * @param fieldImg amulet's field image
     * @param cd       amulet's countdown, 0 if it does not have one
     * @param effects  amulet's effects
     */
    Amulet(String nme, String lead, int cst, String rar, Image handImg, Image fieldImg, int cd, String effects) {
        super("AMULET", nme, lead, cst, rar, handImg, fieldImg, effects, false);

        if (cd <= 0) {
            isCountDown = false;
            countDown = -1;
        } else {
            isCountDown = true;
            countDown = cd;
        }

        ORIGINAL_COUNT_DOWN = countDown;
        isAlive = true;
    }

    /**
     * Decreases the countdown by a specific amount. If the countdown turns below or equal to 0 after the decrease, it
     * will self-destroy by setting {@linkplain #isAlive} to false.
     *
     * @param amount the number of countdown to be decreased
     * @throws IllegalStateException when the amulet is not a countdown type
     */
    public void decreaseCountDown(int amount) {
        if (isCountDown) {
            countDown -= amount;
        } else {
            throw new IllegalStateException("Amulet is not a count down type!");
        }

        if (countDown <= 0) {
            countDown = 0;
            isAlive = false;
        }
    }

    /**
     * @return true if the amulet is a countdown type, false otherwise
     */
    public boolean isCountDown() {
        return isCountDown;
    }

    /**
     * @return the amulet's remaining countdown, -1 if it is not a countdown type
     */
    public int getCountDown() {
        return countDown;
    }

    /**
     * @return the amulet's original countdown, -1 if it is not a countdown type
     */
    public int getOrgCountDown() {
        return ORIGINAL_COUNT_DOWN;
    }

    /**
     * @return true if the amulet is alive, false otherwise
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Destroys the amulet instantly
     */
    public void destroy() {
        isAlive = false;
    }

}
