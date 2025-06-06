package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Main game;
    SpriteBatch batch;
    BitmapFont font;
    Texture fondo1, fondo2,fondo3, jugadorTexture, portal, portal2;
    String rutaActual = "jade_normal.png";
    boolean salaA = true;
    boolean salaB = false;
    ShapeRenderer shapeRenderer;


    float silasX = 100, silasY = 100;
    float speed = 200;

    float elapsedTime = 0f;
    float animTimerFondo = 0f;
    float animTimerSprite = 0f;
    float animTimerPortal = 0f;
    boolean usarFondo1 = true;
    boolean usarPortal1 = true;
    boolean mostrarMensaje = true;
    int frame = 0;

    Array<Polygon> paredes;
    Array<Polygon> columnas;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
        shapeRenderer = new ShapeRenderer();


        fondo1 = new Texture("dungeon1.png");
        fondo2 = new Texture("dungeon2.png");
        fondo3 = new Texture("dungeon_b1.png");
        portal = new Texture("canvas.png");
        portal2 = new Texture("canvas_invertido.png");
        jugadorTexture = new Texture(rutaActual);

        paredes = new Array<>();
        columnas = new Array<>();

        // Colisiones de la primera sala
        paredes.addAll(
            new Polygon(new float[]{104, 545, 768, 545, 768, 545, 0, 545}),
            new Polygon(new float[]{0, 40, 768, 40, 768, 40, 0, 40}),
            new Polygon(new float[]{0, 64, 0, 64, 104, 545, 104, 545}),
            new Polygon(new float[]{768, 64, 768, 64, 650, 545, 650, 545})
        );

        columnas.addAll(
            new Polygon(new float[]{160, 370, 110, 370, 110, 250, 160, 250}),
            new Polygon(new float[]{640, 490, 590, 490, 590, 380, 640, 380}),
            new Polygon(new float[]{600, 220, 555, 220, 555, 100, 600, 100})
        );
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        animTimerFondo += delta;
        animTimerSprite += delta;
        animTimerPortal += delta;

        ScreenUtils.clear(Color.BLACK);
        batch.begin();

        if (mostrarMensaje) {
            font.draw(batch, "LOADING...", 250, 300);
            if (elapsedTime > 0.8f) {
                mostrarMensaje = false;
                elapsedTime = 0f;
                animTimerFondo = 0f;
                animTimerSprite = 0f;
                animTimerPortal = 0f;
            }
        } else {
            if (animTimerFondo >= 0.1f) {
                usarFondo1 = !usarFondo1;
                if (salaA) usarPortal1 = !usarPortal1;
                animTimerFondo = 0f;
                animTimerPortal = 0f;
            }

            Texture fondoActual = usarFondo1 ? fondo1 : fondo2;
            batch.draw(fondoActual, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            if (salaA) {
                Texture portalActual = usarPortal1 ? portal : portal2;
                batch.draw(portalActual, 290, 520, 180, 180);
            }

            float dx = 0, dy = 0;
            String direccion = null;
            boolean moviendo = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += speed * delta; direccion = "up"; moviendo = true; }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= speed * delta; direccion = "down"; moviendo = true; }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) { dx -= speed * delta; direccion = "left"; moviendo = true; }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) { dx += speed * delta; direccion = "right"; moviendo = true; }

            Polygon futura = new Polygon(new float[]{
                silasX + dx, silasY + dy,
                silasX + dx + 64, silasY + dy,
                silasX + dx + 64, silasY + dy + 64,
                silasX + dx, silasY + dy + 64
            });

            boolean colision = false;
            for (Polygon p : paredes)
                if (Intersector.overlapConvexPolygons(futura, p)) colision = true;
            for (Polygon p : columnas)
                if (Intersector.overlapConvexPolygons(futura, p)) colision = true;

            if (!colision) { silasX += dx; silasY += dy; }

            if (moviendo && animTimerSprite >= 0.1f) {
                frame = 1 - frame;
                String nuevaRuta = "jade_" + direccion + ".png";
                if (!nuevaRuta.equals(rutaActual)) {
                    rutaActual = nuevaRuta;
                    jugadorTexture.dispose();
                    jugadorTexture = new Texture(rutaActual);
                }
                animTimerSprite = 0f;
            }

            if (!moviendo && !rutaActual.equals("jade_normal.png")) {
                rutaActual = "jade_normal.png";
                jugadorTexture.dispose();
                jugadorTexture = new Texture(rutaActual);
                animTimerSprite = 0f;
            }

            batch.draw(jugadorTexture, silasX, silasY, 100, 100);

            // CAMBIO DE DUNGEON
            if (salaA) {
                Rectangle hitboxJugador = new Rectangle(silasX, silasY, 64, 64);
                Rectangle portalRect = new Rectangle(290, 520, 180, 180);
                if (portalRect.overlaps(hitboxJugador)) {
                    salaA = false;
                    salaB = true;
                    fondo1 = fondo3;
                    fondo2 = fondo3;

                    batch.draw(fondoActual, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                    portal = null; portal2 = null;
                    paredes.clear(); columnas.clear();

                    paredes.addAll(
                        new Polygon(new float[]{104, 545, 768, 545, 768, 545, 0, 545}),
                        new Polygon(new float[]{
                            0, 0,
                            768, 0,
                            768, 0,
                            0, 0}),
                        new Polygon(new float[]{0, 64,
                            0, 64,
                            80, 545,
                            80, 545}),

                        new Polygon(new float[]{768, 64,
                            768, 64,
                            690, 545,
                            690, 545})
                    );
                }
            }
        }
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

// Dibujar paredes
        for (Polygon p : paredes) {
            shapeRenderer.polygon(p.getTransformedVertices());
        }

// Dibujar columnas
        for (Polygon c : columnas) {
            shapeRenderer.polygon(c.getTransformedVertices());
        }

        shapeRenderer.end();

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
        jugadorTexture.dispose();
        shapeRenderer.dispose();
        if (portal != null) portal.dispose();
        if (portal2 != null) portal2.dispose();
    }
}
