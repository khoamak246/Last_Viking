package ui;

import gamestates.GameState;
import gamestates.Playing;
import main.Game;
import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class GameOverOverlay {
    private final Playing playing;

    private BufferedImage backgroundContainerButton;

    private int backgroundXPos, backgroundYPos, backgroundW, backgroundH;
    private UrmButton urmMenuButton, urmPlayButton;

    public GameOverOverlay(Playing playing) {
        this.playing = playing;
        createImg();
        createButtons();
    }

    private void createButtons() {
        int menuBtnX = (int) (335 * Game.SCALE);
        int playBtnX = (int) (440 * Game.SCALE);
        int y = (int) (195 * Game.SCALE);
        int urmSize = Constants.UI.URMButtons.URM_SIZE;
        urmPlayButton = new UrmButton(playBtnX, y, urmSize, urmSize, 0);
        urmMenuButton = new UrmButton(menuBtnX, y, urmSize, urmSize, 2);

    }


    private void createImg() {
        backgroundContainerButton = LoadSave.GetSpriteAtlas(LoadSave.DEATH_SCREEN);
        backgroundW = (int) (backgroundContainerButton.getWidth() * Game.SCALE);
        backgroundH = (int) (backgroundContainerButton.getHeight() * Game.SCALE);
        backgroundXPos = Game.GAME_WIDTH / 2 - backgroundW / 2;
        backgroundYPos = (int) (100 * Game.SCALE);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.drawImage(backgroundContainerButton, backgroundXPos, backgroundYPos, backgroundW, backgroundH, null);

        urmMenuButton.draw(g);
        urmPlayButton.draw(g);
    }

    public void update() {
        urmMenuButton.update();
        urmPlayButton.update();
    }

    public void keyPressed(KeyEvent e) {

    }


    private boolean isIn(UrmButton b, MouseEvent e) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e) {
        urmPlayButton.setMouseOver(false);
        urmMenuButton.setMouseOver(false);

        if (isIn(urmMenuButton, e))
            urmMenuButton.setMouseOver(true);
        else if (isIn(urmPlayButton, e))
            urmPlayButton.setMouseOver(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(urmMenuButton, e)) {
            if (urmMenuButton.isMousePressed()) {
                playing.resetAll();
                playing.setGameState(GameState.MENU);
            }
        } else if (isIn(urmPlayButton, e))
            if (urmPlayButton.isMousePressed()) {
                playing.resetAll();
                playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLvlIndex());
            }

        urmMenuButton.resetAll();
        urmPlayButton.resetAll();
    }

    public void mousePressed(MouseEvent e) {
        if (isIn(urmMenuButton, e))
            urmMenuButton.setMousePressed(true);
        else if (isIn(urmPlayButton, e))
            urmPlayButton.setMousePressed(true);
    }
}

