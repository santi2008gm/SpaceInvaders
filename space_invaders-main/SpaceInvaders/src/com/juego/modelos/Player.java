package com.juego.modelos;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

public class Player {
    
    // Posición del jugador en la pantalla
    private int x, y;

    // Velocidad o desplazamiento horizontal
    private int dx;

    // Dimensiones del jugador
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;

    // Indicadores de estado del jugador
    private boolean destroyed = false; // Si el jugador fue destruido
    private boolean visible = true;    // Si el jugador se dibuja en pantalla

    // Determina de qué lado disparará la próxima bala
    private boolean shootLeftNext = true;

    // Vidas del jugador
    private int lives = 3;

    // Variables para rastrear las teclas presionadas
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Imagen del jugador
    private Image image;
    
    // Constructor del jugador
    public Player(int x, int y) {
        this.x = x;           // Posición inicial X
        this.y = y;           // Posición inicial Y
        this.destroyed = false; // Inicialmente no está destruido
        this.visible = true;    // Visible por defecto

        // Cargar imagen del jugador desde recursos
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/caza estelar.png"));
        this.image = icon.getImage();
    }

    // Método para reiniciar la posición del jugador
    public void resetPosition(int startX, int startY) {
        this.x = startX;       // Nueva posición X
        this.y = startY;       // Nueva posición Y
        this.destroyed = false; // Asegura que el jugador no esté destruido
    }
    
    // Método que actualiza la posición del jugador en cada frame
    public void move() {
        x += dx; // Actualiza X sumando la velocidad horizontal

        // Limitar al jugador dentro de la pantalla
        if (x < 0) x = 0;            // No puede ir más a la izquierda de la pantalla
        if (x > 800 - WIDTH) x = 800 - WIDTH; // No puede ir más a la derecha (800 = ancho pantalla)
    }

    // Devuelve la posición X desde donde se dispara la bala
    public int getShootX() {
        int offset = 10; // Separación del centro del jugador

        if (shootLeftNext) { 
            // Disparo izquierdo
            shootLeftNext = false; // Alterna para la próxima bala
            return x + offset - 40; // Ajuste para cañón izquierdo
        } else {
            // Disparo derecho
            shootLeftNext = true; // Alterna para la próxima bala
            return x + WIDTH - offset - Bullet.WIDTH - 35; // Ajuste para cañón derecho
        }
    }
    
    // Dibuja el jugador en pantalla
    public void draw(Graphics g) {
        if (!destroyed && visible) { // Solo dibuja si no está destruido y es visible
            g.drawImage(image, x, y, WIDTH, HEIGHT, null);
        }
    }

    // Maneja teclas presionadas
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) { // Movimiento izquierda
            leftPressed = true;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) { // Movimiento derecha
            rightPressed = true;
        }
        updateMovement(); // Actualiza dx según teclas presionadas
    }

    // Maneja teclas liberadas
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            rightPressed = false;
        }
        updateMovement(); // Actualiza dx según teclas liberadas
    }

    // Actualiza la velocidad dx según el estado de las teclas
    private void updateMovement() {
        if (leftPressed && !rightPressed) {
            dx = -3; // Mueve a la izquierda
        } else if (rightPressed && !leftPressed) {
            dx = 3; // Mueve a la derecha
        } else {
            dx = 0; // No se mueve
        }
    }
    
    // Reduce la vida del jugador en 1
    public void decreaseLife() {
        lives--;
        if (lives < 0) lives = 0; // Evita vidas negativas
    }
    
    // Métodos para acceder y modificar el estado del jugador
    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    // Getters de posición
    public int getX() { return x; }
    public int getY() { return y; }

    // Devuelve un rectángulo de colisión para detección de impactos
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
