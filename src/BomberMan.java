
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

class Bomb {

    int x, y;
    boolean exploded;
    int countToExplode, intervalToExplode = 4;
}

public class BomberMan extends JPanel implements Runnable, KeyListener {

    boolean isRunning;
    Thread thread;
    BufferedImage view, concreteTile, blockTile, player, puerta, PowerUp1, PowerUp2, PowerUp3, PowerUp4,
            PowerUp5, PowerUp6, PowerUp7, PowerUp8;

    Bomb bomb;
    int[][] scene;
    int playerX, playerY, puertaX, puertaY, PowerUp1X, PowerUp1Y,
            PowerUp2X, PowerUp2Y, PowerUp3X, PowerUp3Y, PowerUp4X, PowerUp4Y,
            PowerUp5X, PowerUp5Y, PowerUp6X, PowerUp6Y, PowerUp7X, PowerUp7Y, PowerUp8X, PowerUp8Y;
    int tileSize = 16, rows = 13, columns = 15;
    int speed = 4;
    boolean right, left, up, down, enter;
    boolean moving;
    int framePlayer = 0, intervalPlayer = 5, indexAnimPlayer = 0;
    BufferedImage[] playerAnimUp, playerAnimDown, playerAnimRight, playerAnimLeft;
    int frameBomb = 0, intervalBomb = 7, indexAnimBomb = 0;
    BufferedImage[] bombAnim;
    BufferedImage[] fontExplosion, rightExplosion, leftExplosion, upExplosion, downExplosion;
    int frameExplosion = 0, intervalExplosion = 3, indexAnimExplosion = 0;
    BufferedImage[] concreteExploding;
    int frameConcreteExploding = 0, intevalConcreteExploding = 4, indexConcreteExploding = 0;
    boolean concreteAnim = false;
    int bombX, bombY;

    //int enemyX, enemyY;
    int lives = 3;

    // Agrega esta variable para rastrear si el jugador está vivo
    boolean playerAlive = true;
    boolean playerAtDoor = false;
    boolean PowerUp_1 = false;
    boolean PowerUp_2 = false;
    boolean PowerUp_3 = false;
    boolean PowerUp_4 = false;
    boolean PowerUp_5 = false;
    boolean PowerUp_6 = false;
    boolean PowerUp_7 = false;
    boolean PowerUp_8 = false;

    boolean PowerUp1Collected = false;
    boolean PowerUp2Collected = false;
    boolean PowerUp3Collected = false;
    boolean PowerUp4Collected = false;
    boolean PowerUp5Collected = false;
    boolean PowerUp6Collected = false;
    boolean PowerUp7Collected = false;
    boolean PowerUp8Collected = false;

    final int SCALE = 3;
    final int WIDTH = (tileSize * SCALE) * columns;
    final int HEIGHT = (tileSize * SCALE) * rows;

