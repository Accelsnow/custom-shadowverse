/**
 * This class collaborates with {@linkplain Game} class and processes all effects. It also provides target selection
 * requirements for targeting effect and string descriptions for all effects. This class is only to be accessed the
 * static way.
 *
 * <p>All uncommented methods are functional methods for different effects to actually execute. They are basic
 * individual simple effects and are invoked through and only through {@linkplain #processEffect(String, Card, Game,
 * Player, Player)} and {@linkplain #proceedEffect(Card, Player)}.</p>
 *
 * @see Game#sendEffect(String, Card, Player, Card, Player)
 */
public final class EffectLib {
    private static String[] efxInfo;
    private static Game game;
    private static Player sourcePlayer, opponentPlayer;
    private static Card sourceCard;

    private EffectLib() {

    }

    /**
     * This methods provides string description for the given effect
     *
     * @param effect the effect to be interpreted
     * @param source the origin card of the effect
     * @return the string description of the card
     */
    public static String getEffectDescription(String effect, Card source) {
        String[] effectInfo = effect.split("@");

        switch (effectInfo[0]) {
            case "DMGENEFOL":
                return "Deal " + effectInfo[1] + " damage to an enemy follower";

            case "DRWCRD":
                return "Draw " + effectInfo[1] + " card(s) from the deck";

            case "RDMBUFFRIFOL":
                return "Give a random allied follower +" + effectInfo[1] + "/+" + effectInfo[2];

            case "DMGENELED":
                return "Deal " + effectInfo[1] + " damage to the enemy leader";

            case "DMGALLENEFOL":
                return "Deal " + effectInfo[1] + " damage to all enemy followers";

            case "PUTFOL":
                return "Put " + effectInfo[2] + " " + effectInfo[1] + " into your hand";

            case "GVEEFXBUFSLFFCOND":
                return getConditionString(effectInfo[4]) +
                        "gain +" + effectInfo[2] + "/+" + effectInfo[3] + " and " + effectInfo[1];

            case "DMGENEFOLPUTFOL":
                return "Deal " + effectInfo[1] + " damage to an enemy follower. Put " +
                        effectInfo[3] + " " + effectInfo[2] + " into your hand";

            case "BUFSLF":
                return "Gain +" + effectInfo[1] + "/+" + effectInfo[2];

            case "RTNFOLTOHND":
                return "Return another follower to its owner's hand";

            case "BUFSLFCOND":
                if (Integer.parseInt(effectInfo[1]) < 0)
                    return getConditionString(effectInfo[3]) + "gain " + effectInfo[1] + "/+" + effectInfo[2];
                else
                    return getConditionString(effectInfo[3]) + "gain +" + effectInfo[1] + "/+" + effectInfo[2];

            case "DMGRDMMULENEFOL":
                return "Deal " + effectInfo[1] + " damage to a random enemy follower " + effectInfo[2] + " times";

            case "BUFFRIFOLCOND":
                return getConditionString(effectInfo[3]) +
                        "give a friendly follower +" + effectInfo[1] + "/+" + effectInfo[2];

            case "SUMFOL":
                StringBuilder sumFolResult = new StringBuilder("Summon ");
                for (int n = 1; n < effectInfo.length; n++)
                    sumFolResult.append("a ").append(effectInfo[n]).append(" and ");
                sumFolResult.replace(sumFolResult.length() - 5, sumFolResult.length(), "");
                return sumFolResult.toString();

            case "DMGENEFOLVAR":
                return "Deal X damage to an enemy follower. X equals " + getVariableString(effectInfo[1]);

            case "BUFFRIFOL":
                return "Give +" + effectInfo[1] + "/+" + effectInfo[2] + " to an allied follower";

            case "BUFFRIOFF":
                return "Give +" + effectInfo[1] + "/+" + effectInfo[2] + " to an allied Officer follower";

            case "BUFFRIOFFETR":
                return "Give +" + effectInfo[1] + "/+" + effectInfo[2] + " to all allied Officer followers that come" +
                        " into play (does not include summoned ones)";

            case "BUFALLFRIOFF":
                return "Give +" + effectInfo[1] + "/+" + effectInfo[2] + " to all other allied Officer followers";

            case "BUFALLFRIFOL":
                return "Give +" + effectInfo[1] + "/+" + effectInfo[2] + " to all other allied followers";

            case "BTHDRWCRD":
                return "Both players draw " + effectInfo[1] + " card(s)";

            case "DMGENEFOLDRWCRD":
                return "Deal " + effectInfo[1] + " damage to an enemy. Draw " + effectInfo[2] + " card(s)";

            case "DMGENEFOLBSTDMG":
                int damage = Integer.parseInt(effectInfo[1]) + source.getBoostAmount();
                return "Deal " + damage + " damage to an enemy follower.\nSpellboost: Deal 1 more";

            case "SUMFOLBSTNUM":
                int amount = 1 + source.getBoostAmount();
                return "Summon " + amount + " " + effectInfo[1] + "\nSpellboost: Summon 1 more";

            case "KILENEFOLBSTCST":
                return "Destroy an enemy follower.\nSpellboost: Subtract 1 more cost of this card";

            case "SUMMULFOL":
                return "Summon " + effectInfo[2] + " " + effectInfo[1];

            case "BSTCST":
                return "Spellboost: Subtract 1 more cost of this card";

            case "INCCSTDRWCRDCOND":
                return ", increase max cost by 1. " + getConditionString(effectInfo[2]) +
                        "draw " + effectInfo[1] + " card(s) from the deck";

            case "GVEEFXCOND":
                return getConditionString(effectInfo[2]) + "gain " + effectInfo[1];

            case "DMGALLFOL":
                return "Deal " + effectInfo[1] + " damage to all followers";

            case "ADDNEC":
                return "Gain " + effectInfo[1] + " necromancy";

            case "SUMFOLCOND":
                StringBuilder sumFolCondResult = new StringBuilder("summon ");
                for (int n = 1; n < effectInfo.length - 1; n++)
                    sumFolCondResult.append("a ").append(effectInfo[n]).append(" and ");
                sumFolCondResult.replace(sumFolCondResult.length() - 5, sumFolCondResult.length(), "");
                return getConditionString(effectInfo[2]) + sumFolCondResult.toString();

            case "SLFBAN":
                return "Banish self";

            case "KILENEFOLSUMFOLCOND":
                return "Destroy an enemy follower. " + getConditionString(effectInfo[2]) + "summon a " + effectInfo[1];

            case "GVERDMFRIFOLEFX":
                return "Give " + effectInfo[1] + " to a random allied follower";

            case "DMGSLFLED":
                return "Deal " + effectInfo[1] + " damage to your leader";

            case "DMGENEFOLHELSLFLED":
                return "Deal " + effectInfo[1] + " damage to an enemy follower. " +
                        "Then restore " + effectInfo[2] + " health to your leader";

            case "DMGSLFLEDKILENEFOL":
                return "Deal " + effectInfo[1] + " damage to your leader. Destroy an enemy";

            case "DMGALL":
                return "Deal " + effectInfo[1] + " damage to all allied and enemy followers and leaders";

            case "DECCDDRWCRD":
                return "Subtract " + effectInfo[1] + " from the Countdown of an allied amulet. " +
                        "Draw " + effectInfo[2] + " card(s)";

            case "BANENEFOLHPLES":
                return "Banish an enemy follower with " + effectInfo[1] + " health or less";

            case "DECCDALL":
                return "Subtract " + effectInfo[1] + " from all allied amulets";

            case "BANENEFOL":
                return "Banish an enemy follower";

            case "HELSLFLED":
                return "Restore " + effectInfo[1] + " health to your leader";

            case "DISHNDDRWHND":
                return "Discard your hand. Draw a card for each card you discarded";

            case "DMGENEALL":
                return "Deal " + effectInfo[1] + " damage to all enemy followers and leader";

            case "KILFOLREVFOL":
                return "Destroy a follower, and then return it to play.";

            case "DMGENEALLCOND":
                return getConditionString(effectInfo[2]) +
                        "Deal " + effectInfo[1] + " damage to all enemy followers and leader";

            case "DMGALLFRIFOL":
                return "Deal " + effectInfo[1] + " damage to all other allied followers";

            case "KILENEFOLAMU":
                return "Destroy an enemy follower or amulet";

            case "BUFSLFVARDISHND":
                return "Gain +" + effectInfo[1] + "/+" + effectInfo[2] + " for X times, " +
                        "X equals to " + getVariableString(effectInfo[3]) +
                        ". Discard your hand.";

            case "LUCIFER":
                return "Replace this card's fanfare with: At the end of your turn, deal 4 damage to the enemy leader";

            case "DMGENELEDVAR":
                return "Deal " + getVariableString(effectInfo[1]) + " damage to the enemy leader";

            case "REPDEK":
                return "Replace your deck with the " + effectInfo[1] + " Deck";

            case "DMGRDMENEFOL":
                return "Deal " + effectInfo[1] + " damage to a random enemy follower";

            case "RTNFRIFOLAMUDRWCRD":
                return "Return an allied follower or amulet to your hand. Draw " + effectInfo[1] + " card(s)";

            case "BUFRDMFRIFOLCOND":
                return getConditionString(effectInfo[3]) +
                        "give +" + effectInfo[1] + "/+" + effectInfo[2] + " to one of them randomly";

            case "RTNFRIFOLAMU":
                return "Return an allied follower or amulet to your hand";

            case "CHGCST":
                return "Change the cost of a " + effectInfo[1] + " in your hand to " + effectInfo[2];

            case "DRWCRDCOND":
                return getConditionString(effectInfo[2]) + "draw " + effectInfo[1] + " card(s) from the deck";

            case "GVEALLFRIFOLEFX":
                return "Give " + effectInfo[1] + " to all allied followers";

            case "REVALLFRIFOLEFX":
                return "Revoke " + effectInfo[1] + " from all allied followers";

            case "GVEEFX":
                return "This follower gains " + effectInfo[1];

            case "BUFSLFVAR":
                return "Gain +" + getVariableString(effectInfo[1]) + "/+" + getVariableString(effectInfo[2]);

            case "CHGSLFATKVAR":
                return "Change this follower's attack to " + getVariableString(effectInfo[1]);

            case "RTNFRIFOLRDMRTNENEFOL":
                return "Return an allied follower or amulet to your hand. Return a random enemy follower";

            case "CHGENEFOLHP":
                return "Change an enemy follower's health to " + effectInfo[1];

            case "GVEENEFOLEFX":
                return "Grant " + effectInfo[1] + " to an enemy follower";

            case "KILENEAMUCOND":
                return getConditionString(effectInfo[1]) + "destroy an enemy amulet";

            case "BUFSLFVARRTNALLFRIFOL":
                return "Gain +X/+X. X equals " + getVariableString(effectInfo[1]) +
                        " Then return all other allied followers to your hand.";

            case "PUTFOLCHGCST":
                return "Put " + effectInfo[2] + " " + effectInfo[1] + " into your hand and change their cost to " +
                        effectInfo[3];

            case "CHGETRCRD":
                return "Transform each allied " + effectInfo[1] + " that comes into play into a " + effectInfo[2];

            case "CHGENEFLDHPCOND":
                return getConditionString(effectInfo[2]) +
                        "change the health of all enemy followers to " + effectInfo[1];

            case "DMGFIGENEFOL":
                return "Deal " + effectInfo[1] + " damage to the opponent";

            case "DMGRDMMULENEFOLVAR":
                return "Deal " + effectInfo[1] + " damage to a random enemy follower X times. X equals to " +
                        getVariableString(effectInfo[2]);

            case "FREEVOFOL":
                return "if the follower is" + effectInfo[1] + ", evolve it for free(count as round evolve)";

            case "EVOSLFCOND":
                return getConditionString(effectInfo[1]) + "evolve this follower";

            case "HELSLFLEDVAR":
                return "Restore X health to your leader. X equals to" + getVariableString(effectInfo[1]);

            case "PUTHNDFULFOL":
                return "Put " + effectInfo[1] + " into your hand until it is full";

            case "GVEALLENEFOLEFX":
                return "Give " + effectInfo[1] + " to all enemy followers";

            case "RTNALLFOL":
                return "Return all followers to the players' hands";

            case "TRAALLHNDCRD":
                return "Transform each " + effectInfo[1] + " in your hand into " + effectInfo[2];

            case "DRWCRDDMGENETARVAR":
                return "Draw " + effectInfo[1] + " card. " +
                        "Then deal X damage to an enemy target. X equals to" + getVariableString(effectInfo[2]);

            case "DMGENETAR":
                return "Deal " + effectInfo[3] + " to an enemy follower or leader.";

            case "BUFRDMFRIOFF":
                return "Give +" + effectInfo[1] + "/+" + effectInfo[2] + " to a random allied Officer follower";

            case "BUFSLFCOMETR":
                return "Gain +" + effectInfo[1] + "/+" + effectInfo[2] + " when an allied Commander comes into play.";

            case "GVESLFEFXCOND":
                return getConditionString(effectInfo[2]) + "gain " + effectInfo[1];

            case "DRWRDMCOM":
                return "Put " + effectInfo[1] + " random Commander(s) from the deck to your hand.";

            case "GVEFRICOMEFX":
                return "Give " + effectInfo[1] + " to an allied Commander";

            case "BUFALLFRIFOLCOND":
                return getConditionString(effectInfo[3]) +
                        "give all other allied followers " + effectInfo[1] + "/+" + effectInfo[2];

            case "BUFONECSTFRIFOL":
                return "Give +" + effectInfo[1] + "/+" + effectInfo[2] + " to a allied follower with 1 cost";

            case "DRWRDMOFFVAR":
                return "Draw, " + getVariableString(effectInfo[1]) + ", random Officer(s) from the deck to your hand";

            case "GVEALLFRIOFFEFX":
                return "Give " + effectInfo[1] + " to all allied Officer followers";

            case "GVEETROFFEFX":
                return "give storm to all allied Officers that enter the field";

            case "DRWCRDVAR":
                return "Draw X cards. X equals " + getVariableString(effectInfo[1]);

            case "BUFSLFGVESLFEFXVARCOND":
                return "Gain +X/+0. X equals " + getVariableString(effectInfo[2]) + ". "
                        + getConditionString(effectInfo[2]) + "gain " + effectInfo[1];

            case "GVEETRFRIFOLEFXCOND":
                return getConditionString(effectInfo[2]) + "give it " + effectInfo[1];

            case "SUMEFXFOL":
                return "Summon a " + effectInfo[1] + ", give it " + effectInfo[2];

            case "SUMCSTDEKFOL":
                return "Summon a random cost " + effectInfo[1] + " follower from your deck to your field.";

            case "KILENEATKFOL":
                return "Destryo an enemy follower with at least " + effectInfo[1] + " attack";

            case "SUMFULFLDFOL":
                return "Summon " + effectInfo[1] + " until the field is full";

            default:
                throw new IllegalArgumentException("NO CONDITION FOUND " + effectInfo[0]);
        }

    }

