package com.juego.spaceinvaders;
// Define el paquete donde se encuentra la clase.

import javax.swing.*;
// Importa Swing para JFrame, JPanel y utilidades gráficas.

import java.awt.*;
// Importa AWT para manejar gráficos y colores.

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
// Importa eventos de teclado.

public class Menu extends JFrame {
    // La clase Menu extiende JFrame, por lo que es una ventana principal del juego.

    public Menu() {
        setTitle("Space Invaders - Menú");
        // Título de la ventana.

        setSize(800, 600);
        // Tamaño de la ventana.

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Cierra la aplicación al cerrar la ventana.

        setLocationRelativeTo(null);
        // Centra la ventana en la pantalla.

        setResizable(false);
        // Evita que el usuario cambie el tamaño de la ventana.

        MenuPanel menuPanel = new MenuPanel();
        // Crea un panel personalizado para el menú.

        add(menuPanel);
        // Agrega el panel al JFrame.

        menuPanel.setFocusable(true);
        // Permite que el panel reciba el foco para capturar teclas.

        menuPanel.requestFocusInWindow();
        // Solicita que el panel tenga el foco de inmediato.
    }

    private void startGame() {
        // Método que inicia el juego cuando se pulsa ESPACIO.
        JFrame gameFrame = new JFrame("Space Invaders");
        // Nueva ventana para el juego.

        SpaceInvadersGame gamePanel = new SpaceInvadersGame(1); 
        // Crea el panel del juego, comenzando en nivel 1.

        gameFrame.add(gamePanel);
        // Agrega el panel de juego a la ventana.

        gameFrame.pack();
        // Ajusta la ventana al tamaño preferido del panel.

        gameFrame.setResizable(false);
        // Evita que la ventana de juego se redimensione.

        gameFrame.setLocationRelativeTo(null);
        // Centra la ventana del juego en la pantalla.

        gameFrame.setVisible(true);
        // Muestra la ventana.

        gamePanel.requestFocusInWindow();
        // Da foco al panel del juego para capturar teclas.

        dispose();
        // Cierra el menú una vez iniciado el juego.
    }

    private class MenuPanel extends JPanel {
        // Panel interno que dibuja el menú y escucha teclas.

        public MenuPanel() {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        // Si se pulsa la tecla ESPACIO, inicia el juego.
                        startGame();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Llama al método original para limpiar el panel.

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            // Pinta el fondo negro de todo el panel.

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            drawCenteredString(g, "SPACE INVADERS", getWidth(), getHeight() / 2 - 50);
            // Dibuja el título centrado.

            g.setFont(new Font("Arial", Font.PLAIN, 30));
            drawCenteredString(g, "Pulsa ESPACIO para comenzar", getWidth(), getHeight() / 2 + 30);
            // Dibuja el texto de instrucción centrado debajo del título.
        }

        private void drawCenteredString(Graphics g, String text, int width, int y) {
            // Método auxiliar para centrar texto horizontalmente.
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int x = (width - metrics.stringWidth(text)) / 2;
            g.drawString(text, x, y);
        }
    }

    public static void main(String[] args) {
        // Método principal que arranca la aplicación.
        SwingUtilities.invokeLater(() -> new Menu().setVisible(true));
        // Crea la ventana del menú en el hilo de eventos de Swing.
    }
}
