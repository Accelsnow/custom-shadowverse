/**
 * This interface contains all available trait a card can have
 */
public interface Trait {
    /**
     * @return the current card's swordcraft trait
     */
    SwordCraftTrait getSwordCraftTrait();

    /**
     * Special traits for swordcraft. Some swordcraft cards rely on this trait value to activate special effects.
     */
    enum SwordCraftTrait {
        OFFICER, COMMANDER, OTHER
    }
}
