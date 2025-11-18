package com.juego.modelos;
// Define el paquete donde se encuentra la clase.

import javax.swing.*;
// Importa Swing para usar ImageIcon y cargar imágenes.

import java.awt.*;
// Importa AWT para usar Graphics y Rectangle (colisiones).

public class Bullet {
    private int x, y;
    // Coordenadas de la bala en pantalla.

    private int dy;
    // Velocidad vertical de la bala; positiva hacia abajo, negativa hacia arriba.

    public static final int WIDTH = 6;   
    public static final int HEIGHT = 20; 
    // Tamaño de la bala (ancho y alto).

    private boolean visible = true;
    // Indica si la bala sigue activa (visible) o debe eliminarse.

    private Image bulletImage;
    // Imagen que representa la bala en pantalla.

    private final boolean fromAlien;
    // Indica si la bala fue disparada por un alien (true) o por el jugador (false).

    public Bullet(int x, int y, boolean isAlienBullet) {
        this.x = x;
        this.y = y + 20;
        // Inicializa la posición de la bala. Ajuste de +20 para centrarla al disparar.

        this.dy = isAlienBullet ? 2 : -4;
        // Si la bala es de un alien, baja lentamente (2 px por frame), 
        // si es del jugador, sube más rápido (-4 px por frame).

        this.fromAlien = isAlienBullet;
        // Guarda el origen de la bala (jugador o alien).

        // Cargar imagen de la bala
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/bala_rebeldes.png"));
        bulletImage = icon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        // Carga la imagen desde recursos y la ajusta al tamaño de la bala.
    }

    public void move() {
        y += dy;
        // Mueve la bala según su velocidad vertical.

        if (y < 0 || y > 600) {
            visible = false;
        }
        // Si la bala sale de la pantalla, se vuelve invisible para eliminarla del juego.
    }

    public void draw(Graphics g) {
        if (visible) {
            g.drawImage(bulletImage, x, y, null);
        }
        // Dibuja la bala en pantalla solo si sigue activa.
    }

    // Getters y setters para atributos importantes:
    public int getX() { return x; }
    public int getY() { return y; }

    public Rectangle getBounds() { return new Rectangle(x, y, WIDTH, HEIGHT); }
    // Devuelve el rectángulo de colisión de la bala.

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public int getDy() { return dy; }
    // Devuelve la velocidad vertical.

    public boolean isFromAlien() { return fromAlien; }
    // Devuelve si la bala fue disparada por un alien.
}
