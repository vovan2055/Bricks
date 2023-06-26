package com.vovangames.bricks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;

import java.util.Locale;

import  static com.vovangames.bricks.Main.*;

public class Bricks extends ScreenAdapter {

	SpriteBatch batch;
	OrthographicCamera camera;
	Array<Rectangle> bricks = new Array<>();


	final Vector2 brickDimensions = new Vector2(50, 20);
	float ballSpeed = 400f;
	final Vector2 ballDirection = new Vector2(Vector2.Y).scl(ballSpeed);

	int rows = 20;
	int columns = 16;
	int yOffset = 180;
	float brickGap = 5f;

	int platformY = 120;
	int platformWidth = 400;
	float platformSpeed;

	int score = 0;
	float countDown = 3;
	boolean inGame = false, pause = false;

	Circle ball;
	Rectangle platform;

	Stage stage;
	Table endDialog;
	ImageTextButton retryButton;
	ImageTextButton menuButton;
	Label pauseLabel, scoreLabel;

	@Override
	public void show() {

		Gdx.input.setCatchKey(Input.Keys.BACK, true);

		batch = new SpriteBatch();
		camera = new OrthographicCamera();

		addBricks(rows, columns);

		ball = new Circle(Gdx.graphics.getWidth() / 2f, platformY * 2, 20);
		platform = new Rectangle(Gdx.graphics.getWidth() / 2f - platformWidth / 2f, platformY, platformWidth, 10);

		ballDirection.setToRandomDirection().nor().scl(ballSpeed);

		setupGUI();

		new Timer().scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				countDown -= 0.1f;
				if (countDown <= 0) inGame = true;
			}
		}, 0.1f, 0.1f, 30);
	}

	private void setupGUI() {
		stage = new Stage();

		endDialog = new Table();
		endDialog.setSize(Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.8f);
		endDialog.setBackground(new TextureRegionDrawable(new Texture("background.png")));

		ImageTextButton.ImageTextButtonStyle s = new ImageTextButton.ImageTextButtonStyle();
		s.font = font;
		s.up = s.down = buttonDrawable;
		s.fontColor = Color.BLACK;
		s.overFontColor = Color.CHARTREUSE;

		pauseLabel = new Label(locales.get("game.pause"), new Label.LabelStyle(font, Color.WHITE));

		scoreLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
		scoreLabel.setAlignment(Align.center);
		endDialog.add(scoreLabel).center().size(500, 80).align(Align.center).padBottom(120).row();

		retryButton = new ImageTextButton(locales.get("game.retry"), s);
		retryButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				instance.setScreen(new Bricks());
			}
		});
		endDialog.add(retryButton).center().size(400, 240).fill().padBottom(60).row();

		menuButton = new ImageTextButton(locales.get("game.menu"), s);
		menuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				instance.setScreen(new Menu());
			}
		});
		endDialog.add(menuButton).center().size(300, 200).fill().row();
		endDialog.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
		Gdx.input.setInputProcessor(stage);
	}


	private void addBricks(int rows, int columns) {
		float startX = Gdx.graphics.getWidth() / 2f - rows / 2f * brickDimensions.x;
		float startY = Gdx.graphics.getHeight() / 2f + columns / 2f * brickDimensions.y + yOffset;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				float x = startX + i * brickDimensions.x;
				float y = startY - j * brickDimensions.y;
				bricks.add(new Rectangle().set(x + brickGap * i, y - brickGap * j, brickDimensions.x, brickDimensions.y));
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		stage.getCamera().viewportWidth = width;
		stage.getCamera().viewportHeight = height;

		platformSpeed = width * 2f / platformWidth;

		endDialog.setSize(width * 0.8f, height * 0.8f);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);

		if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && inGame)
			if (pause) {
				stage.clear();
				pause = false;
			} else {
				stage.addActor(pauseLabel);
				pause = true;
			}

		if (inGame && !pause) {
			handleInput();
			checkCollisions();
		}

		batch.begin();
		space.draw(batch, delta);
		for (Rectangle r : bricks) {
			batch.draw(brickTexture, r.x, r.y, r.width, r.height);

		}
		trail.draw(batch, delta);
		burst.draw(batch, delta);
		brickDrawable.draw(batch, platform.x, platform.y, platform.width, platform.height);
		batch.draw(brickTexture, ball.x - ball.radius / 2f, ball.y - ball.radius / 2f, ball.radius, ball.radius);
		font.draw(batch, countDown > 0 ? String.format(Locale.ENGLISH, "%.1f", countDown): locales.get("game.score") + ": " + score, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 80);
		batch.end();

		stage.draw();

	}

	@Override
	public void pause() {
		pause = true;
	}

	private void handleInput() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) if (Gdx.graphics.isFullscreen()) Gdx.graphics.setWindowedMode(1280, 720);
		else Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isTouched() && Gdx.input.getX() < Gdx.graphics.getWidth() / 2f)) platform.x -= platformSpeed;
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 2f)) platform.x += platformSpeed;
		platform.x = MathUtils.clamp(platform.x, 0, Gdx.graphics.getWidth() - platformWidth);
	}

	private void checkCollisions() {
		if (ball.x < ball.radius || ball.x > Gdx.graphics.getWidth() - ball.radius) {
			ballDirection.x = -ballDirection.x;
			ball.x = MathUtils.clamp(ball.x, ball.radius, Gdx.graphics.getWidth() - ball.radius);
		}
		if (ball.y - 1 > Gdx.graphics.getHeight() - ball.radius) {
			ballDirection.y = -ballDirection.y;
			ball.y = MathUtils.clamp(ball.y, ball.radius, Gdx.graphics.getHeight() - ball.radius);
		}
		if (Intersector.overlaps(ball, platform)) {
			ball.y = platformY + platform.height + ball.radius;
			ballDirection.y = -ballDirection.y;
		}
		if (ball.y < platformY) {
			explosion.play();
			ballDirection.setZero();
			trail.setDuration(0);
			burst.start();
			inGame = false;
			stage.addActor(endDialog);
			if (score > highScore) saveFile.writeString(String.valueOf(score), false);
			scoreLabel.setText("[GREEN]" + locales.get("game.score") + ": []" + score);
			return;
		}

		for (Rectangle r : bricks) {
			if (!Intersector.overlaps(ball, r)) continue;
			hit.play();
			bricks.removeValue(r, true);
			ballDirection.nor().scl(ballSpeed += 2.5f);
			score++;
			if (ball.x <= r.x + r.width && ball.x >= r.x) {
				ballDirection.y = -ballDirection.y;
				continue;
			}
			if (ball.y <= r.y + r.height && ball.y >= r.y) {
				ballDirection.x = -ballDirection.x;
			}
		}

		if (bricks.isEmpty()) {
			inGame =  false;
			return;
		}

		ball.x += ballDirection.x * Gdx.graphics.getDeltaTime();
		ball.y += ballDirection.y * Gdx.graphics.getDeltaTime();
		trail.setPosition(ball.x, ball.y);
		burst.setPosition(ball.x, ball.y);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
