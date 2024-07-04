package Disk;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

class Kanvas extends Canvas implements KeyListener {
    private int ovalX, ovalY, velocity, ovalWidth = 20, ovalHeight = 20, canvasWidth, canvasHeight;
    private double angle, angleRadian;
    private int barWidth = 10, barHeight = 80, goalWidth = 20;
    private int scorePlayer1 = 0, scorePlayer2 = 0;
    private long startTime;
    private boolean gameEnded = false;
    private boolean upPressed, downPressed, aPressed, zPressed;

    private int paddle1Y, paddle2Y;
    private final int PADDLE_SPEED = 10;
    private Random random;

    public Kanvas(int width, int height, long seed) {
        setBackground(Color.CYAN);
        ovalX = width / 2 - ovalWidth / 2;
        ovalY = height / 2 - ovalHeight / 2;
        random = new Random(seed);
        angle = generateRandomAngle();
        angleRadian = Math.toRadians(angle);
        velocity = 8;
        canvasWidth = width;
        canvasHeight = height;
        paddle1Y = (canvasHeight - barHeight) / 2;
        paddle2Y = (canvasHeight - barHeight) / 2;
        addKeyListener(this);
        setFocusable(true);
        startTime = System.currentTimeMillis();
    }

    private int generateRandomAngle() {
        int angle = random.nextInt(360);
        if ((angle % 90 >= 75 && angle % 90 <= 89) || (angle % 90 >= 1 && angle % 90 <= 15) || angle % 90 == 0) {
            return generateRandomAngle();
        }
        System.out.println(angle);
        return angle;
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillOval(ovalX, ovalY, ovalWidth, ovalHeight);

        g.setColor(Color.BLACK);
        g.drawLine(30, 0, 30, canvasHeight);
        g.drawLine(canvasWidth - 50, 0, canvasWidth - 50, canvasHeight);

        g.setColor(Color.MAGENTA);
        g.fillRect(50, paddle1Y, barWidth, barHeight);
        g.fillRect(canvasWidth - 79, paddle2Y, barWidth, barHeight);

        int fontSize = 18;
        Font font = new Font("Calibri", Font.PLAIN, fontSize);

        g.setFont(font);
        g.drawString("Player 1 -->  " + scorePlayer1, 60, 20);
        g.drawString("Player 2 -->  " + scorePlayer2, canvasWidth - 180, 20);

        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        g.drawString("Time: " + elapsedTime + " seconds", canvasWidth / 2 - 50, 20);

        if (gameEnded) {
            String winner = (scorePlayer1 >= 3) ? "Player 1" : "Player 2";
            String message = "Game Over! " + winner + " won! Press ENTER to start again or esc to exit!";
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(message);
            int messageHeight = fm.getHeight();
            g.drawString(message, (canvasWidth - messageWidth) / 2, (canvasHeight - messageHeight) / 2);
        }
    }

