package com.nikitolch.flappytrump.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Screen;

public class TestScreen implements Screen {
    private final Application app;

    public TestScreen(Application app) {
        this.app = app;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        System.out.println("resized");
    }

    @Override
    public void pause() {
        System.out.println("paused");
    }

    @Override
    public void resume() {
        System.out.println("resumed");
    }

    @Override
    public void hide() {
        System.out.println("hide");
    }

    @Override
    public void dispose() {
        System.out.println("disposed");
    }
}
