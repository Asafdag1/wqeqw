import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int TILE_SIZE = 20;
    private static final int BOARD_WIDTH = 30;
    private static final int BOARD_HEIGHT = 25;
    private static final int WINDOW_WIDTH = BOARD_WIDTH * TILE_SIZE;
    private static final int WINDOW_HEIGHT = BOARD_HEIGHT * TILE_SIZE;

    private final Timer timer;
    private final Random random = new Random();
    private final Deque<Point> snake = new ArrayDeque<>();

    private Point food;
    private int dx = TILE_SIZE;
    private int dy = 0;
    private boolean running = true;
    private int score = 0;

    public SnakeGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        initGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!running) {
                    initGame();
                    repaint();
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (dy == 0) {
                            dx = 0;
                            dy = -TILE_SIZE;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (dy == 0) {
                            dx = 0;
                            dy = TILE_SIZE;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (dx == 0) {
                            dx = -TILE_SIZE;
                            dy = 0;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (dx == 0) {
                            dx = TILE_SIZE;
                            dy = 0;
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        timer = new Timer(120, this);
        timer.start();
    }

    private void initGame() {
        snake.clear();
        snake.addFirst(new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2));
        snake.addLast(new Point((BOARD_WIDTH / 2) - 1, BOARD_HEIGHT / 2));
        snake.addLast(new Point((BOARD_WIDTH / 2) - 2, BOARD_HEIGHT / 2));

        dx = TILE_SIZE;
        dy = 0;
        score = 0;
        running = true;
        food = spawnFood();
    }

    private Point spawnFood() {
        while (true) {
            int x = random.nextInt(BOARD_WIDTH);
            int y = random.nextInt(BOARD_HEIGHT);
            Point candidate = new Point(x, y);
            if (!snake.contains(candidate)) {
                return candidate;
            }
        }
    }

    private void moveSnake() {
        Point head = snake.peekFirst();
        int nextX = head.x + dx / TILE_SIZE;
        int nextY = head.y + dy / TILE_SIZE;

        if (nextX < 0 || nextX >= BOARD_WIDTH || nextY < 0 || nextY >= BOARD_HEIGHT) {
            running = false;
            return;
        }

        Point nextHead = new Point(nextX, nextY);
        if (snake.contains(nextHead)) {
            running = false;
            return;
        }

        snake.addFirst(nextHead);

        if (nextHead.equals(food)) {
            score += 10;
            food = spawnFood();
        } else {
            snake.removeLast();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) {
            timer.stop();
            repaint();
            return;
        }

        moveSnake();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                drawCell(g, x, y, Color.DARK_GRAY);
            }
        }

        g.setColor(Color.GREEN);
        for (Point segment : snake) {
            drawCell(g, segment.x, segment.y, Color.GREEN);
        }

        g.setColor(Color.RED);
        drawCell(g, food.x, food.y, Color.RED);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 22);

        if (!running) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Over! Press any key to restart", 30, WINDOW_HEIGHT / 2);
        }
    }

    private void drawCell(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    private static class Point {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Point)) {
                return false;
            }
            Point other = (Point) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
