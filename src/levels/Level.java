package levels;

import entities.Crabby;
import main.Game;
import objects.Cannon;
import objects.GameContainer;
import objects.Potion;
import objects.Spike;
import utilz.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Level {
    private final BufferedImage img;
    private int[][] lvlData;
    private ArrayList<Crabby> crabs;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;
    private ArrayList<Cannon> cannons;
    private int maxLvOffsetX;
    private Point playerSpawn;

    public Level(BufferedImage img) {
        this.img = img;
        createLevelData();
        createEnemies();
        createPotions();
        createContainers();
        createSpikes();
        createCannons();
        calcLvlOffsets();
        calcPlayerSpawn();
    }

    private void createCannons() {
        cannons = HelpMethods.GetCannons(img);
    }

    private void createSpikes() {
        spikes = HelpMethods.GetSpikes(img);
    }

    private void createContainers() {
        containers = HelpMethods.GetCotainers(img);
    }

    private void createPotions() {
        potions = HelpMethods.GetPotions(img);
    }

    private void calcPlayerSpawn() {
        playerSpawn = HelpMethods.GetPlayerSpawn(img);
    }

    private void calcLvlOffsets() {
        int lvlTilesWide = img.getWidth();
        int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
        maxLvOffsetX = Game.TILES_SIZE * maxTilesOffset;
    }

    private void createEnemies() {
        crabs = HelpMethods.GetCrabs(img);
    }

    private void createLevelData() {
        lvlData = HelpMethods.GetLevelData(img);
    }

    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }

    public int[][] getLevelData() {
        return lvlData;
    }

    public int getLvlOffset() {
        return maxLvOffsetX;
    }

    public ArrayList<Crabby> getCrabs() {
        return crabs;
    }

    public Point getPlayerSpawn() {
        return playerSpawn;
    }

    public ArrayList<Potion> getPotions() {
        return potions;
    }

    public ArrayList<GameContainer> getContainers() {
        return containers;
    }

    public ArrayList<Spike> getSpikes() {
        return spikes;
    }

    public ArrayList<Cannon> getCannons() {
        return cannons;
    }
}