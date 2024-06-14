import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;

public class Game extends JFrame {
    private final JLabel backgroundLabel;
    private final JLabel playerCastleLabel;
    private final JLabel enemyCastleLabel;
    private final JLabel highscoreLabel;
    private final JLabel waveCountLabel;
    private final JLabel livesLabel;
    private int highscore;
    private int waveCount;
    private int enemySpeed;
    private int lives;
    private boolean isPaused;
    private final ArrayList<Enemy> enemies;
    private int activeEnemies;
    private String playerName;
    private final JLabel playerNameLabel;
    private final JTextField playerNameField;
    private final JButton startButton;
    private int defeatedEnemiesCount;

    public Game() {
        setTitle("Super Gierka");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(null);
        add(gamePanel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int boardWidth = screenSize.width;
        int boardHeight = screenSize.height;

        ImageIcon backgroundImage = new ImageIcon("background.jpg");
        Image scaledBackgroundImage = backgroundImage.getImage().getScaledInstance(boardWidth, boardHeight, Image.SCALE_SMOOTH);
        backgroundLabel = new JLabel(new ImageIcon(scaledBackgroundImage));
        backgroundLabel.setBounds(0, 0, boardWidth, boardHeight);
        gamePanel.add(backgroundLabel);

        ImageIcon playerCastleImage = new ImageIcon("player_castle.png");
        int playerCastleWidth = 300;
        int playerCastleHeight = 450;
        playerCastleLabel = new JLabel(playerCastleImage);
        playerCastleLabel.setBounds(0, boardHeight / 2 - playerCastleHeight / 2, playerCastleWidth, playerCastleHeight);
        backgroundLabel.add(playerCastleLabel);

        ImageIcon enemyCastleImage = new ImageIcon("enemy_castle.png");
        int enemyCastleWidth = 300;
        int enemyCastleHeight = 450;
        enemyCastleLabel = new JLabel(enemyCastleImage);
        enemyCastleLabel.setBounds(boardWidth - enemyCastleWidth, boardHeight / 2 - enemyCastleHeight / 2, enemyCastleWidth, enemyCastleHeight);
        backgroundLabel.add(enemyCastleLabel);

        highscore = 0;
        highscoreLabel = new JLabel("Highscore: " + highscore);
        highscoreLabel.setBounds(10, 10, 150, 30);
        highscoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        backgroundLabel.add(highscoreLabel);

        waveCount = 1;
        waveCountLabel = new JLabel("Wave: " + waveCount);
        waveCountLabel.setBounds(boardWidth / 2 - 50, 10, 100, 30);
        waveCountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        backgroundLabel.add(waveCountLabel);

        lives = 3;
        livesLabel = new JLabel("Lives: " + lives);
        livesLabel.setBounds(boardWidth - 100, 10, 80, 30);
        livesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        backgroundLabel.add(livesLabel);

        enemySpeed = 5;
        isPaused = false;

        enemies = new ArrayList<>();
        activeEnemies = 0;

        playerNameLabel = new JLabel("Wpisz Gracza:");
        playerNameLabel.setBounds(boardWidth / 2 - 100, boardHeight / 2 - 100, 200, 30);
        playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundLabel.add(playerNameLabel);

        playerNameField = new JTextField();
        playerNameField.setBounds(boardWidth / 2 - 100, boardHeight / 2 - 60, 200, 30);
        backgroundLabel.add(playerNameField);

        startButton = new JButton("Start");
        startButton.setBounds(boardWidth / 2 - 50, boardHeight / 2 - 20, 100, 30);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = playerNameField.getText();
                playerNameLabel.setVisible(false);
                playerNameField.setVisible(false);
                startButton.setVisible(false);
                highscoreLabel.setText("Gracz: " + playerName + "  Highscore: " + highscore);
                spawnWave();
            }
        });

        JButton exitButton = new JButton("Wyjdź z gry");
        exitButton.setBounds(boardWidth - 120, boardHeight - 50, 100, 30);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Zapis.zapiszHighscore(playerName, highscore, defeatedEnemiesCount);
                System.exit(0);
            }
        });
        backgroundLabel.add(exitButton);
        backgroundLabel.setComponentZOrder(exitButton, 0);
        backgroundLabel.add(startButton);

        setVisible(true);
    }

    private void spawnWave() {
        Thread waveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int spawned = 0;

                while (spawned < waveCount * 3) {
                    if (!isPaused) {
                        spawnEnemy();
                        spawned++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                while (activeEnemies > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                enemySpeed += 1;
                waveCount++;
                waveCountLabel.setText("Wave: " + waveCount);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                spawnWave();
            }
        });
        waveThread.start();
    }

    private void spawnEnemy() {
        Enemy enemy = new Enemy(this);
        enemies.add(enemy);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                backgroundLabel.add(enemy);
                backgroundLabel.setComponentZOrder(enemy, 0);
                incrementActiveEnemies();

                Thread movementThread = new Thread(enemy);
                movementThread.start();

                return null;
            }
        };

        worker.execute();
    }

    public void score() {
        highscore += 10;
        highscoreLabel.setText("Highscore: " + highscore);
        defeatedEnemiesCount++;
    }


    public void decreaseLives() {
        lives--;
        if (lives < 0) {
            lives = 0;
        }
        livesLabel.setText("Lives: " + lives);

        if (lives == 0) {
            endGame();
        }
    }

    private void endGame() {
        isPaused = true;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(Game.this, "PRZEGRANA", "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
                Zapis.zapiszHighscore(playerName, highscore, defeatedEnemiesCount);
                dispose();
                System.exit(0);
            }
        });
    }

    public JLabel getBackgroundLabel() {
        return backgroundLabel;
    }

    public JLabel getPlayerCastleLabel() {
        return playerCastleLabel;
    }

    public JLabel getEnemyCastleLabel() {
        return enemyCastleLabel;
    }

    public int getEnemySpeed() {
        return enemySpeed;
    }

    public void incrementActiveEnemies() {
        activeEnemies++;
    }

    public void decrementActiveEnemies() {
        activeEnemies--;
    }

    public boolean isPaused() {
        return isPaused;
    }



    private void playBackgroundMusic(String filePath) {
        Thread musicThread = new Thread(() -> {
            try {
                Music music = new Music(filePath, this);
                music.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        musicThread.setDaemon(true);
        musicThread.start();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.playBackgroundMusic("muzyka.mp3");
        });
    }





}
