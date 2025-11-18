package com.juego.spaceinvaders; 
// Define el paquete donde se encuentra esta clase.

import com.juego.modelos.Alien;
import com.juego.modelos.Bullet;
import com.juego.modelos.Player;
import com.juego.modelos.KamikazeAlien;
import com.juego.modelos.BossAlien;
// Importa las clases de modelos: Alien, Kamikaze, jefe, jugador y balas.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
// Importa librerías de Swing para gráficos y eventos, y utilidades como listas y colecciones.

public class SpaceInvadersGame extends JPanel implements ActionListener {
    // Clase principal del juego que extiende JPanel (para dibujar) e implementa ActionListener para manejar el timer.

    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 600;
    private final int DELAY = 10;
    // Tamaño de la pantalla del juego y retraso del timer (10 ms).

    private long levelStartTime; // cuándo arranca el nivel
    private static final long KAMIKAZE_DELAY = 5000; // 5 segundos de espera
    // Variables de tiempo para controlar eventos especiales como kamikazes.

    private int level = 1; // Nivel actual
    private int lives = 3; // Vidas del jugador
    private String message = ""; // Mensaje que se muestra en pantalla
    private boolean gameOver = false; // Flag de juego terminado
    private boolean gameFinished = false; // Flag de juego finalizado (boss vencido)

    private long kamikazeHighlightStart = 0;
    private boolean kamikazeHighlightActive = false;
    private boolean firstBatchLaunched = false;
    private boolean secondBatchLaunched = false;
    // Variables para controlar kamikazes y animaciones especiales.

    private BossAlien boss; // Campo para el jefe del nivel 3
    private final long KAMIKAZE_PHASE2_COOLDOWN = 10000; // 10 seg
    private long lastPhase2KamikazeTime = 0; 
    // Variables para controlar el cooldown de la fase 2 de kamikazes del jefe.

    private Player player; // Jugador
    private List<Alien> aliens; // Lista de enemigos normales y kamikazes
    private List<Bullet> bullets; // Lista de balas
    private javax.swing.Timer timer; // Timer que actualiza el juego periódicamente
    private boolean showHitboxes = false; // Mostrar hitboxes para depuración
    private int alienDrops = 0; // Contador de veces que los aliens bajaron fila

    private int alienDirection = 1; // Dirección de movimiento de los aliens (1 = derecha, -1 = izquierda)
    private int alienSpeed = 5; // Velocidad de los aliens
    private int alienDropAmount = 10; // Cuánto bajan los aliens al cambiar de dirección
    private int score = 0; // Puntaje del jugador
    private long lastShotTime = 0; // Control de cooldown del disparo
    private final long SHOOT_COOLDOWN = 300; // Tiempo mínimo entre disparos en ms

    // Kamikazes
    private List<KamikazeAlien> pendingKamikazes = new ArrayList<>();
    private int kamikazesPerBatch = 5; // Cantidad de kamikazes por tanda
    private long lastKamikazeLaunchTime = 0; // Última vez que se lanzó una tanda
    private final long KAMIKAZE_COOLDOWN = 2000; // Tiempo entre tandas en ms

    public SpaceInvadersGame(int startLevel) {
        this.level = startLevel; // Nivel inicial
        setFocusable(true); // Permite recibir eventos de teclado
        setBackground(Color.BLACK); // Fondo negro
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); // Tamaño del panel

        player = new Player(SCREEN_WIDTH / 2 - 40, SCREEN_HEIGHT - 70); // Crear jugador
        bullets = new ArrayList<>(); // Lista vacía de balas
        aliens = new ArrayList<>(); // Lista vacía de aliens

        initAliensForLevel(level); // Inicializar aliens según el nivel

