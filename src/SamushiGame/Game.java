/*
I have decided to put the source code in .jar since I don't really
have anything important to hide, and it might be interesting
for someone to read without opening a decompiler.
May God forgive me for this awful mess of a game. Sorry if you see this, Erik.
I feel terrible for drawing BingHurtGif, he looks so sad I want to cry too.
Pretty good though considering this gif should make player feel bad.
Also, I am tired from making each frame by hand.
Imagine if java had a good gif player...
 */
package SamushiGame;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Game {
    public static final int GAME_WINDOW_WIDTH = 1000;
    public static final int GAME_WINDOW_HEIGHT = 700;
    public static final int ROW_DISTANCE = 210;
    public static final int COLUMN_DISTANCE = 320;
    public static final int ROWS = 3;
    public static final int COLUMNS = 3;
    public static final String GAME_NAME = "Samushi Smasher";
    public static final String GAME_ICON = "Images/GameIcon.png";
    //Configurable
    public static int ACCELERATION_INTERVAL = 100;
    public static int GAME_ACCELERATION = 10;
    public static int MAX_HOLES_PER_TICK = 2;
    public static int GIF_SPEED = 10;
    public static int DISAPPEAR_MULTIPLIER = 2;
    public static int OG_SPEED = 3000;
    public static int MAX_SPEED = 300;
    public static int MINIMAl_ADD_TIME = 300;
    public static boolean PLAY_MUSIC = true;
    public static boolean RED_EYES_CLOSE_GAME_INSTEAD = false;
    //Sounds
    public static final String SUCCESSFUL_HIT_SOUND = "sounds/SuccessfulHit.wav";
    public static final String FAIL_HIT_SOUND = "sounds/Fail.wav";
    //Enemy random, configurable
    public static int RED_EYES_CHANCE = 5;
    public static int COP_CHANCE = 20; //15
    public static int NO_MONOCLE_CHANCE = 40; //20
    public static int MAX_ENEMY_RANDOM = 100; //60
    //Unique hit random, configurable
    public static int HUMAN_CHANCE = 15;
    public static int MAX_HIT_RANDOM = 100;
    //General hit chance out of 100, configurable
    public static int HIT_CHANCE = 60;
    public int gameSpeed = OG_SPEED;
    public int score = 0;
    public int timeLeft = 90; //configurable
    public int misses = 0;

    JTextField textScore = new JTextField();
    JTextField textTimeLeft = new JTextField();
    JTextField congratulationsText = new JTextField();
    JTextField finalScoreText = new JTextField();
    JTextField missesText = new JTextField();

    Timer timer = new Timer(1000, e -> {
        timeLeft--;
        textTimeLeft.setText(parseTime(timeLeft));
        if (timeLeft <= 0) {
            endGame();
            showScore();
        }
    });

    Timer gameSpeedUpTimer = new Timer(ACCELERATION_INTERVAL, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            setGameSpeed(gameSpeed - GAME_ACCELERATION);
            gameTickTimer.setDelay(gameSpeed);
        }
    });
    Timer gameTickTimer = new Timer(gameSpeed, e -> gameTick());

    Gif bingGif = new Gif("gifs/BingGif", "png");
    Gif bingHurtGif = new Gif("gifs/BingHurtGif", "png");
    Gif humanGif = new Gif("gifs/HumanGif", "png");
    Gif humanRedEyesGif = new Gif("gifs/HumanRedEyesGif", "png");
    Gif notFeelSoGoodGif = new Gif("gifs/NotFeelSoGood", "png");
    Gif penguinDieGif = new Gif("gifs/PenguinDieGif", "png");
    Gif penguinGif = new Gif("gifs/PenguinGif", "png");
    Gif penguinRedEyesGif = new Gif("gifs/PenguinRedEyesGif", "png");
    Gif copGif = new Gif("gifs/PoliceGif", "png");
    Icon holeIcon;
    Image gameIcon;

    JFrame frame = new JFrame();
    List<Hole> holes = new ArrayList<>();
    List<Hole> occupiedHoles = new ArrayList<>();
    List<JLabel> gameObjects = new ArrayList<>();
    Gif[] enemyGifs = new Gif[] {
        new Gif("gifs/OrcaGif", "png"),
        new Gif("gifs/PolarBearGif", "png"),
        new Gif("gifs/WalrusGif", "png")
    };

    AudioPlayer music = new AudioPlayer("sounds/Music.wav");

    public void setValues(Properties properties) {
        PLAY_MUSIC = Boolean.parseBoolean(properties.getProperty("PLAY_MUSIC", "true"));
        RED_EYES_CLOSE_GAME_INSTEAD = Boolean.parseBoolean(properties.getProperty("RED_EYES_CLOSE_GAME_INSTEAD", "true"));
        GAME_ACCELERATION = Integer.parseInt(properties.getProperty("GAME_ACCELERATION", "10"));
        MAX_HOLES_PER_TICK = Integer.parseInt(properties.getProperty("MAX_HOLES_PER_TICK", "2"));
        GIF_SPEED = Integer.parseInt(properties.getProperty("GIF_SPEED", "10"));
        timeLeft = Integer.parseInt(properties.getProperty("STARTING_TIME", "90"));
        DISAPPEAR_MULTIPLIER = Integer.parseInt(properties.getProperty("DISAPPEAR_MULTIPLIER", "2"));
        OG_SPEED = Integer.parseInt(properties.getProperty("OG_SPEED", "3000"));
        MAX_SPEED = Integer.parseInt(properties.getProperty("MAX_SPEED", "300"));
        MINIMAl_ADD_TIME = Integer.parseInt(properties.getProperty("MINIMAl_ADD_TIME", "300"));
        RED_EYES_CHANCE = Integer.parseInt(properties.getProperty("RED_EYES_CHANCE", "5"));
        COP_CHANCE = Integer.parseInt(properties.getProperty("COP_CHANCE", "20"));
        NO_MONOCLE_CHANCE = Integer.parseInt(properties.getProperty("NO_MONOCLE_CHANCE", "40"));
        MAX_ENEMY_RANDOM = Integer.parseInt(properties.getProperty("MAX_ENEMY_RANDOM", "100"));
        HUMAN_CHANCE = Integer.parseInt(properties.getProperty("HUMAN_CHANCE", "15"));
        MAX_HIT_RANDOM = Integer.parseInt(properties.getProperty("MAX_HIT_RANDOM", "100"));
        HIT_CHANCE = Integer.parseInt(properties.getProperty("HIT_CHANCE", "60"));
    }

    public Game() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("./settings.properties"));
        setValues(properties);
        holeIcon = new ImageIcon(getClass().getClassLoader().getResource("Images/Hole.png"));
        //mercyImage = ImageIO.read(new File("./rsc/gifs/MercyPic.gif"));
        gameIcon = ImageIO.read(getClass().getClassLoader().getResource(GAME_ICON));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        frame.setTitle(GAME_NAME);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(gameIcon);
        frame.setResizable(false);

        textScore.setBounds(5, 0, 100, 50);
        textScore.setForeground(new Color(255, 0, 255));
        textScore.setFont(new Font("Arial", Font.BOLD, 40));
        textScore.setBorder(null);
        textScore.setEditable(false);
        textScore.setText("0");

        textTimeLeft.setBounds(890, 0, 100, 50);
        textTimeLeft.setForeground(new Color(255, 0, 0));
        textTimeLeft.setFont(new Font("Arial", Font.BOLD, 40));
        textTimeLeft.setBorder(null);
        textTimeLeft.setHorizontalAlignment(JTextField.CENTER);
        textTimeLeft.setEditable(false);
        textTimeLeft.setText(parseTime(timeLeft));

        congratulationsText.setBounds(350, 200, 300, 50);
        congratulationsText.setBackground(new Color(0, 0, 0));
        congratulationsText.setForeground(new Color(0, 0, 255));
        congratulationsText.setFont(new Font("Arial", Font.BOLD, 60));
        congratulationsText.setBorder(null);
        congratulationsText.setHorizontalAlignment(JTextField.CENTER);
        congratulationsText.setEditable(false);

        finalScoreText.setBounds(350, 250, 300, 50);
        finalScoreText.setBackground(new Color(0, 0, 0));
        finalScoreText.setForeground(new Color(0, 0, 255));
        finalScoreText.setFont(new Font("Arial", Font.BOLD, 30));
        finalScoreText.setBorder(null);
        finalScoreText.setHorizontalAlignment(JTextField.CENTER);
        finalScoreText.setEditable(false);

        missesText.setBounds(350, 300, 300, 50);
        missesText.setBackground(new Color(0, 0, 0));
        missesText.setForeground(new Color(0, 0, 255));
        missesText.setFont(new Font("Arial", Font.BOLD, 30));
        missesText.setBorder(null);
        missesText.setHorizontalAlignment(JTextField.CENTER);
        missesText.setEditable(false);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                JLabel holeLabel = new JLabel();
                gameObjects.add(holeLabel);
                holeLabel.setIcon(holeIcon);
                holeLabel.setBounds(100 + j * COLUMN_DISTANCE, 60 + i * ROW_DISTANCE, 130, 150);
                holeLabel.setVerticalAlignment(JLabel.BOTTOM);
                Hole hole = new Hole(holeLabel, this);
                holeLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        hole.onHit();
                    }
                });
                frame.add(holeLabel);
                holes.add(hole);
            }
        }


        frame.add(textTimeLeft);
        frame.add(textScore);
        frame.setVisible(true);
        startGame();
    }
    public void startGame() {
        music.play();
        if (!PLAY_MUSIC) {
            music.stop();
            music = null;
        }
        timer.start();
        gameSpeedUpTimer.start();
        gameTickTimer.start();
    }
    public void gameTick() {
        Random random = new Random();
        int numOfHoles = random.nextInt(MAX_HOLES_PER_TICK) + 1;
        for (int i = 0; i < numOfHoles; i++) {
            if (holes.isEmpty()) break;
            int randomHole = Math.max(0, random.nextInt(holes.size()) - 1); 
            int sucrnd = random.nextInt(100);
            boolean isSuccessful = sucrnd < HIT_CHANCE;
            if (isSuccessful) {
                int hitRandom = random.nextInt(MAX_HIT_RANDOM);
                if (hitRandom < HUMAN_CHANCE) {
                    holes.get(randomHole).activate(HoleType.Human, humanGif, notFeelSoGoodGif);
                } else {
                    holes.get(randomHole).activate(HoleType.GenericHit, penguinGif, penguinDieGif);
                }
            } else {
                int enemyRandom = random.nextInt(MAX_ENEMY_RANDOM);
                if (enemyRandom < RED_EYES_CHANCE) {
                    int rnd = random.nextInt(2);
                    if (rnd == 0) {
                        holes.get(randomHole).activate(HoleType.RedEyes, humanRedEyesGif, null);
                    } else {
                        holes.get(randomHole).activate(HoleType.RedEyes, penguinRedEyesGif, null);
                    }
                } else if (enemyRandom < COP_CHANCE) {
                    holes.get(randomHole).activate(HoleType.Cop, copGif, null);
                } else if (enemyRandom < NO_MONOCLE_CHANCE) {
                    holes.get(randomHole).activate(HoleType.NoMonocle, bingGif, bingHurtGif);
                } else {
                    int rndEnemy = random.nextInt(enemyGifs.length);
                    holes.get(randomHole).activate(HoleType.GenericEnemy, enemyGifs[rndEnemy], null);
                }
            }
        }
    }
    public String parseTime(int seconds) {
        if (seconds < 60) {
            return String.valueOf(seconds);
        } else {
            int minutes = seconds / 60;
            int secondsLeft = seconds % 60;
            if (secondsLeft < 10) {
                return String.format("%d:0%d", minutes, secondsLeft);
            } else {
                return String.format("%d:%d", minutes, secondsLeft);
            }
        }
    }
    public void endGame() {
        timer.stop();
        gameTickTimer.stop();
        gameSpeedUpTimer.stop();
    }
    public void showScoreBusted() {
        gameObjects.forEach(o -> o.setVisible(false));
        congratulationsText.setText("Busted");
        finalScoreText.setText(String.format("Final score: %d", score));
        missesText.setText(String.format("Misses: %d", misses));
        textScore.setVisible(false);
        textTimeLeft.setVisible(false);
        frame.add(congratulationsText);
        frame.add(finalScoreText);
        frame.add(missesText);
    }
    public void showScore() {
        gameObjects.forEach(o -> o.setVisible(false));
        congratulationsText.setText("Why?");
        finalScoreText.setText(String.format("Final score: %d", score));
        missesText.setText(String.format("Misses: %d", misses));
        textScore.setVisible(false);
        textTimeLeft.setVisible(false);
        frame.add(congratulationsText);
        frame.add(finalScoreText);
        frame.add(missesText);
    }
    public void setGameSpeed(int newSpeed) {
        this.gameSpeed = Math.max(newSpeed, MAX_SPEED);
    }
    public void setScore(int newScore) {
        this.score = Math.max(newScore, 0);
        textScore.setText(String.valueOf(score));
    }
}

