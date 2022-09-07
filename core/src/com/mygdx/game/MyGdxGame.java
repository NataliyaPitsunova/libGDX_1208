package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	int clk;
	Anim animation;
	boolean dir;

	private boolean lookRight = true;
	private int animPositionX = 0;


	@Override
	public void create () {
		batch = new SpriteBatch();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		animation.setTime(Gdx.graphics.getDeltaTime());
		float x = Gdx.input.getX() - animation.getFrame().getRegionWidth()/2.0f;
		float y = Gdx.graphics.getHeight() - Gdx.input.getY() - animation.getFrame().getRegionHeight()/2.0f;
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) clk++;
		Gdx.graphics.setTitle("Clicked "+clk+" times!");

		if (Gdx.input.isKeyJustPressed(Input.Keys.L)) dir = true;
		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) dir = false;

		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) lookRight = false;
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) lookRight = true;
		if (animPositionX + 128 >= Gdx.graphics.getWidth()) lookRight = false;
		if (animPositionX <= 0) lookRight = true;

		if (!animation.getFrame().isFlipX() && !lookRight) animation.getFrame().flip(true, false);
		if (animation.getFrame().isFlipX() && lookRight) animation.getFrame().flip(true, false);

		if (lookRight) {
			animPositionX += 5;
		} else {
			animPositionX -= 5;
		}

		batch.begin();
		batch.draw(animation.getFrame(), animPositionX, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		animation.dispose();
	}
}
