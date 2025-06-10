package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Main game;
    SpriteBatch batch;
    BitmapFont font;
    ShapeRenderer shapeRenderer;

    Texture fondo1, fondo2, fondo3;
    Texture portal, portal2;
    Texture disparoTexture;

    Jugador jugador;
    Enemigo rancor;
    Array<Polygon> paredes;
    Array<Polygon> columnas;
    Array<Disparo> disparos;

    boolean salaA = true;
    boolean salaB = false;

    float elapsedTime = 0f;
    float animTimerFondo = 0f;
    float animTimerPortal = 0f;
    boolean usarFondo1 = true;
    boolean usarPortal1 = true;
    boolean mostrarMensaje = true;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);

        fondo1 = new Texture("dungeon1.png");
        fondo2 = new Texture("dungeon2.png");
        fondo3 = new Texture("dungeon_b1.png");
        portal = new Texture("canvas.png");
        portal2 = new Texture("canvas_invertido.png");
        disparoTexture = new Texture("fuegoPistola.png");

        jugador = new Jugador(100, 100, 200, 100, new Texture("jade_normal.png"));
        rancor = new Enemigo(600, 100, 60, 150,
            new Texture("rancor_right.png"),
            new Texture("rancor_left.png"),
            new Texture("rancor_dead.png")
        );

        paredes = new Array<>();
        columnas = new Array<>();
        disparos = new Array<>();

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
        animTimerPortal += delta;
        jugador.actualizarAnimacion(delta);
        jugador.incrementarTiempoGolpe(delta);

        ScreenUtils.clear(Color.BLACK);
        batch.begin();

        if (mostrarMensaje) {
            Texture loadingFondo = new Texture("dungeon_loading.png");
            batch.draw(loadingFondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            font.draw(batch, "CARGANDO...", 300, 50);
            if (elapsedTime > 1.5f) {
                mostrarMensaje = false;
                elapsedTime = 0f;
                loadingFondo.dispose(); // libera memoria
            }
            batch.end();
            return;
        }
        else {
            if (animTimerFondo >= 0.1f) {
                usarFondo1 = !usarFondo1;
                if (salaA) usarPortal1 = !usarPortal1;
                animTimerFondo = 0f;
            }

            Texture fondoActual = usarFondo1 ? fondo1 : fondo2;
            batch.draw(fondoActual, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            if (salaA) {
                Texture portalActual = usarPortal1 ? portal : portal2;
                batch.draw(portalActual, 290, 520, 180, 180);
            }

            jugador.procesarEntrada(delta);

            Polygon futura = jugador.getPoligonoFuturo();
            boolean colision = false;
            for (Polygon p : paredes) if (Intersector.overlapConvexPolygons(futura, p)) colision = true;
            for (Polygon p : columnas) if (Intersector.overlapConvexPolygons(futura, p)) colision = true;
            if (!colision) jugador.mover();

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                disparos.add(new Disparo(jugador.getX() + 50, jugador.getY() + 40, jugador.getDireccion()));
            }

            for (Iterator<Disparo> it = disparos.iterator(); it.hasNext();) {
                Disparo d = it.next();
                d.actualizar(delta);
                batch.draw(disparoTexture, d.x, d.y, 8, 8);
                Rectangle hitRancor = new Rectangle(rancor.getX(), rancor.getY(), rancor.getWidth(), rancor.getHeight());
                if (salaB && new Rectangle(d.x, d.y, d.ancho, d.alto).overlaps(hitRancor)) {
                    rancor.recibirDanio(jugador.getAtaque());
                    it.remove();
                }
                if (d.x < 0 || d.x > 800 || d.y < 0 || d.y > 600) {
                    it.remove();
                }
            }

            jugador.dibujar(batch);

            if (salaB) {
                Rectangle hitJugador = jugador.getRectangulo();
                Rectangle hitRancor = new Rectangle(rancor.getX(), rancor.getY(), rancor.getWidth(), rancor.getHeight());

                if (!rancor.estaMuerto()) {
                    rancor.seguir(jugador.getX(), jugador.getY(), delta);
                    rancor.dibujar(batch);
                    if (hitJugador.overlaps(hitRancor)) {
                        if (jugador.puedeSerGolpeado()) {
                            jugador.recibirDanio(rancor.getAtaque());
                        }
                    }
                } else {
                    if (!rancor.estaMuertoDelTodo()) {
                        rancor.animacionMuerte(batch, delta);
                    } else {
                        rancor.dibujar(batch);
                    }
                }
            }

            if (salaA) {
                Rectangle hitboxJugador = jugador.getRectangulo();
                Rectangle portalRect = new Rectangle(290, 520, 180, 180);
                if (portalRect.overlaps(hitboxJugador)) {
                    salaA = false;
                    salaB = true;
                    fondo1 = fondo3;
                    fondo2 = fondo3;
                    portal = null;
                    portal2 = null;
                    paredes.clear();
                    columnas.clear();
                    paredes.addAll(
                        new Polygon(new float[]{104, 545, 768, 545, 768, 545, 0, 545}),
                        new Polygon(new float[]{0, 0, 768, 0, 768, 0, 0, 0}),
                        new Polygon(new float[]{0, 64, 0, 64, 80, 545, 80, 545}),
                        new Polygon(new float[]{768, 64, 768, 64, 690, 545, 690, 545})
                    );
                }
            }
        }
        batch.end();

        if (jugador.getVida() <= 0) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        new BarraVida(jugador.getVida(), jugador.getMaxVida(), 20, 20, 200, 20, Color.RED).dibujar(shapeRenderer);
        new BarraVida(rancor.getVida(), rancor.getMaxVida(), 580, 20, 200, 20, Color.PURPLE).dibujar(shapeRenderer);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Polygon p : paredes) shapeRenderer.polygon(p.getTransformedVertices());
        for (Polygon c : columnas) shapeRenderer.polygon(c.getTransformedVertices());
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
        shapeRenderer.dispose();
        fondo1.dispose();
        fondo2.dispose();
        fondo3.dispose();
        jugador.dispose();
        rancor.dispose();
        disparoTexture.dispose();
        if (portal != null) portal.dispose();
        if (portal2 != null) portal2.dispose();
    }

    class Disparo {
        float x, y, ancho = 8, alto = 8;
        String direccion;

        public Disparo(float x, float y, String direccion) {
            this.x = x;
            this.y = y;
            this.direccion = direccion;
        }

        public void actualizar(float delta) {
            switch (direccion) {
                case "up": y += 400 * delta; break;
                case "down": y -= 400 * delta; break;
                case "left": x -= 400 * delta; break;
                case "right": x += 400 * delta; break;
            }
        }
    }
}
