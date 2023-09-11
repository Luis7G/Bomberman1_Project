package Negocio;

import Interfaz.JFInicio;
import Interfaz.Pausa;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class BomberMan extends JPanel implements Runnable, KeyListener {

    boolean isRunning;
    public Thread thread;
    BufferedImage view, concreteTile, blockTile, player, puerta, PowerUp1, PowerUp2, PowerUp3, PowerUp4,
            PowerUp5, PowerUp6, PowerUp7, PowerUp8;
    ArrayList<Enemy> enemies = new ArrayList<>();

    Bomb bomb;
    int[][] scene;
    int playerX, playerY, puertaX, puertaY, PowerUp1X, PowerUp1Y,
            PowerUp2X, PowerUp2Y, PowerUp3X, PowerUp3Y, PowerUp4X, PowerUp4Y,
            PowerUp5X, PowerUp5Y, PowerUp6X, PowerUp6Y, PowerUp7X, PowerUp7Y, PowerUp8X, PowerUp8Y;
    int tileSize = 16, rows = 13, columns = 15;
    int speed = 4;
    boolean right, left, up, down;
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

    //variables para vidas
    int lives = 3;

    //variable para puntuacion
    int score = 0;

    // Agrega esta variable para rastrear si el jugador está vivo
    boolean playerAlive = true;

    int timer = 180; // Inicializa el temporizador en 3 segundos
    int updateInterval = 60; // Actualizar el contador cada 60 ciclos de juego (1 segundo)
    int frameCount = 0;

    final int SCALE = 3;
    final int WIDTH = (tileSize * SCALE) * columns;
    final int HEIGHT = (tileSize * SCALE) * rows;
    
    public JFrame w;
    String firstTime = "reset";
    String winnerText;

    // Agrega esta variable para rastrear si el jugador está vivo
    boolean playerAtDoor = false;
    
    PowerUp powerUp = new PowerUp(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);

    public BomberMan() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
    }
    
    public void ejecutar(){
        w = new JFrame("Bomberman - Grupo C");
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

            BufferedImage spriteSheet = ImageIO.read(getClass().getResource("sheets.png"));
            BufferedImage puertaImage = ImageIO.read(getClass().getResource("Puerta.jpg"));
            BufferedImage PowerUp1Image = ImageIO.read(getClass().getResource("PowerUP1.png"));
            BufferedImage PowerUp2Image = ImageIO.read(getClass().getResource("PowerUp2.jpg"));
            BufferedImage PowerUp3Image = ImageIO.read(getClass().getResource("PowerUp3.jpg"));
            BufferedImage PowerUp4Image = ImageIO.read(getClass().getResource("PowerUp4.jpg"));
            BufferedImage PowerUp5Image = ImageIO.read(getClass().getResource("PowerUp5.jpg"));
            BufferedImage PowerUp6Image = ImageIO.read(getClass().getResource("PowerUp6.png"));
            BufferedImage PowerUp7Image = ImageIO.read(getClass().getResource("PowerUp7.png"));
            BufferedImage PowerUp8Image = ImageIO.read(getClass().getResource("PowerUp8.png"));
            BufferedImage enemyImage = ImageIO.read(getClass().getResource("enemigo.png"));
            
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

            if (firstTime.equals("reset")){
                scene = scene();
                
                playerX = (tileSize * SCALE);
                playerY = (tileSize * SCALE);
                
                if(enemies.size() == 1) enemies = new ArrayList<Enemy>();
                enemies.add(new Enemy(enemyImage, SCALE, tileSize, scene));
                
                score = 0;
                timer = 180;
                firstTime = "pause";
            }
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

        // Incrementa el contador de cuadros (frames)
        frameCount++;

        // Actualiza el temporizador cada segundo
        if (frameCount >= updateInterval) {
            frameCount = 0;

            // Resta 1 al temporizador cada segundo
            timer--;

            // Convierte el tiempo restante en minutos y segundos
            int minutes = timer / 60;
            int seconds = timer % 60;

            // Verifica si el temporizador ha llegado a cero
            if (timer <= 0) {
                timer = 0; // Asegura que el temporizador no sea negativo
                // Realiza alguna acción cuando el tiempo se agota (por ejemplo, game over)
                playerAlive = false; // Game over en este ejemplo
            }
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

        if (playerX / (tileSize * SCALE) == PowerUp1X && playerY / (tileSize * SCALE) == PowerUp1Y && !powerUp.isPowerUp1Collected()) {
            powerUp.setPowerUp_1(true);
            powerUp.setPowerUp1Collected(true);
            score += 30;
        }

        if (playerX / (tileSize * SCALE) == PowerUp2X && playerY / (tileSize * SCALE) == PowerUp2Y && !powerUp.isPowerUp2Collected()) {
            powerUp.setPowerUp_2(true);
            powerUp.setPowerUp2Collected(true);
            score += 30;
        }

        if (playerX / (tileSize * SCALE) == PowerUp3X && playerY / (tileSize * SCALE) == PowerUp3Y && !powerUp.isPowerUp3Collected()) {
            powerUp.setPowerUp_3(true);
            powerUp.setPowerUp3Collected(true);
            score += 30;
        }

        if (playerX / (tileSize * SCALE) == PowerUp4X && playerY / (tileSize * SCALE) == PowerUp4Y && !powerUp.isPowerUp4Collected()) {
            powerUp.setPowerUp_4(true);
            powerUp.setPowerUp4Collected(true);
            score += 30;
        }

        if (playerX / (tileSize * SCALE) == PowerUp5X && playerY / (tileSize * SCALE) == PowerUp5Y && !powerUp.isPowerUp5Collected()) {
            powerUp.setPowerUp_5(true);
            powerUp.setPowerUp5Collected(true);
            score += 30;
        }

        if (playerX / (tileSize * SCALE) == PowerUp6X && playerY / (tileSize * SCALE) == PowerUp6Y && !powerUp.isPowerUp6Collected()) {
            powerUp.setPowerUp_6(true);
            powerUp.setPowerUp6Collected(true);
            score += 30;
        }

        if (playerX / (tileSize * SCALE) == PowerUp7X && playerY / (tileSize * SCALE) == PowerUp7Y && !powerUp.isPowerUp7Collected()) {
            powerUp.setPowerUp_7(true);
            powerUp.setPowerUp7Collected(true);
            score += 30;
        }

        if (playerX / (tileSize * SCALE) == PowerUp8X && playerY / (tileSize * SCALE) == PowerUp8Y && !powerUp.isPowerUp8Collected()) {
            powerUp.setPowerUp_8(true);
            powerUp.setPowerUp8Collected(true);
            score += 30;
        }

        if (bomb != null) {
            frameBomb++;
            if (frameBomb == intervalBomb) {
                frameBomb = 0;
                indexAnimBomb++;
                if (indexAnimBomb > 2) {
                    indexAnimBomb = 0;
                    bomb.setCountToExplode(bomb.getCountToExplode() + 1);
                }
                if (bomb.getCountToExplode() >= bomb.getIntervalToExplode()) {
                    concreteAnim = true;
                    bombX = bomb.getX();
                    bombY = bomb.getY();
                    bomb.setExploded(true);
                    if (scene[bomb.getY() + 1][bomb.getX()] == 2) {
                        scene[bomb.getY() + 1][bomb.getX()] = -1;
                        score += 10; // Aumenta la puntuación en 10 puntos por bloque destruido
                    }
                    if (scene[bomb.getY() - 1][bomb.getX()] == 2) {
                        scene[bomb.getY() - 1][bomb.getX()] = -1;
                        score += 10; // Aumenta la puntuación en 10 puntos por bloque destruido
                    }
                    if (scene[bomb.getY()][bomb.getX() + 1] == 2) {
                        scene[bomb.getY()][bomb.getX() + 1] = -1;
                        score += 10; // Aumenta la puntuación en 10 puntos por bloque destruido
                    }
                    if (scene[bomb.getY()][bomb.getX()- 1] == 2) {
                        scene[bomb.getY()][bomb.getX() - 1] = -1;
                        score += 10; // Aumenta la puntuación en 10 puntos por bloque destruido
                    }
                }
            }

            if (bomb.getExploded()) {
                frameExplosion++;
                if (frameExplosion == intervalExplosion) {
                    frameExplosion = 0;
                    indexAnimExplosion++;
                    if (indexAnimExplosion == 4) {
                        indexAnimExplosion = 0;
                        scene[bomb.getY()][bomb.getX()]= 0;
                        bomb = null;
                    }
                }
            }

            // Verificar colisión con la explosión de la bomba
            if (bomb != null && bomb.getExploded()) {
                double explosionCenterX = (bomb.getX() * tileSize * SCALE) + (tileSize * SCALE / 2);
                double explosionCenterY = (bomb.getY() * tileSize * SCALE) + (tileSize * SCALE / 2);
                double playerCenterX = playerX + (tileSize * SCALE / 2);
                double playerCenterY = playerY + (tileSize * SCALE / 2);
                double playerTileX = playerX / (tileSize * SCALE);
                double playerTileY = playerY / (tileSize * SCALE);

                double collisionDistance = tileSize * SCALE / 2;

                if (Math.abs(explosionCenterX - playerCenterX) < collisionDistance
                        && Math.abs(explosionCenterY - playerCenterY) < collisionDistance) {
                    playerAlive = false;
                }
                if (Math.abs(bomb.getX() - playerTileX) == 1 && bomb.getY() == playerTileY) {
                    playerAlive = false;
                }
                if (Math.abs(bomb.getY() - playerTileY) == 1 && bomb.getX() == playerTileX) {
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
        for (Enemy enemy : enemies) {
            enemy.update();
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
            scene[bomb.getY()][bomb.getX()] = 0;
            bomb = null;
        }
        for (Enemy enemy : enemies) {
            enemy.reset(scene);
        }

        // Restablece la escena
        scene = scene();
    }

    public void draw() throws InterruptedException {
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
                        if (bomb.getExploded()) {
                            g2.drawImage(fontExplosion[indexAnimExplosion], bomb.getX() * size, bomb.getY() * size, size, size, null);
                            if (scene[bomb.getY()][bomb.getX() + 1] == 0) {
                                g2.drawImage(rightExplosion[indexAnimExplosion], (bomb.getX() + 1) * size, bomb.getY() * size, size, size, null);
                            }
                            if (scene[bomb.getY()][bomb.getX() - 1] == 0) {
                                g2.drawImage(leftExplosion[indexAnimExplosion], (bomb.getX() - 1) * size, bomb.getY() * size, size, size, null);
                            }
                            if (scene[bomb.getY() - 1][bomb.getX()] == 0) {
                                g2.drawImage(upExplosion[indexAnimExplosion], bomb.getX() * size, (bomb.getY() - 1) * size, size, size, null);
                            }
                            if (scene[bomb.getY() + 1][bomb.getX()] == 0) {
                                g2.drawImage(downExplosion[indexAnimExplosion], bomb.getX() * size, (bomb.getY() + 1) * size, size, size, null);
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
                winnerText = "You are the winner...";
             
                int textWidth = g2.getFontMetrics().stringWidth(winnerText);
                g2.drawString(winnerText, (WIDTH - textWidth) / 2, HEIGHT / 2);
                
            } else {
                g2.drawImage(player, playerX, playerY, size, size, null);
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp1X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp1Y) <= 1) {

                if (!powerUp.isPowerUp1Collected()) {
                    g2.drawImage(PowerUp1, PowerUp1X * size, PowerUp1Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp2X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp2Y) <= 1) {

                if (!powerUp.isPowerUp2Collected()) {
                    g2.drawImage(PowerUp2, PowerUp2X * size, PowerUp2Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp3X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp3Y) <= 1) {

                if (!powerUp.isPowerUp3Collected()) {
                    g2.drawImage(PowerUp3, PowerUp3X * size, PowerUp3Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp4X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp4Y) <= 1) {

                if (!powerUp.isPowerUp4Collected()) {
                    g2.drawImage(PowerUp4, PowerUp4X * size, PowerUp4Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp5X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp5Y) <= 1) {

                if (!powerUp.isPowerUp5Collected()) {
                    g2.drawImage(PowerUp5, PowerUp5X * size, PowerUp5Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp6X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp6Y) <= 1) {

                if (!powerUp.isPowerUp6Collected()) {
                    g2.drawImage(PowerUp6, PowerUp6X * size, PowerUp6Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp7X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp7Y) <= 1) {

                if (!powerUp.isPowerUp7Collected()) {
                    g2.drawImage(PowerUp7, PowerUp7X * size, PowerUp7Y * size, size, size, null);
                }
            }

            if (Math.abs(playerX / (tileSize * SCALE) - PowerUp8X) <= 1 && Math.abs(playerY / (tileSize * SCALE) - PowerUp8Y) <= 1) {

                if (!powerUp.isPowerUp8Collected()) {
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
        
        for (Enemy enemy : enemies) {
            enemy.draw(g2,size);
        }

        // Código para mostrar la puntuación y las vidas
        g2.setColor(Color.blue);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        // Muestra "Vidas" a la izquierda
        g2.drawString("Vidas: " + lives, 10, 30);

        // Calcula la posición X para "Puntuación" para que esté a la derecha
        String scoreText = "Puntuación: " + score;
        int scoreTextWidth = g2.getFontMetrics().stringWidth(scoreText);
        int xPositionScore = WIDTH - scoreTextWidth - 10; // Alinea a la derecha
        int yPositionTimer = 30;
        g2.drawString(scoreText, xPositionScore, yPositionTimer);

        // Convierte el tiempo restante en minutos y segundos
        int minutes = timer / 60;
        int seconds = timer % 60;

        // Calcula la posición X para el temporizador para que esté en la mitad
        String timerText = "Tiempo: " + String.format("%02d:%02d", minutes, seconds);
        int timerTextWidth = g2.getFontMetrics().stringWidth(timerText);
        int xPositionTimer = (WIDTH - timerTextWidth) / 2;
        // Cambia la coordenada Y según tu preferencia
        g2.drawString(timerText, xPositionTimer, yPositionTimer); // Posición del temporizador

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
                bomb.setX((playerX + ((SCALE * tileSize) / 2)) / (SCALE * tileSize));
                bomb.setY((playerY + ((SCALE * tileSize) / 2)) / (SCALE * tileSize));
                scene[bomb.getY()][bomb.getX()] = 3;
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
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            isRunning = false;
            Pausa pausa = new Pausa(this);
            pausa.setVisible(true);
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
    }
    
    public int[][] scene() {
        int[][] scene = new int[][]{
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

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (scene[i][j] == 0) {
                    if (new Random().nextInt(10) < 5) {
                        scene[i][j] = 2;
                    }
                }
            }
        }

        scene[1][1] = 0;
        scene[2][1] = 0;
        scene[1][2] = 0;

        do {
            puertaX = new Random().nextInt(columns);
            puertaY = new Random().nextInt(rows);
        } while (scene[puertaY][puertaX] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp1X = new Random().nextInt(columns);
            PowerUp1Y = new Random().nextInt(rows);
        } while (scene[PowerUp1Y][PowerUp1X] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp2X = new Random().nextInt(columns);
            PowerUp2Y = new Random().nextInt(rows);
        } while (scene[PowerUp2Y][PowerUp2X] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp3X = new Random().nextInt(columns);
            PowerUp3Y = new Random().nextInt(rows);
        } while (scene[PowerUp3Y][PowerUp3X] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp4X = new Random().nextInt(columns);
            PowerUp4Y = new Random().nextInt(rows);
        } while (scene[PowerUp4Y][PowerUp4X] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp5X = new Random().nextInt(columns);
            PowerUp5Y = new Random().nextInt(rows);
        } while (scene[PowerUp5Y][PowerUp5X] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp6X = new Random().nextInt(columns);
            PowerUp6Y = new Random().nextInt(rows);
        } while (scene[PowerUp6Y][PowerUp6X] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp7X = new Random().nextInt(columns);
            PowerUp7Y = new Random().nextInt(rows);
        } while (scene[PowerUp7Y][PowerUp7X] == 1 || scene[puertaY][puertaX] == 0);

        do {
            PowerUp8X = new Random().nextInt(columns);
            PowerUp8Y = new Random().nextInt(rows);
        } while (scene[PowerUp8Y][PowerUp8X] == 1 || scene[puertaY][puertaX] == 0);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (i == puertaY && j == puertaX) {
                    scene[i][j] = 4; // Usa un valor diferente para representar la puerta
                }

                if (i == PowerUp1Y && j == PowerUp1X) {
                    scene[i][j] = 6; // Usa un valor diferente para representar la puerta

                }

                if (i == PowerUp2Y && j == PowerUp2X) {
                    scene[i][j] = 8; // Usa un valor diferente para representar la puerta

                }

                if (i == PowerUp3Y && j == PowerUp3X) {
                    scene[i][j] = 10; // Usa un valor diferente para representar la puerta

                }

                if (i == PowerUp4Y && j == PowerUp4X) {
                    scene[i][j] = 12; // Usa un valor diferente para representar la puerta

                }

                if (i == PowerUp5Y && j == PowerUp5X) {
                    scene[i][j] = 14; // Usa un valor diferente para representar la puerta

                }

                if (i == PowerUp6Y && j == PowerUp6X) {
                    scene[i][j] = 16; // Usa un valor diferente para representar la puerta

                }

                if (i == PowerUp7Y && j == PowerUp7X) {
                    scene[i][j] = 18; // Usa un valor diferente para representar la puerta

                }

                if (i == PowerUp8Y && j == PowerUp8X) {
                    scene[i][j] = 20; // Usa un valor diferente para representar la puerta

                }
            }
        }

        return scene;
    }
    
    public void reset(){
        firstTime = "reset";
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }
    
    public void quitPause(){
        firstTime = "pause";
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }
    
}