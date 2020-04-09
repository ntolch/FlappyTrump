package com.nikitolch.flappytrump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;

import static com.nikitolch.flappytrump.FlappyTrump.*;

// Todo add player sound?

public class Player {

    float playerY, playerX; // Position
    float velocity = 0; // Initial velocity
    Circle playerCircle;  // Player's shape for collision dectection
    Texture playerTexture;  // Player texture/image
    Animation playerAnimation;  // Animation

    // Sound flapSound;

    public Player() {
        playerTexture = new Texture("trump-smile-combo.png");
        playerX = SCREEN_WIDTH / 4;
        playerY = (SCREEN_HEIGHT / 2) - (playerTexture.getHeight() / 2); // - (gameoverText.getHeight() / 3)
        playerCircle = new Circle();
        playerAnimation = new Animation(new TextureRegion(playerTexture), 9, 0.5f);
        // flapSound = Gdx.audio...
    }

    public void update() {
        playerAnimation.update();
        playerCircle.set(playerX, playerY, playerTexture.getHeight() / 2);

        playerCircle.set(SCREEN_WIDTH / 3, playerY + playerTexture.getHeight() / 2, playerTexture.getHeight() / 2);

        if (playerY + playerTexture.getHeight() > Gdx.graphics.getHeight()) { // stop player from going passed top of screen
            velocity = 0;
            playerY = Gdx.graphics.getHeight() - playerTexture.getHeight();
        } else if (playerY > 0) {
            velocity++;
            playerY -= velocity; // stops player from moving past bottom of screen
        } else if (playerY < 0) {
            playerY = 0;
        }

    }

    public void jump() {
        velocity -= 25;
        // flapSound.play(0.5f0
    }

    public TextureRegion getTexture() {
        return  playerAnimation.getFrame();
    }

    public Circle getPlayerCircle() {
        return playerCircle;
    }

    public void dispose(){
        playerTexture.dispose();
    }

}
