package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class EnemigoFinal {
    private float x, y;
    private float velocidad;
    private int vida, maxVida, ataque;
    private Texture texturaDerecha, texturaIzquierda, texturaMuerte;
    private Texture texturaActual;
    private int width = 130, height = 130;

    private boolean muerto = false;
    private boolean parpadeando = false;
    private boolean visible = true;
    private float tiempoParpadeo = 0f, tiempoMuerte = 0f;

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
        float dx = jugadorX - x;
        float dy = jugadorY - y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        if (distancia > 1) {
            x += (dx / distancia) * velocidad * delta;
            y += (dy / distancia) * velocidad * delta;
        }
        texturaActual = dx > 0 ? texturaDerecha : texturaIzquierda;
    }

    public void actualizar(float delta, float jugadorX, float jugadorY) {
        if (muerto) return;
        seguir(jugadorX, jugadorY, delta);

        tiempoDisparo += delta;
        if (tiempoDisparo >= 1.5f) {
            fuegos.add(new DisparoFuego(x + width / 2, y + height / 2, jugadorX - x, jugadorY - y));
            tiempoDisparo = 0f;
        }

        for (DisparoFuego fuego : fuegos) {
            fuego.actualizar(delta);
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (!muerto) {
            batch.draw(texturaActual, x, y, width, height);
        } else {
            batch.draw(texturaMuerte, x, y, width, height);
        }
    }

    public void recibirDanio(int danio) {
        if (muerto) return;
        vida -= danio;
        if (vida <= 0) muerto = true;
    }

    public Rectangle getRectangulo() {
        return new Rectangle(x, y, width, height);
    }

    public Array<DisparoFuego> getFuegos() {
        return fuegos;
    }

    public int getVida() { return vida; }
    public int getMaxVida() { return maxVida; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getAtaque() { return ataque; }
}
