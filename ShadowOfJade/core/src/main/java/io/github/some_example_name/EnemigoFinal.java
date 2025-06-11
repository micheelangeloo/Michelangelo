package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class EnemigoFinal {
    private boolean puedeHacerDanio = true;
    private float x, y;
    private float velocidad;
    private int vida, maxVida, ataque;
    private Texture texturaDerecha, texturaIzquierda, texturaMuerte;
    private Texture texturaActual;
    private int width = 300, height = 300;

    private boolean muerto = false;
    private boolean parpadeando = false;
    private boolean visible = true;
    private boolean segundaFase = false;
    private boolean parpadeoFinal = false;
    private float tiempoParpadeo = 0f;
    private float tiempoDisparo = 0f;
    private Array<DisparoFuego> fuegos = new Array<>();

    public EnemigoFinal(float x, float y, float velocidad, int vida, Texture derecha, Texture izquierda, Texture muerte) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.vida = vida;
        this.maxVida = vida;
        this.ataque = 40;
        this.texturaDerecha = derecha;
        this.texturaIzquierda = izquierda;
        this.texturaMuerte = muerte;
        this.texturaActual = derecha;
    }

    public void seguir(float jugadorX, float jugadorY, float delta) {
        if (parpadeando || estaMuerto()) return;
        float dx = jugadorX - x;
        float dy = jugadorY - y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        if (distancia > 1) {
            x += (dx / distancia) * velocidad * delta;
            y += (dy / distancia) * velocidad * delta;
        }
        texturaActual = dx > 0 ? texturaDerecha : texturaIzquierda;
    }

    public void actualizar(float delta, Jugador jugador) {
        if (parpadeando) {
            tiempoParpadeo += delta;
            visible = ((int)(tiempoParpadeo * 10) % 2 == 0);
            if (tiempoParpadeo >= 2f) {
                parpadeando = false;
                visible = true;
                if (!segundaFase) {
                    segundaFase = true;
                    muerto = false;
                    vida = maxVida;
                    velocidad *= 1.5f;
                    ataque = 50;
                    fuegos.clear();
                    texturaDerecha = new Texture("dragon_right.png");
                    texturaIzquierda = new Texture("dragon_left.png");
                    texturaActual = texturaDerecha;
                } else {
                    parpadeoFinal = false;
                }
            }
            return;
        }

        if (!muerto) {
            seguir(jugador.getX(), jugador.getY(), delta);

            if (segundaFase) {
                if (jugador.getRectangulo().overlaps(getRectangulo())) {
                    if (jugador.puedeSerGolpeado()) {
                        jugador.recibirDanio(50); // daño por colisión en segunda fase
                    }
                }
            } else {
                tiempoDisparo += delta;
                if (tiempoDisparo >= 1.5f) {
                    fuegos.add(new DisparoFuego(x + width / 2, y + height / 2, jugador.getX() - x, jugador.getY() - y));
                    tiempoDisparo = 0f;
                }

                for (DisparoFuego fuego : fuegos) {
                    fuego.actualizar(delta);
                }
            }
        }
    }


    public void dibujar(SpriteBatch batch) {
        if (parpadeando || parpadeoFinal) {
            visible = ((int)(tiempoParpadeo * 10) % 2 == 0);
            if (visible) {
                batch.draw(parpadeoFinal ? texturaMuerte : texturaActual, x, y, width, height);
            }
        } else if (!muerto) {
            batch.draw(texturaActual, x, y, width, height);
        } else {
            batch.draw(texturaMuerte, x, y, width, height);
        }
    }

    public void recibirDanio(int danio) {
        if (parpadeando || muerto) return;
        vida -= danio;
        if (vida <= 0) {
            if (!segundaFase) {
                muerto = true;
                parpadeando = true;
                tiempoParpadeo = 0f;
            } else {
                muerto = true;
                parpadeando = true;
                parpadeoFinal = true;
                tiempoParpadeo = 0f;
            }
        }
    }

    public Rectangle getRectangulo() {
        return new Rectangle(x, y, width, height);
    }

    public Array<DisparoFuego> getFuegos() {
        return fuegos;
    }
    public boolean enParpadeoFinal() {
        return parpadeoFinal;
    }



    public int getVida() { return vida; }
    public int getMaxVida() { return maxVida; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getAtaque() { return ataque; }
    public boolean estaMuerto() { return muerto; }
    public boolean enSegundaFase() { return segundaFase; }

    public void dispose() {
        if (texturaDerecha != null) texturaDerecha.dispose();
        if (texturaIzquierda != null) texturaIzquierda.dispose();
        if (texturaMuerte != null) texturaMuerte.dispose();
    }
}
