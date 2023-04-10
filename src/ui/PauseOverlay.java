package ui;

import gamestates.GameState;
import gamestates.Playing;
import main.Game;
import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PauseOverlay {
    private final Playing playing;
    private BufferedImage backgroundContainerButton;
    private int backgroundXPos, backgroundYPos, backgroundW, backgroundH;
    private final AudioOptions audioOptions;
    private UrmButton urmMenuButton, urmReplayButton, urmUnpauseButton;


    public PauseOverlay(Playing playing) {
        this.playing = playing;
        loadBackground();
        audioOptions = playing.getGame().getAudioOptions();
        createUrmButtons();
    }

    private void loadBackground() {
        backgroundContainerButton = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        backgroundW = (int) (backgroundContainerButton.getWidth() * Game.SCALE);
        backgroundH = (int) (backgroundContainerButton.getHeight() * Game.SCALE);
        backgroundXPos = Game.GAME_WIDTH / 2 - backgroundW / 2;
        backgroundYPos = (int) (25 * Game.SCALE);
    }


    private void createUrmButtons() {
        int menuBtnXPos = (int) (313 * Game.SCALE);
        int replayBtnXPos = (int) (387 * Game.SCALE);
        int unpauseBtnXPos = (int) (462 * Game.SCALE);
        int bY = (int) (325 * Game.SCALE);
        int urmSize = Constants.UI.URMButtons.URM_SIZE;
        urmMenuButton = new UrmButton(menuBtnXPos, bY, urmSize, urmSize, 2);
        urmReplayButton = new UrmButton(replayBtnXPos, bY, urmSize, urmSize, 1);
        urmUnpauseButton = new UrmButton(unpauseBtnXPos, bY, urmSize, urmSize, 0);
    }


    public void update() {

        urmMenuButton.update();
        urmReplayButton.update();
        urmUnpauseButton.update();
        audioOptions.update();

    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundContainerButton, backgroundXPos, backgroundYPos, backgroundW, backgroundH, null);


        //UrmButtons
        urmMenuButton.draw(g);
        urmReplayButton.draw(g);
        urmUnpauseButton.draw(g);

        audioOptions.draw(g);
    }

    public void mouseDragged(MouseEvent e) {
        audioOptions.mouseDragged(e);
    }


    public void mousePressed(MouseEvent e) {
        if (isIn(e, urmMenuButton))
            urmMenuButton.setMousePressed(true);
        else if (isIn(e, urmReplayButton))
            urmReplayButton.setMousePressed(true);
        else if (isIn(e, urmUnpauseButton))
            urmUnpauseButton.setMousePressed(true);
        else
            audioOptions.mousePressed(e);
    }


    public void mouseReleased(MouseEvent e) {
        if (isIn(e, urmMenuButton)) {
            if (urmMenuButton.isMousePressed()) {
                playing.resetAll();
                playing.setGameState(GameState.MENU);
                playing.unpauseGame();
            }
        } else if (isIn(e, urmReplayButton)) {
            if (urmReplayButton.isMousePressed()) {
                playing.resetAll();
                playing.unpauseGame();
            }
        } else if (isIn(e, urmUnpauseButton)) {
            if (urmUnpauseButton.isMousePressed())
                playing.unpauseGame();
        } else
            audioOptions.mouseReleased(e);


        urmMenuButton.resetAll();
        urmReplayButton.resetAll();
        urmUnpauseButton.resetAll();

    }


    public void mouseMoved(MouseEvent e) {
        urmMenuButton.setMouseOver(false);
        urmReplayButton.setMouseOver(false);
        urmUnpauseButton.setMouseOver(false);

        if (isIn(e, urmMenuButton))
            urmMenuButton.setMouseOver(true);
        else if (isIn(e, urmReplayButton))
            urmReplayButton.setMouseOver(true);
        else if (isIn(e, urmUnpauseButton))
            urmUnpauseButton.setMouseOver(true);
        else
            audioOptions.mouseMoved(e);
    }

    private boolean isIn(MouseEvent e, PauseButton b) {
        return (b.getBounds().contains(e.getX(), e.getY()));
    }

}
