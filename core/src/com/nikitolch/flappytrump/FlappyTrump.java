package com.nikitolch.flappytrump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import java.util.Random;

public class FlappyTrump extends ApplicationAdapter {
	public static float HEIGHT;
	public static float WIDTH;

	SpriteBatch batch;
//	ShapeRenderer shapeRenderer;
	Texture background;
	Texture gameover;

	Boolean playSound = true;
	Sound iDontTakeResponsibility;
	float delay = .5f;
	long id;
	Texture responsibilityTexture;
	Animation responsibilityAnimation;


	Boolean playedSound;
	Sound obstacleSound;
	Sound nastyQuestion;


	float gravity = 1.3f;

	Texture player;
	Circle playerCircle;
	float playerY;
	float playerX;
	Animation playerAnimation;
	float velocity = 0;
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

	- Bonus Videos of Trump (ex: singing/compilation-autotune: https://gfycat.com/afraidreflectingenglishsetter)

	- Select random soundbite from media sounds when passing media obstacle: https://www.youtube.com/watch?v=CLMMbssYd6c

	- TO FIX:
		- glitch where y position of tubes changes on screen
		- stop extra obstacle from changing image after showing (still happening?)
	 */

	@Override
	public void create () {
		batch = new SpriteBatch();
//		shapeRenderer = new ShapeRenderer();
		// Player Sprite
		HEIGHT = Gdx.graphics.getHeight();
		WIDTH = Gdx.graphics.getWidth();

		iDontTakeResponsibility = Gdx.audio.newSound(Gdx.files.internal("sound/responsibility.mp3"));
		responsibilityTexture = new Texture("responsiblity-gif.png");
		responsibilityAnimation = new Animation(new TextureRegion(responsibilityTexture), 23, 9f);

		nastyQuestion = Gdx.audio.newSound(Gdx.files.internal("sound/nasty-question.mp3"));
		playedSound = false;

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
		playerY = HEIGHT/2 - (player.getHeight() / 2) - (gameover.getHeight() / 3);
		//

        // Tube Sprite
		for (int i = 0; i < numberOfTubes; i++) {
			tubeYOffset[i] =  (rand.nextFloat() - 0.5f) * (HEIGHT - gap - 200);
			tubeX[i] = WIDTH/2 - topTubeMedia.getWidth()/2 + i*distanceBetweenTubes + WIDTH;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
			extraObsRectangle[i] = new Rectangle();
		}
		////
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, WIDTH, HEIGHT);

//		shapeRenderer.begin(ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);

		if (gameState == 1) {
			if (Gdx.input.justTouched()) {
				velocity -= 25;
			}


			// Tube Sprite
			// reposition tube as it goes off screen
			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < -topTubeMedia.getWidth()*2) {  // if tube goes off the screen...
					tubeX[i] += numberOfTubes * distanceBetweenTubes - tubeX[i];
					tubeYOffset[i] = (rand.nextFloat()-0.5f) * HEIGHT - gap - extraObsAnimation.getFrame().getRegionHeight();

					tubeYFluctuation[i] = rand.nextInt(200);


					if (tubeYOffset[i] < -200) {
						tubeYOffset[i] = rand.nextInt(100) + 10;
					} else if (tubeYOffset[i] > 100) {
						tubeYOffset[i] -= 200 + extraObsTexture[i].getHeight();
					} else if (tubeYOffset[i] > 300) {
						tubeYOffset[i] -= 400 + extraObsTexture[i].getHeight();
					}


					if (i % 3 == 0) {
						// Select rand obstacle image from assets
						extraObsTexture[i] = extraObsTexture[rand.nextInt(numberOfTubes)];
						// Set animations specs depending on rand obstacle image selected
						String path = ((FileTextureData) extraObsTexture[i].getTextureData()).getFileHandle().name();
						switch (path) {
							case "odonnell-combo-2.png":
								obsFrameCount[i] = 19;
								obsCycleTime[i] = .05f;
								break;
							case "media.png":
								obsFrameCount[i] = 1;
								obsCycleTime[i] = .05f;
								obstacleSound = nastyQuestion;
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


				} else {
					tubeX[i] -= tubeVelocity;  // Move tubes to the left

//					if (tubeX[i] > playerX) {  // If the tube has not yet passed the player
						extraObsY[i] += extraObsVelocity;  // Move extra obstacle up from bottom
						// if tubeX has passed playerx AND obsY is below the top of the bottom tube (not yet visible), stop moving obsY
//					}

				}


				// Move extra obstacle up through bottom of screen when it reaches top of screen
				for (int x = 0; x < numberOfTubes; x++) {
					if (extraObsY[x] > HEIGHT) {  // when obstacle reach top of screen, then reset to bottom of screen
						extraObsY[x] -= HEIGHT;
					}
				}

				// Tube Position Variables
				float topTubeY = HEIGHT/2 + gap/2 + extraObsAnimation.getFrame().getRegionHeight()/2 + tubeYOffset[i] + tubeYFluctuation[i]/2;
				if (topTubeY > HEIGHT) topTubeY -= extraObsAnimation.getFrame().getRegionHeight();

				float botTubeY = HEIGHT/2 - gap/2 - extraObsAnimation.getFrame().getRegionHeight()/2 - bottomTube.getHeight() + tubeYOffset[i] + tubeYFluctuation[i]/2;
				if (botTubeY + bottomTube.getHeight() - extraObsAnimation.getFrame().getRegionHeight() < extraObsAnimation.getFrame().getRegionHeight()*2) botTubeY += extraObsAnimation.getFrame().getRegionHeight();

				// Extra Obs
					// create extraOb for every third tube
				if (i % 3 == 0) {
					batch.draw(extraObsAnimation.getFrame(), tubeX[i]+4, extraObsY[i], extraObsAnimation.getFrame().getRegionWidth(), extraObsAnimation.getFrame().getRegionHeight());
					extraObsRectangle[i] = new Rectangle(tubeX[i]+4, extraObsY[i], bottomTube.getWidth(), extraObsAnimation.getFrame().getRegionHeight());

					// Play appropriate sound (if application) as player passes obstacle

					if (extraObsY[i] > botTubeY + bottomTube.getHeight() && extraObsY[i] < topTubeY) { // if obstacle is visible (above bottom tube and under top tube)...
						if (playerX > tubeX[i] && !playedSound && obstacleSound != null) {
							obstacleSound.play(.5f);
							playedSound = true;
							// then delay and then make playedSound false again
							//maybe make array of booleans so each obstacle[i] can have a playedSound
						}
					}
				}



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
				velocity += gravity;
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
			gameover();
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
//				startGame();
				gameState = 2;
			}
		}

		extraObsAnimation.update(.9f);
		////

		// Player Sprite
		playerAnimation.update(2.5f);
		//


	}

