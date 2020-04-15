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
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class FlappyTrump extends ApplicationAdapter {
	public static float HEIGHT;
	public static float WIDTH;

	SpriteBatch batch;
//	ShapeRenderer shapeRenderer;
	Texture background;
	Texture gameover;

	Texture topTubeMedia;
	Texture bottomTube;
	Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    Texture extraObsTexture;
    Animation extraObsAnimation;
	Rectangle[] extraObsRectangle;

	// Player Sprite
	Texture player;
	Circle playerCircle;
	float playerY;
	float playerX;
	Animation playerAnimation;
	float velocity = 0;

	float halfScreenHeight;
	float halfScreenWidth;
	////

	int gameState = 0;

	BitmapFont font;

	Rectangle screenRectangle;


	int score = 0;

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
	- Bonus Round:
			(achieved either by 1. reaching points/levels or
			2. removing a top or bottom pipe and putting Putin or something to get instead of avoid)
			- get to say "You're fired" to every obstacle passed for 10 seconds and get double points
	 */
	
	@Override
	public void create () {
		batch = new SpriteBatch();
//		shapeRenderer = new ShapeRenderer();
		// Player Sprite
		HEIGHT = Gdx.graphics.getHeight();
		WIDTH = Gdx.graphics.getWidth();


		halfScreenHeight = Gdx.graphics.getHeight() / 2;
		halfScreenWidth = Gdx.graphics.getWidth() / 2;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		gameover = new Texture("gameover.png");

		// Player Sprite
		player = new Texture("trump-smile-combo.png");
		playerAnimation = new Animation(new TextureRegion(player), 9, .05f);
		playerX = Gdx.graphics.getWidth() / 4;
		playerCircle = new Circle();
		//


        background = new Texture("ny-background.png");
		topTubeMedia = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

		extraObsTexture = new Texture("odonnell-combo-2.png");
        extraObsAnimation = new Animation(new TextureRegion(extraObsTexture), 19, .5f);
        extraObsRectangle = new Rectangle[numberOfTubes];

        screenRectangle = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		maxTubeOffset = (halfScreenHeight) - (gap / 2) - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth();

		startGame();
	}

	public void startGame() {
		// Player Sprite
		playerY = halfScreenHeight - (player.getHeight() / 2) - (gameover.getHeight() / 3);
		//

		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = halfScreenWidth - (topTubeMedia.getWidth() / 2) + (i * distanceBetweenTubes)  + Gdx.graphics.getWidth();
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
			extraObsRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//		shapeRenderer.begin(ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);

		if (gameState == 1) {
			if (Gdx.input.justTouched()) {
				velocity -= 25;
			}

			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < -topTubeMedia.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat()) * (Gdx.graphics.getHeight() - gap - 500);
				} else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(topTubeMedia, tubeX[i], halfScreenHeight + (gap /2) + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], halfScreenHeight - (gap/2) - bottomTube.getHeight() + tubeOffset[i]);
				batch.draw(extraObsAnimation.getFrame(), tubeX[i], halfScreenHeight - (gap/2) + tubeOffset[i], extraObsTexture.getWidth() / 19, extraObsTexture.getHeight());

				topTubeRectangles[i] = new Rectangle(tubeX[i], halfScreenHeight + (gap/2) + tubeOffset[i], topTubeMedia.getWidth(), topTubeMedia.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], halfScreenHeight - (gap/2) - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
				extraObsRectangle[i] = new Rectangle(tubeX[i], halfScreenHeight - (gap/2) + tubeOffset[i], bottomTube.getWidth(), extraObsTexture.getHeight());
			}

			if (tubeX[scoringTube] < playerCircle.x) {
				score++;
				if (scoringTube < numberOfTubes - 1) scoringTube++;
				else scoringTube = 0;
			}


			// Player Sprite
			if (playerY + player.getHeight() > Gdx.graphics.getHeight()) {
				velocity = 0;
				playerY = Gdx.graphics.getHeight() - player.getHeight();
			} else if (playerY > 0) { // just while testing: stops player from falling off screen
				velocity ++;
				playerY -= velocity; // stops player from moving past bottom of screen
			}
			////
			else {
				gameState = 2; // if player at bottom of screen, show game over text
			}

		} else if (gameState == 0){
			if (Gdx.input.justTouched()) {
				gameState = 1;
				velocity -= 25;
			}

		} else if (gameState == 2 ){
			batch.draw(gameover, halfScreenWidth - (gameover.getWidth() / 2), halfScreenHeight - (gameover.getHeight() / 8));
			if (Gdx.input.justTouched()) {
				velocity -= 25;
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		// Player Sprite
		batch.draw(playerAnimation.getFrame(), playerX, playerY, player.getWidth() / 9, player.getHeight());
		playerCircle.set(Gdx.graphics.getWidth() / 3, playerY + player.getHeight() / 2, player.getHeight() / 2);
		//

		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();


		for (int i = 0; i < numberOfTubes; i++) {
			if (Intersector.overlaps(playerCircle, topTubeRectangles[i]) ||
					Intersector.overlaps(playerCircle, bottomTubeRectangles[i]) ||
					Intersector.overlaps(playerCircle, extraObsRectangle[i])) {
				startGame();
				gameState = 2;
			}
		}

		// Player Sprite
		playerAnimation.update(.5f);
		//

		extraObsAnimation.update(.9f);

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
