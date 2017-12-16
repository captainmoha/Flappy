package com.flappy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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

    private SpriteBatch batch;
    private Texture background;
    private ShapeRenderer shapeRenderer;

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
    private int gameState = 0;
    private int score;
    private Set<Integer> scoredTubes;
    private BitmapFont font;

    private Random randomGen;

    @Override
    public void create () {

        gameover = new Texture("restart.png");
        score = 0;
        scoredTubes = new TreeSet<Integer>();
        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.setColor(Color.WHITE);
        // start and prepare textures to draw
        batch = new SpriteBatch();
        background = new Texture("grassy.jpg");


        birds = new Texture[2];
        birds[0] = new Texture("bird1.png");
        birds[1] = new Texture("bird2.png");

        /*shapeRenderer = new ShapeRenderer();*/

        birdCircle = new Circle();

        // game is not in progress at first
        flapState = 0;



        // tubes
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        topRecs = new Rectangle[numberOfTubes];
        bottomRecs = new Rectangle[numberOfTubes];
        // max offset to move the tubes with
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;

        // random generator to set offset to a random value
        randomGen = new Random();


        distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4 ;

        startGame();

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

        float topTubeY = 0;
        float bottomTubeY = 0;

        batch.begin();

        // draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());




        if (gameState == 1) {
            // game started here

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
                // over
                gameState = 2;
            }

        }

        else if (gameState == 0){
            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");
                gameState = 1;
            }
        }

        else if (gameState == 2){
            batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);
            if (Gdx.input.justTouched()) {
                Gdx.app.log("Touched", "Yep!");
                gameState = 1;
                score = 0;
                velocity = -20;
                scoredTubes.clear();
                startGame();
            }
        }


        // draw bird
        batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);

        // set up font for adding score

        font.draw(batch, score + " ", 100, 200);

        batch.end();

        // collision detection

        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);*/

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth()/2);

        /*shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);*/
        for (int i = 0; i < numberOfTubes; i++) {
           /* shapeRenderer.rect(tubeX[i], topTubeY  + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            shapeRenderer.rect(tubeX[i], bottomTubeY  + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());*/

            if (Intersector.overlaps(birdCircle, topRecs[i]) || Intersector.overlaps(birdCircle, bottomRecs[i])) {
                gameState = 2;
//                Gdx.app.log("collision", "WOO!");
            }
            else {

                // update score
                if (tubeX[i] < Gdx.graphics.getWidth() / 2 && !scoredTubes.contains(i)) {
                    score += 1;
                    Gdx.app.log("score", score + "");
                    scoredTubes.add(i);
                }
            }
        }

        /*shapeRenderer.end();*/

    }
}
