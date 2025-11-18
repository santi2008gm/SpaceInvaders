package com.juego.modelos;
// Define el paquete donde se encuentra la clase.

import java.awt.*;
// Importa clases de AWT para gráficos y colisiones (Rectangle).

import javax.swing.ImageIcon;
// Importa ImageIcon de Swing para cargar imágenes.

public class BossAlien extends Alien {
    // Clase que representa al jefe final, hereda de Alien.

    private int maxHealth = 70;
    // Vida máxima del jefe.

    private int health = maxHealth;
    // Vida actual del jefe.

    private int phase = 1; // fase 1
    // Fase del jefe (1 o 2), para cambiar comportamiento según daño.

    private boolean rayActive = false;
    // Indica si el rayo del jefe está activo.

    private long rayStartTime;
    // Momento en que se activó el rayo, para medir duración.

    private long lastRayTime = 0;
    // Momento en que se lanzó el último rayo, para controlar cooldown.

    private int width, height;
    // Tamaño del jefe.

    private int dx = 2;
    // Velocidad horizontal del jefe.

    private Image image;
    // Imagen del jefe.

    public BossAlien(int x, int y, int width, int height, String imagePath) {
        super(x, y, imagePath);
        // Llama al constructor de Alien para inicializar posición e imagen base.

        this.width = width;
        this.height = height;
        this.image = new ImageIcon(getClass().getResource(imagePath)).getImage();
        // Carga la imagen del jefe sin escalado, se usará su tamaño propio.
    }

    public void damage(int amount) {
        if (health <= 0) return;
        // Si ya está muerto, no hace nada.

        health -= amount;
        // Reduce la vida según el daño recibido.

        if (health <= maxHealth / 2 && phase == 1) phase = 2;
        // Cuando llega a la mitad de vida, cambia a fase 2.

        if (health < 0) health = 0;
        // Evita valores negativos de vida.
    }

    public void update() {
        x += dx;
        // Mueve el jefe horizontalmente según dx.

        if (x <= 0 || x + width >= 800) dx = -dx;
        // Si llega a los bordes de la pantalla, invierte dirección.

        if (phase == 2) {
            long now = System.currentTimeMillis();
            // Controla el tiempo actual para rayo.

            if (!rayActive && now - lastRayTime >= 10000) {
                rayActive = true;
                rayStartTime = now;
                lastRayTime = now;
                // Activa el rayo cada 10 segundos.
            }

            if (rayActive && now - rayStartTime >= 2000) {
                rayActive = false;
                // Desactiva el rayo después de 2 segundos de estar activo.
            }
        }
    }

    public boolean isRayActive() { return rayActive; }
    // Devuelve si el rayo está activo.

    public void setRayActive(boolean active) {
        this.rayActive = active;
    }
    // Permite activar o desactivar el rayo manualmente.

    public Rectangle getRayBounds() {
        if (!rayActive) return new Rectangle(0,0,0,0);
        // Si el rayo no está activo, devuelve un rectángulo vacío.

        int rayWidth = 40;
        return new Rectangle(x + width/2 - rayWidth/2, y + height, rayWidth, 600);
        // Devuelve el rectángulo del rayo, usado para detectar colisión con el jugador.
    }

    // Getters de atributos importantes
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Image getImage() { return image; }
    public int getHealth() { return health; }
    public boolean isDead() { return health <= 0; }
    public int getPhase() { return phase; }
}