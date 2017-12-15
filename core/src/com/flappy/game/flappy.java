package com.flappy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class flappy extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;

	Texture[] birds;
	int flapState;

	float birdY = 0;
	float velocity = 0;

	int gameState = 0;
	float gravity = (float) 1.3;

	@Override
	public void create () {

		batch = new SpriteBatch();
		background = new Texture("bg.png");

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		flapState = 0;
		birdY =  Gdx.graphics.getHeight() / 2  - birds[0].getHeight() / 2;
	}

	@Override
	public void render () {

        if (Gdx.input.justTouched()) {

            Gdx.app.log("Touched", "Yep!");
            gameState = 1;
        }

        if (gameState == 1) {
            velocity += gravity;
            birdY -= velocity;

            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");
                velocity = -20;
            }
        }

        else {
            if (Gdx.input.justTouched()) {

                Gdx.app.log("Touched", "Yep!");
                gameState = 1;
            }
        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2,
                birdY);
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
