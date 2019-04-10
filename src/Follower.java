import java.awt.*;

/**
 * The class represents a follower object with all its attributes and actions. It is only to be created through
 * {@linkplain CardLib#getCard(String)}.
 */
public final class Follower extends Card implements Attackable, Trait {

    private final int ORIGINAL_HEALTH, ORIGINAL_ATTACK, ORIGINAL_EVOLVE_ATTACK, ORIGINAL_EVOLVE_HEALTH;
    private int health, attack;
    private int evoHpAdd, evoAtkAdd;
    private boolean isEvolved, hasBane, hasWard, hasDrain, hasAmbush, hasRush, hasStorm, hasUntargetable,
            hasDamageImmune, hasWardPierce;
    private Image evoImage;
    private AttackStatus atkStatus;
    private SwordCraftTrait swordCraftTrait;
    private StringBuilder buffList;

    /**
     * The constructor initializes all default attributes for a follower instance
     *
     * @param nme                 name
     * @param lead                leader
     * @param cst                 cost
     * @param rar                 rarity
     * @param handImg             image when the follower is in hand
     * @param fieldImg            image when the follower is on field
     * @param swordTrait          follower's swordcraft trait
     * @param hp                  health
     * @param atk                 attack
     * @param evolvedHpAdd        the health increase upon evolving
     * @param evolvedAtkAdd       the attack increase upon evolving
     * @param evoImg              follower's image after evolving
     * @param defaultAttackStatus follower's default attack status
     * @param bane                true if the follower has BANE effect
     * @param ward                true if the follower has WARD effect
     * @param drain               true if the follower has DRAIN effect
     * @param ambush              true if the follower has AMBUSH effect
     * @param untargetable        true if the follower has UNTARGETABLE effect
     * @param efx                 the effect string for the follower, it is encoded and only to be interpreted by
     *                            {@linkplain EffectLib}
     * @param hasSpellBoost       true if the follower has spellboost effect
     * @see Trait.SwordCraftTrait
     * @see AttackStatus
     * @see SimpleEffects
     */
    Follower(String nme, String lead, int cst, String rar, Image handImg, Image fieldImg,
             String swordTrait, int hp, int atk, int evolvedHpAdd, int evolvedAtkAdd, Image evoImg,
             String defaultAttackStatus, boolean bane, boolean ward, boolean drain, boolean ambush,
             boolean untargetable, String efx, boolean hasSpellBoost) {
        super("FOLLOWER", nme, lead, cst, rar, handImg, fieldImg, efx, hasSpellBoost);

        swordCraftTrait = SwordCraftTrait.valueOf(swordTrait);
        health = hp;
        attack = atk;
        evoHpAdd = evolvedHpAdd;
        evoAtkAdd = evolvedAtkAdd;
        ORIGINAL_HEALTH = hp;
        ORIGINAL_ATTACK = atk;
        ORIGINAL_EVOLVE_ATTACK = atk + evolvedAtkAdd;
        ORIGINAL_EVOLVE_HEALTH = hp + evolvedHpAdd;
        evoImage = evoImg;
        buffList = new StringBuilder();

        if (bane) hasBane = true;
        if (ward) hasWard = true;
        if (drain) hasDrain = true;
        if (untargetable) hasUntargetable = true;

        atkStatus = AttackStatus.valueOf(defaultAttackStatus);

        if (atkStatus == AttackStatus.STORM) hasStorm = true;
        if (atkStatus == AttackStatus.RUSH) hasRush = true;

        isEvolved = false;
        hasAmbush = ambush;
        hasDamageImmune = false;
        hasWardPierce = false;
    }

    /**
     * @return the current attack status of the follower
     */
    public AttackStatus getAtkStatus() {
        return atkStatus;
    }

    /**
     * @return true if the follower is alive, false otherwise
     */
    @Override
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Follower takes damage from an enemy follower. The damage taken is equal to the enemy follower's attack. If the
     * follower has damage immune effect, no damage will be taken.
     *
     * @param attacker the attacker follower
     */
    @Override
    public void takeDamage(Follower attacker) {
        if (!hasDamageImmune)
            health -= attacker.getAttack();
    }