    /**
     * This methods cleans up all effect variables so that the next effect will not be affected
     */
    private static void reset() {
        game = null;
        sourcePlayer = null;
        opponentPlayer = null;
        efxInfo = null;
    }

    /**
     * This methods processes the effect. All effects must be passed to this method first. It returns the result of the
     * process for the {@linkplain Game} class to further process. The method will call {@linkplain #reset()} if the
     * effect is completely processed.
     *
     * @param effect       effect to be processed
     * @param source       the origin card of the effect
     * @param g            the game object reference
     * @param originPlayer the player that owns the origin card
     * @param oppoPlayer   the opponent player to the origin player
     * @return the response of the effect process. {@linkplain Response#DONE} if the effect is successfully processed.
     * {@linkplain Response#FAIL} if the effect failed to execute(due to unmet effect activation requirements). All
     * other return values indicates that further target selection is required to proceed the effect.
     * @see Response
     */
    public static Response processEffect(String effect, Card source, Game g, Player originPlayer, Player oppoPlayer) {
        game = g;
        sourcePlayer = originPlayer;
        opponentPlayer = oppoPlayer;
        sourceCard = source;
        efxInfo = effect.split("@");
        sourceCard.effectActivated();

        switch (efxInfo[0]) {
            case "DMGENEFOL":
                return Response.ENEMY_FOLLOWER;

            case "DRWCRD":
                drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[1]));
                break;

