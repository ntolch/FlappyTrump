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
	public static final float SCREEN_WIDTH = 400;
	public static final float SCREEN_HEIGHT = 800;

	SpriteBatch batch;
//	ShapeRenderer shapeRenderer;
	Texture background;
	Texture gameover;
	Texture topTubeMedia;
	Texture bottomTube;

//	Animation playerAnimation;
//	Texture playerTexture;

	BitmapFont font;

//	int playerState = 1;
//	float playerY;
//	float playerX;
//	float velocity = 0;
//	Circle playerCircle;
	PlayerSprite player;

	Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;
    Rectangle screenRectangle;

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

//		playerCircle = new Circle();
//		playerTexture = new Texture("trump-smile-combo.png");
//		playerAnimation = new Animation(new TextureRegion(playerTexture), 9, 0.5f);
		player = new PlayerSprite();

		gameover = new Texture("gameover.png");
		topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];
        screenRectangle = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        background = new Texture("ny-background.png");
		topTubeMedia = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");


//		playerX = Gdx.graphics.getWidth() / 4;

		maxTubeOffset = (halfScreenHeight) - (gap / 2) - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth();

		startGame();
	}

	public void startGame() {
		player.playerY = halfScreenHeight - (player.getTexture().getRegionHeight() / 2) - (gameover.getHeight() / 3);

		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = halfScreenWidth - (topTubeMedia.getWidth() / 2) + (i * distanceBetweenTubes)  + Gdx.graphics.getWidth();
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

			if (Gdx.input.justTouched()) {
				player.jump();
			}

			if (tubeX[scoringTube] < player.getPlayerCircle().x) {
				score++;
				if (scoringTube < numberOfTubes - 1) scoringTube++;
				else scoringTube = 0;
			}

//			if (playerY + playerTexture.getHeight() > Gdx.graphics.getHeight()) {
//				velocity = 0;
//				playerY = Gdx.graphics.getHeight() - playerTexture.getHeight();
//			} else if (playerY > 0) { // just while testing: stops player from falling off screen
//				velocity ++;
//				playerY -= velocity; // stops player from moving past bottom of screen
//			}

			// If player goes passed bottom of screen, it's game over
//			if (playerY < 0) {
//				gameState = 2;
//			}

		} else if (gameState == 0){
			if (Gdx.input.justTouched()) gameState = 1;

		} else if (gameState == 2 ){
			batch.draw(gameover, halfScreenWidth - (gameover.getWidth() / 2), halfScreenHeight - (gameover.getHeight() / 8));
			if (Gdx.input.justTouched()) {
				startGame();
				gameState = 1;
				score = 0;
				scoringTube = 0;
				player.velocity = 0;
			}
		}

		player.update();

		batch.draw(player.getTexture(), player.playerX, player.playerY, player.playerTexture.getWidth() / 9, player.getTexture().getRegionHeight());
		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();

//		playerCircle.set(Gdx.graphics.getWidth() / 3, playerY + playerTexture.getHeight() / 2, playerTexture.getHeight() / 2);

		for (int i = 0; i < numberOfTubes; i++) {
			if (Intersector.overlaps(player.getPlayerCircle(), topTubeRectangles[i]) || Intersector.overlaps(player.getPlayerCircle(), bottomTubeRectangles[i])) {
				startGame();
				gameState = 2;
			}
		}

//		Gdx.input.setInputProcessor(new InputAdapter() {
//			@Override
//			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//				if (playerState == 1) playerState = 0;
//				velocity -= 25;
//				return super.touchDown(screenX, screenY, pointer, button);
//			}
//			@Override
//			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//				if (playerState == 0) playerState = 1;
//				return super.touchUp(screenX, screenY, pointer, button);
//			}
//		});
	}
	
	@Override
	public void dispose () {
		player.dispose();
		batch.dispose();
		background.dispose();
	}
}
