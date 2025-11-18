package com.juego.modelos;
// Define el paquete donde se encuentra la clase.

import java.awt.*;
// Importa AWT para usar Graphics y Rectangle (para colisiones).

import javax.swing.ImageIcon;
// Importa Swing para manejar imágenes.

public class KamikazeAlien extends Alien {
    // KamikazeAlien es un tipo de Alien que se mueve directamente hacia el jugador cuando se activa.

    private boolean active = false; 
    // Indica si el kamikaze está activo. Solo se mueve cuando active = true.

    private Player player; 
    // Referencia al jugador para conocer su posición y calcular movimiento hacia él.

    private boolean collidedWithPlayer = false; 
    // Indica si el kamikaze colisionó con el jugador.

    private int targetX, targetY; 
    // Guarda la posición del jugador al momento de activarse, hacia donde se dirige.

    public KamikazeAlien(int x, int y, Player player, String imagePath) {
        super(x, y, imagePath);
        // Llama al constructor de Alien para inicializar posición e imagen.
        this.player = player;
        // Guarda la referencia al jugador para usar su posición en movimiento.
    }

    public void setActive(boolean val) {
        if (val && !active) {
            // Si se activa por primera vez, registra la posición actual del jugador
            targetX = player.getX();
            targetY = player.getY();
        }
        active = val; 
        // Cambia el estado de activación del kamikaze.
    }

    public boolean isActive() {
        return active; 
        // Devuelve si el kamikaze está activo.
    }

    public boolean isCollidedWithPlayer() {
        return collidedWithPlayer; 
        // Devuelve si ya colisionó con el jugador.
    }

    public void update() {
        if (!active || !isVisible()) return;
        // Solo se actualiza si está activo y visible.

        // Calcular vector hacia la posición registrada del jugador
        int dx = targetX - getX();
        int dy = targetY - getY();

        double dist = Math.sqrt(dx*dx + dy*dy);
        if (dist == 0) dist = 1; 
        // Evita división por cero si ya está exactamente en el objetivo.

        int speed = 6; 
        // Velocidad del kamikaze

        int moveX = (int) Math.round(speed * dx / dist);
        int moveY = (int) Math.round(speed * dy / dist);
        // Normaliza el vector de dirección y multiplica por velocidad para moverse proporcionalmente.

        setX(getX() + moveX);
        setY(getY() + moveY);
        // Actualiza posición del kamikaze hacia el objetivo.

        // Si llega al objetivo sin chocar con el jugador, se destruye
        if (Math.abs(getX() - targetX) <= speed && Math.abs(getY() - targetY) <= speed) {
            setVisible(false);
        }

        // Detectar colisión con jugador
        Rectangle alienRect = getBounds();
        Rectangle playerRect = player.getBounds();
        if (alienRect.intersects(playerRect)) {
            collidedWithPlayer = true;
            setVisible(false);
        }
        // Marca la colisión y oculta el kamikaze al chocar con el jugador.
    }

    @Override
    public void draw(Graphics g) {
        if (isVisible()) super.draw(g);
        // Dibuja el kamikaze solo si está visible.
    }
}
