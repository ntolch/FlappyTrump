package com.nikitolch.flappytrump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;

import java.util.Random;

public class FlappyTrump extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Texture background;
	Texture topTubeMedia;
	Texture bottomTube;
	Texture[] player;

	int playerState = 1;
	float playerY = 0;
	float playerX;
	float velocity = 0;
	Circle playerCircle;

	boolean gameActive = false;
	float gap = 350;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		playerCircle = new Circle();

		background = new Texture("ny-background.png");
		topTubeMedia = new Texture("toptube-media.png");
		bottomTube = new Texture("bottomtube.png");
		player = new Texture[2];
		player[0] = new Texture("trumpUp.png");
		player[1] = new Texture("trumpDown.png");
		playerY = (Gdx.graphics.getHeight() / 2) - (player[playerState].getHeight() / 2);
		playerX = Gdx.graphics.getWidth() / 4;
		maxTubeOffset = (Gdx.graphics.getHeight() / 2) - (gap / 2) - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth();

		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = (Gdx.graphics.getWidth() / 2) - (topTubeMedia.getWidth() / 2) + (i * distanceBetweenTubes);
		}
	}

	/* TODO:
	- Add obstacle tubes
	- Game over:
		- Check for collision
		- Check for falling off screen
		- Game over method
			- Add textview "Tap to start game"
			- reset positions of player and tubes

	- Prevent player from going above the screen height
	- Add extra obstacle (ie media) to either bottom of the topTube or top of bottomTube
	- Add rotation of extra obstacle (ie China, Nancy Pelosi, Rosy O'Donnel)
	 */
	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Start game when screen is touched
		if (gameActive) {
			if (playerY > 0 || velocity < 0) { // just while testing: stops player from falling off screen
				velocity ++;
				playerY -= velocity;
			}
		} else {
			if (Gdx.input.justTouched()) {
				gameActive = true;
			}
		}

		for (int i = 0; i < numberOfTubes; i++) {
			if (tubeX[i] < -topTubeMedia.getWidth()) {
				tubeX[i] += numberOfTubes * distanceBetweenTubes;
				tubeOffset[i] = (randomGenerator.nextFloat()) * (Gdx.graphics.getHeight() - gap - 500);
			} else {
				tubeX[i] -= tubeVelocity;
			}

			batch.draw(topTubeMedia, tubeX[i], (Gdx.graphics.getHeight() / 2) + (gap /2) + tubeOffset[i]);
			batch.draw(bottomTube, tubeX[i], (Gdx.graphics.getHeight() / 2) - (gap/2) - bottomTube.getHeight() + tubeOffset[i]);
		}

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (playerState == 1) playerState = 0;
                velocity -= 25;
				return super.touchDown(screenX, screenY, pointer, button);
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (playerState == 0) playerState = 1;
				return super.touchUp(screenX, screenY, pointer, button);
			}
		});

		batch.draw(player[playerState], playerX, playerY, player[playerState].getWidth(), player[playerState].getHeight());
		batch.end();

		playerCircle.set(Gdx.graphics.getWidth() / 3, playerY + player[playerState].getHeight() / 2, player[playerState].getHeight() / 2);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);

		shapeRenderer.circle(playerCircle.x, playerCircle.y, playerCircle.radius);
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