	private void gameover() {

		batch.draw(gameover, WIDTH/2 - (gameover.getWidth()/2), HEIGHT/2 - (gameover.getHeight()/2));
		// Player animation after delay
		// if high score has been reach, play Trump soundbite about being the best maybe in history
		// or/else play Trump soundbite about it not being being his fault
		if (Gdx.input.justTouched()) {
			velocity -= 25;
			gameState = 0;
			startGame();
			score = 0;
			scoringTube = 0;
		}


		// Look into gdx Actors & Action classes to use delayAction when game over

		if (playSound) {
//			playerX -= WIDTH;
			id = iDontTakeResponsibility.play(.9f);
			iDontTakeResponsibility.setLooping(id, false);


			playSound = false;
			batch.draw(responsibilityAnimation.getFrame(), WIDTH/2-responsibilityAnimation.getFrame().getRegionWidth()/2, HEIGHT/2-responsibilityTexture.getHeight()/2, responsibilityAnimation.getFrame().getRegionWidth(), responsibilityAnimation.getFrame().getRegionHeight());


			responsibilityAnimation.update(.08f);
		}

//		responsibilityTexture.dispose();

		/* make responsibilityAnimation same size as game over texture.
		 play responsibility sound and gif,
		 set delay for 2sec (however long resp gif is)
		 	then display game over texture, (over player and resp gif)
		 on touch screen, reset play, get rid of gameover, (gamestate = 0)

		 Sequence of Actions
		 	- player stops moving
		 	- delay
		 	- player disappears + responsibility video and audio play once
		 	- video disappears and/or gameover texture appears over vid
		 	- display play btn
		 */
	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		iDontTakeResponsibility.dispose();
	}
}