    public BomberMan() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
    }

    public static void main(String[] args) {
        JFrame w = new JFrame("Bomberman");
        w.setResizable(false);
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.add(new BomberMan());
        w.pack();
        w.setLocationRelativeTo(null);
        w.setVisible(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            isRunning = true;
            thread.start();
        }
    }

    public boolean isFree(int nextX, int nextY) {
        int size = SCALE * tileSize;

        int nextX_1 = nextX / size;
        int nextY_1 = nextY / size;

        int nextX_2 = (nextX + size - 1) / size;
        int nextY_2 = nextY / size;

        int nextX_3 = nextX / size;
        int nextY_3 = (nextY + size - 1) / size;

        int nextX_4 = (nextX + size - 1) / size;
        int nextY_4 = (nextY + size - 1) / size;

        return !((scene[nextY_1][nextX_1] == 1 || scene[nextY_1][nextX_1] == 2)
                || (scene[nextY_2][nextX_2] == 1 || scene[nextY_2][nextX_2] == 2)
                || (scene[nextY_3][nextX_3] == 1 || scene[nextY_3][nextX_3] == 2)
                || (scene[nextY_4][nextX_4] == 1 || scene[nextY_4][nextX_4] == 2));
    }

    public void start() {
        try {
            view = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

            BufferedImage spriteSheet = ImageIO.read(getClass().getResource("/sheets.png"));
            BufferedImage puertaImage = ImageIO.read(getClass().getResource("/Puerta.jpg"));
            BufferedImage PowerUp1Image = ImageIO.read(getClass().getResource("/PowerUP1.png"));
            BufferedImage PowerUp2Image = ImageIO.read(getClass().getResource("/PowerUp2.jpg"));
            BufferedImage PowerUp3Image = ImageIO.read(getClass().getResource("/PowerUp3.jpg"));
            BufferedImage PowerUp4Image = ImageIO.read(getClass().getResource("/PowerUp4.jpg"));
            BufferedImage PowerUp5Image = ImageIO.read(getClass().getResource("/PowerUp5.jpg"));
            BufferedImage PowerUp6Image = ImageIO.read(getClass().getResource("/PowerUp6.png"));
            BufferedImage PowerUp7Image = ImageIO.read(getClass().getResource("/PowerUp7.png"));
            BufferedImage PowerUp8Image = ImageIO.read(getClass().getResource("/PowerUp8.png"));

            concreteTile = spriteSheet.getSubimage(4 * tileSize, 3 * tileSize, tileSize, tileSize);
            blockTile = spriteSheet.getSubimage(3 * tileSize, 3 * tileSize, tileSize, tileSize);
            player = spriteSheet.getSubimage(4 * tileSize, 0, tileSize, tileSize);
            puerta = puertaImage;
            PowerUp1 = PowerUp1Image;
            PowerUp2 = PowerUp2Image;
            PowerUp3 = PowerUp3Image;
            PowerUp4 = PowerUp4Image;
            PowerUp5 = PowerUp5Image;
            PowerUp6 = PowerUp6Image;
            PowerUp7 = PowerUp7Image;
            PowerUp8 = PowerUp8Image;

            playerAnimUp = new BufferedImage[3];
            playerAnimDown = new BufferedImage[3];
            playerAnimRight = new BufferedImage[3];
            playerAnimLeft = new BufferedImage[3];
            bombAnim = new BufferedImage[3];
            fontExplosion = new BufferedImage[4];
            rightExplosion = new BufferedImage[4];
            leftExplosion = new BufferedImage[4];
            upExplosion = new BufferedImage[4];
            downExplosion = new BufferedImage[4];
            concreteExploding = new BufferedImage[6];

            for (int i = 0; i < 6; i++) {
                concreteExploding[i] = spriteSheet.getSubimage((i + 5) * tileSize, 3 * tileSize, tileSize, tileSize);
            }

            fontExplosion[0] = spriteSheet.getSubimage(2 * tileSize, 6 * tileSize, tileSize, tileSize);
            fontExplosion[1] = spriteSheet.getSubimage(7 * tileSize, 6 * tileSize, tileSize, tileSize);
            fontExplosion[2] = spriteSheet.getSubimage(2 * tileSize, 11 * tileSize, tileSize, tileSize);
            fontExplosion[3] = spriteSheet.getSubimage(7 * tileSize, 11 * tileSize, tileSize, tileSize);

            rightExplosion[0] = spriteSheet.getSubimage(4 * tileSize, 6 * tileSize, tileSize, tileSize);
            rightExplosion[1] = spriteSheet.getSubimage(9 * tileSize, 6 * tileSize, tileSize, tileSize);
            rightExplosion[2] = spriteSheet.getSubimage(4 * tileSize, 11 * tileSize, tileSize, tileSize);
            rightExplosion[3] = spriteSheet.getSubimage(9 * tileSize, 11 * tileSize, tileSize, tileSize);

            leftExplosion[0] = spriteSheet.getSubimage(0, 6 * tileSize, tileSize, tileSize);
            leftExplosion[1] = spriteSheet.getSubimage(5 * tileSize, 6 * tileSize, tileSize, tileSize);
            leftExplosion[2] = spriteSheet.getSubimage(0, 11 * tileSize, tileSize, tileSize);
            leftExplosion[3] = spriteSheet.getSubimage(5 * tileSize, 11 * tileSize, tileSize, tileSize);

            upExplosion[0] = spriteSheet.getSubimage(2 * tileSize, 4 * tileSize, tileSize, tileSize);
            upExplosion[1] = spriteSheet.getSubimage(7 * tileSize, 4 * tileSize, tileSize, tileSize);
            upExplosion[2] = spriteSheet.getSubimage(2 * tileSize, 9 * tileSize, tileSize, tileSize);
            upExplosion[3] = spriteSheet.getSubimage(7 * tileSize, 9 * tileSize, tileSize, tileSize);

            downExplosion[0] = spriteSheet.getSubimage(2 * tileSize, 8 * tileSize, tileSize, tileSize);
            downExplosion[1] = spriteSheet.getSubimage(7 * tileSize, 8 * tileSize, tileSize, tileSize);
            downExplosion[2] = spriteSheet.getSubimage(2 * tileSize, 13 * tileSize, tileSize, tileSize);
            downExplosion[3] = spriteSheet.getSubimage(7 * tileSize, 13 * tileSize, tileSize, tileSize);

            for (int i = 0; i < 3; i++) {
                playerAnimLeft[i] = spriteSheet.getSubimage(i * tileSize, 0, tileSize, tileSize);
                playerAnimRight[i] = spriteSheet.getSubimage(i * tileSize, tileSize, tileSize, tileSize);
                playerAnimDown[i] = spriteSheet.getSubimage((i + 3) * tileSize, 0, tileSize, tileSize);
                playerAnimUp[i] = spriteSheet.getSubimage((i + 3) * tileSize, tileSize, tileSize, tileSize);
                bombAnim[i] = spriteSheet.getSubimage(i * tileSize, 3 * tileSize, tileSize, tileSize);
            }

            scene = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            };

            do {
                puertaX = new Random().nextInt(columns);
                puertaY = new Random().nextInt(rows);
            } while (scene[puertaY][puertaX] != 0 || (puertaX == 1 && puertaY == 1));

            do {
                PowerUp1X = new Random().nextInt(columns);
                PowerUp1Y = new Random().nextInt(rows);
            } while (scene[PowerUp1Y][PowerUp1X] != 0 || (PowerUp1X == 1 && PowerUp1Y == 1));

            do {
                PowerUp2X = new Random().nextInt(columns);
                PowerUp2Y = new Random().nextInt(rows);
            } while (scene[PowerUp2Y][PowerUp2X] != 0 || (PowerUp2X == 1 && PowerUp2Y == 1));

            do {
                PowerUp3X = new Random().nextInt(columns);
                PowerUp3Y = new Random().nextInt(rows);
            } while (scene[PowerUp3Y][PowerUp3X] != 0 || (PowerUp3X == 1 && PowerUp3Y == 1));

            do {
                PowerUp4X = new Random().nextInt(columns);
                PowerUp4Y = new Random().nextInt(rows);
            } while (scene[PowerUp4Y][PowerUp4X] != 0 || (PowerUp4X == 1 && PowerUp4Y == 1));

            do {
                PowerUp5X = new Random().nextInt(columns);
                PowerUp5Y = new Random().nextInt(rows);
            } while (scene[PowerUp5Y][PowerUp5X] != 0 || (PowerUp5X == 1 && PowerUp5Y == 1));

            do {
                PowerUp6X = new Random().nextInt(columns);
                PowerUp6Y = new Random().nextInt(rows);
            } while (scene[PowerUp6Y][PowerUp6X] != 0 || (PowerUp6X == 1 && PowerUp6Y == 1));

            do {
                PowerUp7X = new Random().nextInt(columns);
                PowerUp7Y = new Random().nextInt(rows);
            } while (scene[PowerUp7Y][PowerUp7X] != 0 || (PowerUp7X == 1 && PowerUp7Y == 1));

            do {
                PowerUp8X = new Random().nextInt(columns);
                PowerUp8Y = new Random().nextInt(rows);
            } while (scene[PowerUp8Y][PowerUp8X] != 0 || (PowerUp8X == 1 && PowerUp8Y == 1));

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == puertaY && j == puertaX) {
                        scene[i][j] = 4; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp1Y && j == PowerUp1X) {
                        scene[i][j] = 6; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp2Y && j == PowerUp2X) {
                        scene[i][j] = 8; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp3Y && j == PowerUp3X) {
                        scene[i][j] = 10; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp4Y && j == PowerUp4X) {
                        scene[i][j] = 12; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp5Y && j == PowerUp5X) {
                        scene[i][j] = 14; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp6Y && j == PowerUp6X) {
                        scene[i][j] = 16; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp7Y && j == PowerUp7X) {
                        scene[i][j] = 18; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }

                    if (i == PowerUp8Y && j == PowerUp8X) {
                        scene[i][j] = 20; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }
                }
            }
            scene[1][1] = 0;
            scene[2][1] = 0;
            scene[1][2] = 0;

            playerX = (tileSize * SCALE);
            playerY = (tileSize * SCALE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (!playerAlive) {
            if (lives > 0) {
                // Si quedan vidas, reinicia el juego
                resetGame();
            } else {
                // Si no quedan vidas
                isRunning = false;
            }
            return;
        }

        moving = false;
        if (right && isFree(playerX + speed, playerY)) {
            playerX += speed;
            moving = true;
        }
        if (left && isFree(playerX - speed, playerY)) {
            playerX -= speed;
            moving = true;
        }
        if (up && isFree(playerX, playerY - speed)) {
            playerY -= speed;
            moving = true;
        }
        if (down && isFree(playerX, playerY + speed)) {
            playerY += speed;
            moving = true;
        }

        if (playerX / (tileSize * SCALE) == puertaX && playerY / (tileSize * SCALE) == puertaY) {
            playerAtDoor = true;
            playerAlive = false; // Detiene el movimiento del jugador
        }

        if (playerX / (tileSize * SCALE) == PowerUp1X && playerY / (tileSize * SCALE) == PowerUp1Y && !PowerUp1Collected) {
            PowerUp_1 = true;
            PowerUp1Collected = true;
        }

        if (playerX / (tileSize * SCALE) == PowerUp2X && playerY / (tileSize * SCALE) == PowerUp2Y && !PowerUp2Collected) {
            PowerUp_2 = true;
            PowerUp2Collected = true;
        }

        if (playerX / (tileSize * SCALE) == PowerUp3X && playerY / (tileSize * SCALE) == PowerUp3Y && !PowerUp3Collected) {
            PowerUp_3 = true;
            PowerUp3Collected = true;
        }

        if (playerX / (tileSize * SCALE) == PowerUp4X && playerY / (tileSize * SCALE) == PowerUp4Y && !PowerUp4Collected) {
            PowerUp_4 = true;
            PowerUp4Collected = true;
        }

        if (playerX / (tileSize * SCALE) == PowerUp5X && playerY / (tileSize * SCALE) == PowerUp5Y && !PowerUp5Collected) {
            PowerUp_5 = true;
            PowerUp5Collected = true;
        }

        if (playerX / (tileSize * SCALE) == PowerUp6X && playerY / (tileSize * SCALE) == PowerUp6Y && !PowerUp6Collected) {
            PowerUp_6 = true;
            PowerUp6Collected = true;
        }

        if (playerX / (tileSize * SCALE) == PowerUp7X && playerY / (tileSize * SCALE) == PowerUp7Y && !PowerUp7Collected) {
            PowerUp_7 = true;
            PowerUp7Collected = true;
        }

        if (playerX / (tileSize * SCALE) == PowerUp8X && playerY / (tileSize * SCALE) == PowerUp8Y && !PowerUp8Collected) {
            PowerUp_8 = true;
            PowerUp8Collected = true;
        }

        if (bomb != null) {
            frameBomb++;
            if (frameBomb == intervalBomb) {
                frameBomb = 0;
                indexAnimBomb++;
                if (indexAnimBomb > 2) {
                    indexAnimBomb = 0;
                    bomb.countToExplode++;
                }
                if (bomb.countToExplode >= bomb.intervalToExplode) {
                    concreteAnim = true;
                    bombX = bomb.x;
                    bombY = bomb.y;
                    bomb.exploded = true;
                    if (scene[bomb.y + 1][bomb.x] == 2) {
                        scene[bomb.y + 1][bomb.x] = -1;
                    }
                    if (scene[bomb.y - 1][bomb.x] == 2) {
                        scene[bomb.y - 1][bomb.x] = -1;
                    }
                    if (scene[bomb.y][bomb.x + 1] == 2) {
                        scene[bomb.y][bomb.x + 1] = -1;
                    }
                    if (scene[bomb.y][bomb.x - 1] == 2) {
                        scene[bomb.y][bomb.x - 1] = -1;
                    }
                }
            }

            if (bomb.exploded) {
                frameExplosion++;
                if (frameExplosion == intervalExplosion) {
                    frameExplosion = 0;
                    indexAnimExplosion++;
                    if (indexAnimExplosion == 4) {
                        indexAnimExplosion = 0;
                        scene[bomb.y][bomb.x] = 0;
                        bomb = null;
                    }
                }

            }

            // Verificar colisión con la explosión de la bomba
            if (bomb != null && bomb.exploded) {
                int explosionCenterX = (bomb.x * tileSize * SCALE) + (tileSize * SCALE / 2);
                int explosionCenterY = (bomb.y * tileSize * SCALE) + (tileSize * SCALE / 2);
                int playerCenterX = playerX + (tileSize * SCALE / 2);
                int playerCenterY = playerY + (tileSize * SCALE / 2);
                int playerTileX = playerX / (tileSize * SCALE);
                int playerTileY = playerY / (tileSize * SCALE);

                int collisionDistance = tileSize * SCALE / 2;

                if (Math.abs(explosionCenterX - playerCenterX) < collisionDistance
                        && Math.abs(explosionCenterY - playerCenterY) < collisionDistance) {
                    playerAlive = false;
                }
                if (Math.abs(bomb.x - playerTileX) == 1 && bomb.y == playerTileY) {
                    playerAlive = false;
                }
                if (Math.abs(bomb.y - playerTileY) == 1 && bomb.x == playerTileX) {
                    playerAlive = false;
                }
            }
        }

        if (concreteAnim) {
            frameConcreteExploding++;
            if (frameConcreteExploding == intevalConcreteExploding) {
                frameConcreteExploding = 0;
                indexConcreteExploding++;
                if (indexConcreteExploding == 5) {
                    indexConcreteExploding = 0;
                    if (scene[bombY + 1][bombX] == -1) {
                        scene[bombY + 1][bombX] = 0;
                    }
                    if (scene[bombY - 1][bombX] == -1) {
                        scene[bombY - 1][bombX] = 0;
                    }
                    if (scene[bombY][bombX + 1] == -1) {
                        scene[bombY][bombX + 1] = 0;
                    }
                    if (scene[bombY][bombX - 1] == -1) {
                        scene[bombY][bombX - 1] = 0;
                    }
                    concreteAnim = false;
                }
            }
        }

        if (moving) {
            framePlayer++;
            if (framePlayer > intervalPlayer) {
                framePlayer = 0;
                indexAnimPlayer++;
                if (indexAnimPlayer > 2) {
                    indexAnimPlayer = 0;
                }
            }

            if (right) {
                player = playerAnimRight[indexAnimPlayer];
            } else if (left) {
                player = playerAnimLeft[indexAnimPlayer];
            } else if (up) {
                player = playerAnimUp[indexAnimPlayer];
            } else if (down) {
                player = playerAnimDown[indexAnimPlayer];
            }
        } else {
            player = playerAnimDown[1];
        }

    }

    public void resetGame() {
        // Restablece la posición del jugador
        playerX = (tileSize * SCALE);
        playerY = (tileSize * SCALE);
        puertaX = (tileSize * SCALE);
        puertaY = (tileSize * SCALE);
        PowerUp1X = (tileSize * SCALE);
        PowerUp1Y = (tileSize * SCALE);
        PowerUp2X = (tileSize * SCALE);
        PowerUp2Y = (tileSize * SCALE);
        PowerUp3X = (tileSize * SCALE);
        PowerUp3Y = (tileSize * SCALE);
        PowerUp4X = (tileSize * SCALE);
        PowerUp4Y = (tileSize * SCALE);
        PowerUp5X = (tileSize * SCALE);
        PowerUp5Y = (tileSize * SCALE);
        PowerUp6X = (tileSize * SCALE);
        PowerUp6Y = (tileSize * SCALE);
        PowerUp7X = (tileSize * SCALE);
        PowerUp7Y = (tileSize * SCALE);
        PowerUp8X = (tileSize * SCALE);
        PowerUp8Y = (tileSize * SCALE);

        
        

        // Restablece la variable de control de jugador vivo
        playerAlive = true;

        // Disminuye una vida
        lives--;

        // Limpia la explosión y la bomba si existe
        if (bomb != null) {
            scene[bomb.y][bomb.x] = 0;
            bomb = null;
        }

        // Restablece la escena
        scene = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        do {
            puertaX = new Random().nextInt(columns);
            puertaY = new Random().nextInt(rows);
        } while (scene[puertaY][puertaX] != 0 || (puertaX == 1 && puertaY == 1));

        do {
            PowerUp1X = new Random().nextInt(columns);
            PowerUp1Y = new Random().nextInt(rows);
        } while (scene[PowerUp1Y][PowerUp1X] != 0 || (PowerUp1X == 1 && PowerUp1Y == 1));

        do {
            PowerUp2X = new Random().nextInt(columns);
            PowerUp2Y = new Random().nextInt(rows);
        } while (scene[PowerUp2Y][PowerUp2X] != 0 || (PowerUp2X == 1 && PowerUp2Y == 1));

        do {
            PowerUp3X = new Random().nextInt(columns);
            PowerUp3Y = new Random().nextInt(rows);
        } while (scene[PowerUp3Y][PowerUp3X] != 0 || (PowerUp3X == 1 && PowerUp3Y == 1));

        do {
            PowerUp4X = new Random().nextInt(columns);
            PowerUp4Y = new Random().nextInt(rows);
        } while (scene[PowerUp4Y][PowerUp4X] != 0 || (PowerUp4X == 1 && PowerUp4Y == 1));

        do {
            PowerUp5X = new Random().nextInt(columns);
            PowerUp5Y = new Random().nextInt(rows);
        } while (scene[PowerUp5Y][PowerUp5X] != 0 || (PowerUp5X == 1 && PowerUp5Y == 1));

        do {
            PowerUp6X = new Random().nextInt(columns);
            PowerUp6Y = new Random().nextInt(rows);
        } while (scene[PowerUp6Y][PowerUp6X] != 0 || (PowerUp6X == 1 && PowerUp6Y == 1));

        do {
            PowerUp7X = new Random().nextInt(columns);
            PowerUp7Y = new Random().nextInt(rows);
        } while (scene[PowerUp7Y][PowerUp7X] != 0 || (PowerUp7X == 1 && PowerUp7Y == 1));

        do {
            PowerUp8X = new Random().nextInt(columns);
            PowerUp8Y = new Random().nextInt(rows);
        } while (scene[PowerUp8Y][PowerUp8X] != 0 || (PowerUp8X == 1 && PowerUp8Y == 1));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == puertaY && j == puertaX) {
                    scene[i][j] = 4; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp1Y && j == PowerUp1X) {
                    scene[i][j] = 6; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp2Y && j == PowerUp2X) {
                    scene[i][j] = 8; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp3Y && j == PowerUp3X) {
                    scene[i][j] = 10; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp4Y && j == PowerUp4X) {
                    scene[i][j] = 12; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp5Y && j == PowerUp5X) {
                    scene[i][j] = 14; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp6Y && j == PowerUp6X) {
                    scene[i][j] = 16; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp7Y && j == PowerUp7X) {
                    scene[i][j] = 18; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }

                if (i == PowerUp8Y && j == PowerUp8X) {
                    scene[i][j] = 20; // Usa un valor diferente para representar la puerta
                } else if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }
            }
        }
        scene[1][1] = 0;
        scene[2][1] = 0;
        scene[1][2] = 0;

    }

    public void NextNevel() {
        // Restablece la posición del jugador
        playerX = (tileSize * SCALE);
        playerY = (tileSize * SCALE);
        puertaX = (tileSize * SCALE);
        puertaY = (tileSize * SCALE);

        // Restablece la variable de control de jugador vivo
        playerAlive = true;

        lives++;

        if (lives > 3) {
            lives = 3;
        }
        // Limpia la explosión y la bomba si existe
        if (bomb != null) {
            scene[bomb.y][bomb.x] = 0;
            bomb = null;
        }

        // Restablece la escena
        scene = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        do {
            puertaX = new Random().nextInt(columns);
            puertaY = new Random().nextInt(rows);
        } while (scene[puertaY][puertaX] != 0 || (puertaX == 1 && puertaY == 1));

        // Reinicia los bloques destructibles de manera aleatoria
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                    if (i == puertaY && j == puertaX) {
                        scene[i][j] = 4; // Usa un valor diferente para representar la puerta
                    } else if (scene[i][j] == 0) {
                        if (new Random().nextInt(10) < 5) {
                            scene[i][j] = 2;
                        }
                    }
                }
            }
        }
        scene[1][1] = 0;
        scene[2][1] = 0;
        scene[1][2] = 0;

    }

    public void draw() {
        Graphics2D g2 = (Graphics2D) view.getGraphics();
        g2.setColor(new Color(56, 135, 0));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int size = tileSize * SCALE;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (scene[j][i] == 1) {
                    g2.drawImage(blockTile, i * size, j * size, size, size, null);
                } else if (scene[j][i] == 2) {
                    g2.drawImage(concreteTile, i * size, j * size, size, size, null);
                } else if (scene[j][i] == 3) {
                    if (bomb != null) {
                        if (bomb.exploded) {
                            g2.drawImage(fontExplosion[indexAnimExplosion], bomb.x * size, bomb.y * size, size, size, null);
                            if (scene[bomb.y][bomb.x + 1] == 0) {
                                g2.drawImage(rightExplosion[indexAnimExplosion], (bomb.x + 1) * size, bomb.y * size, size, size, null);
                            }
                            if (scene[bomb.y][bomb.x - 1] == 0) {
                                g2.drawImage(leftExplosion[indexAnimExplosion], (bomb.x - 1) * size, bomb.y * size, size, size, null);
                            }
                            if (scene[bomb.y - 1][bomb.x] == 0) {
                                g2.drawImage(upExplosion[indexAnimExplosion], bomb.x * size, (bomb.y - 1) * size, size, size, null);
                            }
                            if (scene[bomb.y + 1][bomb.x] == 0) {
                                g2.drawImage(downExplosion[indexAnimExplosion], bomb.x * size, (bomb.y + 1) * size, size, size, null);
                            }
                        } else {
                            g2.drawImage(bombAnim[indexAnimBomb], i * size, j * size, size, size, null);
                        }
                    }
                } else if (scene[j][i] == -1) {
                    g2.drawImage(concreteExploding[indexConcreteExploding], i * size, j * size, size, size, null);
                }
            }
        }

        if (playerAlive) {
            if (Math.abs(playerX / (tileSize * SCALE) - puertaX) <= 1 && Math.abs(playerY / (tileSize * SCALE) - puertaY) <= 1) {
                g2.drawImage(puerta, puertaX * size, puertaY * size, size, size, null);
            } else {
                g2.drawImage(player, playerX, playerY, size, size, null);
            }

            if (playerAtDoor) {
                g2.setColor(Color.GREEN);
                g2.setFont(new Font("Arial", Font.BOLD, 48));
                String winnerText = "You are the winner...";
                System.exit(0);
                int textWidth = g2.getFontMetrics().stringWidth(winnerText);
                g2.drawString(winnerText, (WIDTH - textWidth) / 2, HEIGHT / 2);
            } else {
                g2.drawImage(player, playerX, playerY, size, size, null);
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp1X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp1Y) <= 1) {

                if (!PowerUp1Collected) {
                    g2.drawImage(PowerUp1, PowerUp1X * size, PowerUp1Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp2X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp2Y) <= 1) {

                if (!PowerUp2Collected) {
                    g2.drawImage(PowerUp2, PowerUp2X * size, PowerUp2Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp3X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp3Y) <= 1) {

                if (!PowerUp3Collected) {
                    g2.drawImage(PowerUp3, PowerUp3X * size, PowerUp3Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp4X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp4Y) <= 1) {

                if (!PowerUp4Collected) {
                    g2.drawImage(PowerUp4, PowerUp4X * size, PowerUp4Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp5X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp5Y) <= 1) {

                if (!PowerUp5Collected) {
                    g2.drawImage(PowerUp5, PowerUp5X * size, PowerUp5Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp6X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp6Y) <= 1) {

                if (!PowerUp6Collected) {
                    g2.drawImage(PowerUp6, PowerUp6X * size, PowerUp6Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp7X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp7Y) <= 1) {

                if (!PowerUp7Collected) {
                    g2.drawImage(PowerUp7, PowerUp7X * size, PowerUp7Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp8X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp8Y) <= 1) {

                if (!PowerUp8Collected) {
                    g2.drawImage(PowerUp8, PowerUp8X * size, PowerUp8Y * size, size, size, null);
                }
            }

        } else {
            // Dibuja una imagen de "jugador muerto" o muestra un mensaje de game over
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = "Game Over";
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, (WIDTH - textWidth) / 2, HEIGHT / 2);
        }

        // Agrega este código para mostrar las vidas del jugador
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Vidas: " + lives, 10, 30);

        Graphics g = getGraphics();
        g.drawImage(view, 0, 0, WIDTH, HEIGHT, null);
        g.dispose();
    }

    @Override
    public void run() {
        try {
            requestFocus();
            start();
            while (isRunning) {
                update();
                draw();
                Thread.sleep(1000 / 60);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (bomb == null) {
                bomb = new Bomb();
                bomb.x = (playerX + ((SCALE * tileSize) / 2)) / (SCALE * tileSize);
                bomb.y = (playerY + ((SCALE * tileSize) / 2)) / (SCALE * tileSize);
                scene[bomb.y][bomb.x] = 3;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            enter = true;
            NextNevel();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            enter = false;
        }
    }
}
