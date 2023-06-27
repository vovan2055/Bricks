package com.vovangames.bricks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

import static com.vovangames.bricks.Main.*;

public class Menu extends ScreenAdapter {


    Stage stage;
    ImageTextButton startButton;
    ImageTextButton optionsButton;
    Label bestScore;

    @Override
    public void show() {
        stage = new Stage();

        if (saveFile.exists()) highScore = Integer.parseInt(saveFile.readString());
        else highScore = 0;

        ImageTextButton.ImageTextButtonStyle s = new ImageTextButton.ImageTextButtonStyle();
        s.font = font;
        s.up = s.down = buttonDrawable;
        s.fontColor = Color.BLACK;
        s.overFontColor = Color.CHARTREUSE;
        startButton = new ImageTextButton(locales.get("menu.start"), s);
        startButton.setSize(600, 400);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                instance.setScreen(new Bricks());
            }
        });

        optionsButton = new ImageTextButton(locales.get("menu.options"), s);
        optionsButton.setSize(220, 180);
        optionsButton.setPosition(80, 80);
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                instance.setScreen(new Options());
            }
        });

        bestScore = new Label(locales.get("menu.best") + ": " + highScore, new Label.LabelStyle(font, Color.WHITE));
        bestScore.setAlignment(Align.center);

        stage.addActor(bestScore);
        stage.addActor(optionsButton);
        stage.addActor(startButton);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.getBatch().begin();
        space.draw(stage.getBatch(), delta);
        stage.getBatch().end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getCamera().viewportWidth = width;
        stage.getCamera().viewportHeight = height;
        startButton.setPosition(width / 2f, height /2f, Align.center);
        bestScore.setPosition(startButton.getX(Align.center), startButton.getY(Align.top) + 80, Align.center);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
