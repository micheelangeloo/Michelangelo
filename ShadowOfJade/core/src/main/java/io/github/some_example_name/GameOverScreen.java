package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {
    final Main game;
    SpriteBatch batch;
    Texture gameOverImage;
    BitmapFont font;

    public GameOverScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        gameOverImage = new Texture("game_over.png");
        font = new BitmapFont();
        font.getData().setScale(3);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    game.setScreen(new GameScreen(game));
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        batch.draw(gameOverImage,
            Gdx.graphics.getWidth() / 2f - 128,
            Gdx.graphics.getHeight() / 2f,
            256, 256);

        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER",
            Gdx.graphics.getWidth() / 2f - 100,
            Gdx.graphics.getHeight() / 2f - 50);

        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        font.draw(batch, "Pulsa ENTER para reiniciar",
            Gdx.graphics.getWidth() / 2f - 170,
            Gdx.graphics.getHeight() / 2f - 100);
        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        gameOverImage.dispose();
        font.dispose();
    }
}

