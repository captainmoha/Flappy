package com.flappy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.awt.Shape;
import java.util.Set;
import java.util.Random;
import java.util.TreeSet;

public class flappy extends ApplicationAdapter {

    private boolean isGameRunning;

    private SpriteBatch batch;
    private Texture background;

    private Texture[] birds;

    // used to animate wings
    private int flapState;

    // make the bird appear to fall
    private float birdY = 0;
    private float velocity = -10;
    private float gravity = (float) 1.3;



    // tubes
    private Texture topTube;
    private Texture bottomTube;


    // collision detection
    private Circle birdCircle;
    private Rectangle[] topRecs;
    private Rectangle[] bottomRecs;

    // gap between tubes
    private float gap = 500;

    // max offset to move the tubes with
    private float maxTubeOffset;

    private int numberOfTubes = 4;

    // current tube offsets, will be randomly generated in a certain range
    private float[] tubeOffset = new float[numberOfTubes];

    // tubes x positions
    private float[] tubeX = new float[numberOfTubes];

    // distance between moving tubes
    private float distanceBetweenTubes;

    final private float tubeVelocity = 4;

    // game in progress
    private Texture gameover;
    private int gameState;
    private int score;
    private int highScore;

    private Set<Integer> scoredTubes;
    private BitmapFont scoreFont;
    
    private Preferences prefs;

    private Random randomGen;

    // sound
    private Sound jumpSound, overSound, crossSound;
    private Music mainMusic, highMusic;


    @Override
    public void create () {
        /*
        * game starts here where everything is initiated
        */

        configGame();

        initTextures();

        initShapes();
        initSounds();

        startGame();

    }


    private void configGame() {
        /*
        * prepares initial game configurations and scores
        * */


        score = 0;
        prefs = Gdx.app.getPreferences("highScore");

        loadScore();

        isGameRunning = false;

        scoreFont = new BitmapFont(Gdx.files.internal("font.fnt"));
        scoreFont.setColor(Color.WHITE);

        scoredTubes = new TreeSet<Integer>();

        // game is not in progress at first
        gameState = 0;

        // bird wings state
        flapState = 0;


        // max offset to move the tubes with
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;

        // random generator to set offset to a random value
        randomGen = new Random();

        distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4 ;

    }


    private void initTextures() {
        /*
        * initiates textures and prepares for drawing
        * */


        // start and prepare textures to draw
        batch = new SpriteBatch();
        background = new Texture("grassy.jpg");
        gameover = new Texture("restart.png");

        birds = new Texture[2];
        birds[0] = new Texture("bird1.png");
        birds[1] = new Texture("bird2.png");

        // tubes
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

    }


    private void initShapes() {
        /*
        * initiate shapes used for collision detection
        * */

        birdCircle = new Circle();
        topRecs = new Rectangle[numberOfTubes];
        bottomRecs = new Rectangle[numberOfTubes];

    }


    private void initSounds() {
        /*
        * initiate sound files
        * */
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("zoop.wav"));
        overSound = Gdx.audio.newSound(Gdx.files.internal("lose.mp3"));
        crossSound = Gdx.audio.newSound(Gdx.files.internal("cross.wav"));

        mainMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        highMusic = Gdx.audio.newMusic(Gdx.files.internal("win.mp3"));
    }


    private void startGame() {

        // bird starts at the middle of the screen
        birdY =  Gdx.graphics.getHeight() / 2  - birds[0].getHeight() / 2;

        // initialize offsets and x positions of all tubes
        for (int i = 0; i < numberOfTubes; i++) {
            // generate a random offset in range
            tubeOffset[i] = (randomGen.nextFloat() - (float) 0.5) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth()/ 2 - bottomTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            topRecs[i] = new Rectangle();
            bottomRecs[i] = new Rectangle();
        }

    }


    @Override
    public void render () {

        // tubes y positions
        float topTubeY = 0;
        float bottomTubeY = 0;

        // prepare for sprites drawings
        batch.begin();

        // draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // game is ongoing
        if (gameState == 1) {

            // tap to make the bird go up
            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");

                // user tapped make the bird go higher
                velocity = -25;

                // animate bird wings
                if (flapState == 0) {
                    flapState = 1;
                } else {
                    flapState = 0;
                }

                jumpSound.play();
            }

            // show tubes and animate them
            for (int i = 0; i < numberOfTubes; i++) {


                // if a tube moves out of the window make it reappear again at the start
                if (tubeX[i] < - topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGen.nextFloat() - (float) 0.5) * (Gdx.graphics.getHeight() - gap - 200);

                    // remove tube flag so we can use it for score again
                    scoredTubes.remove(i);
                }

                else {
                    // animate tubes
                    tubeX[i] -= tubeVelocity;
                }


                // draw tubes after adding the random offset
                topTubeY = Gdx.graphics.getHeight()/2 + gap/ 2;
                batch.draw(topTube, tubeX[i], topTubeY + tubeOffset[i]);
                // used for collision detection with top tubes
                topRecs[i] = new Rectangle(tubeX[i], topTubeY + tubeOffset[i], topTube.getWidth(), topTube.getHeight());


                bottomTubeY = Gdx.graphics.getHeight()/2 - gap / 2 - bottomTube.getHeight();
                batch.draw(bottomTube, tubeX[i], bottomTubeY + tubeOffset[i]);
                // used for collision detection with bottom tubes
                bottomRecs[i] = new Rectangle(tubeX[i], bottomTubeY + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            }

            // prevent bird from disappearing at the bottom of the window
            if (birdY > 0) {
                velocity += gravity;
                birdY -= velocity;
            } else {
                // over because the bird fell
                gameState = 2;
            }

        }

        // game hasn't started yet
        else if (gameState == 0){

            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");
                // start game
                isGameRunning = true;
                gameState = 1;
                mainMusic.play();
                jumpSound.play();
            }
        }

        // game over
        else if (gameState == 2){

            // please don't stop the music

            if (isGameRunning) {

                // I have to.
                mainMusic.stop();
                overSound.play();
                isGameRunning = false;
            }

            // draw gameover sprite
            batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);

            // restart game if user taps
            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");
                gameState = 1;
                score = 0;
                velocity = -20;
                scoredTubes.clear();
                isGameRunning = true;
                overSound.stop();
                startGame();
                mainMusic.play();
            }
        }


        // draw bird
        batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);

        // set up font for adding score

        scoreFont.draw(batch, score + "", 100, 200);
        scoreFont.draw(batch, "" + highScore, Gdx.graphics.getWidth() - 200, 200);

        batch.end();

        // collision detection

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth()/2);

        for (int i = 0; i < numberOfTubes; i++) {


            if (Intersector.overlaps(birdCircle, topRecs[i]) || Intersector.overlaps(birdCircle, bottomRecs[i])) {
                gameState = 2;
                Gdx.app.log("collision", "WOO!");
            }
            else {

                // update score
                if (tubeX[i] < Gdx.graphics.getWidth() / 2 && !scoredTubes.contains(i)) {
                    score += 1;
                    crossSound.play();
                    highScore(score);
                    Gdx.app.log("score", score + "");
                    scoredTubes.add(i);
                }
            }
        }

    }


    private void loadScore() {
        /*
        * Load high Score
        * */

        if (prefs.contains("highScore")) {
            highScore = prefs.getInteger("highScore");

        } else {

            highScore = 0;
            prefs.putInteger("highScore", highScore);
            prefs.flush();
        }

    }


    private void highScore(int high) {
        /*
        * Checks if user got a high score
        * */

        if (prefs.getInteger("highScore") < high) {
            highScore = high;
            Gdx.app.log("NEW HIGH", high + "");
            prefs.putInteger("highScore", high);
            prefs.flush();
            mainMusic.stop();
            highMusic.play();
        }
    }
}
