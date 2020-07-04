package com.alexianhentiu;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.*;

public class GUI {

    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());

    private static final File USER_FILES = new File(System.getProperty("user.home") +
            "/TicTacToe - Swing Edition - User Files");

    private static String TITLE = "TicTacToe - Swing Edition - 1.0";
    private static String mainMenuTitle = " - MAIN MENU";
    private static String gameType1 = "1 PLAYER GAME";
    private static String gameType2 = "2 PLAYERS GAME";
    private static String howToPlay = "HOW TO PLAY";
    private static String settingsTitle = "SETTINGS";
    private static String difficulty = "Medium";
    private static String currentLanguage = "English";
    private static String onText = "ENABLED";
    private static String offText = "DISABLED";

    private static boolean customName = true;
    private static boolean bgMusic = true; //background music status indicator
    private static boolean soundFX = true;
    private static boolean end = false; //true if game ends; resets when starting new game
    private static boolean singlePlayer = false;

    private static int turn = 1; //keep track of the general turn
    private static int turnX = 0; //keep track of player1's turn
    private static int turn0 = 0; //keep track of player2's turn

    private static double gain; //numeric value for volume
    private static double currentGain;

    private static float dB; //decibel value for volume

    private static File[] sounds = new File[15]; //sound files
    private static Clip[] clips = new Clip[15]; //sound clips

    private static FloatControl control;
    //volume control

    private static JSlider volumeSlider;

    private static JFrame frame;

    private static JPanel panel; //main panel
    private static JPanel gamePanel; //secondary panel
    private static JPanel grid; //3x3 grid
    private static JPanel settings; //setings menu panel

    private static JLabel titleLabel;
    private static JLabel authorLabel;
    private static JLabel turnText; //shows who's turn it is
    private static JLabel instructions; //for tutorial text
    private static JLabel tutorial; //for tutorial image
    private static JLabel musicVolume;
    private static JLabel musicLabel;
    private static JLabel soundFxLabel;
    private static JLabel toggleLabel; //custom names toggle label
    private static JLabel selectorLabel; //difficulty selector label
    private static JLabel languageLabel;

    private static JButton[] buttons = { new JButton("1 Player"), new JButton("2 Players"),
            new JButton("How to play?"), new JButton("Settings"), new JButton("Exit") };
    private static JButton[] gridSpaces = new JButton[9]; //squares from 3x3 grid
    private static JButton confirm;
    private static JButton menuButton; //button for returning to the main menu

    private static JToggleButton musicToggle;
    private static JToggleButton soundFXToggle;
    private static JToggleButton toggle; //custom names toggle

    private static JComboBox<String> selector; //difficulty selector
    private static JComboBox<String> languageSelector;

    private static ImageIcon iconBlank; //empty grid icon

    private static List<ImageIcon> listX; //X models
    private static List<ImageIcon> list0; //0 models

    public GUI() throws InterruptedException {
        if (!USER_FILES.exists()) {
            //noinspection ResultOfMethodCallIgnored
            USER_FILES.mkdir();
        }

        checkLogLimit();
        try {
            setUpLogger();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("An error occurred while creating the logger.");
        }

        getSoundFiles();
        playSound(sounds[5], 5); //intro sound
        Thread.sleep(clips[5].getMicrosecondLength() / 2000);

        //create the actual GUI
        frame = new JFrame();
        panel = new JPanel(null);
        frame.add(panel);
        panel.setBackground(new Color(127, 64, 62));
        frame.setTitle(TITLE + mainMenuTitle);
        frame.setSize(500, 400);
        frame.setResizable(false);
        frame.setLocation(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setEnabled(false);
        frame.setVisible(true);

        titleLabel = new JLabel("Tic Tac Toe");
        titleLabel.setBounds(40, 10, 400, 70);
        titleLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 60));
        panel.add(titleLabel);

        authorLabel = new JLabel("By Alexian Hențiu");
        authorLabel.setBounds(270, 80, 300, 35);
        authorLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 26));
        authorLabel.setForeground(Color.gray);
        panel.add(authorLabel);

        //set up main menu buttons
        int yIndex = 120;
        for (JButton button : buttons) {
            button.setBounds(90, yIndex, 300, 25);
            button.setBackground(new Color(0x7F5B5A));
            button.setForeground(Color.lightGray);
            button.setBorder(new LineBorder(new Color(0xA15757), 3, false));
            button.addActionListener(e -> {
                if (soundFX && !button.getText().equals("Exit") && !button.getText().equals("Ieșire")) {
                    playSound(sounds[2], 2);
                }
            });
            panel.add(button);
            yIndex += 50;
        }

        gamePanel = new JPanel(null);
        gamePanel.setBackground(new Color(127, 64, 62));
        frame.add(gamePanel);
        gamePanel.setVisible(false);

        settings = new JPanel(null);
        settings.setBounds(50, 50, 440, 440);
        settings.setBackground(new Color(127, 64, 62));
        gamePanel.add(settings);
        settings.setVisible(false);

        musicLabel = new JLabel("Background music");
        musicLabel.setBounds(0, 10, 200, 30);
        musicLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        musicLabel.setForeground(Color.LIGHT_GRAY);
        settings.add(musicLabel);

        musicToggle = new JToggleButton(onText);
        musicToggle.setBounds(230, 10, 100, 30);
        musicToggle.setBackground(new Color(0xDCA1A0));
        musicToggle.setForeground(Color.WHITE);
        musicToggle.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        musicToggle.setSelected(false);
        musicToggle.addItemListener(e -> {
            if (!soundFXToggle.isSelected()) {
                playSound(sounds[10], 10);
            }
            if(!musicToggle.isSelected()) {
                musicToggle.setText(onText);
                musicToggle.setForeground(Color.WHITE);
                if (!clips[7].isRunning()) {
                    clips[7].start();
                    clips[7].loop(Clip.LOOP_CONTINUOUSLY);
                }
            } else {
                musicToggle.setText(offText);
                musicToggle.setForeground(Color.BLACK);
                if (clips[7].isRunning()) {
                    playSound(sounds[8], 8);
                    clips[7].stop();
                }
            }

        });
        settings.add(musicToggle);

        musicVolume = new JLabel("Music volume");
        musicVolume.setBounds(0, 50, 200, 30);
        musicVolume.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        musicVolume.setForeground(Color.LIGHT_GRAY);
        settings.add(musicVolume);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
        volumeSlider.setBounds(180, 50, 200, 30);
        volumeSlider.setBackground(new Color(0xDCA1A0));
        volumeSlider.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting() && sounds[7].exists()) {
                gain = (double) volumeSlider.getValue() / 10;
                dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
                control.setValue(dB);
            }
        });
        settings.add(volumeSlider);

        soundFxLabel = new JLabel("Sound effects");
        soundFxLabel.setBounds(0, 90, 200, 30);
        soundFxLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        soundFxLabel.setForeground(Color.LIGHT_GRAY);
        settings.add(soundFxLabel);

        soundFXToggle = new JToggleButton(onText);
        soundFXToggle.setBounds(230, 90, 100, 30);
        soundFXToggle.setBackground(new Color(0xDCA1A0));
        soundFXToggle.setForeground(Color.WHITE);
        soundFXToggle.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        soundFXToggle.setSelected(false);
        soundFXToggle.addItemListener(e -> {
            if(!soundFXToggle.isSelected()) {
                soundFXToggle.setText(onText);
                soundFXToggle.setForeground(Color.WHITE);
            } else {
                playSound(sounds[10], 10);
                soundFXToggle.setText(offText);
                soundFXToggle.setForeground(Color.BLACK);
            }

        });
        settings.add(soundFXToggle);

        toggleLabel = new JLabel("Custom player names");
        toggleLabel.setBounds(0, 130, 200, 30);
        toggleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        toggleLabel.setForeground(Color.LIGHT_GRAY);
        settings.add(toggleLabel);

        toggle = new JToggleButton(onText);
        toggle.setBounds(230, 130, 100, 30);
        toggle.setBackground(new Color(0xDCA1A0));
        toggle.setForeground(Color.WHITE);
        toggle.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        toggle.setSelected(false);
        toggle.addItemListener(e -> {
            if (!soundFXToggle.isSelected()) {
                playSound(sounds[10], 10);
            }
            if(!toggle.isSelected()) {
                toggle.setText(onText);
                toggle.setForeground(Color.WHITE);
            } else {
                toggle.setText(offText);
                toggle.setForeground(Color.BLACK);
            }

        });
        settings.add(toggle);

        selectorLabel = new JLabel("Singleplayer game difficulty");
        selectorLabel.setBounds(0, 170, 200, 30);
        selectorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        selectorLabel.setForeground(Color.LIGHT_GRAY);
        settings.add(selectorLabel);

        selector = new JComboBox<>(); //set up difficulty selector
        selector.setBackground(new Color(0xB08180));
        selector.setForeground(Color.WHITE);
        selector.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        selector.addItem("Easy");
        selector.addItem("Medium");
        selector.addItem("Hard");
        selector.setBounds(230, 170, 100, 30);
        selector.setSelectedIndex(1);
        settings.add(selector);

        languageLabel = new JLabel("Language");
        languageLabel.setBounds(0, 210, 200, 30);
        languageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        languageLabel.setForeground(Color.LIGHT_GRAY);
        settings.add(languageLabel);

        languageSelector = new JComboBox<>();
        languageSelector.setBackground(new Color(0xB08180));
        languageSelector.setForeground(Color.WHITE);
        languageSelector.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        languageSelector.addItem("English");
        languageSelector.addItem("Română");
        languageSelector.setBounds(230, 210, 100, 30);
        languageSelector.setSelectedIndex(0);
        settings.add(languageSelector);

        confirm = new JButton("CONFIRM");
        confirm.setBounds(140, 260, 90, 30);
        confirm.setBackground(new Color(0x7F5B5A));
        confirm.setForeground(Color.lightGray);
        confirm.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        confirm.addMouseListener(new java.awt.event.MouseAdapter() {
            //hover effect
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                confirm.setBackground(new Color(0xB08180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                confirm.setBackground(new Color(0x7F5B5A));
            }
        });
        confirm.addActionListener(e -> {
            currentGain = gain;
            customName = (toggle.getText().equals(onText));
            difficulty = setProperDifficultyValue();
            if (!currentLanguage.equals(languageSelector.getItemAt(languageSelector.getSelectedIndex()))) {
                currentLanguage = languageSelector.getItemAt(languageSelector.getSelectedIndex());
                changeLanguage();
            }
            bgMusic = clips[7].isRunning();
            soundFX = (soundFXToggle.getText().equals(onText));
            if (soundFX) {
                playSound(sounds[9], 9);
            }
            backToMainMenu();
        });
        settings.add(confirm);

        instructions = new JLabel("- Click on a empty space to place an X or a 0. Get 3 in a line to win.");
        instructions.setForeground(Color.LIGHT_GRAY);
        instructions.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        instructions.setBounds(10, 50, 500, 30);

        addTutorialImage();

        turnText = new JLabel("Turn - Player1");
        turnText.setForeground(Color.LIGHT_GRAY);
        turnText.setBounds(10, 3, 200, 20);
        turnText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

        grid = new JPanel(new GridLayout(3, 3));
        grid.setBounds(115, 65, 250, 250);
        gamePanel.add(grid);
        grid.setVisible(false);

        for (int i = 0; i < 9; i++) {
            gridSpaces[i] = new JButton("", iconBlank);  //add buttons to the 3x3 grid
            int index = i;
            gridSpaces[i].addActionListener(e -> {
                if (soundFX && !end)
                playSound(sounds[13], 13);
                Main.next(index);
            });
            grid.add(gridSpaces[i], i);
        }

        setGridImages();

        //configure main menu buttons actions
        buttons[0].addActionListener(e -> choose(Option.ONE_PLAYER_GAME));
        buttons[1].addActionListener(e -> choose(Option.TWO_PLAYERS_GAME));
        buttons[2].addActionListener(e -> choose(Option.INSTRUCTIONS));
        buttons[3].addActionListener(e -> {
            loadCurrentSettings();
            choose(Option.SETTINGS);
        });
        buttons[4].addActionListener(e -> {
            try {
                displayExitPrompt();
            } catch (InterruptedException f) {
                LOGGER.info("The outro was interrupted.");
                LOGGER.warning(f.getMessage());
            }
        });

        menuButton = new JButton("Menu");
        menuButton.setBounds(415, 3, 65, 25);
        menuButton.setBackground(new Color(0x7F5B5A));
        menuButton.setForeground(Color.lightGray);
        menuButton.setBorder(new LineBorder(new Color(0xA15757), 3, false));
        menuButton.addMouseListener(new java.awt.event.MouseAdapter() {
            //hover effect
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuButton.setBackground(new Color(0xB08180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuButton.setBackground(new Color(0x7F5B5A));
            }
        });
        menuButton.addActionListener(e -> {
            if (!settings.isVisible() && soundFX) {
                playSound(sounds[3], 3);
            } else if (settings.isVisible() && soundFXToggle.getText().equals(onText)) {
                playSound(sounds[3], 3);
            }
            backToMainMenu();
        });
        gamePanel.add(menuButton);

        //add hover effect to main menu buttons
        for (JButton button : buttons) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(0xB08180));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(0x7F5B5A));
                }
            });
        }

        gain = 0.50; // volume level [0,1]
        currentGain = gain;


        loadSettingsPreset();

        //start background music
        if (bgMusic && sounds[7].exists()) {
            playSound(sounds[7], 7);
            clips[7].loop(Clip.LOOP_CONTINUOUSLY);
            control = (FloatControl) clips[7].getControl(FloatControl.Type.MASTER_GAIN);
            dB = (float) (Math.log(currentGain) / Math.log(10.0) * 20.0);
            control.setValue(dB);
        } else  if (sounds[7].exists()) {
            playSound(sounds[7], 7);
            clips[7].stop();
            control = (FloatControl) clips[7].getControl(FloatControl.Type.MASTER_GAIN);
        } else {
            try {
                clips[7] = AudioSystem.getClip();
                control = (FloatControl) clips[7].getControl(FloatControl.Type.MASTER_GAIN);
            } catch (LineUnavailableException | IllegalArgumentException e) {
                LOGGER.info("Check background_music file.");
                LOGGER.warning(e.getMessage());
            }
        }

        frame.setEnabled(true);
    }

    private static void checkLogLimit() {
        //ensures no more than 10 logs are stored by deleting old ones
        File logsFolder = new File(USER_FILES + "/Logs");
        if (!logsFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            logsFolder.mkdir();
        }
        Queue<File> logs = new LinkedList<>();
        try {
            for (String fileName : Objects.requireNonNull(logsFolder.list())) {
                if (fileName.contains(".lck")) {
                    new File(logsFolder + "/" + fileName).deleteOnExit();
                } else {
                    logs.add(new File(logsFolder + "/" + fileName));
                }
            }
            while (logs.size() > 9) {
                logs.poll().deleteOnExit();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("An error occurred while checking the Logs folder.");
        }
    }

    private static void setUpLogger() throws IOException {
        LOGGER.setLevel(Level.ALL);
        String logFileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS'.txt'").format(new Date());
        FileHandler fileTxt = new FileHandler(USER_FILES + "/Logs/Log_" + logFileName);
        fileTxt.setFormatter(new SimpleFormatter());
        fileTxt.setLevel(Level.ALL);
        LOGGER.addHandler(fileTxt);
    }

    private static void getSoundFiles() {
        sounds[0] = new File("Resources/Audio/audience_clapping.wav");
        sounds[1] = new File("Resources/Audio/kids_cheering.wav");
        sounds[2] = new File("Resources/Audio/open.wav");
        sounds[3] = new File("Resources/Audio/close.wav");
        sounds[4] = new File("Resources/Audio/oof.wav");
        sounds[5] = new File("Resources/Audio/intro.wav");
        sounds[6] = new File("Resources/Audio/sad_trombone.wav");
        sounds[7] = new File("Resources/Audio/background_music.wav");
        sounds[8] = new File("Resources/Audio/dj_stop.wav");
        sounds[9] = new File("Resources/Audio/punch.wav");
        sounds[10] = new File("Resources/Audio/switch.wav");
        sounds[11] = new File("Resources/Audio/outro.wav");
        sounds[12] = new File("Resources/Audio/party.wav");
        sounds[13] = new File("Resources/Audio/scribble.wav");
        sounds[14] = new File("Resources/Audio/draw_fx.wav");
    }

    private static void playSound(File sound, int index) { //Clip clip = new Clip();
        try {
            clips[index] = AudioSystem.getClip();
            clips[index].open(AudioSystem.getAudioInputStream(sound));
            clips[index].start();
        } catch (Exception e) {
            LOGGER.warning("Check the sound file for " + sound.getName() + ".");
        }
    }

    private static void addTutorialImage() {
        tutorial = new JLabel();
        tutorial.setBounds(115, 90, 250, 250);
        try {
            tutorial.setIcon(new ImageIcon(ImageIO.read(new File("Resources/Images/tutorial.png"))
            .getScaledInstance(250, 250, Image.SCALE_DEFAULT)));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.info("Check the tutorial image.");
        }
    }

    private static void setGridImages() {
        try {
            iconBlank = new ImageIcon(ImageIO.read(new File("Resources/Images/button_blank.jpg"))
                    .getScaledInstance(90, 90, Image.SCALE_DEFAULT));
            listX = new ArrayList<>();
            list0 = new ArrayList<>();
            //get the 5 "X" models and 4 "0" models
            for (int i = 1; i  <= 5; i++) {
                listX.add(new ImageIcon(ImageIO.read(new File("Resources/Images/button_X/button_X_"
                        + i + ".jpg")).getScaledInstance(90, 90, Image.SCALE_DEFAULT)));
                if (i == 5) {
                    break;
                }
                list0.add(new ImageIcon(ImageIO.read(new File("Resources/Images/button_0/button_0_"
                        + i + ".jpg")).getScaledInstance(90, 90, Image.SCALE_DEFAULT)));
            }
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.info("Check the button_X and button_0 icons.");
            iconBlank = new ImageIcon();
            listX = new ArrayList<>();
            list0 = new ArrayList<>();
        }
    }

    private static void randomizeIcons(List<ImageIcon> icons) {
        Random random = new Random();
        for (int i = icons.size() - 1; i >= 1; i--) {
            int j = random.nextInt(i + 1);
            ImageIcon icon = icons.get(i);
            icons.set(i, icons.get(j));
            icons.set(j, icon);
        }
    }

    private String setProperDifficultyValue() {
        //keep the same difficulty format regardless of language
        if (selector.getSelectedIndex() == 0) {
            return "Easy";
        } else if (selector.getSelectedIndex() == 1) {
            return "Medium";
        }
        return "Hard";
    }

    private void choose(Option opt) {
        panel.setVisible(false);
        gamePanel.setVisible(true);
        switch (opt) {
            case ONE_PLAYER_GAME:
                singlePlayer = true;
                initiate(gameType1);
                if (difficulty.equals("Hard")) {
                    new HardGame();
                } else {
                    Main.play(1);
                }
                break;
            case TWO_PLAYERS_GAME:
                singlePlayer = false;
                gamePanel.add(turnText);
                initiate(gameType2);
                Main.play(2);
                break;
            case INSTRUCTIONS:
                frame.setTitle(TITLE + " - " + howToPlay);
                gamePanel.add(instructions);
                gamePanel.add(tutorial);
                break;
            case SETTINGS:
                frame.setTitle(TITLE + " - " + settingsTitle);
                settings.setVisible(true);
        }
    }

    private static void initiate(String gameType) {
        //call when starting a new game
        end = false;
        randomizeIcons(listX);
        randomizeIcons(list0);
        for (int i = 0; i < 9; i++) {
            int index = i;
            gridSpaces[i].addActionListener(e -> {
                if (gameType.equals(gameType1) && difficulty.equals("Hard")) {
                    HardGame.next(index);
                } else {
                    Main.next(index);
                }
            });
        }
        turnX = 0;
        turn0 = 0;
        turn = 1;
        turnText.setText(currentLanguage.equals("English") ? "Turn - Player1" : "Rândul lui Jucător1");
        frame.setTitle(TITLE + " - "+ gameType);
        grid.setVisible(true);
    }

    private static void backToMainMenu() {
        singlePlayer = false;
        if (sounds[7].exists()) {
            dB = (float) (Math.log(currentGain) / Math.log(10.0) * 20.0);
            control.setValue(dB);
        }
        if (bgMusic) {
            clips[7].start();
            clips[7].loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clips[7].stop();
        }
        settings.setVisible(false);
        frame.setTitle(TITLE + mainMenuTitle);
        grid.setVisible(false);
        gamePanel.remove(turnText);
        gamePanel.remove(instructions);
        gamePanel.remove(tutorial);
        gamePanel.setVisible(false);
        panel.setVisible(true);
    }

    private static void loadCurrentSettings() {
        //ensure settings page remains up to date
        volumeSlider.setValue((int) (currentGain * 10));
        for (int i = 0 ; i < selector.getItemCount(); i++) {
            if (selector.getItemAt(i).equals(difficulty)) {
                selector.setSelectedIndex(i);
            }
        }
        for (int i = 0 ; i < languageSelector.getItemCount(); i++) {
            if (languageSelector.getItemAt(i).equals(currentLanguage)) {
                languageSelector.setSelectedIndex(i);
            }
        }
        if (customName) {
            toggle.setText(onText);
            toggle.setSelected(false);
        } else {
            toggle.setText(offText);
            toggle.setSelected(true);
        }
        if (bgMusic) {
            musicToggle.setText(onText);
            musicToggle.setSelected(false);
        } else {
            musicToggle.setText(offText);
            musicToggle.setSelected(true);
        }
        if (soundFX) {
            soundFXToggle.setText(onText);
            soundFXToggle.setSelected(false);
        } else {
            soundFXToggle.setText(offText);
            soundFXToggle.setSelected(true);
        }
    }

    private static void saveSettingsPreset() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File(USER_FILES + "/settings_preset.zz")), StandardCharsets.UTF_8))) {
            writer.write(bgMusic + "\n");
            writer.write(volumeSlider.getValue() + "\n");
            writer.write(soundFX + "\n");
            writer.write(customName + "\n");
            writer.write(difficulty + "\n");
            if (currentLanguage.equals("English")) {
                writer.write(currentLanguage + "\n");
            } else {
                writer.write("Romana" + "\n");
            }
            writer.write("DO NOT MODIFY ANYTHING ABOVE THIS LINE IN ORDER FOR THE SAVE TO WORK.\n");
            writer.write("OTHERWISE, THE DEFAULT SETTINGS WILL BE USED AND A NEW FILE " +
                    "WILL BE CREATED AFTER EXITING THE PROGRAM.");
            LOGGER.info("Saving was successful");
            logCurrentPreset(bgMusic, (int) (currentGain * 10), soundFX, customName,
                    difficulty, currentLanguage, "Saved");
        } catch (IOException e) {
            LOGGER.info("Error while saving.");
            LOGGER.warning(e.getMessage());
        }
    }

    private static void loadSettingsPreset() {
        try {
            File file = new File(USER_FILES + "/settings_preset.zz");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String[] line = new String[6];
            for (int i = 0; i < 6; i++) {
                line[i] = reader.readLine();
                if (line[i] == null) {
                    throw new IOException();
                }
            }
            if (!line[4].equals("Easy") && !line[4].equals("Medium") && !line[4].equals("Hard")) {
                throw new IOException();
            }
            if (!line[5].equals("English") && !line[5].equals("Romana")) {
                throw new IOException();
            }
            if (!line[0].equals("false") && !line[0].equals("true")) {
                throw new IOException();
            }
            boolean isIntInBounds = false;
            for (int i = 0; i <= 10; i++) {
                if (line[1].equals(String.valueOf(i))) {
                    isIntInBounds = true;
                    break;
                }
            }
            if (!isIntInBounds) {
                throw new IOException();
            }
            if (!line[2].equals("false") && !line[2].equals("true")) {
                throw new IOException();
            }
            if (!line[3].equals("false") && !line[3].equals("true")) {
                throw new IOException();
            }
            boolean music = Boolean.parseBoolean(line[0]);
            int volumeLevel = Integer.parseInt(line[1]);
            boolean sound = Boolean.parseBoolean(line[2]);
            boolean name = Boolean.parseBoolean(line[3]);
            LOGGER.info("Successfully read save file.");
            logCurrentPreset(music, volumeLevel, sound, name, line[4], line[5], "Loaded");
            applySettingsPreset(music, volumeLevel, sound, name, line[4], line[5]);
        } catch (IOException e) {
            LOGGER.warning("Save file damaged or nonexistent.");
            LOGGER.info("Using default settings.");
        }
    }

    private static void applySettingsPreset(boolean bgMusic, int volumeLevel, boolean soundFX, boolean customName,
                                            String difficulty, String currentLanguage) {
        GUI.bgMusic = bgMusic;
        GUI.currentGain = (double) volumeLevel / 10;
        GUI.soundFX = soundFX;
        GUI.customName = customName;
        GUI.difficulty = difficulty;
        if (currentLanguage.equals("English")) {
            GUI.currentLanguage = currentLanguage;
        } else {
            GUI.currentLanguage = "Română";
        }
        changeLanguage();
    }

    private static void logCurrentPreset(boolean bgMusic, int volumeLevel, boolean soundFX, boolean customName,
                                         String difficulty, String currentLanguage, String type) {
        LOGGER.info(type + " preset is: " +
                "Music: " + bgMusic +
                ", Volume: " + volumeLevel +
                ", Sound FX: " + soundFX +
                ", Custom names: " + customName +
                ", Difficulty: " + difficulty +
                ", Language: " + currentLanguage);
    }

    private static void changeLanguage() {
        if (currentLanguage.equals("Română")) {
            setRo();
        } else {
            setEng();
        }
    }

    private static void setRo() {
        TITLE = "X și 0 - Ediția Swing - 1.0";
        buttons[0].setText("1 jucător");
        buttons[1].setText("2 jucători");
        buttons[2].setText("Cum se joacă?");
        buttons[3].setText("Setări");
        buttons[4].setText("Ieșire");
        mainMenuTitle = " - MENIU PRINCIPAL";
        frame.setTitle(TITLE + mainMenuTitle);
        titleLabel.setText("X și 0");
        titleLabel.setBounds(135, 10, 220, 70);
        titleLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 60));
        authorLabel.setText("De Hențiu Alexian");
        musicLabel.setText("Muzică de fundal");
        musicVolume.setText("Volum muzică");
        soundFxLabel.setText("Efecte sonore");
        toggleLabel.setText("Alege nume jucători");
        onText = "PORNIT";
        offText = "OPRIT";
        musicToggle.setText(!musicToggle.isSelected() ? onText : offText);
        soundFXToggle.setText(!soundFXToggle.isSelected() ? onText : offText);
        toggle.setText(!toggle.isSelected() ? onText : offText);
        selectorLabel.setText("Dificultate mod \"1 jucător\"");
        selectCorrectDifficultyOption("Ușor", "Mediu", "Greu");
        languageLabel.setText("Limbă");
        confirm.setText("Confirmă");
        instructions.setText("- Click pe un pătrat gol pentru a plasa un X sau un 0. " +
                "Obține 3 pe o line pentru a căștiga.");
        instructions.setBounds(5, 50, 500, 30);
        instructions.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        turnText.setText("Rândul lui Jucător1");
        menuButton.setText("Meniu");
        menuButton.setBounds(410, 3, 70, 25);
        gameType1 = "JOC DE UNUL SINGUR";
        gameType2 = "JOC ÎN 2";
        howToPlay = "CUM SE JOACĂ?";
        settingsTitle = "SETĂRI";
    }

    private static void setEng() {
        TITLE = "Tic Tac Toe - Swing Edition - 1.0";
        buttons[0].setText("1 Player");
        buttons[1].setText("2 Players");
        buttons[2].setText("How to play?");
        buttons[3].setText("Settings");
        buttons[4].setText("Exit");
        mainMenuTitle = " - MAIN MENU";
        titleLabel.setText("Tic Tac Toe");
        titleLabel.setBounds(40, 10, 400, 70);
        titleLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 60));
        authorLabel.setText("By Alexian Hențiu");
        musicLabel.setText("Background music");
        musicVolume.setText("Music volume");
        soundFxLabel.setText("Sound effects");
        toggleLabel.setText("Custom player names");
        onText = "ENABLED";
        offText = "DISABLED";
        musicToggle.setText(!musicToggle.isSelected() ? onText : offText);
        soundFXToggle.setText(!soundFXToggle.isSelected() ? onText : offText);
        toggle.setText((!toggle.isSelected()) ? onText : offText);
        selectorLabel.setText("Singleplayer game difficulty");
        selectCorrectDifficultyOption("Easy", "Medium", "Hard");
        languageLabel.setText("Language");
        confirm.setText("Confirm");
        instructions.setText("- Click on a empty space to place an X or a 0. Get 3 in a line to win.");
        instructions.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        instructions.setBounds(10, 50, 500, 30);
        turnText.setText("Turn - Player1");
        menuButton.setText("Menu");
        menuButton.setBounds(415, 3, 65, 25);
        gameType1 = "1 PLAYER GAME";
        gameType2 = "2 PLAYERS GAME";
        howToPlay = "HOW TO PLAY?";
        settingsTitle = "SETTINGS";
    }

    private static void selectCorrectDifficultyOption(String dif1, String dif2, String dif3) {
        //ensure the same difficulty is selected in the settings menu regardless of language
        selector.removeAllItems();
        selector.addItem(dif1);
        selector.addItem(dif2);
        selector.addItem(dif3);
        if (difficulty.equals("Easy")) {
            selector.setSelectedIndex(0);
        } else if (difficulty.equals("Medium")) {
            selector.setSelectedIndex(1);
        } else {
            selector.setSelectedIndex(2
            );
        }
    }

    private static void loadWinnerImages(String sign) {
        ImageIcon icon = getWinIcon(sign);
        gridSpaces[Main.getWinnerIndexes()[0]].setIcon(icon);
        gridSpaces[Main.getWinnerIndexes()[1]].setIcon(icon);
        gridSpaces[Main.getWinnerIndexes()[2]].setIcon(icon);
    }

    private static ImageIcon getWinIcon(String sign) {
        try {
            return new ImageIcon(ImageIO.read(new File("Resources/Images/button_" + sign
                    + "/button_" + sign + "_win.jpg")).getScaledInstance(90, 90, Image.SCALE_DEFAULT));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.info("Check win icon for " + sign + ".");
            return new ImageIcon();
        }
    }

    private static void displayExitPrompt() throws InterruptedException {
        String message = currentLanguage.equals("English") ? "Do you want to save the current settings preset?" :
                "Dorești să salvezi setările actuale?";
        UIManager.put("OptionPane.cancelButtonText", currentLanguage.equals("English") ? "Cancel" : "Anulează");
        UIManager.put("OptionPane.noButtonText", currentLanguage.equals("English") ? "No" : "Nu");
        UIManager.put("OptionPane.yesButtonText", currentLanguage.equals("English") ? "Yes" : "Da");
        int dialogResult = JOptionPane.showConfirmDialog(frame, message, currentLanguage.equals("English") ?
                        "Warning" : "Atenție", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        switch (dialogResult) {
            case 0:
                saveSettingsPreset();
            case 1:
                if (bgMusic) {
                    clips[7].stop();
                }
                playSound(sounds[11], 11);
                Thread.sleep(clips[11].getMicrosecondLength() / 1000);
                System.exit(0);
                break;
        }
    }

    private static void playWinOrLossSound(Player winner) {
        if (soundFX) {
            if (winner.getClass().toString().contains("BotPlayer") && difficulty.equals("Easy")) {
                playSound(sounds[4], 4);
            } else if (winner.getClass().toString().contains("BotPlayer")) {
                playSound(sounds[6], 6);
            } else if (winner.getClass().toString().contains("HumanPlayer") && singlePlayer
                    && difficulty.equals("Easy")) {
                playSound(sounds[1], 1);
            } else if (winner.getClass().toString().contains("HumanPlayer") && singlePlayer
                    && difficulty.equals("Hard")) {
                playSound(sounds[0], 0);
            } else {
                playSound(sounds[12], 12);
            }
        }
    }

    private static void playDrawSound() {
        if (soundFX) {
            playSound(sounds[14], 14);
        }
    }

    public static void prepareNewGrid() {
        for (int i = 0; i < 9; i++) {
            gridSpaces[i].setText("");
            gridSpaces[i].setIcon(iconBlank);
        }
    }

    public static void setNextTurn(Player player1, Player player2) {
        //set player [name]'s turn text or call next bot move
        if (player2.getClass().toString().contains("BotPlayer") && turn % 2 == 0) {
            Main.nextBotMove();
        } else if (player2.getClass().toString().contains("HumanPlayer")){
            String playerName = (turn % 2 != 0) ? player1.getName() : player2.getName();
            turnText.setText(currentLanguage.equals("English") ? "Turn - " + playerName : "Rândul lui " + playerName);
        }
    }

    public static String getNextMove(int index) {
        //get X or 0 depending on who's turn it is
        if (turn++ % 2 != 0) {
            gridSpaces[index].setIcon(listX.get(turnX++));
            return "X";
        }
        gridSpaces[index].setIcon(list0.get(turn0++));
        return "0";
    }

    public static JButton getGridSpace(int index) {
        return gridSpaces[index];
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static void showPopup(String type, Player winner, String sign) {
        //display end of game message box
        end = true;
        String message;
        if (type.equals("win")) {
            playWinOrLossSound(winner);
            loadWinnerImages(sign);
            if (winner.getName().equals("Tu") && currentLanguage.equals("Română")) {
                message = winner.getName() + " ai câștigat!";
            } else {
                message = winner.getName() + (currentLanguage.equals("English") ? " won the game!" : " a câștigat!");
            }
        } else {
            playDrawSound();
            message = currentLanguage.equals("English") ? "It's a draw" : "Remiză";
        }
        String title = currentLanguage.equals("English") ? "Message" : "Mesaj";
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
        backToMainMenu();
    }

    public static boolean isCustomName() {
        return customName;
    }

    public static String getDifficulty() {
        return difficulty;
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }
}
