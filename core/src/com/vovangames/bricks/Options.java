package com.vovangames.bricks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

import static com.vovangames.bricks.Main.*;

public class Options extends ScreenAdapter {

    ImageTextButton eraseData;
    ImageTextButton back;
    Stage stage;

    @Override
    public void show() {
        stage = new Stage();

        ImageTextButton.ImageTextButtonStyle s = new ImageTextButton.ImageTextButtonStyle();
        s.font = font;
        s.up = s.down = buttonDrawable;
        s.fontColor = Color.BLACK;
        s.overFontColor = Color.RED;
        eraseData = new ImageTextButton(locales.get("options.reset"), s);
        eraseData.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (saveFile.exists()) saveFile.delete();
            }
        });
        eraseData.setSize(600, 400);
        back = new ImageTextButton(locales.get("game.menu"), s);
        back.setSize(220, 180);
        back.setPosition(80, 80);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                instance.setScreen(new Menu());
            }
        });

        stage.addActor(eraseData);
        stage.addActor(back);
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
        eraseData.setPosition(width / 2f, height /2f, Align.center);
    }

    @Override
    public void dispose() {

    }
}
