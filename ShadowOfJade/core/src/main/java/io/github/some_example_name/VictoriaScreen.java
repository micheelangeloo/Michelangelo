package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

public class VictoriaScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture[] jadeSprites;
    private Texture fondoVictoria;
    private float animTimer = 0f;
    private int jadeIndex = 0;

    public VictoriaScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        jadeSprites = new Texture[]{
            new Texture("jade_up.png"),
            new Texture("jade_down.png"),
            new Texture("jade_left.png"),
            new Texture("jade_right.png")
        };

        fondoVictoria = new Texture("victoria.png");
    }

    @Override
    public void render(float delta) {
        animTimer += delta;
        jadeIndex = ((int)(animTimer * 6)) % jadeSprites.length;

        ScreenUtils.clear(Color.BLACK);
        batch.begin();

        batch.draw(fondoVictoria, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture actual = jadeSprites[jadeIndex];
        batch.draw(actual, 250, 180, 400, 400); // Jade gigante

        font.draw(batch, "Pulsa R para reiniciar o ESC para salir", 160, 120);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new GameScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondoVictoria.dispose();
        for (Texture t : jadeSprites) t.dispose();
    }
}
