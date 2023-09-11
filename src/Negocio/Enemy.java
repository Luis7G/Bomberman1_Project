package Negocio;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Graphics2D;

/**
 *
 * @author joelg
 */
public class Enemy {

    int SCALE;
    int tileSize;
    int x, y;
    int direction; // 0: arriba, 1: abajo, 2: izquierda, 3: derecha
    int speed = 2; // Velocidad de movimiento del enemigo
    BufferedImage enemyImage; // Imagen del enemigo
    int currentDirection;
    int stepsInCurrentDirection;
    int maxStepsInDirection = 30;
    int scene[][];
    int initialX;
    int initialY;

    public Enemy(BufferedImage enemyImage, int SCALE, int tileSize, int scene[][]) {
        this.direction = new Random().nextInt(4);
        this.enemyImage = enemyImage;
        this.SCALE = SCALE;
        this.tileSize = tileSize;
        this.currentDirection = new Random().nextInt(4);
        this.scene = scene;
        this.initialX = x; // Guarda la posición X inicial
        this.initialY = y;
        Point emptySpace = findEmptySpace(scene);
        if (emptySpace != null) {
            this.x = emptySpace.x * tileSize * SCALE;
            this.y = emptySpace.y * tileSize * SCALE;
        } else {
            // Si no se encuentra espacio vacío, establece una posición predeterminada.
            this.x = tileSize * SCALE;
            this.y = tileSize * SCALE;
        }
    }

    public void reset(int[][] scene) {
        Point newPosition = findEmptySpace(scene); // Obtener una nueva posición vacía
        if (newPosition != null) {
            x = newPosition.x * tileSize * SCALE; // Establecer la nueva posición X
            y = newPosition.y * tileSize * SCALE; // Establecer la nueva posición Y
        }
    }

    private Point findEmptySpace(int[][] scene) {
        ArrayList<Point> emptySpaces = new ArrayList<>();

        for (int i = 0; i < scene.length; i++) {
            for (int j = 0; j < scene[0].length; j++) {
                if (scene[i][j] == 0) {
                    emptySpaces.add(new Point(j, i));
                }
            }
        }

        if (emptySpaces.isEmpty()) {
            return null; // No se encontraron espacios vacíos
        }

        // Escoge una posición aleatoria de los espacios vacíos
        Random random = new Random();
        int randomIndex = random.nextInt(emptySpaces.size());
        return emptySpaces.get(randomIndex);
    }

    private boolean canMoveTo(int nextX, int nextY) {
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

    public boolean isMoveValid(int nextX, int nextY, int[][] scene) {
        int size = SCALE * tileSize;
        int nextX_1 = nextX / size;
        int nextY_1 = nextY / size;

        // Verifica si el siguiente movimiento colisiona con bloques sólidos (1) o bombas (3)
        return !(scene[nextY_1][nextX_1] == 1 || scene[nextY_1][nextX_1] == 3);
    }

    public void update() {
        // Mover el enemigo en la dirección actual
        switch (direction) {
            case 0: // Arriba
                if (canMoveTo(x, y - speed)) {
                    y -= speed;
                }
                break;
            case 1: // Abajo
                if (canMoveTo(x, y + speed)) {
                    y += speed;
                }
                break;
            case 2: // Izquierda
                if (canMoveTo(x - speed, y)) {
                    x -= speed;
                }
                break;
            case 3: // Derecha
                if (canMoveTo(x + speed, y)) {
                    x += speed;
                }
                break;
        }

        // Cambiar la dirección del enemigo de manera aleatoria cada cierto tiempo
        if (new Random().nextInt(100) < 5) {
            direction = new Random().nextInt(4);
        }
    }

    public void draw(Graphics2D g2, int elementSize) {
        g2.drawImage(enemyImage, x, y, elementSize, elementSize, null);
    }
}


