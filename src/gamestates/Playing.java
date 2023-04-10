package gamestates;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    private boolean paused = false;
    private int xLvOffset;
    private int maxLvOffsetX;
    private final BufferedImage backgroundImg, bigCloud, smallCloud;
    private final int[] smallCloudsPos;
    private boolean gameOver;
    private boolean lvlCompleted;
    private boolean playerDying;

    public Playing(Game game) {
        super(game);
        initClasses();
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        Random randomNumber = new Random();
        for (int i = 0; i < smallCloudsPos.length; i++)
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + randomNumber.nextInt((int) (100 * Game.SCALE));

        calcLvlOffset();
        loadStartLevel();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
    }

    private void calcLvlOffset() {
        maxLvOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        objectManager.loadObjects(levelManager.getCurrentLevel());
    }

    public void loadNextLevel() {
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        resetAll();
    }

    @Override
    public void update() {
        if (paused) {
            pauseOverlay.update();
        } else if (lvlCompleted) {
            levelCompletedOverlay.update();
        } else if (gameOver) {
            gameOverOverlay.update();
        } else if (playerDying) {
            player.update();
        } else {
            levelManager.update();
            objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            checkCloseToBorder();
        }
    }

    private void checkCloseToBorder() {
        int lvlLeftRange = (int) (0.2 * Game.GAME_WIDTH);
        int lvlRightRange = (int) (0.8 * Game.GAME_WIDTH);
        int playerX = (int) player.getHitBox().x;
        int diff = playerX - xLvOffset;
        if (diff > lvlRightRange)
            xLvOffset += diff - lvlRightRange;
        else if (diff < lvlLeftRange)
            xLvOffset += diff - lvlLeftRange;

        if (xLvOffset > maxLvOffsetX)
            xLvOffset = maxLvOffsetX;
        else if (xLvOffset < 0)
            xLvOffset = 0;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);
        levelManager.draw(g, xLvOffset);
        player.render(g, xLvOffset);
        enemyManager.draw(g, xLvOffset);
        objectManager.draw(g, xLvOffset);
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);

        } else if (gameOver)
            gameOverOverlay.draw(g);
        else if (lvlCompleted)
            levelCompletedOverlay.draw(g);
    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 3; i++)
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int) (xLvOffset * 0.3), (int) (204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

        for (int i = 0; i < smallCloudsPos.length; i++) {
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLvOffset * 0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
        }
    }

    public void resetAll() {
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        playerDying = false;
        player.resetAll();
        enemyManager.resetAllEnemies();
        objectManager.resetAllObjects();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) {
        objectManager.checkObjectHit(attackBox);
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public void checkPotionTouched(Rectangle2D.Float hitBox) {
        objectManager.checkObjectTouched(hitBox);
    }

    public void checkSpikesTouched(Player p) {
        objectManager.checkSpikesTouched(p);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver) {
            if (e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
            else if (e.getButton() == MouseEvent.BUTTON3)
                player.powerAttack();
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (!gameOver)
            if (paused)
                pauseOverlay.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mousePressed(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mousePressed(e);
        } else
            gameOverOverlay.mousePressed(e);


        if (!gameOver) {
            if (e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
            else if (e.getButton() == MouseEvent.BUTTON3)
                player.powerAttack();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mouseReleased(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseReleased(e);
        } else
            gameOverOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mouseMoved(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseMoved(e);
        } else
            gameOverOverlay.mouseMoved(e);
    }

    public void setLevelCompleted(boolean lvlCompleted) {
        this.lvlCompleted = lvlCompleted;
        if (lvlCompleted)
            game.getAudioPlayer().lvlCompleted();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            gameOverOverlay.keyPressed(e);
        else
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
            }
    }

    public void maxLvOffset(int lvlOffset) {
        this.maxLvOffsetX = lvlOffset;
    }

    public void unpauseGame() {
        paused = false;
    }

    public Player getPlayer() {
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setPlayerDying(boolean playerDying) {
        this.playerDying = playerDying;
    }
}
