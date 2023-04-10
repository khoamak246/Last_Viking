package ui;

import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.VolumeButtons.SLIDER_DEFAULT_WIDTH;
import static utilz.Constants.UI.VolumeButtons.VOLUME_DEFAULT_HEIGHT;

public class VolumeButton extends PauseButton {
    private BufferedImage[] volumeBtnImgs;
    private BufferedImage slider;
    private int index = 0;
    private boolean mouseOver, mousePressed;
    private int changeRangeButtonXPos;
    private final int minVolumeSliderXPos;
    private final int maxVolumeSliderXPos;
    private float volumeRange = 0f;

    public VolumeButton(int x, int y, int width, int height) {
        super(x + width / 2, y, Constants.UI.VolumeButtons.VOLUME_WIDTH, height);
        int volumeWidth = Constants.UI.VolumeButtons.VOLUME_WIDTH;
        bounds.x -= volumeWidth / 2;
        changeRangeButtonXPos = x + width / 2;
        this.x = x;
        this.width = width;
        minVolumeSliderXPos = x + volumeWidth / 2;
        maxVolumeSliderXPos = x + width - volumeWidth / 2;
        loadImgs();
    }

    private void loadImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTONS);
        volumeBtnImgs = new BufferedImage[3];
        int volumeDefaultWidth = Constants.UI.VolumeButtons.VOLUME_DEFAULT_WIDTH;
        for (int i = 0; i < volumeBtnImgs.length; i++) {
            volumeBtnImgs[i] = temp.getSubimage(i * volumeDefaultWidth, 0, volumeDefaultWidth, VOLUME_DEFAULT_HEIGHT);
        }
        slider = temp.getSubimage(3 * volumeDefaultWidth, 0, SLIDER_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);

    }

    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;
    }

    public void draw(Graphics g) {
        g.drawImage(slider, x, y, width, height, null);
        int volumeWidth = Constants.UI.VolumeButtons.VOLUME_WIDTH;
        g.drawImage(volumeBtnImgs[index], changeRangeButtonXPos - volumeWidth / 2, y, volumeWidth, height, null);
    }

    public void changeX(int x) {
        if (x < minVolumeSliderXPos)
            changeRangeButtonXPos = minVolumeSliderXPos;
        else changeRangeButtonXPos = Math.min(x, maxVolumeSliderXPos);
        updateVolumeRange();
        bounds.x = changeRangeButtonXPos - Constants.UI.VolumeButtons.VOLUME_WIDTH / 2;
    }

    private void updateVolumeRange() {
        float range = maxVolumeSliderXPos - minVolumeSliderXPos;
        float value = changeRangeButtonXPos - minVolumeSliderXPos;
        volumeRange = value / range;
    }

    public void resetAll() {
        mouseOver = false;
        mousePressed = false;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public float getVolumeRange() {
        return volumeRange;
    }
}
