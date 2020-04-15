package com.nikitolch.flappytrump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Player {
    Texture playerTexture;
    Circle playerCircle;
    float playerY;
    float playerX;
    Vector2 postion;
    Animation playerAnimation;
    float velocity = 0;

    public Player() {
        playerTexture = new Texture("trump-smile-combo.png");
        playerAnimation = new Animation(new TextureRegion(playerTexture), 9, .05f);
        playerX = Gdx.graphics.getWidth() / 4;
        playerY = FlappyTrump.HEIGHT/2 - (playerTexture.getHeight() / 2);
        postion = new Vector2(playerX, playerY);
        playerCircle = new Circle(FlappyTrump.WIDTH/3, postion.y + playerTexture.getHeight()/2, playerTexture.getHeight()/2);
    }

    public void resetPosition() {
        playerY = FlappyTrump.HEIGHT/2 - (playerTexture.getHeight() / 2);

    }

    public void updatePosition() {
        if (playerY + playerTexture.getHeight() > FlappyTrump.HEIGHT) {
            velocity = 0;
            playerY = FlappyTrump.HEIGHT - playerTexture.getHeight();
        } else if (playerY > 0) { // just while testing: stops player from falling off screen
            velocity ++;
            playerY -= velocity; // stops player from moving past bottom of screen
        }

        playerCircle.setPosition(postion.x, postion.y);

    }

    public void update() {
        playerAnimation.update(.5f);

    }

    public TextureRegion getTexture() {
        return playerAnimation.getFrame();
    }
}
