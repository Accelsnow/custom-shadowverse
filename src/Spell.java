import java.awt.*;

/**
 * This class represent a card of type spell. Spells are not allowed to exist on field as they are disposed immediately
 * after usage. Spells have only FANFARE effect that are active upon usage.
 */
final class Spell extends Card {

    Spell(String nme, String lead, int cst, String rar, Image handImg, Image fieldImg,
          String effects, boolean hasSpellBoost) {
        super("SPELL", nme, lead, cst, rar, handImg, fieldImg, effects, hasSpellBoost);
    }

    /**
     * @return false always, as spell can never be on field
     */
    public boolean isAlive() {
        return false;
    }

}
