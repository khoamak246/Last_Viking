package gamestates;

import main.Game;
import ui.AudioOptions;
import ui.PauseButton;
import ui.UrmButton;
import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameOptions extends State implements Statemethods {
    private final AudioOptions audioOptions;
    private BufferedImage backgroundScreen, backgroundContainerButton;
    private int bgX, bgY, bgW, bgH;
    private UrmButton optionMenuButton;

    public GameOptions(Game game) {
        super(game);
        loadImgs();
        loadButton();
        audioOptions = game.getAudioOptions();
    }

    private void loadButton() {
        int menuX = (int) (387 * Game.SCALE);
        int menuY = (int) (325 * Game.SCALE);
        int urmSize = Constants.UI.URMButtons.URM_SIZE;
        optionMenuButton = new UrmButton(menuX, menuY, urmSize, urmSize, 2);
    }

    private void loadImgs() {

        backgroundScreen = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);
        backgroundContainerButton = LoadSave.GetSpriteAtlas(LoadSave.OPTIONS_MENU);
        bgW = (int) (backgroundContainerButton.getWidth() * Game.SCALE);
        bgH = (int) (backgroundContainerButton.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (33 * Game.SCALE);

    }

    @Override
    public void update() {
        optionMenuButton.update();
        audioOptions.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundScreen, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(backgroundContainerButton, bgX, bgY, bgW, bgH, null);
        optionMenuButton.draw(g);
        audioOptions.draw(g);
    }

    private boolean isIn(MouseEvent e, PauseButton b) {
        return (b.getBounds().contains(e.getX(), e.getY()));
    }

    public void mouseDragged(MouseEvent e) {
        audioOptions.mouseDragged(e);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        if (isIn(e, optionMenuButton)) {
            optionMenuButton.setMousePressed(true);
        } else
            audioOptions.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isIn(e, optionMenuButton)) {
            if (optionMenuButton.isMousePressed())
                GameState.state = GameState.MENU;
        } else
            audioOptions.mouseReleased(e);

        optionMenuButton.resetAll();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        optionMenuButton.setMouseOver(false);
        if (isIn(e, optionMenuButton))
            optionMenuButton.setMouseOver(true);
        else
            audioOptions.mouseMoved(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            GameState.state = GameState.MENU;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

}