            case "RDMBUFFRIFOL":
                randomBuffFriendlyFollower(Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                break;

            case "DMGENELED":
                damageEnemyLeader(Integer.parseInt(efxInfo[1]));
                break;

            case "DMGALLENEFOL":
                damagePlayerFollowers(Integer.parseInt(efxInfo[1]), opponentPlayer);
                break;

            case "PUTFOL":
                putFollower(efxInfo[1], Integer.parseInt(efxInfo[2]));
                break;

            case "GVEEFXBUFSLFCOND":
                if (willActivate(efxInfo[4])) {
                    grantSimpleEffect((Follower) sourceCard, efxInfo[1]);
                    buffFollower((Follower) sourceCard, Integer.parseInt(efxInfo[2]), Integer.parseInt(efxInfo[3]));
                } else
                    return Response.FAIL;
                break;

            case "DMGENEFOLCOND":
                if (willActivate(efxInfo[2]))
                    return Response.ENEMY_FOLLOWER;
                else
                    return Response.FAIL;

            case "DMGENEFOLPUTFOL":
                return Response.ENEMY_FOLLOWER;

            case "BUFSLF":
                buffFollower((Follower) sourceCard, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                break;

            case "RTNFOLTOHND":
                return Response.ANY_FOLLOWER;

            case "BUFSLFCOND":
                if (willActivate(efxInfo[3]))
                    buffFollower((Follower) sourceCard, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                else
                    return Response.FAIL;
                break;

            case "DMGRDMMULENEFOL":
                for (int n = 0; n < Integer.parseInt(efxInfo[2]); n++)
                    randomDamageEnemyFollower(Integer.parseInt(efxInfo[1]));
                break;

            case "BUFFFRIFOLCOND":
                if (willActivate(efxInfo[3]))
                    return Response.FRIENDLY_FOLLOWER;
                else
                    return Response.FAIL;

            case "SUMFOL":
                for (int n = 1; n < efxInfo.length; n++)
                    summonFollower(efxInfo[n]);
                break;

            case "DMGENEFOLVAR":
                return Response.ENEMY_FOLLOWER;

            case "BUFFRIFOL":
                return Response.FRIENDLY_FOLLOWER;

            case "BUFFRIOFF":
                return Response.FRIENDLY_OFFICER;

            case "BUFFRIOFFETR":
                return Response.FRIENDLY_OFFICER_ENTRANCE;

            case "BUFALLFRIOFF":
                buffAllFriendlyOfficer(Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                break;

            case "BUFALLFRIFOL":
                buffAllFriendlyFollower(Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                break;

            case "BTHDRWCRD":
                if (drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[1])))
                    drawCard(opponentPlayer, sourcePlayer, Integer.parseInt(efxInfo[1]));
                break;

            case "DMGENEFOLDRWCRD":
                return Response.ENEMY_FOLLOWER;

            case "DMGENEFOLBSTDMG":
                return Response.ENEMY_FOLLOWER;

            case "SUMFOLBSTNUM":
                for (int n = 0; n < 1 + sourceCard.getBoostAmount(); n++)
                    summonFollower(efxInfo[1]);
                break;

            case "KILENEFOLBSTCST":
                return Response.ENEMY_FOLLOWER;

            case "SUMMULFOL":
                for (int n = 0; n < Integer.parseInt(efxInfo[2]); n++)
                    summonFollower(efxInfo[1]);
                break;

            case "BSTCST":
                break;

            case "INCCSTDRWCRDCOND":
                if (willActivate(efxInfo[2]))
                    drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[1]));
                increaseMaxCost();
                break;

            case "GVEEFXCOND":
                if (willActivate(efxInfo[2]))
                    grantSimpleEffect((Follower) sourceCard, efxInfo[1]);
                else
                    return Response.FAIL;
                break;

            case "DMGALLFOL":
                damagePlayerFollowers(Integer.parseInt(efxInfo[1]), opponentPlayer);
                damagePlayerFollowers(Integer.parseInt(efxInfo[1]), sourcePlayer);
                break;

            case "ADDNEC":
                addNecromancy(sourcePlayer.getGrave(), Integer.parseInt(efxInfo[1]));
                break;

            case "SUMFOLCOND":
                if (willActivate(efxInfo[2]))
                    summonFollower(efxInfo[1]);
                else
                    return Response.FAIL;
                break;

            case "SLFBAN":
                banishFollower((Follower) sourceCard, sourcePlayer);
                break;

            case "KILENEFOLSUMFOLCOND":
                return Response.ENEMY_FOLLOWER;

            case "GVERDMFRIFOLEFX":
                grantRandomPlayerFollowerSimpleEffect(sourcePlayer, efxInfo[1]);
                break;

            case "DMGSLFLEDDRWCRD":
                if (damageSelfLeader(Integer.parseInt(efxInfo[1])))
                    drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[2]));
                break;

            case "DMGSLFLEDDMGENETAR":
                return Response.ENEMY_FOLLOWER_OR_LEADER;

            case "DMGSLFLED":
                damageSelfLeader(Integer.parseInt(efxInfo[1]));
                break;

            case "DMGENEFOLHELSLFLED":
                healSelfLeader(Integer.parseInt(efxInfo[2]));
                return Response.ENEMY_FOLLOWER;

            case "DMGSLFLEDKILENEFOL":
                return Response.ENEMY_FOLLOWER;

            case "DMGALL":
                damageAll(Integer.parseInt(efxInfo[1]));
                break;

            case "DECCDDRWCRD":
                if (drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[2])))
                    return Response.FRIENDLY_CD_AMULET;
                break;

            case "BANENEFOLHPLES":
                Response.setParameter(Integer.parseInt(efxInfo[1]));
                return Response.ENEMY_FOLLOWER_HP_LESS;

            case "DECCDALL":
                decreaseAllCountDown(Integer.parseInt(efxInfo[1]));
                break;

            case "BANENEFOL":
                return Response.ENEMY_FOLLOWER;

            case "HELSLFLED":
                healSelfLeader(Integer.parseInt(efxInfo[1]));
                break;

            case "DISHNDDRWHND":
                int amount = sourcePlayer.getHand().size();
                discardHand();
                drawCard(sourcePlayer, opponentPlayer, amount);
                break;

            case "DMGENEALL":
                damageAllEnemy(Integer.parseInt(efxInfo[1]));
                break;

            case "KILFOLREVFOL":
                return Response.ANY_FOLLOWER;

            case "DMGENEALLCOND":
                if (willActivate(efxInfo[2]))
                    damageAllEnemy(Integer.parseInt(efxInfo[1]));
                break;

            case "DMGALLFRIFOL":
                damagePlayerFollowers(Integer.parseInt(efxInfo[1]), sourcePlayer);
                break;

            case "KILENEFOLAMU":
                return Response.ENEMY_FOLLOWER_OR_AMULET;

            case "BUFSLFVARDISHND":
                for (int n = 0; n < getVariable(efxInfo[3]); n++)
                    buffFollower((Follower) sourceCard, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                discardHand();
                break;

            case "DMGENETARHELSLFLED":
                return Response.ENEMY_FOLLOWER_OR_LEADER;

            case "LUCIFER":
                sourceCard.changeEffect();
                break;

            case "DMGENELEDVAR":
                damageEnemyLeader(getVariable(efxInfo[1]));
                break;

            case "REPDEK":
                replaceDeck(efxInfo[1]);
                break;

            case "DMGRDMENEFOL":
                randomDamageEnemyFollower(Integer.parseInt(efxInfo[1]));
                break;

            case "RTNFRIFOLAMUDRWCRD":
                return Response.FRIENDLY_FOLLOWER_OR_AMULET;

            case "BUFRDMFRIFOLCOND":
                if (willActivate(efxInfo[3])) {
                    Card randomSelection;
                    Field targetField = sourcePlayer.getField();

                    if (targetField.size() > 1) {
                        do {
                            randomSelection = targetField.get((int) (Math.random() * targetField.size()));
                        }
                        while (!(randomSelection instanceof Follower) || ((Follower) randomSelection).getAttack() != 1);

                        ((Follower) randomSelection).buff(Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]), sourceCard);
                    }
                }
                break;

            case "RTNFRIFOLAMU":
                return Response.FRIENDLY_FOLLOWER_OR_AMULET;

            case "CHGCST":
                changeCost(sourcePlayer, efxInfo[1], Integer.parseInt(efxInfo[2]));
                break;

            case "DRWCRDCOND":
                if (willActivate(efxInfo[2]))
                    drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[1]));
                break;

            case "GVEALLFRIFOLEFX":
                grantAllPlayerFollowerSimpleEffect(sourcePlayer, efxInfo[1]);
                break;

            case "REVALLFRIFOLEFX":
                revokeAllPlayerFollowerSimpleEffect(sourcePlayer, efxInfo[1]);
                break;

            case "GVEEFX":
                grantSimpleEffect((Follower) sourceCard, efxInfo[1]);
                break;

            case "BUFSLFVAR":
                buffFollower((Follower) sourceCard, getVariable(efxInfo[1]), getVariable(efxInfo[2]));
                break;

            case "CHGSLFATKVAR":
                changeFollowerAttack((Follower) sourceCard, getVariable(efxInfo[1]));
                break;

            case "RTNFRIFOLRDMRTNENEFOL":
                return Response.FRIENDLY_FOLLOWER_OR_AMULET;

            case "CHGENEFOLHP":
                return Response.ENEMY_FOLLOWER;

            case "GVERDMENEFOLEFX":
                grantRandomPlayerFollowerSimpleEffect(opponentPlayer, efxInfo[1]);
                break;

            case "GVEENEFOLEFX":
                return Response.ENEMY_FOLLOWER;

            case "KILENEAMUCOND":
                if (willActivate(efxInfo[1]))
                    return Response.ENEMY_AMULET;
                break;

            case "BUFSLFVARRTNALLFRIFOL":
                buffFollower((Follower) sourceCard, getVariable(efxInfo[1]), getVariable(efxInfo[1]));
                returnPlayerFollowersToHand(sourcePlayer);
                break;

            case "PUTFOLCHGCST":
                putChangedCostFollower(efxInfo[1], Integer.parseInt(efxInfo[2]), Integer.parseInt(efxInfo[3]));
                break;

            case "CHGETRCRD":
                return Response.FRIENDLY_FOLLOWER_ENTRANCE;

            case "CHGENEFLDHPCOND":
                if (willActivate(efxInfo[2]))
                    for (Card c : opponentPlayer.getField())
                        if (c instanceof Follower)
                            changeFollowerHealth((Follower) c, Integer.parseInt(efxInfo[1]));
                break;

            case "DMGFIGENEFOL":
                return Response.FIGHTING_ENEMY;

            case "DMGRDMMULENEFOLVAR":
                int damage = Integer.parseInt(efxInfo[1]);
                for (int n = 0; n < getVariable(efxInfo[2]); n++)
                    randomDamageEnemyFollower(damage);
                break;

            case "FREEVOFOL":
                return Response.FRIENDLY_FOLLOWER_ENTRANCE;

            case "EVOSLFCOND":
                if (willActivate(efxInfo[1]))
                    ((Follower) sourceCard).evolve();
                break;

            case "HELSLFLEDVAR":
                healSelfLeader(getVariable(efxInfo[1]));
                break;

            case "PUTHNDFULFOL":
                while (true)
                    if (!(sourcePlayer.getHand().add(CardLib.getCard(efxInfo[1])))) break;
                break;

            case "GVEALLENEFOLEFX":
                grantAllPlayerFollowerSimpleEffect(opponentPlayer, efxInfo[1]);
                break;

            case "RTNALLFOL":
                returnPlayerFollowersToHand(sourcePlayer);
                returnPlayerFollowersToHand(opponentPlayer);
                break;

            case "TRAALLHNDCRD":
                transformHandCard(sourcePlayer, efxInfo[1], efxInfo[2]);
                break;

            case "DRWCRDDMGENETARVAR":
                if (drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[1])))
                    return Response.ENEMY_FOLLOWER_OR_LEADER;
                break;

            case "DMGENETAR":
                return Response.ENEMY_FOLLOWER_OR_LEADER;

            case "BUFRDMFRIOFF":
                randomBuffFriendlyOfficer(Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                break;

            case "BUFSLFCOMETR":
                return Response.FRIENDLY_COMMANDER_ENTRANCE;

            case "GVESLFEFXCOND":
                if (willActivate(efxInfo[2]))
                    grantSimpleEffect((Follower) sourceCard, efxInfo[1]);
                break;

            case "DRWRDMCOM":
                drawRandomSwordCraft(Trait.SwordCraftTrait.COMMANDER, Integer.parseInt(efxInfo[1]));
                break;

            case "GVEFRICOMEFX":
                return Response.FRIENDLY_COMMANDER;

            case "BUFALLFRIFOLCOND":
                if (willActivate(efxInfo[3]))
                    buffAllFriendlyFollower(Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                break;

            case "BUFONECSTFRIFOL":
                return Response.FRIENDLY_COST_ONE_FOLLOWER;

            case "DRWRDMOFFVAR":
                drawRandomSwordCraft(Trait.SwordCraftTrait.OFFICER, getVariable(efxInfo[1]));
                break;

            case "SUMFOLCOMETR":
                return Response.FRIENDLY_COMMANDER_ENTRANCE;

            case "GVEALLFRIOFFEFX":
                grantAllOfficerSimpleEffect(efxInfo[1]);
                break;

            case "SUMCSTDEKFOL":
                summonGivenCostRandomFollower(Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                break;

            case "KILENEATKFOL":
                Response.setParameter(Integer.parseInt(efxInfo[1]));
                return Response.ENEMY_FOLLOWER_ATK_MORE;

            case "GVEETROFFEFX":
                return Response.FRIENDLY_OFFICER_ENTRANCE;

            case "DRWCRDVAR":
                drawCard(sourcePlayer, opponentPlayer, getVariable(efxInfo[1]));
                break;

            case "BUFSLFGVESLFEFXVARCOND":
                buffFollower((Follower) sourceCard, getVariable(efxInfo[2]), 0);
                if (willActivate(efxInfo[2]))
                    grantSimpleEffect((Follower) sourceCard, efxInfo[1]);
                break;

            case "GVEETRFRIFOLEFXCOND":
                return Response.FRIENDLY_FOLLOWER_ENTRANCE;

            case "SUMEFXFOL":
                Follower follower = (Follower) CardLib.getCard(efxInfo[1]);
                grantSimpleEffect(follower, efxInfo[2]);
                sourcePlayer.getField().add(follower);
                break;

            case "SUMFULFLDFOL":
                while (sourcePlayer.getField().size() < Field.MAXIMUM_FIELD_SIZE - 1)
                    summonFollower(efxInfo[1]);
                break;

            default:
                throw new IllegalArgumentException("NO EFX FOUND: " + efxInfo[0]);

        }

        reset();
        return Response.DONE;
    }

    /**
     * This method takes the card player selected as effect target and proceeds the unfinished effect processing.
     * {@linkplain #reset()} is called at the end to clear up.
     *
     * @param card  the selected card target
     * @param owner the owner of the card target
     */
    public static void proceedEffect(Card card, Player owner) {

        if (efxInfo != null && card != null) {
            switch (efxInfo[0]) {
                case "DMGENEFOL":
                    damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "DMGENEFOLCOND":
                    damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "DMGENEFOLPUTFOL":
                    putFollower(efxInfo[2], Integer.parseInt(efxInfo[3]));
                    damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "RTNFOL":
                    returnCardToHand(card, owner);
                    break;

                case "BUFFRIFOLCOND":
                    buffFollower((Follower) card, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                    break;

                case "DMGENEFOLVAR":
                    damageEnemyFollower((Follower) card, getVariable(efxInfo[1]));
                    break;

                case "BUFFRIFOL":
                    buffFollower((Follower) card, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                    break;

                case "BUFFRIOFF":
                    buffFollower((Follower) card, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                    break;

                case "BUFFRIOFFETR":
                    buffFollower((Follower) card, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                    break;

                case "DMGENEFOLDRWCRD":
                    if (drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[2])))
                        damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "DMGENEFOLBSTDMG":
                    damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]) +
                            sourceCard.getBoostAmount());
                    break;

                case "KILENEFOLBSTCST":
                    killFollowerOrAmulet(card);
                    break;

                case "KILENEFOLSUMFOLCOND":
                    if (willActivate(efxInfo[2]))
                        summonFollower(efxInfo[1]);
                    killFollowerOrAmulet(card);
                    break;

                case "DMGSLFLEDDMGENETAR":
                    if (damageSelfLeader(Integer.parseInt(efxInfo[1]))) {
                        if (card == CardLib.CARD_REPRESENT_LEADER)
                            damageEnemyLeader(Integer.parseInt(efxInfo[2]));
                        else
                            damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[2]));
                    }
                    break;

                case "DMGENEFOLHELSLFLED":
                    damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "DMGSLFLEDKILENEFOL":
                    if (damageSelfLeader(Integer.parseInt(efxInfo[1])))
                        killFollowerOrAmulet(card);
                    break;

                case "DECCDDRWCRD":
                    decreaseCountdown((Amulet) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "BANENEFOLHPLES":
                    banishFollower((Follower) card, opponentPlayer);
                    break;

                case "BANENEFOL":
                    banishFollower((Follower) card, opponentPlayer);
                    break;

                case "KILFOLREVFOL":
                    killReturnFollower((Follower) card, owner);
                    break;

                case "KILENEFOLAMU":
                    killFollowerOrAmulet(card);
                    break;

                case "DMGENETARHELSLFLED":
                    if (card == CardLib.CARD_REPRESENT_LEADER)
                        damageEnemyLeader(Integer.parseInt(efxInfo[1]));
                    else
                        damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    healSelfLeader(Integer.parseInt(efxInfo[2]));

                case "RTNFRIFOLAMUDRWCRD":
                    returnCardToHand(card, sourcePlayer);
                    drawCard(sourcePlayer, opponentPlayer, Integer.parseInt(efxInfo[1]));
                    break;

                case "RTNFRIFOLAMU":
                    returnCardToHand(card, sourcePlayer);
                    break;

                case "RTNFRIFOLRDMRTNENEFOL":
                    returnCardToHand(card, sourcePlayer);
                    returnRandomCardToHand(opponentPlayer);
                    break;

                case "CHGENEFOLHP":
                    changeFollowerHealth((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "GVEENEFOLEFX":
                    grantSimpleEffect((Follower) card, efxInfo[1]);
                    break;

                case "KILENEAMUCOND":
                    killFollowerOrAmulet(card);
                    break;

                case "CHGETRCRD":
                    if (card instanceof Follower && card.isAlive() && efxInfo[1].equals(card.getName())) {
                        ((Follower) card).kill();
                        sourcePlayer.getField().add(CardLib.getCard(efxInfo[2]));
                    }
                    break;

                case "DMGFIGENEFOL":
                    damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "DRWCRDDMGENETARVAR":
                    if (card == CardLib.CARD_REPRESENT_LEADER)
                        damageEnemyLeader(getVariable(efxInfo[2]));
                    else
                        damageEnemyFollower((Follower) card, getVariable(efxInfo[2]));
                    break;

                case "FREEVOFOL":
                    if (card.getName().equals(efxInfo[1]) && sourcePlayer.evolve(null))
                        ((Follower) card).evolve();
                    break;

                case "DMGENETAR":
                    if (card == CardLib.CARD_REPRESENT_LEADER)
                        damageEnemyLeader(Integer.parseInt(efxInfo[1]));
                    else
                        damageEnemyFollower((Follower) card, Integer.parseInt(efxInfo[1]));
                    break;

                case "BUFSLFCOMETR":
                    buffFollower((Follower) sourceCard, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                    break;

                case "GVEFRICOMEFX":
                    grantSimpleEffect((Follower) card, efxInfo[1]);
                    break;

                case "BUFONECSTFRIFOL":
                    buffFollower((Follower) card, Integer.parseInt(efxInfo[1]), Integer.parseInt(efxInfo[2]));
                    break;

                case "SUMFOLCOMETR":
                    for (int n = 1; n < efxInfo.length; n++)
                        summonFollower(efxInfo[n]);
                    break;

                case "KILENEATKFOL":
                    killFollowerOrAmulet(card);
                    break;

                case "GVEETROFFEFX":
                    grantSimpleEffect((Follower) card, efxInfo[1]);
                    break;

                case "GVEETRFRIFOLEFXCOND":
                    if (willActivate(efxInfo[2] + "?" + card.getName()))
                        grantSimpleEffect((Follower) card, efxInfo[1]);
                    break;

                default:
                    throw new IllegalArgumentException("NO EFX FOUND: " + efxInfo[0]);
            }
        }

        reset();
        Response.resetParameter();
    }

    /**
     * This method determines whether a method with prerequisites can be activated or not.
     *
     * @param condition the condition string from the effect. It is later interpreted as conditions to be checked
     * @return true if the effect can be activated, false otherwise
     */
    private static boolean willActivate(String condition) {
        String[] cond = condition.split("\\?");

        switch (cond[0]) {
            case "USDCRD":
                return sourcePlayer.getUsedCardCount() >= Integer.parseInt(cond[1]);

            case "OVRFLW":
                return sourcePlayer.isOverflow();

            case "ATKED":
                return ((Follower) sourceCard).getAtkStatus() == AttackStatus.ATTACKED;

            case "NEC":
                return sourcePlayer.getGrave().useNecromancy(Integer.parseInt(cond[1]));

            case "VENG":
                return sourcePlayer.isVengeance();

            case "GRAVE":
                return sourcePlayer.getGrave().size() >= Integer.parseInt(cond[1]);

            case "CONATK":
                for (Card c : sourcePlayer.getField())
                    if (c instanceof Follower && ((Follower) c).getAttack() == Integer.parseInt(cond[1]))
                        return true;
                return false;

            case "COMFLD":
                return sourcePlayer.getField().hasSwordTraitFollower(Trait.SwordCraftTrait.COMMANDER);

            case "ACTIVATED":
                return sourceCard.getEffectToggle();

            case "ENEFOL":
                return opponentPlayer.getField().followerCount() >= Integer.parseInt(cond[1]);

            case "CST":
                return CardLib.getCard(cond[2]).getCost() <= Integer.parseInt(cond[1]);

            default:
                throw new IllegalArgumentException("CONDITION NOT FOUND");
        }

    }

    /**
     * This method provides condition descriptions that describes the prerequisites for an effect
     *
     * @param condition the condition string to be interpreted
     * @return the string description of the condition
     */
    private static String getConditionString(String condition) {
        String[] cond = condition.split("\\?");

        switch (cond[0]) {
            case "USDCRD":
                return "If at least " + cond[1] + " other cards were played this round, ";

            case "OVRFLW":
                return "If OVERFLOW is active, ";

            case "ATKED":
                return "If follower has attacked in this round, ";

            case "NEC":
                return "NECROMANCY " + cond[1] + ": ";

            case "VENG":
                return "If VENGEANCE is active, ";

            case "GRAVE":
                return "If you have at least " + cond[1] + " cards in your grave, ";

            case "CONATK":
                return "If there is any " + cond[1] + " attack follower on your field, ";

            case "COMFLD":
                return "If an allied Commander is in play, ";

            case "ACTIVATED":
                return "If this card's other effects was activated this round, ";

            case "ENEFOL":
                return "If there are at least " + cond[1] + " followers in the enemy field, ";

            case "CST":
                return "If the follower's cost is less than or equal to " + cond[1] + ", ";

            default:
                throw new IllegalArgumentException("CONDITION NOT FOUND");
        }


    }

    /**
     * This method provide actual values for effects that are dependent on real-time game status.
     *
     * @param var the variable string. It is later to be interpreted to variable to be checked in game
     * @return the actual value for the effect
     */
    private static int getVariable(String var) {
        try {
            return Integer.parseInt(var);
        } catch (NumberFormatException e) {
            e.getMessage();
        }

        String[] variable = var.split("\\?");

        switch (variable[0]) {
            case "FRIFOLONFLD":
                return sourcePlayer.getField().followerCount();

            case "NEC":
                int necroCost = Integer.parseInt(variable[1]),
                        necroEffect = Integer.parseInt(variable[2]),
                        originalEffect = Integer.parseInt(variable[3]);
                if (sourcePlayer.getGrave().useNecromancy(necroCost))
                    return necroEffect;
                else
                    return originalEffect;

            case "HND":
                return sourcePlayer.getHand().size();

            case "ENELEDONE":
                return opponentPlayer.getLeader().getHealth() - 1;

            case "USDCRD":
                return sourcePlayer.getUsedCardCount();

            case "ORGATK":
                if (sourceCard instanceof Follower) {
                    if (((Follower) sourceCard).hasEvolved())
                        return ((Follower) sourceCard).getOrgEvoAttack();
                    else
                        return ((Follower) sourceCard).getOrgAttack();
                } else {
                    return 0;
                }

            case "COND":
                if (willActivate(variable[1] + "?" + variable[2]))
                    return Integer.parseInt(variable[4]);
                else
                    return Integer.parseInt(variable[3]);

            case "FRIOFFONFLD":
                int count = 0;
                for (Card c : sourcePlayer.getField())
                    if (c instanceof Follower && ((Follower) c).getSwordCraftTrait() == Trait.SwordCraftTrait.OFFICER)
                        count++;
                return count;

            case "ENEFOL":
                return opponentPlayer.getField().followerCount();

            default:
                return 0;
        }

    }

    /**
     * This methods generates string description for the given variable value
     *
     * @param var the variable to be interpreted
     * @return string description for the given variable value
     */
    private static String getVariableString(String var) {
        try {
            return Integer.toString(Integer.parseInt(var));
        } catch (NumberFormatException e) {
            e.getMessage();
        }

        String[] variable = var.split("\\?");

        switch (variable[0]) {
            case "FRIFOLONFLD":
                return "the number of allied follower on the field";

            case "NEC":
                int necroCost = Integer.parseInt(variable[1]),
                        necroEffect = Integer.parseInt(variable[2]),
                        originalEffect = Integer.parseInt(variable[3]);
                return originalEffect + ".   NECROMANCY " + necroCost + ": increase to " + necroEffect;

            case "HND":
                return "the number of cards in your hand";

            case "ENELEDONE":
                return "health-to-one";

            case "USDCRD":
                return "(card played this turn)";

            case "ORGATK":
                return "this follower's original attack(before evolve or after evolve)";

            case "COND":
                return getConditionString(variable[1] + "?" + variable[2]) + variable[4] + ", else " + variable[3];

            case "FRIOFFONFLD":
                return "the number of allied Officer follower on the field";

            case "ENEFOL":
                return "the number of enemy followers on the field";

            default:
                return null;
        }
    }

    private static void damageEnemyFollower(Follower follower, int damage) {
        follower.takeDamage(damage);
    }

    private static boolean drawCard(Player player, Player opponent, int amount) {
        if (!player.drawCards(amount)) {
            game.endGame(opponent);
            return false;
        }
        return true;
    }

    private static void randomBuffFriendlyFollower(int attack, int health) {
        Card randomSelection;
        Field targetField = sourcePlayer.getField();

        if (targetField.followerCount() > 0) {
            do {
                randomSelection = targetField.get((int) (Math.random() * targetField.size()));
            } while (!(randomSelection instanceof Follower) || !randomSelection.isAlive());

            ((Follower) randomSelection).buff(attack, health, sourceCard);
        }
    }

    private static void randomBuffFriendlyOfficer(int attack, int health) {
        Card randomSelection;
        Field targetField = sourcePlayer.getField();

        if (targetField.hasSwordTraitFollower(Trait.SwordCraftTrait.OFFICER)) {
            do {
                randomSelection = targetField.get((int) (Math.random() * targetField.size()));
            } while (!(randomSelection instanceof Follower &&
                    ((Follower) randomSelection).getSwordCraftTrait() == Trait.SwordCraftTrait.OFFICER) ||
                    !randomSelection.isAlive());

            ((Follower) randomSelection).buff(attack, health, sourceCard);
        }
    }

    private static boolean damageEnemyLeader(int damage) {
        opponentPlayer.getLeader().takeDamage(damage);

        if (!opponentPlayer.getLeader().isAlive()) {
            game.endGame(sourcePlayer);
            return false;
        }

        return true;
    }

    private static void damagePlayerFollowers(int damage, Player player) {
        for (Card c : player.getField())
            if (c instanceof Follower)
                ((Follower) c).takeDamage(damage);
    }

    private static void putFollower(String followerName, int amount) {
        for (int n = 0; n < amount; n++) {
            sourcePlayer.getHand().add(CardLib.getCard(followerName));
        }
    }

    private static void returnCardToHand(Card card, Player owner) {
        if (owner.getField().remove(card))
            owner.getHand().add(CardLib.getCard(card.getName()));
    }

    private static void randomDamageEnemyFollower(int damage) {
        Card randomSelection;
        Field targetField = opponentPlayer.getField();

        if (targetField.followerCount() > 0) {
            do
                randomSelection = targetField.get((int) (Math.random() * targetField.size()));
            while (!(randomSelection instanceof Follower) || !randomSelection.isAlive());

            ((Follower) randomSelection).takeDamage(damage);
        }
    }

    private static void buffFollower(Follower follower, int attack, int health) {
        follower.buff(attack, health, sourceCard);
    }

    private static void summonFollower(String followerName) {
        sourcePlayer.getField().add(CardLib.getCard(followerName));
    }

    private static void buffAllFriendlyOfficer(int atk, int def) {
        for (Card c : sourcePlayer.getField())
            if (c instanceof Follower && ((Follower) c).getSwordCraftTrait() == Trait.SwordCraftTrait.OFFICER)
                ((Follower) c).buff(atk, def, sourceCard);
    }

    private static void buffAllFriendlyFollower(int atk, int def) {
        for (Card c : sourcePlayer.getField())
            if (c instanceof Follower)
                ((Follower) c).buff(atk, def, sourceCard);
    }

    private static void increaseMaxCost() {
        sourcePlayer.increaseMaxCost();
    }

    private static void grantSimpleEffect(Follower follower, String effect) {
        follower.grantSimpleEffect(Follower.SimpleEffects.valueOf(effect));
    }

    private static void addNecromancy(Grave grave, int amount) {
        grave.addNecromancy(amount);
    }

    private static void banishFollower(Follower follower, Player player) {
        game.processCardExit(follower, player, true);
    }

    private static boolean damageSelfLeader(int damage) {
        sourcePlayer.getLeader().takeDamage(damage);

        if (!sourcePlayer.getLeader().isAlive()) {
            game.endGame(opponentPlayer);
            return false;
        }

        return true;
    }

    private static void healSelfLeader(int amount) {
        sourcePlayer.getLeader().heal(amount);
    }

    private static void damageAll(int amount) {
        if (damageSelfLeader(amount) && damageEnemyLeader(amount)) {
            damagePlayerFollowers(amount, sourcePlayer);
            damagePlayerFollowers(amount, opponentPlayer);
        }
    }

    private static void decreaseCountdown(Amulet amulet, int amount) {
        amulet.decreaseCountDown(amount);
    }

    private static void decreaseAllCountDown(int amount) {
        for (Card c : sourcePlayer.getField())
            if (c instanceof Amulet && ((Amulet) c).isCountDown())
                ((Amulet) c).decreaseCountDown(amount);
    }

    private static void discardHand() {
        for (Card c : sourcePlayer.getHand())
            sourcePlayer.getGrave().add(c);
        sourcePlayer.getHand().clear();
    }

    private static void damageAllEnemy(int damage) {
        damageEnemyLeader(damage);

        for (Card c : opponentPlayer.getField())
            if (c instanceof Follower)
                ((Follower) c).takeDamage(damage);
    }

    private static void killReturnFollower(Follower follower, Player owner) {
        follower.kill();
        owner.getField().add(CardLib.getCard(follower.getName()));
    }

    private static void killFollowerOrAmulet(Card card) {
        if (card instanceof Follower)
            ((Follower) card).kill();
        else if (card instanceof Amulet)
            ((Amulet) card).destroy();
        else
            throw new IllegalArgumentException("MUST BE Follower type or Amulet type");
    }

    private static void replaceDeck(String deckCode) {
        sourcePlayer.getDeck().replaceDeck(deckCode);
    }

    private static void changeCost(Player player, String cardName, int targetCost) {
        for (Card c : player.getHand()) {
            if (c.getName().equals(cardName)) {
                c.changeCost(targetCost);
                break;
            }
        }
    }

    private static void grantAllPlayerFollowerSimpleEffect(Player player, String effect) {
        for (Card c : player.getField())
            if (c instanceof Follower)
                ((Follower) c).grantSimpleEffect(Follower.SimpleEffects.valueOf(effect));
    }

    private static void revokeAllPlayerFollowerSimpleEffect(Player player, String effect) {
        for (Card c : player.getField())
            if (c instanceof Follower)
                ((Follower) c).revokeSimpleEffect(Follower.SimpleEffects.valueOf(effect));
    }

    private static void grantRandomPlayerFollowerSimpleEffect(Player player, String effect) {
        Card randomSelection;
        Field targetField = player.getField();

        if (targetField.followerCount() > 0) {
            do
                randomSelection = targetField.get((int) (Math.random() * targetField.size()));
            while (!(randomSelection instanceof Follower) || !randomSelection.isAlive());

            grantSimpleEffect((Follower) randomSelection, effect);
        }
    }

    private static void changeFollowerAttack(Follower follower, int targetAttack) {
        follower.changeAttack(targetAttack);
    }

    private static void returnRandomCardToHand(Player player) {
        returnCardToHand(player.getField().get((int) (Math.random() * player.getField().size())), player);
    }

    private static void changeFollowerHealth(Follower follower, int targetHealth) {
        follower.changeHealth(targetHealth);
    }

    private static void returnPlayerFollowersToHand(Player player) {
        Field targetField = player.getField();

        for (int n = 0; n < targetField.size(); n++) {
            Card card = targetField.get(n);

            if (card instanceof Follower) {
                returnCardToHand(card, player);
                n--;
            }
        }
    }

    private static void putChangedCostFollower(String cardName, int amount, int targetCost) {
        for (int n = 0; n < amount; n++) {
            Card c = CardLib.getCard(cardName);
            c.changeCost(targetCost);
            sourcePlayer.getHand().add(c);
        }
    }

    private static void transformHandCard(Player player, String org, String dest) {
        Hand targetHand = player.getHand();

        for (int n = 0; n < targetHand.size(); n++) {
            Card c = targetHand.get(n);

            if (c.getName().equals(org))
                targetHand.set(n, CardLib.getCard(dest));
        }
    }

    private static void drawRandomSwordCraft(Trait.SwordCraftTrait trait, int amount) {
        for (int n = 0; n < amount; n++) {
            Card drawnCard = sourcePlayer.getDeck().
                    getRandomRequestedCard(Leader.LeaderType.SWORDCRAFT, trait, Card.Type.FOLLOWER, 0, 10, true);

            if (drawnCard != null)
                sourcePlayer.getHand().add(drawnCard);
            else
                break;
        }
    }

    private static void grantAllOfficerSimpleEffect(String effect) {
        for (Card c : sourcePlayer.getField())
            if (c instanceof Follower && ((Follower) c).getSwordCraftTrait() == Trait.SwordCraftTrait.OFFICER)
                ((Follower) c).grantSimpleEffect(Follower.SimpleEffects.valueOf(effect));
    }

    private static void summonGivenCostRandomFollower(int max_cost, int min_cost) {
        Card drawnCard = sourcePlayer.getDeck().
                getRandomRequestedCard(null, null, Card.Type.FOLLOWER, max_cost, min_cost, true);

        if (drawnCard != null)
            sourcePlayer.getField().add(drawnCard);
    }

    public enum Response {
        ENEMY_FOLLOWER, FRIENDLY_FOLLOWER, ANY_FOLLOWER, FRIENDLY_OFFICER, FRIENDLY_OFFICER_ENTRANCE,
        ENEMY_FOLLOWER_OR_LEADER, FRIENDLY_FOLLOWER_OR_LEADER, FRIENDLY_CD_AMULET, ENEMY_FOLLOWER_HP_LESS,
        ENEMY_FOLLOWER_OR_AMULET, FRIENDLY_FOLLOWER_OR_AMULET, ENEMY_AMULET, FRIENDLY_FOLLOWER_ENTRANCE,
        FIGHTING_ENEMY, FRIENDLY_COMMANDER_ENTRANCE, FRIENDLY_COMMANDER, FRIENDLY_COST_ONE_FOLLOWER,
        ENEMY_FOLLOWER_ATK_MORE, DONE, FAIL;

        private static int parameter = Integer.MIN_VALUE;

        static void resetParameter() {
            parameter = Integer.MIN_VALUE;
        }

        public static int getParameter() {
            if (parameter != Integer.MIN_VALUE)
                return parameter;
            else
                throw new IllegalStateException("PARAMETER NOT INITIALIZED!");
        }

        static void setParameter(int para) {
            parameter = para;
        }

    }

}
