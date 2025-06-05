package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Main game;
    SpriteBatch batch;
    BitmapFont font;
    Texture fondo1, fondo2, silas;
    float silasX = 100, silasY = 100;
    float speed = 200;
    float elapsedTime = 0f;
    float animTimer = 0f;
    boolean usarFondo1 = true;
    boolean mostrarMensaje = true;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        fondo1 = new Texture("dungeon1.png");
        fondo2 = new Texture("dungeon2.png");
        silas = new Texture("jade_normal.png");
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        animTimer += delta;

        ScreenUtils.clear(Color.BLACK);
        batch.begin();

        if (mostrarMensaje) {
            font.draw(batch, "JUEGO EN MARCHA...", 250, 300);
            if (elapsedTime > 3f) {
                mostrarMensaje = false;
                elapsedTime = 0f;
                animTimer = 0f;
            }
        } else {
            if (animTimer >= 0.1f) {
                usarFondo1 = !usarFondo1;
                animTimer = 0f;
            }

            Texture fondoActual = usarFondo1 ? fondo1 : fondo2;
            batch.draw(fondoActual, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            handleInput(delta);
            batch.draw(silas, silasX, silasY, 100, 100);
        }

        batch.end();
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            silasX -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            silasX += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            silasY += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            silasY -= speed * delta;
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondo1.dispose();
        fondo2.dispose();
        silas.dispose();
    }
}
