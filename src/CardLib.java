import java.util.HashMap;
import java.util.Scanner;

/**
 * This class contains detailed information for every card in the game. All card attributes are stored in allCards.txt
 * in a special format and is to be interpreted by the static initializer of this class.
 *
 * <p>This class only to be accessed in a static way. The card objects provided by this class are individually created
 * and initialized upon request, which makes every card object completely independent.</p>
 *
 * @see Card
 */
public abstract class CardLib {
    /**
     * This variable is a card representation of enemy leader. It is used in cases which both enemy leader and follower
     * are available to be chosen as a target. When the player chooses enemy leader as the target, this card object will
     * be fetched and passed to the {@linkplain EffectLib}.
     */
    public static final Card CARD_REPRESENT_LEADER;
    private static final HashMap<String, String> allCardInfo = new HashMap<>();

    static {
        Scanner scanner = new Scanner(CardLib.class.getResourceAsStream("allCards.txt"));

        while (scanner.hasNextLine()) {
            String nextCardData = scanner.nextLine();

            //  skip comments
            if (nextCardData.startsWith("//")) {
                continue;
            }

            allCardInfo.put(nextCardData.split(" ")[2], nextCardData);
        }

        CARD_REPRESENT_LEADER = new Follower("LEADER", "NEUTRAL", 0, "LEGENDARY", null,
                null, "COMMANDER", 100, 100, 100, 100, null,
                "STORM", true, true, true, true, true, "",
                false);
    }


    /**
     * This method provides independent card objects according to the given name of the card.
     *
     * @param cardName the name of the card
     * @return an independent card object of the requested card
     * @throws IllegalArgumentException when the card name provided does not exist, or an error occurred when reading
     *                                  the file
     */
    public static Card getCard(String cardName) {
        if (!allCardInfo.containsKey(cardName))
            throw new IllegalArgumentException("NO SUCH CARD FOUND IN THE LIBRARY: " + cardName);

        String[] info = allCardInfo.get(cardName).split(" ");
        Card card;

        switch (info[1]) {
            case "F":
                card = new Follower(info[2], info[3], Integer.parseInt(info[4]), info[5],
                        ImageLib.getHandImage(info[0] + "A"), ImageLib.getFieldImage(info[0] + "A"), info[6],
                        Integer.parseInt(info[7]), Integer.parseInt(info[8]), Integer.parseInt(info[9]),
                        Integer.parseInt(info[10]), ImageLib.getFieldImage(info[0] + "B"), info[11],
                        Boolean.parseBoolean(info[12]), Boolean.parseBoolean(info[13]),
                        Boolean.parseBoolean(info[14]), Boolean.parseBoolean(info[15]), Boolean.parseBoolean(info[16]),
                        info[17], Boolean.parseBoolean(info[18])
                );
                break;

            case "A":
                card = new Amulet(info[2], info[3], Integer.parseInt(info[4]), info[5], ImageLib.getHandImage(info[0]),
                        ImageLib.getFieldImage(info[0]), Integer.parseInt(info[6]), info[7]);
                break;

            case "S":
                card = new Spell(info[2], info[3], Integer.parseInt(info[4]), info[5], ImageLib.getHandImage(info[0]),
                        ImageLib.getFieldImage(info[0]), info[6], Boolean.parseBoolean(info[7]));
                break;

            default:
                throw new IllegalArgumentException("CARD TYPE " + info[1] + " NOT FOUND");
        }

        return card;
    }

}
