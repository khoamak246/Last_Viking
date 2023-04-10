package ui;

import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UrmButton extends PauseButton {
    private BufferedImage[] buttonImgs;
    private final int rowIndex;
    private int index;
    private boolean isMouseOver, isMousePressed;

    public UrmButton(int x, int y, int width, int height, int rowIndex) {
        super(x, y, width, height);
        this.rowIndex = rowIndex;
        loadImgs();
    }

    private void loadImgs() {
        int urmDefaultSize = Constants.UI.URMButtons.URM_DEFAULT_SIZE;
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.URM_BUTTONS);
        buttonImgs = new BufferedImage[3];
        for (int i = 0; i < buttonImgs.length; i++) {
            buttonImgs[i] = temp.getSubimage(
                    i * urmDefaultSize,
                    rowIndex * urmDefaultSize,
                    urmDefaultSize,
                    urmDefaultSize
            );
        }

    }

    public void update() {
        index = 0;
        if (isMouseOver)
            index = 1;
        if (isMousePressed)
            index = 2;
    }

    public void draw(Graphics g) {
        int urmSize = Constants.UI.URMButtons.URM_SIZE;
        g.drawImage(buttonImgs[index], x, y, urmSize, urmSize, null);
    }

    public void resetAll() {
        isMouseOver = false;
        isMousePressed = false;
    }

    public boolean isMouseOver() {
        return isMouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.isMouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return isMousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.isMousePressed = mousePressed;
    }
}
