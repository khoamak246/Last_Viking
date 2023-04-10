package ui;

import gamestates.GameState;
import gamestates.Playing;
import main.Game;
import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class LevelCompletedOverlay {
    private final Playing playing;
    private UrmButton urmMenuButton, urmNextButton;
    private BufferedImage backgroundContainerButton;
    private int backgroundXPos, backgroundYPos, backgroundW, backgroundH;

    public LevelCompletedOverlay(Playing playing) {
        this.playing = playing;
        initImg();
        initButton();
    }

    private void initImg() {
        backgroundContainerButton = LoadSave.GetSpriteAtlas(LoadSave.COMPLETED_IMG);
        backgroundW = (int) (backgroundContainerButton.getWidth() * Game.SCALE);
        backgroundH = (int) (backgroundContainerButton.getHeight() * Game.SCALE);
        backgroundXPos = Game.GAME_WIDTH / 2 - backgroundW / 2;
        backgroundYPos = (int) (75 * Game.SCALE);
    }

    private void initButton() {
        int menuBtnXPos = (int) (330 * Game.SCALE);
        int nextBtnXPos = (int) (445 * Game.SCALE);
        int y = (int) (195 * Game.SCALE);
        int urmSize = Constants.UI.URMButtons.URM_SIZE;
        urmNextButton = new UrmButton(nextBtnXPos, y, urmSize, urmSize, 0);
        urmMenuButton = new UrmButton(menuBtnXPos, y, urmSize, urmSize, 2);
    }


    public void draw(Graphics g) {
        g.drawImage(backgroundContainerButton, backgroundXPos, backgroundYPos, backgroundW, backgroundH, null);
        urmNextButton.draw(g);
        urmMenuButton.draw(g);
    }

    public void update() {
        urmNextButton.update();
        urmMenuButton.update();
    }

    private boolean isIn(UrmButton b, MouseEvent e) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e) {
        urmNextButton.setMouseOver(false);
        urmMenuButton.setMouseOver(false);

        if (isIn(urmMenuButton, e))
            urmMenuButton.setMouseOver(true);
        else if (isIn(urmNextButton, e))
            urmNextButton.setMouseOver(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(urmMenuButton, e)) {
            if (urmMenuButton.isMousePressed()) {
                playing.resetAll();
                playing.setGameState(GameState.MENU);
            }
        } else if (isIn(urmNextButton, e))
            if (urmNextButton.isMousePressed()) {
                playing.loadNextLevel();
                playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLvlIndex());
            }

        urmMenuButton.resetAll();
        urmNextButton.resetAll();
    }

    public void mousePressed(MouseEvent e) {
        if (isIn(urmMenuButton, e))
            urmMenuButton.setMousePressed(true);
        else if (isIn(urmNextButton, e))
            urmNextButton.setMousePressed(true);
    }
}
