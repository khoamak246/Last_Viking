package ui;

import main.Game;
import utilz.Constants;

import java.awt.*;
import java.awt.event.MouseEvent;

public class AudioOptions {

    private VolumeButton volumeButton;
    private SoundButton musicButton, sfxButton;
    private final Game game;

    public AudioOptions(Game game) {
        this.game = game;
        createSoundButton();
        createVolumeButton();
    }

    private void createVolumeButton() {
        int volumeBtnXPos = (int) (309 * Game.SCALE);
        int volumeBtnYPos = (int) (278 * Game.SCALE);
        int sliderWidth = Constants.UI.VolumeButtons.SLIDER_WIDTH;
        int volumeHeight = Constants.UI.VolumeButtons.VOLUME_HEIGHT;
        volumeButton = new VolumeButton(volumeBtnXPos, volumeBtnYPos, sliderWidth, volumeHeight);
    }

    private void createSoundButton() {
        int soundBtnXPos = (int) (450 * Game.SCALE);
        int musicBtnYPos = (int) (140 * Game.SCALE);
        int sfxBtnYPos = (int) (186 * Game.SCALE);
        int soundButtonSize = Constants.UI.PauseButtons.SOUND_SIZE;
        musicButton = new SoundButton(soundBtnXPos, musicBtnYPos, soundButtonSize, soundButtonSize);
        sfxButton = new SoundButton(soundBtnXPos, sfxBtnYPos, soundButtonSize, soundButtonSize);
    }

    public void update() {
        musicButton.update();
        sfxButton.update();
        volumeButton.update();
    }

    public void draw(Graphics g) {
        //Sound buttons
        musicButton.draw(g);
        sfxButton.draw(g);
        //Volume slider
        volumeButton.draw(g);
    }

    public void mouseDragged(MouseEvent e) {
        if (volumeButton.isMousePressed()) {
            float valueBefore = volumeButton.getVolumeRange();
            volumeButton.changeX(e.getX());
            float valueAfter = volumeButton.getVolumeRange();
            if (valueBefore != valueAfter)
                game.getAudioPlayer().setVolume(valueAfter);
        }
    }


    public void mousePressed(MouseEvent e) {
        if (isIn(e, musicButton))
            musicButton.setMousePressed(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMousePressed(true);
        else if (isIn(e, volumeButton))
            volumeButton.setMousePressed(true);
    }


    public void mouseReleased(MouseEvent e) {
        if (isIn(e, musicButton)) {
            if (musicButton.isMousePressed()) {
                musicButton.setMuted(!musicButton.isMuted());
                game.getAudioPlayer().toggleSongMute();
            }

        } else if (isIn(e, sfxButton)) {
            if (sfxButton.isMousePressed()) {
                sfxButton.setMuted(!sfxButton.isMuted());
                game.getAudioPlayer().toggleEffectMute();
            }
        }


        musicButton.resetAll();
        sfxButton.resetAll();
        volumeButton.resetAll();

    }


    public void mouseMoved(MouseEvent e) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);
        volumeButton.setMouseOver(false);

        if (isIn(e, musicButton))
            musicButton.setMouseOver(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMouseOver(true);
        else if (isIn(e, volumeButton))
            volumeButton.setMouseOver(true);
    }

    private boolean isIn(MouseEvent e, PauseButton b) {
        return (b.getBounds().contains(e.getX(), e.getY()));
    }
}