    /**
     * Heal the follower for a certain amount of health. There is no limit for health value, so it can exceed follower's
     * original health.
     *
     * @param heal the amount of health to be healed
     */
    @Override
    public void heal(int heal) {
        health += heal;
    }

    /**
     * @return the follower's current health
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * Follower takes a specific amount of damage unless it has damage immune.
     *
     * @param damage the amount of damage to be taken
     */
    @Override
    public void takeDamage(int damage) {
        if (!hasDamageImmune)
            health -= damage;
    }

    /**
     * @return true if the follower has damage immune effect, false otherwise
     */
    @Override
    public boolean hasDamageImmune() {
        return hasDamageImmune;
    }

    /**
     * Instantly kills the follower by reducing the health to 0
     */
    public void kill() {
        health = 0;
    }

    /**
     * @return a string contains all buffs' descriptions that the current follower has received
     */
    public String getBuffListString() {
        return buffList.toString();
    }

    /**
     * @return the original health value of the follower before evolving
     */
    public int getOrgHealth() {
        return ORIGINAL_HEALTH;
    }

    /**
     * @return the original attack value of the follower before evolving
     */
    public int getOrgAttack() {
        return ORIGINAL_ATTACK;
    }

    /**
     * @return the original attack value of the follower after evolving
     */
    public int getOrgEvoAttack() {
        return ORIGINAL_EVOLVE_ATTACK;
    }

    /**
     * @return the original health value of the follower after evolving
     */
    public int getOrgEvoHealth() {
        return ORIGINAL_EVOLVE_HEALTH;
    }

    /**
     * @return the image of the follower after evolving
     */
    public Image getEvolvedImage() {
        return evoImage;
    }

    /**
     * @return the current attack value of the follower
     */
    public int getAttack() {
        return attack;
    }

    /**
     * @return true if the follower has evolved already, false otherwise
     */
    public boolean hasEvolved() {
        return isEvolved;
    }

    /**
     * @return true if the follower has BANE effect, false otherwise
     * @see SimpleEffects#BANE
     */
    public boolean hasBane() {
        return hasBane;
    }

    /**
     * @return true if the follower has WARD effect, false otherwise
     * @see SimpleEffects#WARD
     */
    public boolean hasWard() {
        return hasWard;
    }

    /**
     * @return true if the follower has DRAIN effect, false otherwise
     * @see SimpleEffects#DRAIN
     */
    public boolean hasDrain() {
        return hasDrain;
    }

    /**
     * @return true if the follower has AMBUSH effect, false otherwise
     * @see SimpleEffects#AMBUSH
     */
    public boolean hasAmbush() {
        return hasAmbush;
    }

    /**
     * @return true if the follower has STORM effect, false otherwise
     * @see SimpleEffects#STORM
     */
    public boolean hasStorm() {
        return hasStorm;
    }

    /**
     * @return true if the follower has RUSH effect, false otherwise
     * @see SimpleEffects#RUSH
     */
    public boolean hasRush() {
        return hasRush;
    }

    /**
     * @return true if the follower has UNTARGETABLE effect, false otherwise
     * @see SimpleEffects#UNTARGETABLE
     */
    public boolean hasUntargetable() {
        return hasUntargetable;
    }

    /**
     * @return true if the follower has WARD_PIERCE effect, false otherwise
     * @see SimpleEffects#WARD_PIERCE
     */
    public boolean hasWardPierce() {
        return hasWardPierce;
    }

    /**
     * @return true if the follower can be selected as a target, false otherwise
     */
    public boolean canBeTargeted() {
        return !(hasUntargetable || hasAmbush);
    }

