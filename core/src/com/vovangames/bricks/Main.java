package com.vovangames.bricks;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class Main extends Game {

    static Texture brickTexture, buttonTexture;
    static NinePatchDrawable brickDrawable, buttonDrawable;
    static BitmapFont font;
    static ParticleEffect space, bounce, burst, trail;

    static Sound hit;
    static Sound explosion;

    static Main instance;

    static I18NBundle locales;
    static FileHandle saveFile;
    static int highScore = 0;

    @Override
    public void create() {
        instance = this;

        saveFile = Gdx.files.local("save");
        if (saveFile.exists()) highScore = Integer.parseInt(saveFile.readString());

        locales = I18NBundle.createBundle(Gdx.files.internal("locales/locale"));

        font = generateFont(Gdx.files.internal("game_font.ttf"));
        brickTexture = new Texture("brick.png");
        buttonTexture = new Texture("button.png");
        brickDrawable = new NinePatchDrawable(new NinePatch(brickTexture, 2, 2, 2, 2));

        hit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        explosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        NinePatch patch = new NinePatch(buttonTexture, 14, 14, 14, 14);
        patch.setLeftWidth(50);
        patch.setTopHeight(50);
        patch.setRightWidth(50);
        patch.setBottomHeight(50);
        buttonDrawable = new NinePatchDrawable(patch);

        space = new ParticleEffect();
        space.setPosition(0, 0);
        space.load(Gdx.files.internal("particles/space"), Gdx.files.internal("particles/"));
        space.start();

        trail = new ParticleEffect();
        trail.load(Gdx.files.internal("particles/trail"), Gdx.files.internal("particles/"));
        trail.start();

        burst = new ParticleEffect();
        burst.load(Gdx.files.internal("particles/explosion"), Gdx.files.internal("particles/"));

        setScreen(new Menu());
    }

    public static BitmapFont generateFont(FileHandle fontPath) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(fontPath);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.size = 20;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧЪЫЬЭЮЯ0123456789.!'()>?:[]{}`~@#$%^*_+-=\\|/<>,\0 ";

        FreeTypeFontGenerator.FreeTypeBitmapFontData data = new FreeTypeFontGenerator.FreeTypeBitmapFontData();
        data.markupEnabled = true;

        BitmapFont f = gen.generateFont(parameter, data);

        data.dispose();
        gen.dispose();

        return f;
    }

    @Override
    public void resize(int width, int height) {
        for(ParticleEmitter e : space.getEmitters()) {
            e.getSpawnWidth().setHigh(width);
            e.getSpawnHeight().setHigh(height);
            e.setPosition(width / 2f, height / 2f);
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        buttonTexture.dispose();
        brickTexture.dispose();
        space.dispose();
    }
}
