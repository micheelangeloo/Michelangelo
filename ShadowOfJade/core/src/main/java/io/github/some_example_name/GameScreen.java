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

import java.util.Iterator;

public class GameScreen implements Screen {
    final Main game;
    SpriteBatch batch;
    BitmapFont font;
    ShapeRenderer shapeRenderer;
    Texture fondo1, fondo2, fondo3, jugadorTexture, portal, portal2;
    Texture rancorDerecha, rancorIzquierda, rancorMuertoTexture;
    Texture rancorActual;
    Texture disparoTexture;
    boolean jugadorYaGolpeadoPorRancor = false;
    float tiempoDesdeUltimoGolpe = 0f;



    String rutaActual = "jade_normal.png";
    boolean salaA = true;
    boolean salaB = false;

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
    Array<Disparo> disparos;

    float rancorX = 600, rancorY = 100;
    float velocidadRancor = 60f;

    int vidaJugador = 100;
    int vidaRancor = 150;
    int ataqueJugador = 10;

    float tiempoMuerte = 0f;
    float parpadeoTimer = 0f;
    boolean rancorMuerto = false;
    boolean rancorParpadeando = false;
    boolean rancorVisible = true;

    String direccion = "right";

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
        jugadorTexture = new Texture(rutaActual);

        rancorDerecha = new Texture("rancor_right.png");
        rancorIzquierda = new Texture("rancor_left.png");
        rancorMuertoTexture = new Texture("rancor_dead.png");
        rancorActual = rancorDerecha;

        disparoTexture = new Texture("fuegoPistola.png");
        disparos = new Array<>();

        paredes = new Array<>();
        columnas = new Array<>();

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
        tiempoDesdeUltimoGolpe += delta;
        batch.begin();

        if (mostrarMensaje) {
            font.draw(batch, "LOADING...", 250, 300);
            if (elapsedTime > 0.8f) {
                mostrarMensaje = false;
                elapsedTime = 0f;
            }
        } else {
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

            float dx = 0, dy = 0;
            boolean moviendo = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += speed * delta; direccion = "up"; moviendo = true; }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= speed * delta; direccion = "down"; moviendo = true; }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) { dx -= speed * delta; direccion = "left"; moviendo = true; }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) { dx += speed * delta; direccion = "right"; moviendo = true; }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                disparos.add(new Disparo(silasX + 50, silasY + 40, direccion));
            }

            for (Iterator<Disparo> it = disparos.iterator(); it.hasNext(); ) {
                Disparo d = it.next();
                d.actualizar(delta);
                batch.draw(disparoTexture, d.x, d.y, 8, 8);
                Rectangle hitRancor = new Rectangle(rancorX, rancorY, 64, 64);
                if (salaB && new Rectangle(d.x, d.y, d.ancho, d.alto).overlaps(hitRancor)) {
                    vidaRancor -= ataqueJugador;
                    it.remove();
                }
                if (d.x < 0 || d.x > 800 || d.y < 0 || d.y > 600) {
                    it.remove();
                }
            }

            Polygon futura = new Polygon(new float[]{
                silasX + dx, silasY + dy,
                silasX + dx + 64, silasY + dy,
                silasX + dx + 64, silasY + dy + 64,
                silasX + dx, silasY + dy + 64
            });

            boolean colision = false;
            for (Polygon p : paredes) if (Intersector.overlapConvexPolygons(futura, p)) colision = true;
            for (Polygon p : columnas) if (Intersector.overlapConvexPolygons(futura, p)) colision = true;

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

            if (salaB) {
                Rectangle hitJugador = new Rectangle(silasX, silasY, 64, 64);
                Rectangle hitRancor = new Rectangle(rancorX, rancorY, 64, 64);

                if (vidaRancor > 0) {
                    float dxR = silasX - rancorX;
                    float dyR = silasY - rancorY;
                    float dist = (float)Math.sqrt(dxR * dxR + dyR * dyR);
                    if (dist > 1) {
                        rancorX += (dxR / dist) * velocidadRancor * delta;
                        rancorY += (dyR / dist) * velocidadRancor * delta;
                    }
                    rancorActual = dxR > 0 ? rancorDerecha : rancorIzquierda;
                    batch.draw(rancorActual, rancorX, rancorY, 100, 100);

                    if (hitJugador.overlaps(hitRancor) && tiempoDesdeUltimoGolpe >= 1f) {
                        vidaJugador -= 50;
                        if (vidaJugador < 0) vidaJugador = 0;
                        tiempoDesdeUltimoGolpe = 0f;
                    }




                } else {
                    if (!rancorMuerto) {
                        rancorParpadeando = true;
                        tiempoMuerte += delta;
                        parpadeoTimer += delta;

                        if (parpadeoTimer >= 0.2f) {
                            rancorVisible = !rancorVisible;
                            parpadeoTimer = 0f;
                        }

                        if (tiempoMuerte >= 3f) {
                            rancorMuerto = true;
                            rancorParpadeando = false;
                        }

                        if (rancorVisible) {
                            batch.draw(rancorActual, rancorX, rancorY, 100, 100);
                        }
                    } else {
                        batch.draw(rancorMuertoTexture, rancorX, rancorY, 100, 100);
                    }
                }
            }

            if (salaA) {
                Rectangle hitboxJugador = new Rectangle(silasX, silasY, 64, 64);
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
        if (vidaJugador <= 0) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 20, 200, 20);
        shapeRenderer.rect(580, 20, 200, 20);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(20, 20, Math.max(0, vidaJugador * 2), 20);
        shapeRenderer.setColor(Color.PURPLE);
        shapeRenderer.rect(580, 20, Math.max(0, vidaRancor * 2 / 1.5f), 20);
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
        jugadorTexture.dispose();
        rancorDerecha.dispose();
        rancorIzquierda.dispose();
        rancorMuertoTexture.dispose();
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
