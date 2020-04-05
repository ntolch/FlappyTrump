package com.nikitolch.flappytrump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyTrump extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] player;
	int playerState = 1;
	float playerY = 0;
	float velocity = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("ny-background.png");
		player = new Texture[2];
		player[0] = new Texture("trumpUp.png");
		player[1] = new Texture("trumpDown.png");
		playerY = (Gdx.graphics.getHeight() / 2) - (player[playerState].getHeight() / 2);
	}

	@Override
	public void render () {
		velocity ++;
		playerY -= velocity;

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (playerState == 1) playerState = 0;
				playerY += 250;
				velocity = 0;
				return super.touchDown(screenX, screenY, pointer, button);
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (playerState == 0) playerState = 1;
				return super.touchUp(screenX, screenY, pointer, button);
			}
		});

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(player[playerState], (Gdx.graphics.getWidth() / 2) - (player[playerState].getWidth() / 2),
				playerY,
				player[playerState].getWidth() / 3,
				player[playerState].getHeight() / 3);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
