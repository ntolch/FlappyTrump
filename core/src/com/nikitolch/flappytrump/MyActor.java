package com.nikitolch.flappytrump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class MyActor extends Actor {
    Texture player = new Texture("trump-smile-combo.png");
    Animation playerAnimation = new Animation(new TextureRegion(player), 9, .05f);
    Sprite sprite = new Sprite(player);

//    public MyActor() {
//        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
//        setTouchable(Touchable.enabled);
//
//        addListener(new InputListener() {
//            @Override
//            public boolean keyDown(InputEvent event, int keycode) {
//                if (keycode)
//            }
//        });
//    }

}
