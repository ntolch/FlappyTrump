package com.nikitolch.flappytrump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
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

	Texture player;
	Circle playerCircle;
	float playerY;
	float playerX;
	Animation playerAnimation;
	float velocity = 0;

	float halfScreenHeight;
	float halfScreenWidth;
	////

    // Tube Sprite
	Texture topTubeMedia;
	Texture bottomTube;
	Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

	float gap;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	int scoringTube = 0;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeYOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
    ////

	float extraObsVelocity = 4;
	float[] extraObsY = new float[numberOfTubes];

	int[] obsFrameCount = new int[numberOfTubes];
	float[] obsCycleTime = new float[numberOfTubes];

	// Get directory
	FileHandle obstacleDir;
    Texture[] extraObsTexture;
    int numberOfObs;

    Animation extraObsAnimation;
    Array<Texture> extraObsArray;
	Rectangle[] extraObsRectangle;

	int extraObsHeight = 100;

	int gameState = 0;

	BitmapFont font;

	int score = 0;

	float[] tubeYFluctuation = new float[numberOfTubes];
	Random rand;


	/* TODO:
	- Make face #2 (when screen is touched) display little longer after the screen tap
	- Add obstacle tubes
	- Game over:
		- Check for collision
		- Check for falling off screen
		- Game over method
			- Add textview "Tap to start game"
			- reset positions of player and tubes

	- Add pause and animation when game over
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
        //

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		gameover = new Texture("gameover.png");

		player = new Texture("trump-smile-combo.png");
		playerAnimation = new Animation(new TextureRegion(player), 9, .05f);
		playerX = Gdx.graphics.getWidth() / 3;
		playerCircle = new Circle();
		//

		gap = player.getHeight() * 4;

        background = new Texture("ny-background.png");

        // Tube Sprite
		topTubeMedia = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

		rand = new Random();


		// Get a random texture of obstacleDirectory
//		extraObsTexture = new Texture(obstacleDir.list()[rand.nextInt(obstacleDir.list().length)]);
//		extraObsTexture[i] = new Texture(obstacleDir.list()[rand.nextInt(obstacleDir.list().length)]);

		obstacleDir = Gdx.files.internal("obstacles");
//		numberOfObs = (int) obstacleDir.length();

		extraObsTexture = new Texture[numberOfTubes];

		for (int i = 0; i < numberOfTubes; i++) {
			extraObsTexture[i] = new Texture(obstacleDir.list()[i]);

			// Select rand obstacle image from assets
//					extraObsTexture[i] = new Texture(obstacleDir.list()[rand.nextInt(obstacleDir.list().length)]);
//			extraObsTexture[i] = extraObsTexture[rand.nextInt(numberOfTubes)];
			// Set animations specs depending on rand obstacle image selected
			String path = ((FileTextureData)extraObsTexture[i].getTextureData()).getFileHandle().name();
			switch (path) {
				case "odonnell-combo-2.png":
					obsFrameCount[i] = 19;
					obsCycleTime[i] = .05f;
					break;
				case "media.png":
					obsFrameCount[i] = 1;
					obsCycleTime[i] = .05f;
					break;
				case "china.png":
					obsFrameCount[i] = 1;
					obsCycleTime[i] = .04f;
					break;
				case "pelosi.png":
					obsFrameCount[i] = 1;
					obsCycleTime[i] = .05f;
					break;
				default:
					obsFrameCount[i] = 1;
					obsCycleTime[i] = .06f;
					break;
			}
			extraObsAnimation = new Animation(new TextureRegion(extraObsTexture[i]), obsFrameCount[i], obsCycleTime[i]);
		}


//        extraObsAnimation = new Animation(new TextureRegion(extraObsTexture[0]), 19, .05f);
        extraObsRectangle = new Rectangle[numberOfTubes];
        extraObsArray = new Array<>();

		distanceBetweenTubes = WIDTH;

		////

		startGame();
	}

	public void startGame() {
		// Player Sprite
		playerY = halfScreenHeight - (player.getHeight() / 2) - (gameover.getHeight() / 3);
		//

        // Tube Sprite
		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = WIDTH/2 - (topTubeMedia.getWidth()/2) + (i*distanceBetweenTubes) + WIDTH;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
			extraObsRectangle[i] = new Rectangle();
		}
		////
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


			// Tube Sprite
			// reposition tube as it goes off screen
			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < -topTubeMedia.getWidth()) {  // if tube goes off the screen...
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeYOffset[i] = rand.nextFloat() * HEIGHT - gap - extraObsAnimation.getFrame().getRegionHeight();

					tubeYFluctuation[i] = rand.nextInt(200);

					if (tubeYOffset[i] < -200) {
						tubeYOffset[i] = rand.nextInt(100) + 10;
					} else if (tubeYOffset[i] > 50) {
						tubeYOffset[i] -= 70 + extraObsTexture[i].getHeight();
					} else if (tubeYOffset[i] > 100) {
						tubeYOffset[i] -= 200 + extraObsTexture[i].getHeight();
					} else if (tubeYOffset[i] > 300) {
						tubeYOffset[i] -= 400 + extraObsTexture[i].getHeight();
					}


//					System.out.println("\n\nTube: " + i + "Tube Offset: " + tubeYOffset[i]);
//					System.out.println("Fluctuation: " + tubeYFluctuation[i]);



					// Select rand obstacle image from assets
					extraObsTexture[i] = extraObsTexture[rand.nextInt(numberOfTubes)];
					// Set animations specs depending on rand obstacle image selected
					String path = ((FileTextureData)extraObsTexture[i].getTextureData()).getFileHandle().name();
					switch (path) {
						case "odonnell-combo-2.png":
							obsFrameCount[i] = 19;
							obsCycleTime[i] = .05f;
							break;
						case "media.png":
							obsFrameCount[i] = 1;
							obsCycleTime[i] = .05f;
							break;
						case "china.png":
							obsFrameCount[i] = 1;
							obsCycleTime[i] = .04f;
							break;
						case "pelosi.png":
							obsFrameCount[i] = 1;
							obsCycleTime[i] = .05f;
							break;
						default:
							obsFrameCount[i] = 1;
							obsCycleTime[i] = .06f;
							break;
					}
					System.out.println("PATH: " + path);
					System.out.println("FRAME COUNT: " + obsFrameCount[i]);

					extraObsAnimation = new Animation(new TextureRegion(extraObsTexture[i]), obsFrameCount[i], obsCycleTime[i]);



				} else {
					// Move tubes to the left
					tubeX[i] -= tubeVelocity;
					// Move extra obstacle up from bottom
					extraObsY[i] += extraObsVelocity;
				}

				// Move extra obstacle up through bottom of screen when it reaches top of screen
				for (int x = 0; x < numberOfTubes; x++) {
					if (extraObsY[x] > HEIGHT) {  // when obstacle reach top of screen...
						extraObsY[x] -= HEIGHT;

					}
				}

				// Tube Position Variables
				float topTubeY = HEIGHT/2 + gap/2 + extraObsHeight/2 + tubeYOffset[i] + tubeYFluctuation[i];
				if (topTubeY > HEIGHT) topTubeY -= 120;

				float botTubeY = HEIGHT/2 - gap/2 - extraObsHeight/2 - bottomTube.getHeight() + tubeYOffset[i];
				if (botTubeY + bottomTube.getHeight() - extraObsHeight < extraObsHeight*2) botTubeY += extraObsHeight;

				// Extra Obs
				batch.draw(extraObsAnimation.getFrame(), tubeX[i]+1, extraObsY[i], extraObsAnimation.getFrame().getRegionWidth(), extraObsHeight);
				extraObsRectangle[i] = new Rectangle(tubeX[i]+1, extraObsY[i], bottomTube.getWidth(), extraObsHeight);

				// Bottom Tube
				batch.draw(bottomTube, tubeX[i], botTubeY);
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], botTubeY, bottomTube.getWidth(), bottomTube.getHeight());

				// Top Tube
				batch.draw(topTubeMedia, tubeX[i], topTubeY);
				topTubeRectangles[i] = new Rectangle(tubeX[i], topTubeY, topTubeMedia.getWidth(), topTubeMedia.getHeight());




//				extraObsRectangle[i] = new Rectangle(tubeX[i], botTubeY + bottomTube.getHeight(), bottomTube.getWidth(), extraObsTexture.getHeight());
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
			} else if (playerY < 0) {
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
		batch.draw(playerAnimation.getFrame(), playerX, playerY, player.getWidth() / playerAnimation.frameCount, player.getHeight());
		playerCircle.set(Gdx.graphics.getWidth() / 3, playerY + player.getHeight() / 2, player.getHeight() / 2);
		//

		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();


        // Tube Sprite
		for (int i = 0; i < numberOfTubes; i++) {
			if (Intersector.overlaps(playerCircle, topTubeRectangles[i]) ||
					Intersector.overlaps(playerCircle, bottomTubeRectangles[i]) ||
					Intersector.overlaps(playerCircle, extraObsRectangle[i])) {
				startGame();
				gameState = 2;
			}
		}

		extraObsAnimation.update(.9f);
		////

		// Player Sprite
		playerAnimation.update(2.5f);
		//


	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
