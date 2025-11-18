package com.juego.modelos;
// Define el paquete donde se encuentra esta clase.

import java.awt.*;
// Importa clases de AWT para gráficos y colisiones (Rectangle, Graphics, etc).

import javax.swing.ImageIcon;
// Importa ImageIcon de Swing para cargar imágenes.

public class Alien {
    // Clase que representa a un enemigo básico en el juego.

    protected int x, y; 
    // Posición del alien en pantalla (coordenadas X y Y).

    public static final int WIDTH = 45;
    public static final int HEIGHT = 30;
    // Tamaño fijo del alien (ancho y alto).

    protected boolean visible = true;
    // Flag que indica si el alien está visible (para dibujarlo o no).

    protected Image image;
    // Imagen del alien que se dibuja en pantalla.

    public Alien(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        loadImage(imagePath);
        // Constructor que inicializa posición y carga la imagen.
    }

    protected void loadImage(String path) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        image = icon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        // Carga la imagen desde recursos y la escala al tamaño del alien.
    }

    public void draw(Graphics g) {
        if (visible && image != null) {
            g.drawImage(image, x, y, WIDTH, HEIGHT, null);
            // Dibuja la imagen del alien si es visible
        }
    }

    public void move(int direction, int speed) {
        x += direction * speed;
        // Mueve el alien horizontalmente según la dirección (-1 izquierda, 1 derecha) y velocidad.
    }

    // 🔹 Método update para compatibilidad con KamikazeAlien
    public void update(int direction, int speed) {
        move(direction, speed);
        // Actualiza posición; se usa para mantener la misma interfaz que KamikazeAlien.
    }

    public void drop(int amount) {
        y += amount;
        // Baja la posición vertical del alien (cuando cambia de dirección en Space Invaders)
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
        // Devuelve un rectángulo para detectar colisiones.
    }

    // Getters y setters de posición
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // Getters y setters de visibilidad
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public void setImage(String path) {
        loadImage(path);
        // Cambia la imagen del alien si se quiere actualizar
    }
}