    public void update() {
        if (gameEnded) return;

        int deltaX = (int) (velocity * Math.cos(angleRadian));
        int deltaY = (int) (velocity * Math.sin(angleRadian));

        ovalX += deltaX;
        ovalY += deltaY;

        if (ovalX <= 9 || ovalX + ovalWidth >= canvasWidth - 29) {
            String scoringPlayer = "";
            if (ovalX <= 9) {
                scoringPlayer = "Player 2";
                scorePlayer2++;
            } else {
                scoringPlayer = "Player 1";
                scorePlayer1++;
            }
            checkGameEnd();
            resetBall();

            Graphics2D g2d = (Graphics2D) getGraphics();
            Font font = new Font("Calibri", Font.BOLD, 24);
            g2d.setFont(font);
            g2d.setColor(Color.MAGENTA);
            FontMetrics fm = g2d.getFontMetrics();
            int messageWidth = fm.stringWidth("GOAL by " + scoringPlayer + " ++");
            int messageHeight = fm.getHeight();
            g2d.drawString("GOAL by " + scoringPlayer + " ++", (canvasWidth - messageWidth) / 2, (canvasHeight - messageHeight) / 2);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (ovalY <= 0 || ovalY + ovalHeight >= canvasHeight - 40) {
            angle = 360 - angle;
            angleRadian = Math.toRadians(angle);
        }

        if (ovalX <= 50 + barWidth && ovalY + ovalHeight >= paddle1Y && ovalY <= paddle1Y + barHeight) {
            angle = Math.toDegrees(Math.atan2(-deltaY, -deltaX));
            angleRadian = Math.toRadians(angle);
        }
        if (ovalX + ovalWidth >= canvasWidth - 69 - barWidth && ovalY + ovalHeight >= paddle2Y && ovalY <= paddle2Y + barHeight && ovalX + ovalWidth <= canvasWidth - 69) {
            angle = Math.toDegrees(Math.atan2(-deltaY, -deltaX));
            angleRadian = Math.toRadians(angle);
        }

        if (ovalX <= 50 + barWidth && ovalY + ovalHeight >= paddle1Y && ovalY <= paddle1Y + barHeight) {
            angle = 360 - angle;
            angleRadian = Math.toRadians(angle);
        }
        if (ovalX + ovalWidth >= canvasWidth - 69 - barWidth && ovalY + ovalHeight >= paddle2Y && ovalY <= paddle2Y + barHeight) {
            angle = 360 - angle;
            angleRadian = Math.toRadians(angle);
        }

        repaint();
    }

    private void resetBall() {
        ovalX = canvasWidth / 2 - ovalWidth / 2;
        ovalY = canvasHeight / 2 - ovalHeight / 2;
        angle = generateRandomAngle();
        angleRadian = Math.toRadians(angle);
    }

    private void checkGameEnd() {
        if (scorePlayer1 >= 3 || scorePlayer2 >= 3) {
            gameEnded = true;
        }
    }

    public void restartGame() {
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        gameEnded = false;
        startTime = System.currentTimeMillis();

        resetBall();
        paddle1Y = (canvasHeight - barHeight) / 2;
        paddle2Y = (canvasHeight - barHeight) / 2;

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (keyCode == KeyEvent.VK_A) {
            aPressed = true;
        }
        if (keyCode == KeyEvent.VK_Z) {
            zPressed = true;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
            restartGame();
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (keyCode == KeyEvent.VK_A) {
            aPressed = false;
        }
        if (keyCode == KeyEvent.VK_Z) {
            zPressed = false;
        }
    }

    public void keyTyped(KeyEvent e) {}

    private void movePaddle1Up() {
        if (paddle1Y > 0) {
            paddle1Y -= PADDLE_SPEED;
        }
    }

    private void movePaddle1Down() {
        if (paddle1Y + barHeight < canvasHeight - 40) {
            paddle1Y += PADDLE_SPEED;
        }
    }

    private void movePaddle2Up() {
        if (paddle2Y > 0) {
            paddle2Y -= PADDLE_SPEED;
        }
    }

    private void movePaddle2Down() {
        if (paddle2Y + barHeight < canvasHeight - 40) {
            paddle2Y += PADDLE_SPEED;
        }
    }

    public void startGameLoop() {
        while (true) {
            update();

            if (upPressed) {
                movePaddle2Up();
            }
            if (downPressed) {
                movePaddle2Down();
            }
            if (aPressed) {
                movePaddle1Up();
            }
            if (zPressed) {
                movePaddle1Down();
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Game extends Frame implements WindowListener {
    private Kanvas canvas;
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    public Game(long seed) {
        addWindowListener(this);
        canvas = new Kanvas(WIDTH, HEIGHT, seed);
        add(canvas);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setVisible(true);
        startGameLoop();
    }

    private void startGameLoop() {
        canvas.startGameLoop();
    }

    public static void main(String args[]) {
        long seed = 12345L; 
        new Game(seed);
    }

    public void windowOpened(WindowEvent e) {}

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowClosed(WindowEvent e) {}

    public void windowIconified(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowActivated(WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}
}
