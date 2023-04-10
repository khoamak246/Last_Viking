package gamestates;

import audio.AudioPlayer;
import main.Game;

public class State {
    protected Game game;

    public State(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void setGameState(GameState state) {
        switch (state) {
            case MENU:
                game.getAudioPlayer().playSong(AudioPlayer.MENU_1);
                break;
            case PLAYING:
                playSongInPlayingState();
        }
        GameState.state = state;
    }

    public void playSongInPlayingState() {
        game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLvlIndex());
    }


}
