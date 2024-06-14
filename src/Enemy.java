import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Enemy extends JButton implements Runnable {
    private int enemyX;
    private final int enemyY;
    private boolean isAlive = true;
    private final Game game;

    public Enemy(Game game) {
        this.game = game;
        int enemyWidth = 150;
        enemyX = game.getEnemyCastleLabel().getX() + game.getEnemyCastleLabel().getWidth() / 2 - enemyWidth / 2;
        int enemyHeight = 200;
        enemyY = game.getEnemyCastleLabel().getY() + game.getEnemyCastleLabel().getHeight() - enemyHeight;

        ImageIcon enemyImageIcon = new ImageIcon("enemy.png");
        Image enemyImage = enemyImageIcon.getImage().getScaledInstance(enemyWidth, enemyHeight, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(enemyImage));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);

        setBounds(enemyX, enemyY, enemyWidth, enemyHeight);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAlive) {
                    isAlive = false;
                    game.getBackgroundLabel().remove(Enemy.this);
                    game.getBackgroundLabel().revalidate();
                    game.getBackgroundLabel().repaint();
                    game.score();
                }
            }
        });
    }

    public void run() {
        while (enemyX >= game.getPlayerCastleLabel().getX() + game.getPlayerCastleLabel().getWidth()) {
            if (!game.isPaused()) {
                enemyX -= game.getEnemySpeed();
                setLocation(enemyX, enemyY);

                if (enemyX <= game.getPlayerCastleLabel().getX() + game.getPlayerCastleLabel().getWidth()) {
                    if (isAlive) {
                        isAlive = false;
                        game.decreaseLives();
                    }
                    break;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        game.getBackgroundLabel().remove(Enemy.this);
        game.getBackgroundLabel().revalidate();
        game.getBackgroundLabel().repaint();
        game.decrementActiveEnemies();
    }
}
