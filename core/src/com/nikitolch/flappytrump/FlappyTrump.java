package com.nikitolch.flappytrump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

	/* TODO:
	- Add animation on game over
	- Track high score
	- Add levels
		- more frequent/random appearance of obstacles
		- faster speed of obstacles
		- smaller tube gap
	- Give obstacles sounds (ie as media obstacles approaches,
		occasionally play a reporter's question: "Mr. President, blah blah?"
		As trump passes media, play response "That's a nasty question"
		As trump passes Chine, play trump saying "Chi-na"
	- Add flap sound
	- Check randomization of tube horizontal postions -- increase variation?
	- Game over:
		- Check for collision
		- Check for falling off screen
		- Game over method
			- Add textview "Tap to start game"

	- Prevent player from going above the screen height
	- Add extra obstacle (ie media) to either bottom of the topTube or top of bottomTube
	- Add rotation of extra obstacle (ie China, Nancy Pelosi, Rosy O'Donnel)
	 */

public class FlappyTrump extends ApplicationAdapter {
	public static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
	public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight();

	SpriteBatch batch;
//	ShapeRenderer shapeRenderer;
	Texture background;
	Texture gameover;

	BitmapFont font;

	Player player;

	Array<Tube> tubes;

    Rectangle screenRectangle;

	int gameState = 0;

	int score = 0;

	float halfScreenHeight;
	float halfScreenWidth;
	float maxTubeOffset;
	Random randomGenerator;
	int numberOfTubes = 4;
	int scoringTube = 0;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;



	@Override
	public void create () {
		batch = new SpriteBatch();
//		shapeRenderer = new ShapeRenderer();

		halfScreenHeight = Gdx.graphics.getHeight() / 2;
		halfScreenWidth = Gdx.graphics.getWidth() / 2;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		player = new Player();

		gameover = new Texture("gameover.png");

        screenRectangle = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        background = new Texture("ny-background.png");

        tubes = new Array<Tube>();

//		maxTubeOffset = (halfScreenHeight) - (gap / 2) - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth();

		startGame();
	}

	public void startGame() {
		player.playerY = halfScreenHeight - (player.getTexture().getRegionHeight() / 2) - (gameover.getHeight() / 3);

		for (int i = 0; i < numberOfTubes; i++) {
			tubes.add(new Tube(i * (distanceBetweenTubes + Tube.TUBE_WIDTH)));
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		for (Tube tube : tubes) {
			batch.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
			batch.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
		}
//		shapeRenderer.begin(ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);

		if (gameState == 1) {

			if (Gdx.input.justTouched()) {
				player.jump();
			}

			if (tubeX[scoringTube] < player.getPlayerCircle().x) {
				score++;
				if (scoringTube < numberOfTubes - 1) scoringTube++;
				else scoringTube = 0;
			}

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

		for (int i = 0; i < tubes.size; i++) {
			Tube tube = tubes.get(i);

			if (tube.collides(player.getPlayerCircle())) {
				startGame();
				gameState = 2;
			}
		}

		player.update();

		batch.draw(player.getTexture(), player.playerX, player.playerY, player.playerTexture.getWidth() / 9, player.getTexture().getRegionHeight());
		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		player.dispose();
		batch.dispose();
		background.dispose();
		for (Tube tube : tubes) {
			tube.dispose();
		}
	}
}
