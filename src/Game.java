import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedString;


/**
 * This program is a partial rebuild of "Shadowverse", a Collectible Card Game. All the images, sounds, cards and
 * mechanics used in this program are originated from this game, which full credit shall be given to their owners.
 *
 * <p>This class is the main class of the project (where the main method is located). It is responsible for basic game
 * features, mechanisms and interactions, such as GUI, game initialization and progress management, resource allocations
 * and invocations, etc.</p>
 *
 * <p>The game can be played with 2 players(competitive) or 1 player(self vs. self or for testing purposes). If played
 * by 2 players, each player should have control over 1 leader.</p>
 *
 * <p>The program initializes by invoking its own private constructor. The main JFrame will not be visible until {@link
 * #leaderSelectionPanel()} and {@link #cardSwitchPanel(Player)} finish running. These two methods are responsible for
 * acquiring basic information of the game.</p>
 *
 * <p>Upon the end of the game, {@link #endGame(Player)} will be invoked and a dialog will show up and display the
 * winner. After that, all player interactions will be cut-off, which means no components will respond to player's
 * command any more. However, players are still allowed to check the final moment of the game and check for card
 * details.</p>
 *
 * @author Adrian Zhao
 * @version 1.0
 * @see ImageLib
 * @see SoundLib
 * @see CardLib
 * @see EffectLib
 * @see Player
 * @see Card
 * @since 1.0
 */
public final class Game extends JFrame {
    private static final Color EP_COLOR = new Color(255, 153, 0);
    private Player p1, p2, current_player;
    /**
     * This field represents whether the game is ongoing or finished. Its initial value is true, and it is only to be
     * toggle to false when {@link #endGame(Player)} is invoked.
     *
     * <p>Many listeners check this field's value first before responding to player's commands.</p>
     *
     * @see #endGame(Player)
     */
    private boolean gameAlive;
    private String leader1, leader2;
    private Card[] p1InitialHand, p2InitialHand;
    private HandPanel enemyHandPanel, playerHandPanel;
    private FieldPanel enemyFieldPanel, playerFieldPanel;
    private JLabel enemyHandSizeLabel = new JLabel(),
            enemyGraveSizeLabel = new JLabel(),
            enemyDeckSizeLabel = new JLabel(),
            enemyHealthLabel = new JLabel(),
            enemyCostLabel = new JLabel(),
            enemyLeaderEffectLabel = new JLabel(),
            playerHandSizeLabel = new JLabel(),
            playerGraveSizeLabel = new JLabel(),
            playerDeckSizeLabel = new JLabel(),
            playerHealthLabel = new JLabel(),
            playerCostInfoLabel = new JLabel(),
            playerLeaderEffectLabel = new JLabel();
    /**
     * This label contains the leader image of the first player. It has a MouseListener that picks up left click event
     * when appropriate according to the status of {@link #selection};
     *
     * @see #selection
     * @see Selection
     */
    private JLabel playerLeaderImage;

    /**
     * This label contains the leader image of the second player. It has a MouseListener that picks up left click event
     * when appropriate according to the status of {@link #selection};
     *
     * @see #selection
     * @see Selection
     */
    private JLabel enemyLeaderImage;

    private JTextArea gameGuideText = new JTextArea();
    private JButton playerEvolveButton = new JButton(), enemyEvolveButton = new JButton(), endRoundButton;

    /**
     * Represents the identity/behaviour of the next card clicked. Player should select their target according to this
     * field value.
     *
     * <p>When it's value is {@link Selection#ATTACK}, it will hold the attacker Follower and attacker Player. They can
     * later be acquired by invoking its {@link Selection#getAttackerPlayer()} and {@link Selection#getAttacker()}</p>
     *
     * <p>When there is no action taking place on the next click, its value will be {@link Selection#PEND}</p>
     *
     * @see Selection
     */
    private Selection selection = Selection.PEND;

    /**
     * This constructor is only to be invoked by the main method at the very beginning of the program. <p>The game
     * preparation music will be played through {@link SoundLib} until the program finishes getting game information
     * from the player, which will happen at the point that the dialog created by {@link #cardSwitchPanel(Player)} is
     * being disposed and the main frame becomes visible.</p>
     */
    private Game() {
        SoundLib.GAME_PREP.playSound();
        leaderSelectionPanel();
    }

    public static void main(String[] args) {
        new Game();
    }

