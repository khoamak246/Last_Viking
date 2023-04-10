package ui;

import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SoundButton extends PauseButton {

    private BufferedImage[][] soundImgs;
    private boolean isMouseOver, isMousePressed;
    private boolean isMuted;
    private int rowIndex, colIndex;

    public SoundButton(int x, int y, int width, int height) {
        super(x, y, width, height);

        loadSoundImgs();
    }

    private void loadSoundImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SOUND_BUTTONS);
        soundImgs = new BufferedImage[2][3];
        int soundSizeDefault = Constants.UI.PauseButtons.SOUND_SIZE_DEFAULT;
        for (int j = 0; j < soundImgs.length; j++)
            for (int i = 0; i < soundImgs[j].length; i++) {
                soundImgs[j][i] = temp.getSubimage(
                        i * soundSizeDefault,
                        j * soundSizeDefault,
                        soundSizeDefault,
                        soundSizeDefault
                );
            }
    }

    public void update() {
        if (isMuted)
            rowIndex = 1;
        else
            rowIndex = 0;

        colIndex = 0;
        if (isMouseOver)
            colIndex = 1;
        if (isMousePressed)
            colIndex = 2;

    }

    public void resetAll() {
        isMouseOver = false;
        isMousePressed = false;
    }

    public void draw(Graphics g) {
        g.drawImage(soundImgs[rowIndex][colIndex], x, y, width, height, null);
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

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
    }

}