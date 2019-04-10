import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * The image library initializes, scales and holds all image instances used in the program. It pre-scales all images to
 * be used so they are ready with correct aspect ratio and size upon usage. These processes may take several seconds
 * causing a slight delay at startup, but it greatly improves the run-time response delay as well as fluency of painting
 * components.
 *
 * <p>The initialization code is different between running from IDE and running from an executable jar file. It is
 * noticeable that the initialization takes much more time if running from an executable jar file.</p>
 *
 * <p>This class is only to be accessed the statically. All images are initialized and stored at the beginning of the
 * program, and are passed-by-reference upon usage.</p>
 */
public final class ImageLib {
    private static final HashMap<String, Image> FIELD_IMAGES = new HashMap<>(500),
            HAND_IMAGES = new HashMap<>(500), LEADER_IMAGES = new HashMap<>(10),
            COMPONENT_IMAGES = new HashMap<>(10);
    public static final int FIELD_CARD_WIDTH, FIELD_CARD_HEIGHT, HAND_CARD_WIDTH, HAND_CARD_HEIGHT;

    private ImageLib() {
    }

    static {

        try {

            if (ImageLib.class.getResource("ImageLib.class").toString().startsWith("jar:")) {

                for (int n = 1; n <= 403; n++) {
                    String fileKey = String.format("%05d", n);

                    try {
                        FIELD_IMAGES.put(fileKey, ImageIO.read(ImageLib.class.getResource("field/" + fileKey + ".jpg")));
                        HAND_IMAGES.put(fileKey, ImageIO.read(ImageLib.class.getResource("hand/" + fileKey + ".jpg")));
                    } catch (IllegalArgumentException e) {
                        FIELD_IMAGES.put(fileKey + "A", ImageIO.read(ImageLib.class.getResource("field/" + fileKey + "A.jpg")));
                        FIELD_IMAGES.put(fileKey + "B", ImageIO.read(ImageLib.class.getResource("field/" + fileKey + "B.jpg")));
                        HAND_IMAGES.put(fileKey + "A", ImageIO.read(ImageLib.class.getResource("hand/" + fileKey + "A.jpg")));
                        HAND_IMAGES.put(fileKey + "B", ImageIO.read(ImageLib.class.getResource("hand/" + fileKey + "B.jpg")));
                    }
                }

                for (int n = 90001; n <= 90036; n++) {

                    try {
                        FIELD_IMAGES.put(Integer.toString(n), ImageIO.read(ImageLib.class.getResource("field/" + n + ".jpg")));
                        HAND_IMAGES.put(Integer.toString(n), ImageIO.read(ImageLib.class.getResource("hand/" + n + ".jpg")));
                    } catch (IllegalArgumentException e) {
                        FIELD_IMAGES.put(n + "A", ImageIO.read(ImageLib.class.getResource("field/" + n + "A.jpg")));
                        FIELD_IMAGES.put(n + "B", ImageIO.read(ImageLib.class.getResource("field/" + n + "B.jpg")));
                        HAND_IMAGES.put(n + "A", ImageIO.read(ImageLib.class.getResource("hand/" + n + "A.jpg")));
                        HAND_IMAGES.put(n + "B", ImageIO.read(ImageLib.class.getResource("hand/" + n + "B.jpg")));
                    }
                }

            } else {

                for (File image : Objects.requireNonNull(new File("Resources/field/").listFiles())) {
                    String fileName = image.getName();
                    FIELD_IMAGES.put(fileName.substring(0, fileName.indexOf('.')), ImageIO.read(image));
                }

                for (File image : Objects.requireNonNull(new File("Resources/hand/").listFiles())) {
                    String fileName = image.getName();
                    HAND_IMAGES.put(fileName.substring(0, fileName.indexOf('.')), ImageIO.read(image));
                }

            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Image fieldSample = FIELD_IMAGES.get("00001A");
        FIELD_CARD_WIDTH = fieldSample.getWidth(null);
        FIELD_CARD_HEIGHT = fieldSample.getHeight(null);

        Image handSample = HAND_IMAGES.get("00001A");
        HAND_CARD_WIDTH = handSample.getWidth(null);
        HAND_CARD_HEIGHT = handSample.getHeight(null);

        try {

            LEADER_IMAGES.put("Forestcraft", ImageIO.read(ImageLib.class.getResource("leader/Forestcraft.png")));
            LEADER_IMAGES.put("Swordcraft", ImageIO.read(ImageLib.class.getResource("leader/Swordcraft.png")));
            LEADER_IMAGES.put("Runecraft", ImageIO.read(ImageLib.class.getResource("leader/Runecraft.png")));
            LEADER_IMAGES.put("Dragoncraft", ImageIO.read(ImageLib.class.getResource("leader/Dragoncraft.png")));
            LEADER_IMAGES.put("Shadowcraft", ImageIO.read(ImageLib.class.getResource("leader/Shadowcraft.png")));
            LEADER_IMAGES.put("Bloodcraft", ImageIO.read(ImageLib.class.getResource("leader/Bloodcraft.png")));
            LEADER_IMAGES.put("Havencraft", ImageIO.read(ImageLib.class.getResource("leader/Havencraft.png")));

            COMPONENT_IMAGES.put("atk", ImageIO.read(ImageLib.class.getResource("other/atk.png")).
                    getScaledInstance(FIELD_CARD_WIDTH / 4, FIELD_CARD_HEIGHT / 4, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("def", ImageIO.read(ImageLib.class.getResource("other/def.png")).
                    getScaledInstance(FIELD_CARD_WIDTH / 4, FIELD_CARD_HEIGHT / 4, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("bane", ImageIO.read(ImageLib.class.getResource("other/bane.png")).
                    getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("drain", ImageIO.read(ImageLib.class.getResource("other/drain.png")).
                    getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("ward", ImageIO.read(ImageLib.class.getResource("other/ward.png")).
                    getScaledInstance((int) (FIELD_CARD_WIDTH / 1.5), (int) (FIELD_CARD_HEIGHT / 1.5), Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("ambush", ImageIO.read(ImageLib.class.getResource("other/ambush.png")).
                    getScaledInstance((int) (FIELD_CARD_WIDTH / 1.5), (int) (FIELD_CARD_HEIGHT / 1.5), Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("cant_attack", ImageIO.read(ImageLib.class.getResource("other/cant_attack.png")).
                    getScaledInstance((int) (FIELD_CARD_WIDTH / 1.5), (int) (FIELD_CARD_HEIGHT / 1.5), Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("damage_immune", ImageIO.read(ImageLib.class.getResource("other/damage_immune.png")).
                    getScaledInstance((int) (FIELD_CARD_WIDTH / 1.5), (int) (FIELD_CARD_HEIGHT / 1.5), Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("untargetable", ImageIO.read(ImageLib.class.getResource("other/untargetable.png")).
                    getScaledInstance((int) (FIELD_CARD_WIDTH / 1.5), (int) (FIELD_CARD_HEIGHT / 1.5), Image.SCALE_SMOOTH));

            COMPONENT_IMAGES.put("bronze_spell", ImageIO.read(ImageLib.class.getResource("other/bronze_spell.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("bronze_follower", ImageIO.read(ImageLib.class.getResource("other/bronze_follower.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("bronze_amulet", ImageIO.read(ImageLib.class.getResource("other/bronze_amulet.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("silver_spell", ImageIO.read(ImageLib.class.getResource("other/silver_spell.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("silver_follower", ImageIO.read(ImageLib.class.getResource("other/silver_follower.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("silver_amulet", ImageIO.read(ImageLib.class.getResource("other/silver_amulet.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("gold_spell", ImageIO.read(ImageLib.class.getResource("other/gold_spell.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("gold_follower", ImageIO.read(ImageLib.class.getResource("other/gold_follower.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("gold_amulet", ImageIO.read(ImageLib.class.getResource("other/gold_amulet.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("legendary_spell", ImageIO.read(ImageLib.class.getResource("other/legendary_spell.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("legendary_follower", ImageIO.read(ImageLib.class.getResource("other/legendary_follower.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));
            COMPONENT_IMAGES.put("legendary_amulet", ImageIO.read(ImageLib.class.getResource("other/legendary_amulet.png")).
                    getScaledInstance(HAND_CARD_WIDTH, HAND_CARD_HEIGHT, Image.SCALE_SMOOTH));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * @param key the key code(file name without extension) for the hand image
     * @return the corresponding hand image of the given image key
     */
    public static Image getHandImage(String key) {
        if (HAND_IMAGES.containsKey(key)) {
            return HAND_IMAGES.get(key);
        } else {
            throw new IllegalArgumentException("NO IMAGE KEY FOUND " + key);
        }
    }

    /**
     * @param key the key code(file name without extension) for the field image
     * @return the corresponding field image of the given image key
     */
    public static Image getFieldImage(String key) {
        if (FIELD_IMAGES.containsKey(key)) {
            return FIELD_IMAGES.get(key);
        } else {
            throw new IllegalArgumentException("NO IMAGE KEY FOUND " + key);
        }
    }

    /**
     * @param key the key code(file name without extension) for other images
     * @return the corresponding other image of the given image key
     */
    public static Image getLeaderImage(String key) {
        if (LEADER_IMAGES.containsKey(key)) {
            return LEADER_IMAGES.get(key);
        } else {
            throw new IllegalArgumentException("NO IMAGE KEY FOUND " + key);
        }
    }

    /**
     * @param key the key code(file name without extension) for scaled card component images
     * @return the corresponding scaled card image of the given image key
     */
    public static Image getCardComponentImage(String key) {
        if (COMPONENT_IMAGES.containsKey(key)) {
            return COMPONENT_IMAGES.get(key);
        } else {
            throw new IllegalArgumentException("NO IMAGE KEY FOUND " + key);
        }
    }
}