        addKeyListener(new TAdapter()); // Añadir escucha de teclado
        timer = new javax.swing.Timer(DELAY, this); // Crear timer para actualizar juego
        timer.start(); // Iniciar timer
    }

    private void initAliensForLevel(int lvl) {
        aliens.clear(); // Limpiar lista de aliens
        pendingKamikazes.clear(); // Limpiar lista de kamikazes pendientes

        int startX = 50;
        int startY = 30;
        int spacingX = 50;
        int spacingY = 50;
        int cols = 10; // Configuración de filas y columnas

        if (lvl == 1) { // Nivel 1
            alienSpeed = 3;
            for (int r = 0; r < 5; r++) { // 5 filas
                for (int c = 0; c < cols; c++) { // 10 columnas
                    aliens.add(new Alien(startX + c * spacingX, startY + r * spacingY, "/resources/Caza_TIE.png"));
                }
            }
            return;
        }

        if (lvl == 2) { // Nivel 2
            alienSpeed = 5;
            Set<Integer> kamis = new HashSet<>();
            Random rnd = new Random();
            while (kamis.size() < 10) { // Elegir 10 aliens como kamikazes
                int r = rnd.nextInt(4) + 1;
                int c = rnd.nextInt(cols);
                kamis.add(r * cols + c);
            }

            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < cols; c++) {
                    int x = startX + c * spacingX;
                    int y = startY + r * spacingY;

                    if (kamis.contains(r * cols + c)) {
                        KamikazeAlien k = new KamikazeAlien(x, y, player, "/resources/Caza_TIE.png");
                        aliens.add(k);
                        pendingKamikazes.add(k);
                    } else {
                        aliens.add(new Alien(x, y, "/resources/Caza_TIE.png"));
                    }
                }
            }
            return;
        }

        if (lvl == 3) { // Nivel 3 con jefe
            aliens.clear();
            pendingKamikazes.clear();

            boss = new BossAlien(200, 50, 400, 200, "/resources/death_star.png"); // Crear jefe

            int startXAliens = 50;
            int spacingXAliens = 60;
            for (int i = 0; i < 6; i++) { // Agregar 6 aliens normales
                aliens.add(new Alien(startXAliens + i * spacingXAliens, 0, "/resources/Caza_TIE.png"));
            }

            lastKamikazeLaunchTime = System.currentTimeMillis(); // Reiniciar tiempos
            levelStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpiar pantalla

        if (!gameOver) {
            // --- Juego activo ---
            player.draw(g); // Dibujar jugador
            for (Alien a : aliens) a.draw(g); // Dibujar aliens
            for (Bullet b : bullets) b.draw(g); // Dibujar balas

            // Mostrar puntaje y vidas
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Vidas: " + lives, 700, 20);

            // Dibujar hitboxes si está activado
            if (showHitboxes) {
                g.setColor(Color.RED);
                Rectangle pRect = player.getBounds();
                g.drawRect(pRect.x, pRect.y, pRect.width, pRect.height);

                for (Alien a : aliens) {
                    Rectangle aRect = a.getBounds();
                    g.drawRect(aRect.x, aRect.y, aRect.width, aRect.height);
                }

                for (Bullet b : bullets) {
                    Rectangle bRect = b.getBounds();
                    g.drawRect(bRect.x, bRect.y, bRect.width, bRect.height);
                }
            }

            // Dibujar jefe si nivel 3
            if (level == 3 && boss != null && boss.isVisible()) {
                g.drawImage(boss.getImage(), boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight(), this);
                if (boss.isRayActive()) { // Si el rayo está activo
                    Rectangle ray = boss.getRayBounds();
                    g.setColor(Color.GREEN);
                    g.fillRect(ray.x, ray.y, ray.width, ray.height);
                }
            }

        } else {
            // --- Pantalla de fin ---
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));

            String finalMessage = (message != null && !message.isEmpty()) ? message : "GAME OVER";
            drawCenteredString(g, finalMessage, SCREEN_WIDTH, SCREEN_HEIGHT / 2 - 50);

            g.setFont(new Font("Arial", Font.PLAIN, 30));
            if (finalMessage.equals("GAME OVER")) {
                drawCenteredString(g, "Presiona R para reintentar", SCREEN_WIDTH, SCREEN_HEIGHT / 2 + 10);
            } else if (finalMessage.equals("¡Juego Finalizado!")) {
                // Aquí se podría agregar mensaje final
            }
        }
    }

    private void drawCenteredString(Graphics g, String text, int width, int y) {
        FontMetrics fm = g.getFontMetrics(); // Para centrar texto
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame(); // Actualizar lógica del juego
        repaint(); // Redibujar pantalla
    }

    private void updateAliens() {
        boolean changeDir = false;
        int minY = aliens.stream().mapToInt(Alien::getY).min().orElse(Integer.MAX_VALUE);

        for (Alien a : aliens) {
            if (!(a instanceof KamikazeAlien && !((KamikazeAlien) a).isActive())) {
                a.move(alienDirection, alienSpeed); // Mover alien
            }
            if (a.getX() <= 0 || a.getX() >= SCREEN_WIDTH - Alien.WIDTH) changeDir = true; // Detectar bordes
        }

        if (changeDir) {
            alienDirection *= -1; // Cambiar dirección
            for (Alien a : aliens) {
                if (a.getY() > minY) a.drop(alienDropAmount); // Bajar fila
            }
            alienDrops++;
        }
    }

    private void updateGame() {
        long now = System.currentTimeMillis(); // Tiempo actual

        if (gameOver) return; // No actualizar si juego terminado

        player.move(); // Mover jugador
        updateAliens(); // Mover aliens

        // Lógica kamikazes nivel 2
        if (level == 2 && !pendingKamikazes.isEmpty() && now - lastKamikazeLaunchTime >= 5000) {
            int launches = Math.min(5, pendingKamikazes.size());
            for (int i = 0; i < launches; i++) {
                KamikazeAlien k = pendingKamikazes.remove(0);
                k.setActive(true); // Activar kamikaze
            }
            lastKamikazeLaunchTime = now;
        }

        // Kamikazes fase 2 jefe (nivel 3)
        if (level == 3 && boss != null && boss.getPhase() == 2
                && now - lastPhase2KamikazeTime >= KAMIKAZE_PHASE2_COOLDOWN) {
            for (int i = 0; i < 3; i++) {
                aliens.add(new KamikazeAlien(0, i * 30, player, "/resources/Caza_TIE.png")); 
                aliens.add(new KamikazeAlien(SCREEN_WIDTH - 50, i * 30, player, "/resources/Caza_TIE.png")); 
            }
            lastPhase2KamikazeTime = now;
        }

        // Disparo automático aliens superiores
        if (Math.random() < 0.01 && !aliens.isEmpty()) {
            int minY = aliens.stream().filter(Alien::isVisible).mapToInt(Alien::getY).min().orElse(Integer.MAX_VALUE);
            for (Alien a : aliens) {
                if (a.isVisible() && a.getY() == minY) {
                    bullets.add(new Bullet(a.getX() + Alien.WIDTH / 2 - Bullet.WIDTH / 2, a.getY() + Alien.HEIGHT, true));
                    break;
                }
            }
        }

        // Kamikazes activos colisionan con jugador
        Iterator<Alien> alienIter = aliens.iterator();
        while (alienIter.hasNext()) {
            Alien a = alienIter.next();
            if (a instanceof KamikazeAlien) {
                KamikazeAlien k = (KamikazeAlien) a;
                if (k.isActive()) {
                    k.update();
                    if (k.isCollidedWithPlayer()) {
                        lives = Math.max(0, lives - 1);
                        k.setVisible(false);
                        alienIter.remove();
                        if (lives == 0) {
                            gameOver = true;
                            message = "GAME OVER";
                            timer.stop();
                        }
                    }
                }
            }
        }

        // Actualizar jefe
        if (level == 3 && boss != null && boss.isVisible()) {
            boss.update();
            if (boss.isRayActive()) {
                Rectangle rayRect = boss.getRayBounds();
                if (rayRect.intersects(player.getBounds())) {
                    lives = Math.max(0, lives - 1);
                    boss.setRayActive(false);
                    if (lives == 0) {
                        gameOver = true;
                        message = "GAME OVER";
                        timer.stop();
                    }
                }
            }
        }

        // Mover balas
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.move();
            if (!b.isVisible()) it.remove();
        }

        // Colisiones balas vs jefe y aliens
        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet b = bIter.next();
            if (!b.isVisible() || b.isFromAlien()) continue;

            boolean impact = false;

            if (level == 3 && boss != null && boss.isVisible()
                    && b.getBounds().intersects(new Rectangle(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight()))) {
                boss.damage(1);
                b.setVisible(false);
                bIter.remove();
                impact = true;

                if (boss.isDead()) {
                    gameOver = true;
                    message = "¡Juego Finalizado!";
                    timer.stop();
                }
            }

            if (impact) continue;

            Iterator<Alien> aIter = aliens.iterator();
            while (aIter.hasNext()) {
                Alien a = aIter.next();
                if (!(a instanceof BossAlien) && b.getBounds().intersects(a.getBounds())) {
                    b.setVisible(false);
                    bIter.remove();
                    a.setVisible(false);
                    aIter.remove();
                    score += 10;
                    break;
                }
            }
        }

        // Colisiones balas vs jugador
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet b = bulletIter.next();
            if (b.isVisible() && b.isFromAlien() && b.getBounds().intersects(player.getBounds())) {
                b.setVisible(false);
                bulletIter.remove();
                lives = Math.max(0, lives - 1);
                if (lives == 0) {
                    gameOver = true;
                    message = "GAME OVER";
                    timer.stop();
                } else {
                    player.resetPosition(SCREEN_WIDTH / 2 - Player.WIDTH / 2, SCREEN_HEIGHT - 70);
                }
                break;
            }
        }

        // Transición de niveles
        if (level == 1 && allNormalAliensDead() && !gameOver) {
            level = 2;
            message = "¡Nivel 2!";
            repaint();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            initAliensForLevel(level);
        }

        if (level == 2 && allNormalAliensDead() && !gameOver) {
            level = 3;
            message = "¡Nivel 3!";
            repaint();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            initAliensForLevel(level);
        }
    }

    private boolean allNormalAliensDead() { 
        for (Alien a : aliens) {
            if (!(a instanceof KamikazeAlien)) return false; // Revisar si quedan aliens normales
        }
        return true;
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (!gameOver && !gameFinished) {
                player.keyPressed(e); // Mover jugador

                if (key == KeyEvent.VK_SPACE) { // Disparo jugador
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastShotTime >= SHOOT_COOLDOWN) {
                        int bulletX = player.getShootX() + Player.WIDTH / 2 - Bullet.WIDTH / 2;
                        bullets.add(new Bullet(bulletX, player.getY(), false));
                        lastShotTime = currentTime;
                    }
                }

                if (key == KeyEvent.VK_H) showHitboxes = !showHitboxes; // Mostrar hitboxes

            } else {
                if (gameOver && key == KeyEvent.VK_R) restartLevel(); // Reiniciar
                else if (gameFinished && key == KeyEvent.VK_SPACE) System.exit(0); // Cerrar juego
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (!gameOver && !gameFinished) player.keyReleased(e); // Detener movimiento
        }
    }

    private void restartLevel() {
        gameOver = false;
        player.resetPosition(SCREEN_WIDTH / 2 - Player.WIDTH / 2, SCREEN_HEIGHT - 70);
        lives = 3;
        bullets.clear();
        initAliensForLevel(level);

        firstBatchLaunched = false;
        secondBatchLaunched = false;
        lastKamikazeLaunchTime = System.currentTimeMillis();
        levelStartTime = System.currentTimeMillis();

        timer.start();
    }

    private void returnToMenu() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.getContentPane().removeAll();
        topFrame.getContentPane().add(new Menu());
        topFrame.revalidate();
        topFrame.repaint();
    }
}