    /**
     * Increases the follower's current attack and health by a specified amount, then add this buff action to the
     * follower's buff log
     *
     * @param atk        attack to be increased
     * @param hp         health to be increased
     * @param sourceCard the card that gives this buff
     */
    public void buff(int atk, int hp, Card sourceCard) {
        attack += atk;
        health += hp;
        buffList.append('\n').append(sourceCard.getName()).append(" +").append(atk).append("/+").append(hp);
    }

    /**
     * Grant this follower a simple effect.
     *
     * <p>All modifications related to the follower's {@linkplain #atkStatus} will not be valid if the current status is
     * {@linkplain AttackStatus#DISABLED}. {@linkplain SimpleEffects#RUSH} will not override {@linkplain
     * AttackStatus#STORM}. Besides, this method will override {@linkplain AttackStatus#ATTACKED}, which means granting
     * either {@linkplain SimpleEffects#STORM} or {@linkplain SimpleEffects#RUSH} will allow this follower to attack
     * even if it has already attacked this round.</p>
     *
     * @param simpleEffect the simple effect to be granted
     * @throws IllegalArgumentException if the simple effect does not exist
     * @see SimpleEffects
     * @see AttackStatus
     */
    public void grantSimpleEffect(SimpleEffects simpleEffect) {
        switch (simpleEffect) {
            case STORM:
                if (atkStatus != AttackStatus.DISABLED)
                    atkStatus = AttackStatus.STORM;
                break;

            case RUSH:
                if (atkStatus != AttackStatus.STORM && atkStatus != AttackStatus.DISABLED)
                    atkStatus = AttackStatus.RUSH;
                break;

            case DRAIN:
                hasDrain = true;
                break;

            case WARD:
                hasWard = true;
                break;

            case BANE:
                hasBane = true;
                break;

            case AMBUSH:
                hasAmbush = true;
                break;

            case ATTACKED:
                if (atkStatus != AttackStatus.DISABLED)
                    atkStatus = AttackStatus.ATTACKED;
                break;

            case DAMAGE_IMMUNE:
                hasDamageImmune = true;
                break;

            case UNTARGETABLE:
                hasUntargetable = true;
                break;

            case WARD_PIERCE:
                hasWardPierce = true;
                break;

            case DISABLED:
                atkStatus = AttackStatus.DISABLED;
                break;

            default:
                throw new IllegalArgumentException("No such effect found");
        }
    }

    /**
     * Revoke a simple effect from this follower
     *
     * <p>Only temporary effects are allowed to be removed.</p>
     *
     * @param simpleEffects the simple effect to be revoked
     * @see SimpleEffects
     */
    public void revokeSimpleEffect(SimpleEffects simpleEffects) {
        switch (simpleEffects) {
            case DAMAGE_IMMUNE:
                hasDamageImmune = false;
                break;

            case DISABLED:
                atkStatus = AttackStatus.ENTRY;
                break;
        }
    }

    /**
     * Evolve the current follower. Its attack and health will increase according to its {@linkplain #evoAtkAdd} and
     * {@linkplain #evoHpAdd}, and it will gain {@linkplain AttackStatus#RUSH} if it just entered field. One follower is
     * only allowed to be evolved once.
     *
     * @throws IllegalStateException if the follower has already been evolved
     */
    public void evolve() {
        if (!isEvolved) {
            attack += evoAtkAdd;
            health += evoHpAdd;
            isEvolved = true;

            if (atkStatus == AttackStatus.ENTRY) {
                atkStatus = AttackStatus.RUSH;
            }

        } else {
            throw new IllegalStateException("Follower has already been evolved!");
        }
    }

    /**
     * Attack an enemy follower. Damage dealt to enemy follower equals the current attack value. Counterattack damage
     * received equals to enemy follower's current attack value.
     *
     * <p>This method does not check if the follower's {@linkplain #atkStatus} is valid for attacking. Therefore, it is
     * mandatory to perform such check by calling {@linkplain AttackStatus#canAttackFollower()} before invoking this
     * method.</p>
     *
     * <p>If this follower is currently under {@linkplain SimpleEffects#AMBUSH}, this status will be removed after
     * attacking.</p>
     *
     * @param enemy the enemy follower to attack
     */
    public void attack(Follower enemy) {
        if (enemy != null && enemy.isAlive()) {
            enemy.takeDamage(this);
            takeDamage(enemy);

            if (hasAmbush) {
                hasAmbush = false;
            }

        }

        atkStatus = AttackStatus.ATTACKED;
    }

