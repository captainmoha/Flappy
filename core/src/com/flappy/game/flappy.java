package com.flappy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class flappy extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture background;

    private Texture[] birds;
    // used to animate wings
    private int flapState;

    // make the bird appear to fall
    private float birdY = 0;
    private float velocity = 0;
    private float gravity = (float) 1.3;

    // tubes
    private Texture topTube;
    private Texture bottomTube;

    // gap between tubes
    private float gap = 700;

    // game in progress
    private int gameState = 0;

    @Override
    public void create () {

        // start and prepare textures to draw
        batch = new SpriteBatch();
        background = new Texture("bg.png");


        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        // game is not in progress at first
        flapState = 0;

        // bird starts at the middle of the screen
        birdY =  Gdx.graphics.getHeight() / 2  - birds[0].getHeight() / 2;

        // tubes
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
    }

    @Override
    public void render () {

        batch.begin();

        // draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        // tap to start game
        if (Gdx.input.justTouched()) {

            Gdx.app.log("Touched", "Yep!");
            gameState = 1;
        }

        if (gameState == 1) {
            // game started here


            // draw tubes
            float  topTubeX = Gdx.graphics.getWidth()/ 2 - topTube.getWidth()/2;
            float topTubeY = Gdx.graphics.getHeight()/2 + gap/ 2;
            batch.draw(topTube, topTubeX, topTubeY);

            float bottomTubeX = Gdx.graphics.getWidth()/ 2 - bottomTube.getWidth()/2;
            float bottomTubeY = Gdx.graphics.getHeight()/2 - gap / 2 - bottomTube.getHeight();
            batch.draw(bottomTube, bottomTubeX, bottomTubeY);


            // tap to make the bird go up
            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");
                velocity = -20;
            }

            // prevent bird from disappearing at the bottom of the window
            if (birdY > 0 || velocity < 0) {
                velocity += gravity;
                birdY -= velocity;
            }

        }

        else {
            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");
                gameState = 1;
            }
        }

        // animate bird wings
        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        // draw bird
        batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);

        batch.end();

    }
}
