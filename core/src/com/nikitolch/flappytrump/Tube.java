package com.nikitolch.flappytrump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Tube {
    public static final int TUBE_WIDTH = 60;

    private static final int FLUCTUATION = 130;
    public int TUBE_GAP = 450;
    public int LOWEST_OPENING = 120;
    private Texture topTube, bottomTube;
    private Vector2 positionTopTube, positionBotTube;
    private Rectangle topTubeRectangle, bottomTubeRectangle;
    private Random rand;


    public Tube(float x){
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        rand = new Random();

//        positionTopTube = new Vector2(x, rand.nextInt(FLUCTUATION) + TUBE_GAP + LOWEST_OPENING);
        positionTopTube = new Vector2(x, TUBE_GAP);
        positionBotTube = new Vector2(x, positionTopTube.y - TUBE_GAP - bottomTube.getHeight());

        topTubeRectangle = new Rectangle(positionTopTube.x, positionTopTube.y, topTube.getWidth(), topTube.getHeight());
        bottomTubeRectangle = new Rectangle(positionBotTube.x, positionBotTube.y, bottomTube.getWidth(), bottomTube.getHeight());
    }

    public Texture getBottomTube() {
        return bottomTube;
    }

    public Texture getTopTube() {
        return topTube;
    }

    public Vector2 getPosTopTube() {
        return positionTopTube;
    }

    public Vector2 getPosBotTube() {
        return positionBotTube;
    }

    public void reposition(float x){
        positionTopTube.set(x, rand.nextInt(FLUCTUATION) + TUBE_GAP + LOWEST_OPENING);
        positionBotTube.set(x, positionTopTube.y - TUBE_GAP - bottomTube.getHeight());
        topTubeRectangle.setPosition(positionTopTube.x, positionTopTube.y);
        bottomTubeRectangle.setPosition(positionBotTube.x, positionBotTube.y);
    }

    public boolean collides(Circle player){
        return Intersector.overlaps(player, topTubeRectangle) || Intersector.overlaps(player, bottomTubeRectangle);
    }

    public void dispose(){
        topTube.dispose();
        bottomTube.dispose();
    }
}
