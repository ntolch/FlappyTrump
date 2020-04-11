package com.nikitolch.flappytrump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyTrump extends ApplicationAdapter {
	SpriteBatch batch;
//	ShapeRenderer shapeRenderer;
	Texture background;
	Texture gameover;
	Texture topTubeMedia;
	Texture bottomTube;
	Texture playerTexture;

	float dt = .05f; // time between frames
	float cycleTime = .5f; // time of 1 cycle through all frames
	BitmapFont font;

	float playerY;
	float playerX;
	Animation playerAnimation;

	float velocity = 0;
	Circle playerCircle;
	Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

	int gameState = 0;

	int score = 0;

	float halfScreenHeight;
	float halfScreenWidth;
	float gap = 450;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	int scoringTube = 0;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;


	/* TODO:
	- Make face #2 (when screen is touched) display little longer after the screen tap
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
	public void create () {
		batch = new SpriteBatch();
//		shapeRenderer = new ShapeRenderer();
		halfScreenHeight = Gdx.graphics.getHeight() / 2;
		halfScreenWidth = Gdx.graphics.getWidth() / 2;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		gameover = new Texture("gameover.png");
		playerCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        background = new Texture("ny-background.png");
		topTubeMedia = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		playerTexture = new Texture("trump-smile-combo.png");
		playerAnimation = new Animation(new TextureRegion(playerTexture), 9, cycleTime);

		playerX = Gdx.graphics.getWidth() / 4;

		maxTubeOffset = (halfScreenHeight) - (gap / 2) - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth();

		startGame();
	}

	public void startGame() {
		playerY = halfScreenHeight - (playerTexture.getHeight() / 2) - (gameover.getHeight() / 3);

		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = halfScreenWidth - (topTubeMedia.getWidth() / 2) + (i * distanceBetweenTubes)  + Gdx.graphics.getWidth();
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	public void jump() {
		velocity -= 25;
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (Gdx.input.justTouched()) jump();

//		shapeRenderer.begin(ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);

		if (gameState == 1) {
			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < -topTubeMedia.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat()) * (Gdx.graphics.getHeight() - gap - 500);
				} else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(topTubeMedia, tubeX[i], halfScreenHeight + (gap /2) + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], halfScreenHeight - (gap/2) - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], halfScreenHeight + (gap/2) + tubeOffset[i], topTubeMedia.getWidth(), topTubeMedia.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], halfScreenHeight - (gap/2) - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if (tubeX[scoringTube] < playerCircle.x) {
				score++;
				if (scoringTube < numberOfTubes - 1) scoringTube++;
				else scoringTube = 0;
			}

			if (playerY + playerTexture.getHeight() > Gdx.graphics.getHeight()) {
				velocity = 0;
				playerY = Gdx.graphics.getHeight() - playerTexture.getHeight();
			} else if (playerY > 0) { // just while testing: stops player from falling off screen
				velocity ++;
				playerY -= velocity; // stops player from moving past bottom of screen
			} else {
				gameState = 2; // if player at bottom of screen, show game over text
			}

		} else if (gameState == 0){
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

		} else if (gameState == 2 ){
			batch.draw(gameover, halfScreenWidth - (gameover.getWidth() / 2), halfScreenHeight - (gameover.getHeight() / 8));
			startGame();
			if (Gdx.input.justTouched()) {
				gameState = 0;
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		batch.draw(playerAnimation.getFrame(), playerX, playerY, playerTexture.getWidth() / 9, playerTexture.getHeight());
		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();

		playerAnimation.update(dt);
		playerCircle.set(Gdx.graphics.getWidth() / 3, playerY + playerTexture.getHeight() / 2, playerTexture.getHeight() / 2);

		for (int i = 0; i < numberOfTubes; i++) {
			if (Intersector.overlaps(playerCircle, topTubeRectangles[i]) || Intersector.overlaps(playerCircle, bottomTubeRectangles[i])) {
				gameState = 2;
			}
		}
	}
	
	@Override
	public void dispose () {
		playerTexture.dispose();
		batch.dispose();
		background.dispose();
	}
}