    /**
     * This method creates a JDialog that provides both players with 7 leaders they can choose to play. In version 1.0,
     * only Forestcraft and Swordcraft are available with preset decks. The first-pick player will go first with 2 EPs,
     * the second-pick player will go second with 3 EPs and an extra card drawn in its first round.
     *
     * <p>When the selection is completed, this dialog will be disposed and {@link #cardSwitchPanel(Player)} will be
     * invoked as the second step of the initialization.</p>
     */
    private void leaderSelectionPanel() {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog();
            dialog.setTitle("Leader Selection");
            dialog.setBounds(200, 150, 1500, 370);
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (JOptionPane.showConfirmDialog(dialog,
                            "Are you sure to close this window?", "Really Closing?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            });

            Container dialogContainer = dialog.getContentPane();
            dialogContainer.setLayout(new GridLayout(2, 4));

            JButton[] leaderButtonList = new JButton[]{
                    new JButton("Forestcraft", new ImageIcon(Leader.LeaderType.FORESTCRAFT.getImage())),
                    new JButton("Swordcraft", new ImageIcon(Leader.LeaderType.SWORDCRAFT.getImage())),
                    new JButton("Runecraft", new ImageIcon(Leader.LeaderType.RUNECRAFT.getImage())),
                    new JButton("Dragoncraft", new ImageIcon(Leader.LeaderType.DRAGONCRAFT.getImage())),
                    new JButton("Shadowcraft", new ImageIcon(Leader.LeaderType.SHADOWCRAFT.getImage())),
                    new JButton("Bloodcraft", new ImageIcon(Leader.LeaderType.BLOODCRAFT.getImage())),
                    new JButton("Havencraft", new ImageIcon(Leader.LeaderType.HAVENCRAFT.getImage()))};

            ActionListener leaderButtonListener = event -> {
                if (leader1 == null) {
                    leader1 = ((JButton) event.getSource()).getText();
                } else if (leader2 == null) {
                    leader2 = ((JButton) event.getSource()).getText();
                    p1 = new Player(leader1, Player.RoundOrder.OFFENSIVE);
                    p2 = new Player(leader2, Player.RoundOrder.DEFENSIVE);
                    dialog.dispose();
                    cardSwitchPanel(p1);
                }
            };

            for (JButton button : leaderButtonList) {
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);

                if (button.getText().equals("Forestcraft") || button.getText().equals("Swordcraft")) {
                    button.addActionListener(leaderButtonListener);
                }

                dialogContainer.add(button);
            }

            JTextArea instruction = new JTextArea("Please select 2 leaders. (currently only Forestcraft and Swordcraft " +
                    "available)\nThe first one selected will be playing first.\n For game guide, visit " +
                    "https://shadowverse.com/gameguide/");
            setTextStyle(instruction);
            instruction.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            dialogContainer.add(instruction);
            dialog.setVisible(true);
        });
    }

    /**
     * This method creates a JDialog that reveals the initial three cards drawn from the deck and offers the specified
     * player a change to change any one of them. Each card is allowed to be switched only once. The original card will
     * be added back to the deck while drawing a new card.Player can also choose not to switch a certain card.
     *
     * <p>The method contains a local class SwitchCardButton that combines a JButton object with a Card object. It will
     * be revealed in the dialog as a JButton with its icon set to the card's hand image. When it is left-clicked for
     * the first time, it will perform the card switch action, and then disable itself from further right clicks. The
     * button will show the card's detail whenever it is right-clicked.</p>
     *
     * <p>On the completion of the first player's card switch, the method will dispose its dialog and call itself again
     * with the instance of the second player. On the completion of the second player's card switch, the method will
     * dispose its dialog and invoke {@link #initialize()} for other game initialization tasks (e.g. GUI).</p>
     *
     * @param player Current player that is performing the card switch
     * @throws NullPointerException Occurs when there is no preset deck for the player's leader
     */
    private void cardSwitchPanel(Player player) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog();
            dialog.setTitle(player.getLeader().getName() + " Card Switch");
            dialog.setBounds(200, 150, 700, 400);
            dialog.setResizable(false);
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (JOptionPane.showConfirmDialog(dialog,
                            "Are you sure to leave the game?", "Really Leaving?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            });

            Box mainBox = Box.createVerticalBox();
            mainBox.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            Box cardBox = Box.createHorizontalBox();
            Box instructionBox = Box.createHorizontalBox();

            Deck deck = player.getDeck();

            class SwitchCardButton extends JButton implements ActionListener, MouseListener {
                private Card card;

                private SwitchCardButton() {
                    card = deck.drawCard();

                    if (card == null)
                        throw new NullPointerException();

                    setIcon(new ImageIcon(card.getHandImage()));
                    addActionListener(this);
                    addMouseListener(this);
                }

                Card getCard() {
                    return card;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    deck.add(card);
                    card = deck.drawCard();

                    if (card == null)
                        throw new NullPointerException();

                    setDisabledIcon(new ImageIcon(card.getHandImage()));
                    setEnabled(false);
                }

                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3)
                        showCardDetail(card);
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            }

            SwitchCardButton[] buttons = new SwitchCardButton[]{
                    new SwitchCardButton(), new SwitchCardButton(), new SwitchCardButton()};
            JButton done = new JButton("DONE");

            done.addActionListener(e -> {
                dialog.dispose();

                if (player == p1) {
                    cardSwitchPanel(p2);
                    p1InitialHand = new Card[]{buttons[0].getCard(), buttons[1].getCard(), buttons[2].getCard()};
                } else {
                    p2InitialHand = new Card[]{buttons[0].getCard(), buttons[1].getCard(), buttons[2].getCard()};
                    initialize();
                }
            });


            cardBox.add(Box.createHorizontalStrut(10));
            for (SwitchCardButton button : buttons) {
                cardBox.add(button);
                cardBox.add(Box.createHorizontalStrut(10));
            }

            JTextArea instruction = new JTextArea("These cards are going to be your initial hand. You can switch each card once." +
                    "\nRight click to see card details.");
            setTextStyle(instruction);

            instructionBox.add(instruction);
            instructionBox.add(Box.createHorizontalStrut(50));
            instructionBox.add(done);

            mainBox.add(cardBox);
            mainBox.add(Box.createVerticalStrut(50));
            mainBox.add(instructionBox);

            dialog.add(mainBox);
            dialog.setVisible(true);
        });
    }

    /**
     * This method initializes all GUI components as well as starting the game.
     *
     * <p>The main frame is vertically divided into two main sections with a left-to-right ratio of 0.82:0.18. The left
     * section has 4 horizontal panels representing p2's hand, p2's field, p1's field and p1's hand respectively. The
     * right section has 3 horizontal boxes representing p2's status, game guide and p1's status respectively. </p>
     *
     * <p>The {@link #playerLeaderImage} and {@link #enemyLeaderImage} are clickable as they can be selected as a target
     * for effects and attacks.</p>
     *
     * <p>On the completion of the initialization, a random back ground music will be chosen to play through {@link
     * SoundLib}, and then the game will formally start from the first player.</p>
     */
    private void initialize() {
        p1.getHand().add(p1InitialHand);
        p2.getHand().add(p2InitialHand);

        SwingUtilities.invokeLater(() -> {
            setLabelStyle(enemyCostLabel);
            setLabelStyle(enemyHealthLabel);
            setLabelStyle(playerHealthLabel);
            setLabelStyle(enemyDeckSizeLabel);
            setLabelStyle(enemyGraveSizeLabel);
            setLabelStyle(enemyHandSizeLabel);
            setLabelStyle(playerCostInfoLabel);
            setLabelStyle(playerDeckSizeLabel);
            setLabelStyle(playerGraveSizeLabel);
            setLabelStyle(playerHandSizeLabel);
            setLabelStyle(playerEvolveButton);
            setLabelStyle(enemyEvolveButton);
            setLabelStyle(playerLeaderEffectLabel);
            setLabelStyle(enemyLeaderEffectLabel);

            setTextStyle(gameGuideText);
            gameGuideText.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

            playerHealthLabel.setForeground(Color.RED);
            enemyHealthLabel.setForeground(Color.RED);
            enemyEvolveButton.setForeground(EP_COLOR);
            playerEvolveButton.setForeground(EP_COLOR);

            setBounds(0, 0, 1920, 1020);
            setResizable(false);
            setTitle("Shadowverse Rebuild");

            Container container = getContentPane();

            Box leftPane = Box.createVerticalBox();
            leftPane.setPreferredSize(new Dimension((int) (getWidth() * 0.82), getHeight()));

            enemyHandPanel = new HandPanel(p2);
            leftPane.add(enemyHandPanel);
            leftPane.add(Box.createVerticalStrut(30));
            enemyFieldPanel = new FieldPanel(p2);
            leftPane.add(enemyFieldPanel);
            leftPane.add(Box.createVerticalStrut(15));
            playerFieldPanel = new FieldPanel(p1);
            leftPane.add(playerFieldPanel);
            leftPane.add(Box.createVerticalStrut(30));
            playerHandPanel = new HandPanel(p1);
            leftPane.add(playerHandPanel);

            Box rightPane = Box.createVerticalBox();
            rightPane.setPreferredSize(new Dimension((int) (getWidth() * 0.18), getHeight()));

            Box enemyLeaderBox = Box.createVerticalBox();
            enemyLeaderBox.setPreferredSize(new Dimension((int) (getWidth() * 0.82), (int) (getHeight() * 0.3)));

            Box enemyLeaderInfo = Box.createHorizontalBox();

            enemyLeaderImage = new JLabel(new ImageIcon(p2.getLeader().getImage()));
            enemyLeaderImage.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    switch (selection) {
                        case ATTACK:
                            handleAttack(p2);
                            break;

                        case FRIENDLY_FOLLOWER_OR_LEADER:
                        case ENEMY_FOLLOWER_OR_LEADER:
                            EffectLib.proceedEffect(CardLib.CARD_REPRESENT_LEADER, null);
                            break;

                    }

                    displayMessage(p2.getLeader().getName() + " is hit!");
                    selection = Selection.PEND;
                    updateGUI();
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            enemyLeaderInfo.add(enemyLeaderImage);
            enemyLeaderInfo.add(Box.createHorizontalStrut(5));
            JPanel enemyLeaderStatus = new JPanel();
            enemyLeaderStatus.setLayout(new GridLayout(3, 1));
            enemyLeaderStatus.add(enemyHealthLabel);
            enemyLeaderStatus.add(enemyCostLabel);
            enemyEvolveButton.addActionListener(e -> {
                if (selection == Selection.EVOLUTION)
                    selection = Selection.PEND;
                else
                    selection = Selection.EVOLUTION;

                if (selection == Selection.EVOLUTION) {
                    displayMessage("Please select the follower you want to evolve. Press again to cancel.");
                }

                updateGUI();
            });
            enemyLeaderStatus.add(enemyEvolveButton);
            enemyLeaderInfo.add(enemyLeaderStatus);
            enemyLeaderBox.add(enemyLeaderInfo);
            enemyLeaderBox.add(Box.createVerticalStrut(30));

            JPanel enemyStats = new JPanel();
            enemyStats.setLayout(new GridLayout(1, 2));
            enemyStats.add(enemyLeaderEffectLabel);

            JPanel enemyCardInfo = new JPanel();
            enemyCardInfo.setLayout(new GridLayout(3, 1));
            enemyCardInfo.add(enemyGraveSizeLabel);
            enemyCardInfo.add(enemyDeckSizeLabel);
            enemyCardInfo.add(enemyHandSizeLabel);
            enemyStats.add(enemyCardInfo);

            enemyLeaderBox.add(enemyStats);

            rightPane.add(enemyLeaderBox);
            rightPane.add(Box.createVerticalStrut((int) (getHeight() * 0.1)));

            Box costBox = Box.createVerticalBox();
            costBox.setPreferredSize(new Dimension((int) (getWidth() * 0.18), (int) (getHeight() * 0.2)));

            costBox.add(gameGuideText);
            costBox.add(Box.createVerticalStrut(15));

            endRoundButton = new JButton("End Round");
            endRoundButton.addActionListener(event -> endRound());
            costBox.add(endRoundButton);

            rightPane.add(costBox);

            Box playerLeaderBox = Box.createVerticalBox();
            playerLeaderBox.setPreferredSize(new Dimension((int) (getWidth() * 0.18), (int) (getHeight() * 0.3)));

            Box playerLeaderInfo = Box.createHorizontalBox();

            playerLeaderImage = new JLabel(new ImageIcon(p1.getLeader().getImage()));
            playerLeaderImage.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    switch (selection) {
                        case ATTACK:
                            handleAttack(p1);
                            break;

                        case ENEMY_FOLLOWER_OR_LEADER:
                        case FRIENDLY_FOLLOWER_OR_LEADER:
                            EffectLib.proceedEffect(CardLib.CARD_REPRESENT_LEADER, null);
                            break;
                    }

                    displayMessage(p1.getLeader().getName() + " is hit!");
                    selection = Selection.PEND;
                    updateGUI();
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            playerLeaderInfo.add(playerLeaderImage);
            playerLeaderInfo.add(Box.createHorizontalStrut(5));
            JPanel playerLeaderStatus = new JPanel();
            playerLeaderStatus.setLayout(new GridLayout(3, 1));
            playerLeaderStatus.add(playerHealthLabel);
            playerLeaderStatus.add(playerCostInfoLabel);
            playerEvolveButton.addActionListener(e -> {
                if (selection == Selection.EVOLUTION)
                    selection = Selection.PEND;
                else
                    selection = Selection.EVOLUTION;

                if (selection == Selection.EVOLUTION) {
                    displayMessage("Please select the follower you want to evolve. Press again to cancel.");
                }

                updateGUI();
            });
            playerLeaderStatus.add(playerEvolveButton);
            playerLeaderInfo.add(playerLeaderStatus);
            playerLeaderBox.add(playerLeaderInfo);
            playerLeaderBox.add(Box.createVerticalStrut(30));

            JPanel playerStats = new JPanel();
            playerStats.setLayout(new GridLayout(1, 2));
            playerStats.add(playerLeaderEffectLabel);

            JPanel playerInfoPanel = new JPanel(new GridLayout(3, 1));
            playerInfoPanel.add(playerGraveSizeLabel);
            playerInfoPanel.add(playerDeckSizeLabel);
            playerInfoPanel.add(playerHandSizeLabel);
            playerStats.add(playerInfoPanel);

            playerLeaderBox.add(playerStats);

            rightPane.add(Box.createVerticalStrut((int) (getHeight() * 0.1)));
            rightPane.add(playerLeaderBox);

            Box mainBox = Box.createHorizontalBox();
            mainBox.setBorder(BorderFactory.createEmptyBorder(
                    (int) (getHeight() * 0.02), (int) (getWidth() * 0.02), (int) (getHeight() * 0.02), (int) (getWidth() * 0.02)));
            mainBox.add(leftPane);
            mainBox.add(rightPane);
            container.add(mainBox);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);

            updateGUI();
        });

        gameAlive = true;
        SoundLib.RANDOM_BGM.playSound();

        p1.iniNewRound();
        current_player = p1;
        displayMessage(p1.getLeader().getName() + "'s Round " + p1.getRoundNumber());
    }

    /**
     * This method is invoked whenever the players' statuses change. It checks if the current thread is EDT. If so,
     * {@link #updateGUIaction()} will be invoked directly. Otherwise {@link #updateGUIaction()} will be wrapped into
     * {@link SwingUtilities#invokeLater(Runnable)} for EDT to process.
     *
     * @see #updateGUIaction()
     */
    private void updateGUI() {
        if (SwingUtilities.isEventDispatchThread())
            updateGUIaction();
        else
            SwingUtilities.invokeLater(this::updateGUIaction);
    }

    /**
     * This method is only to be invoked through {@link #updateGUI()} to make sure it runs in the EDT. It repaints all
     * panels and updates all labels and text areas on the main frame.
     *
     * @see #updateGUI()
     */
    private void updateGUIaction() {
        playerHandPanel.repaint();
        playerFieldPanel.repaint();
        enemyHandPanel.repaint();
        enemyFieldPanel.repaint();

        enemyHandSizeLabel.setText("Hand " + p2.getHand().size());
        enemyGraveSizeLabel.setText("Grave " + p2.getGrave().size());
        enemyDeckSizeLabel.setText("Deck " + p2.getDeck().size());

        int enemyEvoCD = getEvolveCD(p2);
        if (enemyEvoCD <= 0)
            enemyEvolveButton.setText("EP " + p2.getEvolvePts());
        else
            enemyEvolveButton.setText("CD " + enemyEvoCD);

        if (gameAlive && enemyEvoCD <= 0 && current_player == p2 && p2.getEvolvePts() > 0)
            enemyEvolveButton.setEnabled(true);
        else
            enemyEvolveButton.setEnabled(false);

        enemyHealthLabel.setText("HP " + p2.getLeader().getHealth());
        enemyCostLabel.setText("Cost " + p2.getCostLeft() + " / " + p2.getMaxCost());

        switch (p2.getLeader().getLeaderType()) {
            case FORESTCRAFT:
                enemyLeaderEffectLabel.setText("Card Used: " + p2.getUsedCardCount());
                break;

            case BLOODCRAFT:
                if (p2.isVengeance())
                    enemyLeaderEffectLabel.setText("VENGEANCE ACTIVATED");
                else
                    enemyLeaderEffectLabel.setText((p2.getLeader().getHealth() - 10) + " HP to Vengeance");
                break;

            case DRAGONCRAFT:
                if (p2.isOverflow())
                    enemyLeaderEffectLabel.setText("OVERFLOW ACTIVATED");
                else
                    enemyLeaderEffectLabel.setText((7 - p2.getMaxCost()) + " COST to Overflow");
                break;

            case SHADOWCRAFT:
                enemyLeaderEffectLabel.setText("NECROMANCY " + p2.getGrave().getNecromancy());
                break;

            default:
                enemyLeaderEffectLabel.setText("No special effect");
                break;
        }

        playerHandSizeLabel.setText("Hand " + p1.getHand().size());
        playerGraveSizeLabel.setText("Grave " + p1.getGrave().size());
        playerDeckSizeLabel.setText("Deck " + p1.getDeck().size());
        playerHealthLabel.setText("HP " + p1.getLeader().getHealth());
        playerCostInfoLabel.setText("Cost " + p1.getCostLeft() + " / " + p1.getMaxCost());

        switch (p1.getLeader().getLeaderType()) {
            case FORESTCRAFT:
                playerLeaderEffectLabel.setText("Card Used: " + p1.getUsedCardCount());
                break;

            case BLOODCRAFT:
                if (p1.isVengeance())
                    playerLeaderEffectLabel.setText("VENGEANCE ACTIVATED");
                else
                    playerLeaderEffectLabel.setText((p1.getLeader().getHealth() - 10) + " HP to Vengeance");
                break;

            case DRAGONCRAFT:
                if (p1.isOverflow())
                    playerLeaderEffectLabel.setText("OVERFLOW ACTIVATED");
                else
                    playerLeaderEffectLabel.setText((7 - p1.getMaxCost()) + " COST to Overflow");
                break;

            case SHADOWCRAFT:
                playerLeaderEffectLabel.setText("NECROMANCY " + p1.getGrave().getNecromancy());
                break;

            default:
                playerLeaderEffectLabel.setText("No special effect");
                break;
        }

        int playerEvoCD = getEvolveCD(p1);
        if (playerEvoCD <= 0)
            playerEvolveButton.setText("EP " + p1.getEvolvePts());
        else
            playerEvolveButton.setText("CD " + playerEvoCD);

        if (gameAlive && playerEvoCD <= 0 && current_player == p1 && p1.getEvolvePts() > 0)
            playerEvolveButton.setEnabled(true);
        else
            playerEvolveButton.setEnabled(false);
    }

    /**
     * This method is invoked every time the {@linkplain #endRoundButton} is pressed. It ends the current player's
     * round, changing field values and activate all "When round end" effects. Then {@link Player#endRound()} is invoked
     * to finalize round.
     *
     * <p>After current player's round finalization, this method will also initialize the next player's round by
     * invoking {@link Player#iniNewRound()} and activating all "Fanfare" effects. {@link #endGame(Player)} will be
     * invoked if there is no card left in the deck and the player that has the drained deck will lose the game.</p>
     *
     * <p>All effects are processed through {@link #sendEffect(String, Card, Player, Card, Player)}. {@link
     * #updateGUI()} will be invoked at the end of the method to refresh the GUI.</p>
     *
     * @see Player#endRound()
     * @see Player#iniNewRound()
     */
    private void endRound() {
        Player nextPlayer;
        if (current_player == p1)
            nextPlayer = p2;
        else
            nextPlayer = p1;

        Field currentField = (Field) current_player.getField().clone();
        for (Card c : currentField) {
            sendEffect(c.getWhenEnd(), c, current_player, null, null);

            if (c.isEntranceRound())
                sendEffect(c.getWhenEntranceEnd(), c, current_player, null, null);

            c.passEntranceRound();
        }

        current_player.endRound();

        if (nextPlayer.iniNewRound()) {

            Field nextField = (Field) nextPlayer.getField().clone();
            for (Card c : nextField)
                sendEffect(c.getWhenStart(), c, nextPlayer, null, null);

        } else {
            endGame(current_player);
        }

        current_player = nextPlayer;
        displayMessage(nextPlayer.getLeader().getName() + "'s Round " + nextPlayer.getRoundNumber());

        updateGUI();
    }

    /**
     * This method is invoked when a click event has been detected on either {@linkplain #playerEvolveButton} or
     * {@linkplain #enemyEvolveButton}. It checks first if the evolve action is valid for the given player. If the
     * conditions are met, {@link Follower#evolve()} will be called, the Follower object will evolve and its "When
     * evolve" effect will be activated, else the game will display an error message to inform the player.
     *
     * @param follower the Follower object to be evolved
     * @param owner    the player which the Follower object belongs to
     * @see Follower#evolve()
     */
    private void useEvolvePts(Follower follower, Player owner) {
        boolean isEvolvable = false;

        switch (owner.getRoundOrder()) {
            case OFFENSIVE:
                if (owner.getRoundNumber() >= 5) {
                    isEvolvable = true;
                } else {
                    displayMessage("ERROR: You can only evolve after round 5");
                }
                break;

            case DEFENSIVE:
                if (owner.getRoundNumber() >= 4) {
                    isEvolvable = true;
                } else {
                    displayMessage("ERROR: You can only evolve after round 4");
                }
                break;
        }

        if (isEvolvable) {
            if (!follower.hasEvolved()) {
                if (!current_player.hasEvolved()) {

                    if (owner.evolve(follower))
                        sendEffect(follower.getWhenEvolve(), follower, owner, null, null);
                    else
                        displayMessage("ERROR: Not enough EP left");

                } else {
                    displayMessage("ERROR: Only one evolve per round is allowed!");
                }
            } else {
                displayMessage("ERROR: Follower has already evolved!");
            }
        }

        updateGUI();
    }

    /**
     * This method creates a JDialog to show the details of the given Card. It is wrapped in {@link
     * SwingUtilities#invokeLater(Runnable)} to make sure it is executed in the EDT.
     *
     * <p>For a {@linkplain Follower}, its name, cost, leader, trait(if applicable), original attack, original health,
     * simple effects, effects, attack after evolve, health after evolve and buff lists will be displayed.</p>
     *
     * <p>For an {@linkplain Amulet}, its name, cost, leader, count down(if applicable) and effects will be
     * displayed.</p>
     *
     * <p>For a {@linkplain Spell}, its name, cost, leader and effects will be displayed.</p>
     *
     * @param card the Card object to be displayed
     * @see EffectLib#getEffectDescription(String, Card)
     */
    private void showCardDetail(Card card) {
        SwingUtilities.invokeLater(() -> {
            JDialog cardInfo = new JDialog(Game.this);
            cardInfo.setTitle(card.getName());
            cardInfo.setBounds(10, 10, 350, 400);
            cardInfo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            cardInfo.setResizable(false);

            Container container = cardInfo.getContentPane();
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextArea description = new JTextArea();
            description.setPreferredSize(new Dimension(cardInfo.getWidth() - 50, cardInfo.getHeight() - 50));
            description.setLineWrap(true);
            description.setOpaque(false);
            description.setWrapStyleWord(true);
            description.setEditable(false);

            description.append(card.getName() + "   COST " + card.getOrgCost() + "   " + card.getLeader().getKind());

            switch (card.getType()) {
                case FOLLOWER:
                    Follower follower = (Follower) card;

                    switch (follower.getSwordCraftTrait()) {
                        case COMMANDER:
                            description.append("   Commander\n");
                            break;

                        case OFFICER:
                            description.append("   Officer\n");
                            break;

                        default:
                            description.append("\n");
                    }

                    description.append("\nATK " + Integer.toString(follower.getOrgAttack()) + "   " +
                            "HP: " + Integer.toString(follower.getOrgHealth()) + "\n");

                    if (follower.hasWard()) description.append("WARD ");
                    if (follower.hasBane()) description.append("BANE ");
                    if (follower.hasDrain()) description.append("DRAIN ");
                    if (follower.hasAmbush()) description.append("AMBUSH ");
                    if (follower.hasStorm()) description.append("STORM ");
                    if (follower.hasRush()) description.append("RUSH ");

                    if (follower.getFanfare() != null) {
                        if (!follower.getFanfare().contains("BSTCST"))
                            description.append("\nFanfare: ");
                        else
                            description.append("\n");

                        description.append(EffectLib.getEffectDescription(card.getFanfare(), follower));
                    }

                    if (follower.getLastword() != null)
                        description.append("\nLastword: " +
                                EffectLib.getEffectDescription(card.getLastword(), follower));

                    if (follower.getWhenStart() != null)
                        description.append("\nAt the beginning of the round, " +
                                EffectLib.getEffectDescription(card.getWhenStart(), follower));

                    if (follower.getWhenEnd() != null)
                        description.append("\nAt the end of the round, " +
                                EffectLib.getEffectDescription(card.getWhenEnd(), follower));

                    if (follower.getWhenAttack() != null)
                        description.append("\nWhen this follower attack, " +
                                EffectLib.getEffectDescription(follower.getWhenAttack(), follower));

                    if (follower.getWhenFight() != null)
                        description.append("\nWhen this follower fight, " +
                                EffectLib.getEffectDescription(follower.getWhenFight(), follower));

                    if (follower.getWhenEntranceEnd() != null)
                        description.append("\nAt the end of this turn, " +
                                EffectLib.getEffectDescription(follower.getWhenEntranceEnd(), follower));

                    if (follower.getWhenOtherEnter() != null)
                        description.append("\nWhen another allied follower comes into play, " +
                                EffectLib.getEffectDescription(follower.getWhenOtherEnter(), follower));

                    description.append("\n\nEATK: " + Integer.toString(follower.getOrgEvoAttack()) +
                            "   EHP: " + Integer.toString(follower.getOrgEvoHealth()));

                    if (follower.getWhenEvolve() != null)
                        description.append("\nWhen this follower evolve, " +
                                EffectLib.getEffectDescription(follower.getWhenEvolve(), follower));

                    description.append("\n" + follower.getBuffListString());

                    break;

                case AMULET:
                    Amulet amulet = (Amulet) card;

                    if (amulet.isCountDown())
                        description.append("\nCount Down: " + amulet.getOrgCountDown());

                    if (amulet.getFanfare() != null)
                        description.append("\nFanfare: " +
                                EffectLib.getEffectDescription(card.getFanfare(), amulet));

                    if (amulet.getLastword() != null)
                        description.append("\nLastword: " +
                                EffectLib.getEffectDescription(card.getLastword(), amulet));

                    if (amulet.getWhenStart() != null)
                        description.append("\nAt the beginning of the round, " +
                                EffectLib.getEffectDescription(card.getWhenStart(), amulet));

                    if (amulet.getWhenEnd() != null)
                        description.append("\nAt the end of the round, " +
                                EffectLib.getEffectDescription(card.getWhenEnd(), amulet));

                    if (amulet.getWhenOtherEnter() != null)
                        description.append("\n" + EffectLib.getEffectDescription(amulet.getWhenOtherEnter(), amulet));

                    break;

                case SPELL:
                    Spell spell = (Spell) card;

                    if (spell.getFanfare() != null)
                        description.append("\n\n" + EffectLib.getEffectDescription(spell.getFanfare(), spell));
                    break;

            }

            panel.add(description);
            container.add(panel);

            cardInfo.setVisible(true);
        });
    }

    /**
     * This method handles attack events targeting an opponent's Follower. The source Player and Follower is acquired
     * through {@linkplain #selection} object.
     *
     * <p>The validity of the attack action will be checked first. The attacker Follower must have correct {@linkplain
     * AttackStatus} and the target must be targetable. If the action is approved, the "When fight" and "When attack"
     * effects on the attacker will be activated first, and the target's "When fight" effect will also be activated.
     * Then, damage is dealt to both sides according to their current attack value. Special simple effects will be
     * processed after the damage is dealt(drain and bane).</p>
     *
     * <p>All effects are processed through {@link #sendEffect(String, Card, Player, Card, Player)}. If any card is
     * destroyed during the attack event, {@link #processCardExit(Card, Player, boolean)} will be invoked and the method
     * will not execute any code further.</p>
     *
     * @param target        the Follower object that is going to be attacked
     * @param target_player the Player object that owns the target Follower
     * @see Selection
     * @see AttackStatus
     */
    private void handleAttack(Follower target, Player target_player) {
        Follower attacker = selection.getAttacker();
        Player attacker_player = selection.getAttackerPlayer();

        if (attacker != null && attacker.getAtkStatus().canAttackFollower()) {

            if ((target_player.getField().haveNoWard() || target.hasWard() || attacker.hasWardPierce())
                    && !target.hasAmbush()) {
                sendEffect(attacker.getWhenFight(), attacker, attacker_player, target, target_player);
                sendEffect(attacker.getWhenAttack(), attacker, attacker_player, target, target_player);
                if (target.isAlive())
                    sendEffect(target.getWhenFight(), target, target_player, attacker, attacker_player);

                attacker.attack(target);
                displayMessage(attacker.getName() + " attacked enemy follower " + target.getName());

                if (attacker.hasDrain())
                    attacker_player.getLeader().heal(attacker.getAttack());

                if (!attacker.isAlive() || target.hasBane())
                    processCardExit(attacker, attacker_player, false);

                if (!target.isAlive() || attacker.hasBane())
                    processCardExit(target, target_player, false);

            } else {
                displayMessage("ERROR: Enemy is untargetable");
            }

        } else {
            displayMessage("ERROR: Attacker is unable to attack under current status");
        }

        updateGUI();
    }

    /**
     * This method handles attack events targeting the opponent's leader. The source Player and Follower is acquired
     * through {@linkplain #selection} object.
     *
     * <p>The validity of the attack action will be checked first. The attacker Follower must have correct {@linkplain
     * AttackStatus} and the opponent leader must be targetable. If the action is approved, the attacker's "When attack"
     * effect will be activated, and the damage will be dealt to the enemy leader. Simple effect "drain" will be
     * processed after the damage is dealt.</p>
     *
     * <p>All effects are processed through {@link #sendEffect(String, Card, Player, Card, Player)}. The game will end
     * if the attack causes the leader's health drops below or equal to 0.</p>
     *
     * @param target_player the Player object that is going to be attacked
     * @see Selection
     * @see AttackStatus
     */
    private void handleAttack(Player target_player) {
        Follower attacker = selection.getAttacker();
        Player attacker_player = selection.getAttackerPlayer();

        if (!target_player.getField().contains(attacker)) {
            if (attacker != null && attacker.getAtkStatus().canAttackLeader()) {
                if (target_player.getField().haveNoWard() || attacker.hasWardPierce()) {
                    sendEffect(attacker.getWhenAttack(), attacker, attacker_player, null, null);

                    attacker.attack(target_player.getLeader());
                    displayMessage(attacker.getName() + " attacked enemy leader " +
                            target_player.getLeader().getName());

                    if (attacker.hasDrain()) {
                        attacker_player.getLeader().heal(attacker.getAttack());
                    }

                    if (!target_player.getLeader().isAlive()) {
                        if (p1 == target_player) {
                            endGame(p2);
                        } else {
                            endGame(p1);
                        }
                    }

                } else {
                    displayMessage("ERROR: Enemy leader is untargetable");
                }
            } else {
                displayMessage("ERROR: Attacker is unable to attack enemy leader under current status");
            }
        } else {
            displayMessage("NO FRIENDLY DAMAGE ALLOWED!!!!!!!!!!");
        }


        updateGUI();
    }

    /**
     * This method finalizes the game. It plays the game end music, creates a dialog that shows the game result(the
     * winner of the game) and disables all interactive components from further receiving player's commands by toggling
     * the value of {@linkplain #gameAlive}(except for right click events, which checks the card details).
     *
     * <p>This method should be and only be invoked when the leader's health drops to 0 or below, or activation of some
     * special effects that allow immediate victory.</p>
     *
     * <p>GUI-related code are wrapped in {@link SwingUtilities#invokeLater(Runnable)} for EDT to execute.</p>
     *
     * @param winner the Player who wins the game
     * @see #gameAlive
     */
    public void endGame(Player winner) {
        SoundLib.GAME_END.playSound();

        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(this);
            Container container = dialog.getContentPane();
            JTextArea gameResult = new JTextArea();
            setTextStyle(gameResult);
            gameResult.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            if (winner == p2) {
                gameResult.setText("Enemy " + p2.getLeader().getName() + " wins!");
            } else {
                gameResult.setText("Player " + p1.getLeader().getName() + " wins!");
            }

            container.add(gameResult);
            dialog.setResizable(false);
            dialog.setBounds(getWidth() / 2 - 100, getHeight() / 2 - 100, 500, 200);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setTitle("GAME OVER");
            dialog.setVisible(true);

            gameAlive = false;
            playerEvolveButton.setEnabled(false);
            enemyEvolveButton.setEnabled(false);
            endRoundButton.setEnabled(false);
            playerLeaderImage.removeMouseListener(playerLeaderImage.getMouseListeners()[0]);
            enemyLeaderImage.removeMouseListener(enemyLeaderImage.getMouseListeners()[0]);
        });
    }

    /**
     * The method sets the default style for all label components (e.g. labels on JButtons and JLabels).
     *
     * <p>Default style is font {@linkplain Font#SANS_SERIF}, {@linkplain Font#BOLD}, size 18 and center alignment</p>
     *
     * @param component the component to be formatted
     */
    private void setLabelStyle(JComponent component) {
        component.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        if (component instanceof JLabel) {
            ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
        }

        if (component instanceof JButton) {
            ((JButton) component).setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    /**
     * This method sets the default style for all JTextArea components
     *
     * <p>Default style is font {@linkplain Font#SANS_SERIF}, {@linkplain Font#BOLD} and size 16</p>
     *
     * @param text the JTextArea to be formatted
     */

    private void setTextStyle(JTextArea text) {
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setBackground(null);
        text.setFont(new Font("Sans-Serif", Font.BOLD, 16));
    }

    /**
     * This method returns the number of rounds left before a player can use evolve points.
     *
     * @param player the Player object to be checked
     * @return the number of rounds left before evolve is available
     */
    private int getEvolveCD(Player player) {
        switch (player.getRoundOrder()) {
            case OFFENSIVE:
                return 5 - player.getRoundNumber();

            case DEFENSIVE:
                return 4 - player.getRoundNumber();

            default:
                throw new IllegalStateException("UNKNOWN ROUND ORDER STATE");
        }
    }

    /**
     * This method sends pending effects to {@linkplain EffectLib} and receives response from it.
     *
     * <p>A neither {@linkplain EffectLib.Response#DONE} nor {@linkplain EffectLib.Response#FAIL} response received
     * indicates that a specific target is needed to proceed the effect. The target's identity can vary according to
     * what the effect requires. Therefore, this method first checks if there is an eligible target. If there is,
     * {@linkplain #selection} will be set to that specific target type. If no card matches the requirement, the effect
     * will be disposed.</p>
     *
     * @param effect        the pending effect's content string
     * @param card          the origin Card of the effect
     * @param sourcePlayer  the origin Player of the effect
     * @param cardTrigger   the Card that triggers the effect
     * @param triggerPlayer the Player that triggers the effect
     * @see EffectLib
     * @see Selection
     */
    private void sendEffect(String effect, Card card, Player sourcePlayer, Card cardTrigger, Player triggerPlayer) {
        if (effect != null) {

            Player opponentPlayer;

            if (p1 != sourcePlayer)
                opponentPlayer = p1;
            else
                opponentPlayer = p2;

            switch (EffectLib.processEffect(effect, card, this, sourcePlayer, opponentPlayer)) {
                case ENEMY_FOLLOWER:
                    if (opponentPlayer.getField().hasTargetableFollower()) {
                        displayMessage("Please select an enemy target");
                        selection = Selection.ENEMY_FOLLOWER;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case FRIENDLY_FOLLOWER:
                    if (sourcePlayer.getField().followerCount() > 0) {
                        displayMessage("Please select a friendly target");
                        selection = Selection.FRIENDLY_FOLLOWER;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case ANY_FOLLOWER:
                    if (sourcePlayer.getField().followerCount() > 0 || opponentPlayer.getField().hasTargetableFollower()) {
                        displayMessage("Please select a follower target");
                        selection = Selection.ANY_FOLLOWER;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case FRIENDLY_OFFICER:
                    if (sourcePlayer.getField().hasSwordTraitFollower(Trait.SwordCraftTrait.OFFICER)) {
                        displayMessage("Please select a friendly officer");
                        selection = Selection.FRIENDLY_OFFICER;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case FRIENDLY_OFFICER_ENTRANCE:
                    if (triggerPlayer == sourcePlayer && cardTrigger instanceof Follower &&
                            ((Follower) cardTrigger).getSwordCraftTrait() == Trait.SwordCraftTrait.OFFICER) {
                        EffectLib.proceedEffect(cardTrigger, sourcePlayer);
                    } else {
                        discardEffect(false);
                    }
                    break;

                case FRIENDLY_COMMANDER:
                    if (sourcePlayer.getField().hasSwordTraitFollower(Trait.SwordCraftTrait.COMMANDER)) {
                        displayMessage("Please select a friendly commander");
                        selection = Selection.FRIENDLY_COMMANDER;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case FRIENDLY_COMMANDER_ENTRANCE:
                    if (triggerPlayer == sourcePlayer && cardTrigger instanceof Follower &&
                            ((Follower) cardTrigger).getSwordCraftTrait() == Trait.SwordCraftTrait.COMMANDER) {
                        EffectLib.proceedEffect(cardTrigger, sourcePlayer);
                    } else {
                        discardEffect(false);
                    }
                    break;

                case FRIENDLY_FOLLOWER_ENTRANCE:
                    if (triggerPlayer == sourcePlayer && cardTrigger instanceof Follower) {
                        EffectLib.proceedEffect(cardTrigger, sourcePlayer);
                    } else {
                        discardEffect(false);
                    }
                    break;

                case ENEMY_FOLLOWER_OR_LEADER:
                    displayMessage("Please select an enemy target");
                    selection = Selection.ENEMY_FOLLOWER_OR_LEADER;
                    break;

                case FRIENDLY_FOLLOWER_OR_LEADER:
                    displayMessage("Please select a friendly target");
                    selection = Selection.FRIENDLY_FOLLOWER_OR_LEADER;
                    break;

                case FRIENDLY_CD_AMULET:
                    if (sourcePlayer.getField().hasCountdownAmulet()) {
                        displayMessage("Please select a friendly countdown amulet");
                        selection = Selection.FRIENDLY_CD_AMULET;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case ENEMY_FOLLOWER_HP_LESS:
                    if (opponentPlayer.getField().hasTargetableFollowerBelowHpLimit(EffectLib.Response.getParameter())) {
                        displayMessage("Please select an enemy follower with 3 health or less");
                        selection = Selection.ENEMY_FOLLOWER_HP_LESS;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case ENEMY_FOLLOWER_OR_AMULET:
                    if (opponentPlayer.getField().hasTargetableFollower() ||
                            opponentPlayer.getField().amuletCount() > 0) {
                        displayMessage("Please select an enemy follower or amulet");
                        selection = Selection.ENEMY_FOLLOWER_OR_AMULET;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case FRIENDLY_FOLLOWER_OR_AMULET:
                    if (sourcePlayer.getField().size() > 0) {
                        displayMessage("Please select a friendly follower or amulet");
                        selection = Selection.FRIENDLY_FOLLOWER_OR_AMULET;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case ENEMY_AMULET:
                    if (opponentPlayer.getField().amuletCount() > 0) {
                        displayMessage("Please select an enemy amulet");
                        selection = Selection.ENEMY_AMULET;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case FIGHTING_ENEMY:
                    if (cardTrigger != null && triggerPlayer != null)
                        EffectLib.proceedEffect(cardTrigger, triggerPlayer);
                    else
                        throw new IllegalArgumentException("Trigger player and card required");
                    break;

                case FRIENDLY_COST_ONE_FOLLOWER:
                    boolean hasOneHealth = false;

                    for (Card c : sourcePlayer.getField()) {
                        if (c instanceof Follower && c.getCost() == 1) {
                            hasOneHealth = true;
                            break;
                        }
                    }

                    if (hasOneHealth) {
                        displayMessage("Please select a friendly follower with 1 cost");
                        selection = Selection.FRIENDLY_ONE_COST_FOLLOWER;
                    } else {
                        discardEffect(true);
                    }
                    break;

                case ENEMY_FOLLOWER_ATK_MORE:
                    if (opponentPlayer.getField().hasTargetableFollowerAboveAtkLimit(EffectLib.Response.getParameter())) {
                        displayMessage("Please select an enemy follower with " + EffectLib.Response.getParameter() + " attack or more");
                        selection = Selection.ENEMY_FOLLOWER_ATK_MORE;
                    } else {
                        discardEffect(true);
                    }
                    break;

                default:
                    updateGUI();
                    break;
            }

            cleanField();
        }
    }

    /**
     * This method discards the effect that is currently processing by {@linkplain EffectLib}. It is only invoked when
     * there is no eligible target found to proceed the effect.
     *
     * @param displayMessage true: display the effect discard information on the {@linkplain #gameGuideText}, nothing
     * @see EffectLib
     */
    private void discardEffect(boolean displayMessage) {
        if (displayMessage)
            displayMessage("Effect discarded due to no available target");

        EffectLib.proceedEffect(null, null);
        selection = Selection.PEND;
    }

    /**
     * This method processes card exit event for on-field cards, whether the card is banished or destroyed.
     *
     * <p>For a banished card, it is simply removed from the field and disappeared. Its "Last word" effect will not be
     * triggered and it will not be transferred to the grave.</p>
     *
     * <p>For a destroyed card, it is removed from the field and added to the grave. Its "Last word" effect will also be
     * activated.</p>
     *
     * @param card     Card that needs to exit the field
     * @param owner    owner of the Card
     * @param isBanish true: banish the card. Otherwise destroy the card.
     */
    public void processCardExit(Card card, Player owner, boolean isBanish) {
        if (isBanish || card.getName().equals("Ghost")) {
            displayMessage(card.getName() + " has vanished.");
            owner.getField().remove(card);
        } else {
            displayMessage(card.getName() + " has been destroyed.");
            owner.getField().remove(card);
            owner.getGrave().add(card);
            sendEffect(card.getLastword(), card, owner, null, null);
        }

        updateGUI();
    }

    /**
     * This method updates String content for {@linkplain #gameGuideText}, which serves as a information display to the
     * players.
     *
     * <p>It checks if the current thread is EDT or not. If it is, change is made directly. If it is not, change will be
     * wrapped in {@link SwingUtilities#invokeLater(Runnable)} for EDT to execute.</p>
     *
     * @param message the message to be displayed
     */
    private void displayMessage(String message) {
        if (SwingUtilities.isEventDispatchThread())
            gameGuideText.setText(message);
        else
            SwingUtilities.invokeLater(() -> gameGuideText.setText(message));
    }

    /**
     * This method checks for both players' fields and destroy any card that is no longer alive. It is called every time
     * an effect is processed, as some effects can deal lethal damage to a follower or destroy cards on the field.
     *
     * <p>Banishing cards and attack events do not invoke this method, as they have their own settlements on the targets
     * they affect.</p>
     */
    private void cleanField() {
        for (int n = 0; n < p1.getField().size(); n++) {
            if (!p1.getField().get(n).isAlive()) {
                processCardExit(p1.getField().get(n), p1, false);
                n--;
            }
        }

        for (int n = 0; n < p2.getField().size(); n++) {
            if (!p2.getField().get(n).isAlive()) {
                processCardExit(p2.getField().get(n), p2, false);
                n--;
            }
        }

    }


    /**
     * This enum includes all possible selection statuses. The value represents the identity of the card current player
     * clicks next. Thus, when a card click event is activated, the method will use the {@linkplain #selection} value to
     * check and see if the action matches the requirement.
     *
     * <p>The default value is {@linkplain Selection#PEND}. It is also used to represent the state of pending when there
     * is no specification of what card the player should click next.</p>
     *
     * <p>When the status is {@linkplain Selection#ATTACK}, {@linkplain Selection#registerAttack(Player, Follower)},
     * {@linkplain Selection#getAttackerPlayer()} and {@linkplain Selection#getAttacker()} will be available to use.
     * They register the current attacker and attacker player for {@linkplain #handleAttack(Player)} or {@linkplain
     * #handleAttack(Follower, Player)} to extract. It is illgeal to use these three methods under any other conditions,
     * or exceptions will be thrown.</p>
     */
    public enum Selection {
        ENEMY_FOLLOWER, ANY_FOLLOWER, FRIENDLY_FOLLOWER, EVOLUTION, ATTACK, PEND, FRIENDLY_OFFICER,
        ENEMY_FOLLOWER_OR_LEADER, FRIENDLY_FOLLOWER_OR_LEADER, FRIENDLY_CD_AMULET, ENEMY_FOLLOWER_HP_LESS,
        ENEMY_FOLLOWER_OR_AMULET, FRIENDLY_FOLLOWER_OR_AMULET, ENEMY_AMULET, FRIENDLY_COMMANDER,
        FRIENDLY_ONE_COST_FOLLOWER, ENEMY_FOLLOWER_ATK_MORE;

        private Player attacker_player;
        private Follower attacker;

        /**
         * The method registers attacker and attacker player into its private fields. It is only to be invoked when the
         * current status is {@linkplain Selection#ATTACK}
         *
         * @param player   attacker player
         * @param follower attacker
         * @throws IllegalStateException when the current status is not {@linkplain Selection#ATTACK}
         */
        void registerAttack(Player player, Follower follower) {
            if (this == ATTACK) {
                attacker_player = player;
                attacker = follower;
            } else {
                throw new IllegalStateException("Method only available when in ATTACK status");
            }
        }

        /**
         * This method is the getter of attacker player. It is only to be invoked when the current status is {@linkplain
         * Selection#ATTACK}
         *
         * @return attacker player
         * @throws IllegalStateException when the current status is not {@linkplain Selection#ATTACK}
         */
        Player getAttackerPlayer() {
            if (this == ATTACK)
                return attacker_player;
            else
                throw new IllegalStateException("Method only available when in ATTACK status");
        }

        /**
         * This method is the getter of attacker. It is only to be invoked when the current status is {@linkplain
         * Selection#ATTACK}
         *
         * @return attacker
         * @throws IllegalStateException when the current status is not {@linkplain Selection#ATTACK}
         */
        Follower getAttacker() {
            if (this == ATTACK)
                return attacker;
            else
                throw new IllegalStateException("Method only available when in ATTACK status");
        }

    }

    /**
     * This inner class is designed to be the container of {@linkplain FieldCardPanel}. It holds a maximum of 5 cards
     * representing a player's field.
     *
     * <p>This panel has the same width as the main frame's left panel, and a height of a quarter of the main frame. In
     * one game, there should be two field panels representing player1's and player2's fields.</p>
     *
     * @see FieldCardPanel
     */
    private class FieldPanel extends JPanel {
        private Player owner;

        /**
         * The constructor sets the layout manager to {@linkplain FlowLayout} with a horizontal gap of 40. It also sets
         * the panel's preferred dimension.
         *
         * @param player the owner player of the field panel
         */
        private FieldPanel(Player player) {
            super();
            owner = player;
            setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
            setPreferredSize(new Dimension((int) (Game.this.getWidth() * 0.82), (int) (Game.this.getHeight() * 0.25)));
        }

        /**
         * This method refreshes, repaints and revalidate every {@linkplain FieldCardPanel} within.
         *
         * @param g Graphics instance
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            removeAll();
            for (Card c : owner.getField()) {
                FieldCardPanel cardPanel = new FieldCardPanel(c, owner);
                add(cardPanel);
                cardPanel.repaint();
            }

            if (!isValid()) {
                validate();
            }
        }
    }

    /**
     * This inner class is designed to be the container of {@linkplain HandCardPanel}. It holds a maximum of 9 cards
     * representing a player's hand.
     *
     * <p>This panel has the same width as the main frame's left panel, and a height of a quarter of the main frame. In
     * one game, there should be two hand panels representing player1's and player2's hands.</p>
     *
     * @see HandCardPanel
     */
    private class HandPanel extends JPanel {
        private Player owner;

        /**
         * The constructor sets the layout manager to {@linkplain FlowLayout} with a horizontal gap of 10. It also sets
         * the panel's preferred dimension.
         *
         * @param player the owner player of the card panel
         */
        private HandPanel(Player player) {
            super();
            owner = player;
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setPreferredSize(new Dimension((int) (Game.this.getWidth() * 0.82), (int) (Game.this.getHeight() * 0.25)));
        }

        /**
         * This method refreshes, repaints and revalidate every {@linkplain HandCardPanel} within.
         *
         * @param g Graphics instance
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            removeAll();
            for (Card c : owner.getHand()) {
                HandCardPanel cardPanel = new HandCardPanel(c, owner);
                add(cardPanel);
                cardPanel.repaint();
            }

            if (!isValid()) {
                validate();
            }

        }
    }

    /**
     * This is designed to illustrate cards on the fields with their statuses and process any click event that takes
     * place. One panel only displays and processes one card object, and it is to be stored and displayed by {@linkplain
     * FieldPanel} only. The panel's dimension is the same as a field card image's dimension.
     *
     * <p>For a {@link Follower}, the card's attack and health are displayed at the bottom left and bottom right corners
     * respectively. Its attack status is displayed at the top left corner represented by a dot with three different
     * possible colors - green, grey and yellow. Drain and Bane effects are illustrated on the top right corner with
     * their symbols. All other effects, such as CANT_ATTACK, AMBUSH, will be displayed in the middle of the card image
     * represented by their png symbols.</p>
     *
     * <p>For a {@link Amulet}, the card's count down will be displayed at the bottom right corner, if this amulet is a
     * count-down type amulet.</p>
     *
     * <p>{@link Spell} will never be displayed on the field, as it goes to grave immediately after using from
     * hand.</p>
     *
     * <p>The panel has a MouseListener that monitors the mouse click events through {@linkplain
     * #mousePressed(MouseEvent)}. The events are going to be processed accordingly to the status of {@linkplain
     * #selection} and the type of the card. An error message will be displayed when the click does not meet the
     * requirement given by {@linkplain #selection}.</p>
     *
     * @see ImageLib
     * @see Follower.SimpleEffects
     * @see AttackStatus
     * @see Selection
     */
    private class FieldCardPanel extends JPanel implements MouseListener {
        private final Card CARD;
        private final Player OWNER;
        private final int WIDTH, HEIGHT;

        /**
         * FieldCardPanel initialization requires the card to be displayed and its owner instances.
         *
         * @param c     the card to be displayed
         * @param owner the owner of the card
         */
        private FieldCardPanel(Card c, Player owner) {
            super();

            CARD = c;
            OWNER = owner;
            WIDTH = ImageLib.FIELD_CARD_WIDTH;
            HEIGHT = ImageLib.FIELD_CARD_HEIGHT;

            setPreferredSize(new Dimension(WIDTH, HEIGHT));

            addMouseListener(this);
        }

        /**
         * <p>Display the round-corner card image and information according to the card's type. </p>
         *
         * @param g Graphic instance to be painted
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);

            Image cardImage;

            if (CARD instanceof Follower && ((Follower) CARD).hasEvolved()) {
                cardImage = ((Follower) CARD).getEvolvedImage();
            } else {
                cardImage = CARD.getFieldImage();
            }

            int x = (WIDTH - cardImage.getWidth(null)) / 2;
            int y = (HEIGHT - cardImage.getHeight(null)) / 2;

            g2d.setClip(new RoundRectangle2D.Double(
                    0, 0, WIDTH, HEIGHT, WIDTH / 4, HEIGHT / 4));
            g2d.drawImage(cardImage, x, y, this);

            if (CARD instanceof Follower) {
                Follower follower = (Follower) CARD;
                g2d.drawImage(ImageLib.getCardComponentImage("atk"), 15, HEIGHT - 60, this);
                g2d.drawImage(ImageLib.getCardComponentImage("def"), WIDTH - 55, HEIGHT - 60, this);

                AttributedString attack = new AttributedString(Integer.toString(follower.getAttack()));
                AttributedString health = new AttributedString(Integer.toString(follower.getHealth()));
                attack.addAttribute(TextAttribute.SIZE, 15);
                health.addAttribute(TextAttribute.SIZE, 15);
                attack.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                health.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

                g2d.drawString(attack.getIterator(), 33, HEIGHT - 27);
                g2d.drawString(health.getIterator(), WIDTH - 37, HEIGHT - 27);

                if (follower.hasWard()) {
                    Image ward = ImageLib.getCardComponentImage("ward");
                    g2d.drawImage(ward, (WIDTH - ward.getWidth(null)) / 2,
                            (HEIGHT - ward.getHeight(null)) / 2, this);
                }

                if (follower.hasAmbush()) {
                    Image ambush = ImageLib.getCardComponentImage("ambush");
                    g2d.drawImage(ambush, (WIDTH - ambush.getWidth(null)) / 2,
                            (HEIGHT - ambush.getHeight(null)) / 4, this);
                }

                if (follower.hasBane()) {
                    g2d.drawImage(ImageLib.getCardComponentImage("bane"), 10, 10, this);
                }

                if (follower.hasDrain()) {
                    g2d.drawImage(ImageLib.getCardComponentImage("drain"), 50, 10, this);
                }

                if (follower.getAtkStatus() == AttackStatus.DISABLED) {
                    Image cant_attack = ImageLib.getCardComponentImage("cant_attack");
                    g2d.drawImage(cant_attack, (WIDTH - cant_attack.getWidth(null)) / 2,
                            (HEIGHT - cant_attack.getHeight(null)) / 4, this);
                }

                if (follower.hasDamageImmune()) {
                    Image damage_immune = ImageLib.getCardComponentImage("damage_immune");
                    g2d.drawImage(damage_immune, (WIDTH - damage_immune.getWidth(null)) / 2,
                            (HEIGHT - damage_immune.getHeight(null)) / 4, this);
                }

                if (follower.hasUntargetable()) {
                    Image untargetable = ImageLib.getCardComponentImage("untargetable");
                    g2d.drawImage(untargetable, (WIDTH - untargetable.getWidth(null)) / 2,
                            (HEIGHT - untargetable.getHeight(null)) / 4, this);
                }

                switch (follower.getAtkStatus()) {
                    case STORM:
                        g2d.setColor(Color.GREEN);
                        break;

                    case RUSH:
                        g2d.setColor(Color.YELLOW);
                        break;

                    default:
                        g2d.setColor(Color.darkGray);
                        break;
                }
                g2d.fillOval(WIDTH - 40, 8, 30, 30);
                g2d.setColor(Color.WHITE);

            } else if (CARD instanceof Amulet) {
                Amulet amulet = (Amulet) CARD;
                if (amulet.isCountDown()) {
                    AttributedString countDown = new AttributedString(Integer.toString(amulet.getCountDown()));
                    countDown.addAttribute(TextAttribute.SIZE, 45);
                    countDown.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

                    g2d.setColor(Color.YELLOW);
                    g2d.drawString(countDown.getIterator(), WIDTH - 40, HEIGHT - 20);
                    g2d.setColor(Color.WHITE);
                }

            } else {
                throw new IllegalStateException("Only Amulet and Follower are allowed to be displayed by HandCardPanel");
            }

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        /**
         * <p>This implemented method processes mouse clicking events on {@linkplain FieldCardPanel}. For left clicks,
         * the status indicated by {@linkplain #selection} will be checked and corresponding actions will be performed.
         * For right clicks, the clicked card's details will be displayed.</p>
         *
         * <p>For left click cases, after the action is completed, {@linkplain #selection} will be reset to its default
         * value {@linkplain Selection#PEND}. If the click event does not agree with the status given, an error message
         * will show up warning the player that it's an invalid move. {@linkplain #selection} will be unchanged in this
         * case.</p>
         *
         * @param e The MouseEvent instance
         * @see #selection
         * @see #displayMessage(String)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            displayMessage("-");

            switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    if (gameAlive) {
                        if (CARD instanceof Follower) {
                            Follower follower = (Follower) CARD;

                            switch (selection) {
                                case ATTACK:
                                    if (!OWNER.getField().contains(selection.getAttacker())) {
                                        handleAttack(follower, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        selection.registerAttack(OWNER, follower);
                                    }
                                    break;

                                case ENEMY_FOLLOWER_OR_AMULET:
                                case ENEMY_FOLLOWER_OR_LEADER:
                                case ENEMY_FOLLOWER:
                                    if (!current_player.getField().contains(follower)) {
                                        if (follower.canBeTargeted()) {
                                            EffectLib.proceedEffect(follower, OWNER);
                                            selection = Selection.PEND;
                                        } else {
                                            displayMessage("Enemy has UNTARGETABLE, cant be selected as an effect target");
                                        }
                                    } else {
                                        displayMessage("Please select an ENEMY target!!!");
                                    }
                                    break;

                                case FRIENDLY_FOLLOWER_OR_AMULET:
                                case FRIENDLY_FOLLOWER_OR_LEADER:
                                case FRIENDLY_FOLLOWER:
                                    if (current_player.getField().contains(follower)) {
                                        EffectLib.proceedEffect(follower, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        displayMessage("Please select a FRIENDLY target!!!");
                                    }
                                    break;

                                case ENEMY_FOLLOWER_HP_LESS:
                                    if (!current_player.getField().contains(follower) && follower.getHealth() <= EffectLib.Response.getParameter()) {
                                        if (follower.canBeTargeted()) {
                                            EffectLib.proceedEffect(follower, OWNER);
                                            selection = Selection.PEND;
                                        } else {
                                            displayMessage("Enemy has UNTARGETABLE, cant be selected as an effect target");
                                        }
                                    } else {
                                        displayMessage("Please select an ENEMY follower with " + EffectLib.Response.getParameter() + " HEALTH OR LESS");
                                    }
                                    break;

                                case ANY_FOLLOWER:
                                    if (!current_player.getField().contains(follower) && !follower.canBeTargeted()) {
                                        displayMessage("Enemy cant be selected as an effect target");
                                    } else {
                                        EffectLib.proceedEffect(follower, OWNER);
                                        selection = Selection.PEND;
                                    }
                                    break;

                                case FRIENDLY_CD_AMULET:
                                    displayMessage("Please select a FRIENDLY COUNTDOWN AMULET!!!");
                                    break;

                                case FRIENDLY_OFFICER:
                                    if (current_player.getField().contains(follower) &&
                                            follower.getSwordCraftTrait() == Trait.SwordCraftTrait.OFFICER) {
                                        EffectLib.proceedEffect(follower, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        displayMessage("Please select a FRIENDLY OFFICER!");
                                    }
                                    break;

                                case FRIENDLY_COMMANDER:
                                    if (current_player.getField().contains(follower) &&
                                            follower.getSwordCraftTrait() == Trait.SwordCraftTrait.COMMANDER) {
                                        EffectLib.proceedEffect(follower, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        displayMessage("Please select a FRIENDLY COMMANDER!");
                                    }
                                    break;

                                case EVOLUTION:
                                    if (current_player.getField().contains(follower)) {
                                        selection = Selection.PEND;
                                        useEvolvePts(follower, OWNER);
                                    } else {
                                        displayMessage("You can only evolve your OWN follower");
                                    }
                                    break;

                                case PEND:
                                    if (current_player.getField().contains(follower)) {
                                        selection = Selection.ATTACK;
                                        selection.registerAttack(OWNER, follower);
                                    } else {
                                        showCardDetail(follower);
                                    }
                                    break;

                                case FRIENDLY_ONE_COST_FOLLOWER:
                                    if (current_player.getField().contains(follower) && follower.getCost() == 1) {
                                        EffectLib.proceedEffect(follower, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        displayMessage("Please select a FRIENDLY FOLLOWER WITH 1 COST");
                                    }
                                    break;

                                case ENEMY_FOLLOWER_ATK_MORE:
                                    if (!current_player.getField().contains(follower) && follower.getAttack() >= EffectLib.Response.getParameter()) {
                                        if (follower.canBeTargeted()) {
                                            EffectLib.proceedEffect(follower, OWNER);
                                            selection = Selection.PEND;
                                        } else {
                                            displayMessage("Enemy cant be selected as an effect target");
                                        }
                                    } else {
                                        displayMessage("Please select an ENEMY follower with " + EffectLib.Response.getParameter() + " ATTACK OR MORE");
                                    }
                                    break;
                            }


                        } else if (CARD instanceof Amulet) {
                            Amulet amulet = (Amulet) CARD;

                            switch (selection) {
                                case FRIENDLY_CD_AMULET:
                                    if (current_player.getField().contains(amulet)) {
                                        if (amulet.isCountDown()) {
                                            EffectLib.proceedEffect(amulet, OWNER);
                                            selection = Selection.PEND;
                                        } else {
                                            displayMessage("Please select an amulet that has COUNTDOWN");
                                        }
                                    } else {
                                        displayMessage("Please select a FRIENDLY countdown amulet");
                                    }
                                    break;

                                case ENEMY_FOLLOWER_OR_AMULET:
                                    if (!current_player.getField().contains(amulet)) {
                                        EffectLib.proceedEffect(amulet, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        displayMessage("Please select an ENEMY target!");
                                    }
                                    break;

                                case FRIENDLY_FOLLOWER_OR_AMULET:
                                    if (current_player.getField().contains(amulet)) {
                                        EffectLib.proceedEffect(amulet, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        displayMessage("Please select a FRIENDLY target!");
                                    }
                                    break;

                                case ENEMY_AMULET:
                                    if (!current_player.getField().contains(amulet)) {
                                        EffectLib.proceedEffect(amulet, OWNER);
                                        selection = Selection.PEND;
                                    } else {
                                        displayMessage("Please select an ENEMY amulet");
                                    }
                                    break;

                                default:
                                    showCardDetail(amulet);
                                    break;

                            }
                        }

                        cleanField();
                    }

                    break;

                case MouseEvent.BUTTON3:
                    showCardDetail(CARD);
                    break;
            }

            updateGUI();
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * This is designed to illustrate cards in hand and process any click event that takes place. One panel only
     * displays and processes one card object, and it is to be stored and displayed by {@linkplain HandPanel} only. The
     * panel's dimension is the same as a hand card image's dimension.
     *
     * <p>Each card has a frame that illustrates its rarity. The cost is displayed on the top left corner. For
     * {@linkplain Follower}, its attack and health will be displayed on the bottom left and bottom right corners. </p>
     *
     * <p>The panel has a MouseListener that monitors the mouse click events through {@linkplain
     * #mousePressed(MouseEvent)}. A left click indicates the action of using a card from hand. A right click indicates
     * the action of showing the card's detail. The left click event will be invalid when it is not the player's turn
     * yet or the player does not have enough cost to use the card or the card is {@linkplain Amulet}/{@linkplain
     * Follower} and the field is full.</p>
     *
     * @see ImageLib
     */
    private class HandCardPanel extends JPanel implements MouseListener {
        private final int WIDTH, HEIGHT;
        private Card CARD;
        private Image frameImage, cardImage;
        private Player OWNER;
        private AttributedString cardName, costNum, atk, hp;
        private int imgX, imgY;

        /**
         * The constructor initializes components that are to be displayed on the panel's graphic later.
         *
         * @param c     card to be displayed on this panel
         * @param owner the owner of the card
         */
        private HandCardPanel(Card c, Player owner) {
            super();

            CARD = c;
            OWNER = owner;
            WIDTH = ImageLib.HAND_CARD_WIDTH;
            HEIGHT = ImageLib.HAND_CARD_HEIGHT;

            setPreferredSize(new Dimension(WIDTH, HEIGHT));

            frameImage = ImageLib.getCardComponentImage(CARD.getRarity() + "_" + CARD.getType()).getScaledInstance(
                    WIDTH, HEIGHT, Image.SCALE_SMOOTH);

            cardName = new AttributedString(CARD.getName());
            cardName.addAttribute(TextAttribute.SIZE, 10);

            costNum = new AttributedString(Integer.toString(CARD.getCost()));
            costNum.addAttribute(TextAttribute.SIZE, 13);
            costNum.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

            switch (CARD.getType()) {
                case FOLLOWER:
                    cardImage = CARD.getHandImage().getScaledInstance(
                            WIDTH - 25, HEIGHT - 25, Image.SCALE_SMOOTH);
                    imgX = (WIDTH - cardImage.getWidth(null)) / 2;
                    imgY = (HEIGHT - cardImage.getHeight(null)) / 2;
                    atk = new AttributedString(Integer.toString(((Follower) CARD).getOrgAttack()));
                    hp = new AttributedString(Integer.toString(((Follower) CARD).getOrgHealth()));
                    atk.addAttribute(TextAttribute.SIZE, 13);
                    hp.addAttribute(TextAttribute.SIZE, 13);
                    atk.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    hp.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    break;

                case SPELL:
                    cardImage = CARD.getHandImage().getScaledInstance(
                            WIDTH - 15, HEIGHT - 15, Image.SCALE_SMOOTH);
                    imgX = WIDTH - cardImage.getWidth(null);
                    imgY = HEIGHT - cardImage.getHeight(null);
                    break;

                case AMULET:
                    cardImage = CARD.getHandImage().getScaledInstance(
                            WIDTH - 15, HEIGHT - 15, Image.SCALE_SMOOTH);
                    imgX = WIDTH - cardImage.getWidth(null);
                    imgY = HEIGHT - cardImage.getHeight(null);
                    break;
            }

            addMouseListener(this);
        }

        /**
         * @return the card represented by this panel
         */
        public Card getCard() {
            return CARD;
        }

        /**
         * <p>Display the card's frame, its image and its information according to the card's type. </p>
         *
         * @param g Graphic instance to be painted
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            switch (CARD.getType()) {
                case FOLLOWER:
                    g2d.drawImage(cardImage, imgX, imgY, this);
                    g2d.drawImage(frameImage, 0, 0, this);

                    g2d.setColor(Color.WHITE);
                    g2d.drawString(atk.getIterator(), 16, HEIGHT - 15);
                    g2d.drawString(hp.getIterator(), WIDTH - 21, HEIGHT - 15);

                    g2d.setColor(Color.WHITE);
                    g2d.drawString(cardName.getIterator(), 38, 27);

                    g2d.setColor(Color.BLACK);
                    g2d.drawString(costNum.getIterator(), 15, 27);
                    break;

                case SPELL:
                    g2d.drawImage(cardImage, imgX, imgY, this);
                    g2d.drawImage(frameImage, 0, 0, this);

                    g2d.setColor(Color.WHITE);
                    g2d.drawString(cardName.getIterator(), 38, 21);

                    g2d.setColor(Color.BLACK);
                    g2d.drawString(costNum.getIterator(), 15, 21);
                    break;

                case AMULET:
                    g2d.drawImage(cardImage, imgX, imgY, this);
                    g2d.drawImage(frameImage, 0, 0, this);

                    if (((Amulet) CARD).isCountDown()) {
                        AttributedString cdNum = new AttributedString(Integer.toString(((Amulet) CARD).getCountDown()));
                        cdNum.addAttribute(TextAttribute.SIZE, 15);
                        cdNum.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

                        g2d.setColor(Color.WHITE);
                        g2d.drawString(cdNum.getIterator(), WIDTH - 30, HEIGHT - 30);
                    }

                    g2d.setColor(Color.WHITE);
                    g2d.drawString(cardName.getIterator(), 38, 27);

                    g2d.setColor(Color.BLACK);
                    g2d.drawString(costNum.getIterator(), 15, 27);
                    break;
            }

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        /**
         * <p>This implemented method processes mouse clicking events on {@linkplain HandCardPanel}. For left clicks,
         * the clicked card will be used from hand if available. An error message will be displayed the card is
         * unavailable to be used. For right clicks, the clicked card's details will be displayed.</p>
         *
         * <p>A card is available to be used when it is the player's turn, the player has enough cost to use the card,
         * and there is space on the field if the card is a {@linkplain Follower} or {@linkplain Amulet} type.</p>
         *
         * @param e The MouseEvent instance
         * @see #displayMessage(String)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if (OWNER == current_player) {
                displayMessage("-");

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        if (gameAlive) {
                            if (CARD.getCost() <= OWNER.getCostLeft()) {

                                if (CARD instanceof Follower || CARD instanceof Amulet) {

                                    if (OWNER.getField().size() < Field.MAXIMUM_FIELD_SIZE) {
                                        OWNER.useCost(CARD.getCost());
                                        OWNER.useCard(CARD);

                                        if (CARD instanceof Follower) {
                                            Field ownerField = (Field) OWNER.getField().clone();

                                            for (Card c : ownerField)
                                                if (OWNER.getField().contains(c))
                                                    sendEffect(c.getWhenOtherEnter(), c, OWNER, CARD, OWNER);
                                        }

                                        sendEffect(CARD.getFanfare(), CARD, OWNER, null, null);

                                        if (CARD.isAlive())
                                            OWNER.getField().add(CARD);

                                    } else {
                                        displayMessage("Unable to use card. Field full.");
                                    }

                                } else {
                                    OWNER.useCost(CARD.getCost());
                                    OWNER.useCard(CARD);
                                    sendEffect(CARD.getFanfare(), CARD, OWNER, null, null);

                                    for (Card c : OWNER.getHand())
                                        if (c.isSpellBoost())
                                            c.boost();

                                    OWNER.getGrave().add(CARD);
                                }

                            } else {
                                displayMessage("ERROR: Not enough cost");
                            }

                            cleanField();
                        }

                        break;

                    case MouseEvent.BUTTON3:
                        showCardDetail(CARD);
                        break;
                }

            } else {
                displayMessage("You are not currently in play! Opponent's round!");
            }

            updateGUI();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
