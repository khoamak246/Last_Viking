package entities;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.Constants;
import utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.GRAVITY;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

public class Player extends Entity {

    private BufferedImage[][] animations;
    private boolean moving = false, attacking = false;
    private boolean left, right, jump;
    private int[][] lvlData;
    private BufferedImage statusBarImg;
    private final int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthWidth = healthBarWidth;
    private final int powerBarWidth = (int) (104 * Game.SCALE);
    private int powerWidth = powerBarWidth;
    private final int powerMaxValue = 200;
    private int powerValue = powerMaxValue;
    private int flipX = 0;
    private int flipW = 1;
    private boolean attackChecked;
    private final Playing playing;
    private int tileY = 0;
    private boolean powerAttackActive;
    private int powerAttackTick;
    private int powerGrowTick;

    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        this.state = Constants.PlayerConstants.IDLE;
        this.maxHealth = 100;
        this.currentHealth = 35;
        this.walkSpeed = Game.SCALE * 1.0f;
        loadAnimations();
        initHitBox(20, 27);
        initAttackBox();
    }

    public void setSpawn(Point spawn) {
        this.x = spawn.x;
        this.y = spawn.y;
        hitBox.x = x;
        hitBox.y = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
        resetAttackBox();
    }

    public void update() {
        updateHealthBar();
        updatePowerBar();

        if (currentHealth <= 0) {
            if (state != DEAD) {
                state = DEAD;
                aniTick = 0;
                aniIndex = 0;
                playing.setPlayerDying(true);
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
            } else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
                playing.setGameOver(true);
                playing.getGame().getAudioPlayer().stopSong();
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAME_OVER);
            } else
                updateAnimationTick();

            return;
        }

        updateAttackBox();

        updatePos();
        if (moving) {
            checkPotionTouched();
            checkSpikesTouched();
            tileY = (int) (hitBox.y / Game.TILES_SIZE);
            if (powerAttackActive) {
                powerAttackTick++;
                if (powerAttackTick >= 35) {
                    powerAttackTick = 0;
                    powerAttackActive = false;
                }
            }
        }
        if (attacking || powerAttackActive)
            checkAttack();

        updateAnimationTick();
        setAnimation();
    }

    private void checkSpikesTouched() {
        playing.checkSpikesTouched(this);

    }

    private void checkPotionTouched() {
        playing.checkPotionTouched(hitBox);
    }

    private void checkAttack() {
        if (attackChecked || aniIndex != 1)
            return;
        attackChecked = true;

        if (powerAttackActive)
            attackChecked = false;

        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
        playing.getGame().getAudioPlayer().playAttackSound();
    }

    private void updateAttackBox() {
        if (right && left) {
            if (flipW == 1) {
                attackBox.x = hitBox.x + hitBox.width + (int) (Game.SCALE * 10);
            } else {
                attackBox.x = hitBox.x - hitBox.width - (int) (Game.SCALE * 10);
            }
        } else if (right || (powerAttackActive && flipW == 1))
            attackBox.x = hitBox.x + hitBox.width + (int) (Game.SCALE * 10);
        else if (left || (powerAttackActive && flipW == -1))
            attackBox.x = hitBox.x - hitBox.width - (int) (Game.SCALE * 10);

        attackBox.y = hitBox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
    }

    private void updatePowerBar() {
        powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth);

        powerGrowTick++;
        int powerGrowSpeed = 15;
        if (powerGrowTick >= powerGrowSpeed) {
            powerGrowTick = 0;
            changePower(1);
        }
    }

    public void render(Graphics g, int lvlOffset) {
        float xDrawOffset = 21 * Game.SCALE;
        float yDrawOffset = 4 * Game.SCALE;
        g.drawImage(animations[state][aniIndex], (int) (hitBox.x - xDrawOffset) - lvlOffset + flipX, (int) (hitBox.y - yDrawOffset), width * flipW, height, null);
//        drawHitBox(g, lvlOffset);
//        drawAttackBox(g, lvlOffset);
        drawUI(g);
    }

    private void drawUI(Graphics g) {
        // Background ui
        int statusBarWidth = (int) (192 * Game.SCALE);
        int statusBarHeight = (int) (58 * Game.SCALE);
        int statusBarXPos = (int) (10 * Game.SCALE);
        int statusBarYPos = (int) (10 * Game.SCALE);
        g.drawImage(statusBarImg, statusBarXPos, statusBarYPos, statusBarWidth, statusBarHeight, null);

        // Health bar
        g.setColor(Color.red);
        int healthBarHeight = (int) (4 * Game.SCALE);
        int healthBarXStart = (int) (34 * Game.SCALE);
        int healthBarYStart = (int) (14 * Game.SCALE);
        g.fillRect(healthBarXStart + statusBarXPos, healthBarYStart + statusBarYPos, healthWidth, healthBarHeight);

        // Power Bar
        g.setColor(Color.yellow);
        int powerBarHeight = (int) (2 * Game.SCALE);
        int powerBarXStart = (int) (44 * Game.SCALE);
        int powerBarYStart = (int) (34 * Game.SCALE);
        g.fillRect(powerBarXStart + statusBarXPos, powerBarYStart + statusBarYPos, powerWidth, powerBarHeight);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(state)) {
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    private void setAnimation() {
        int startAni = state;

        if (moving)
            state = RUNNING;
        else
            state = IDLE;

        if (inAir) {
            if (airSpeed < 0)
                state = JUMP;
            else
                state = FALLING;
        }

        if (powerAttackActive) {
            state = ATTACK;
            aniIndex = 1;
            aniTick = 0;
            return;
        }

        if (attacking) {
            state = ATTACK;
            if (startAni != ATTACK) {
                aniIndex = 1;
                aniTick = 0;
                return;
            }
        }
        if (startAni != state)
            resetAniTick();
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updatePos() {
        moving = false;

        if (jump)
            jump();

        if (!inAir)
            if (!powerAttackActive)
                if ((!left && !right) || (right && left))
                    return;

        float xSpeed = 0;

        if (left && !right) {
            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right && !left) {
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
        }

        if (powerAttackActive) {
            if ((!left && !right) || (left && right)) {
                if (flipW == -1)
                    xSpeed = -walkSpeed;
                else
                    xSpeed = walkSpeed;
            }


            xSpeed *= 3;
        }

        if (!inAir)
            if (!IsEntityOnFloor(hitBox, lvlData))
                inAir = true;

        if (inAir && !powerAttackActive) {
            if (CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)) {
                hitBox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            } else {
                hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, airSpeed);
                float fallSpeedAfterCollision = 0.5f * Game.SCALE;
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed);
            }

        } else
            updateXPos(xSpeed);
        moving = true;
    }

    private void jump() {
        if (inAir)
            return;
        playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
        inAir = true;
        // Jumping / Gravity
        float jumpSpeed = -2.25f * Game.SCALE;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData))
            hitBox.x += xSpeed;
        else {
            hitBox.x = GetEntityXPosNextToWall(hitBox, xSpeed);
            if (powerAttackActive) {
                powerAttackActive = false;
                powerAttackTick = 0;
            }

        }
    }

    public void changeHealth(int value) {
        currentHealth += value;

        if (currentHealth <= 0)
            currentHealth = 0;
        else if (currentHealth >= maxHealth)
            currentHealth = maxHealth;
    }

    public void kill() {
        currentHealth = 0;
    }

    public void changePower(int value) {
        powerValue += value;
        if (powerValue >= powerMaxValue)
            powerValue = powerMaxValue;
        else if (powerValue <= 0)
            powerValue = 0;
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[7][8];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitBox, lvlData))
            inAir = true;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        airSpeed = 0f;
        state = IDLE;
        currentHealth = maxHealth;

        hitBox.x = x;
        hitBox.y = y;


        resetAttackBox();

        if (!IsEntityOnFloor(hitBox, lvlData))
            inAir = true;
    }

    private void resetAttackBox() {
        if (flipW == 1) {
            attackBox.x = hitBox.x + hitBox.width + (int) (Game.SCALE * 10);
        } else {
            attackBox.x = hitBox.x - hitBox.width - (int) (Game.SCALE * 10);
        }
    }

    public int getTileY() {
        return tileY;
    }

    public void powerAttack() {
        if (powerAttackActive)
            return;
        if (powerValue >= 150) {
            powerAttackActive = true;
            changePower(-150);
        }

    }

}