    /**
     * Attack enemy leader. Damage dealt to enemy leader equals the current attack value.
     *
     * <p>This method does not check if the follower's {@linkplain #atkStatus} is valid for attacking. Therefore, it is
     * mandatory to perform such check by calling {@linkplain AttackStatus#canAttackLeader()} before invoking this
     * method.</p>
     *
     * <p>If this follower is currently under {@linkplain SimpleEffects#AMBUSH}, this status will be removed after
     * attacking.</p>
     *
     * @param opponent the enemy leader to attack
     */
    public void attack(Leader opponent) {
        opponent.takeDamage(this);

        if (hasAmbush) {
            hasAmbush = false;
        }

        atkStatus = AttackStatus.ATTACKED;
    }

    /**
     * @return the follower's swordcraft trait
     * @see Trait.SwordCraftTrait
     */
    @Override
    public SwordCraftTrait getSwordCraftTrait() {
        return swordCraftTrait;
    }

    /**
     * Change the follower's attack to a specific value
     *
     * @param targetAttack the target attack value
     */
    public void changeAttack(int targetAttack) {
        attack = targetAttack;
    }

    /**
     * Change the follower's health to a specific value
     *
     * @param targetHealth the target health value
     */
    public void changeHealth(int targetHealth) {
        health = targetHealth;
    }

    /**
     * The library of all simple effects
     */
    public enum SimpleEffects {
        /**
         * Followers with BANE effect will have a guaranteed kill if it deals damage to an enemy follower through
         * attacking or counterattacking. This effect is valid even if the follower does 0 attack damage.
         */
        BANE,
        /**
         * When selecting an attack target, if there is(are) follower(s) with WARD, they must be selected first. In
         * other words, it it invalid to attack enemy follower without WARD or enemy leader when there is(are) enemy
         * follower(s) with WARD on the enemy field.
         */
        WARD,
        /**
         * Followers with DRAIN effect heals their leader upon dealing any attack damage. The heal amount equals the
         * damage dealt.
         */
        DRAIN,
        /**
         * Followers with AMBUSH effect can not be selected as effect or attack target. In other words, they will not be
         * affected by any targeted spells, effects and enemy follower attack. This effect will no longer be valid if
         * the follower attacks.
         */
        AMBUSH,
        /**
         * Followers granted with RUSH effect will immediately be granted with {@linkplain AttackStatus#RUSH}. It
         * enables the follower to attack any enemy follower but not the enemy leader.
         */
        RUSH,
        /**
         * Followers granted with STORM effect will immediately be granted with {@linkplain AttackStatus#STORM}. It
         * enables the follower to attack any enemy target.
         */
        STORM,
        /**
         * Followers with UNTARGETABLE can not be selected as any targeted spell's target.
         */
        UNTARGETABLE,
        /**
         * Followers granted with ATTACKED effect will immediately be granted with {@linkplain AttackStatus#ATTACKED}.
         * It means that the follower has already attacked in this round and can no longer make any other attack.
         */
        ATTACKED,
        /**
         * Followers with damage immune will not receive any spell or effect damage. Attack and counterattack damages
         * still apply.
         */
        DAMAGE_IMMUNE,
        /**
         * Followers with WARD_PIERCE are allowed to attack any enemy target even if there is(are) enemy follower(s)
         * with WARD effect.
         */
        WARD_PIERCE,
        /**
         * Followers granted with DISABLED effect will immediately be granted with {@linkplain AttackStatus#DISABLED}.
         * It means that the follower is banned from making any attack unless this effect is removed.
         */
        DISABLED
    }
}
