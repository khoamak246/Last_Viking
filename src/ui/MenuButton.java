package ui;

import gamestates.GameState;
import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.Buttons.*;

public class MenuButton {
    private int xPos, yPos, rowIndex, index;
    private int xOffsetCenter = Constants.UI.Buttons.B_WIDTH / 2;
    private GameState gamestate;
    private BufferedImage[] buttonImgs;
    private boolean isMouseOver, isMousePressed;

    private Rectangle bounds;

    public MenuButton(int xPos, int yPos, int rowIndex, GameState gamestate) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.rowIndex = rowIndex;
        this.gamestate = gamestate;
        loadImgs();
        initBounds();
    }

    private void initBounds() {
        bounds = new Rectangle(
                xPos - xOffsetCenter,
                yPos,
                Constants.UI.Buttons.B_WIDTH,
                Constants.UI.Buttons.B_HEIGHT
        );
    }

    private void loadImgs() {
        buttonImgs = new BufferedImage[3];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        for (int i = 0; i < buttonImgs.length; i++) {
            buttonImgs[i] = temp.getSubimage(
                    i * B_WIDTH_DEFAULT,
                    rowIndex * B_HEIGHT_DEFAULT,
                    B_WIDTH_DEFAULT,
                    B_HEIGHT_DEFAULT
            );
        }
    }

    public void draw(Graphics g) {
        g.drawImage(buttonImgs[index], xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
    }

    public void update() {
        index = 0;
        if (isMouseOver) {
            index = 1;
        }
        if (isMousePressed) {
            index = 2;
        }
    }

    public boolean isMouseOver() {
        return isMouseOver;
    }

    public void setMouseOver(boolean isMouseOver) {
        this.isMouseOver = isMouseOver;
    }

    public boolean isMousePressed() {
        return isMousePressed;
    }

    public void setMousePressed(boolean isMousePressed) {
        this.isMousePressed = isMousePressed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void applyGameState() {
        GameState.state = gamestate;
    }

    public void resetAll() {
        isMouseOver = false;
        isMousePressed = false;
    }

    public GameState getState() {
        return gamestate;
    }

}