class Hole {
    private Gif gif;
    private Gif onHitGif;
    private final Game game;
    private final JLabel label;
    private HoleType holeType;
    private Timer appearTimer;
    private Timer executionTimer;
    private boolean isActive = false;

    public JLabel getLabel() {
        return label;
    }

    public Hole(JLabel holeLabel, Game game) {
        this.label = holeLabel;
        this.game = game;
    }

    public void activate(HoleType holeType, Gif mainGif, Gif hitGif) {
        isActive = true;
        this.holeType = holeType;
        this.gif = mainGif;
        this.onHitGif = hitGif;
        game.holes.remove(this);
        game.occupiedHoles.add(this);
        GifThread mercyAppearThread = new GifThread(gif, label, Game.GIF_SPEED);
        mercyAppearThread.start();
        Random random = new Random();
        int additionalTIme = random.nextInt((int)(game.gameSpeed * Game.DISAPPEAR_MULTIPLIER - game.gameSpeed)) + Game.MINIMAl_ADD_TIME;
        GifThread outOfTimeDisappearThread = new GifThread(gif, label, -Game.GIF_SPEED);

        executionTimer = new Timer(outOfTimeDisappearThread.getExecutionTime(), e -> {
            game.holes.add(Hole.this);
            game.occupiedHoles.remove(Hole.this);
        });

        appearTimer = new Timer(additionalTIme + mercyAppearThread.getExecutionTime(), e -> {
            if (isActive) {
                isActive = false;
                outOfTimeDisappearThread.start();
                executionTimer.setRepeats(false);
                executionTimer.start();
            }
        });
        appearTimer.setRepeats(false);
        appearTimer.start();
    }

    public void onHit() {
        if (!isActive) {
            return;
        }
        appearTimer.stop();
        isActive = false;
        GifThread disappearThread = onHitGif != null ? new GifThread(onHitGif, label, Game.GIF_SPEED)
                                                     : new GifThread(gif, label, -Game.GIF_SPEED);
        AudioPlayer hit;
        switch (holeType) {
            case GenericHit:
                hit = new AudioPlayer(Game.SUCCESSFUL_HIT_SOUND);
                hit.play();
                game.setScore(game.score + 1);
                break;
            case Human:
                hit = new AudioPlayer(Game.SUCCESSFUL_HIT_SOUND);
                hit.play();
                game.setScore(game.score + 5);
                break;
            case GenericEnemy:
                hit = new AudioPlayer(Game.FAIL_HIT_SOUND);
                hit.play();
                game.setScore(game.score - 1);
                game.misses++;
                game.setGameSpeed(Game.OG_SPEED);
                break;
            case NoMonocle:
                hit = new AudioPlayer(Game.FAIL_HIT_SOUND);
                hit.play();
                game.setScore(game.score - 2);
                game.misses++;
                game.setGameSpeed(Game.OG_SPEED);
                break;
            case Cop:
                if (game.music != null) {
                    game.music.stop();
                }
                game.endGame();
                game.showScoreBusted();
                break;
            case RedEyes:
                if (Game.RED_EYES_CLOSE_GAME_INSTEAD) {
                    System.exit(0);
                } else {
                    try {
                        String name = System.getProperty("os.name");
                        String[] command = null;
                        if ("Linux".equals(name)  || "Mac OS X".equals(name)) {
                            command = new String[]{
                                    "shutdown",
                                    "-h",
                                    "now"
                            };
                        } else if (name.contains("Windows")) {
                            command = new String[]{
                                    "shutdown",
                                    "-s",
                                    "-t",
                                    "0"
                            };
                        } else {
                            System.out.println("Unsupported OS, closing the game instead");
                            System.exit(0);
                        }
                        Runtime.getRuntime().exec(command);
                    } catch (IOException e) {
                        System.out.println("Can't shutdown, closing the game instead");
                    }
                    System.exit(0);
                }
                break;
        }
        disappearThread.start();
        Timer returnTimer = new Timer(disappearThread.getExecutionTime(), e -> {
            game.holes.add(Hole.this);
            game.occupiedHoles.remove(Hole.this);
        });
        returnTimer.setRepeats(false);
        returnTimer.start();
    }
}
