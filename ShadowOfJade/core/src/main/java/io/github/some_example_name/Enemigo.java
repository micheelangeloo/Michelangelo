package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemigo {
    private float x, y;
    private float velocidad;
    private int vida;
    private int maxVida;
    private int ataque;
    private Texture texturaDerecha;
    private Texture texturaIzquierda;
    private Texture texturaMuerte;
    private Texture texturaActual;

    private int width = 100;
    private int height = 100;

    private boolean muerto = false;
    private boolean muertoDelTodo = false;
    private boolean revivido = false;

    private boolean parpadeando = false;
    private boolean visible = true;
    private float tiempoParpadeo = 0f;
    private float tiempoMuerte = 0f;

    public Enemigo(float x, float y, float velocidad, int vida, Texture derecha, Texture izquierda, Texture muerte) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.vida = vida;
        this.maxVida = vida;
        this.ataque = 50;
        this.texturaDerecha = derecha;
        this.texturaIzquierda = izquierda;
        this.texturaMuerte = muerte;
        this.texturaActual = derecha;
    }

    public void seguir(float jugadorX, float jugadorY, float delta) {
        if (muertoDelTodo) return;

        float dx = jugadorX - x;
        float dy = jugadorY - y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        if (distancia > 1) {
            x += (dx / distancia) * velocidad * delta;
            y += (dy / distancia) * velocidad * delta;
        }

        texturaActual = dx > 0 ? texturaDerecha : texturaIzquierda;
    }

    public void recibirDanio(int danio) {
        if (muertoDelTodo || parpadeando) return;

        vida -= danio;

        if (vida <= 0 && !muerto) {
            muerto = true;
            parpadeando = true;
            tiempoParpadeo = 0f;
            tiempoMuerte = 0f;
        } else if (vida <= 0 && revivido && !muertoDelTodo) {
            muertoDelTodo = true;
            parpadeando = true;
            tiempoParpadeo = 0f;
            tiempoMuerte = 0f;
        }
    }

    public void animacionMuerte(SpriteBatch batch, float delta) {
        if (!parpadeando) return;

        tiempoParpadeo += delta;
        tiempoMuerte += delta;

        // Alternar visibilidad cada 0.2s
        if (tiempoParpadeo >= 0.2f) {
            visible = !visible;
            tiempoParpadeo = 0f;
        }

        if (visible && !muertoDelTodo) {
            batch.draw(texturaActual, x, y, width, height);
        }

        if (visible && muertoDelTodo) {
            batch.draw(texturaActual, x, y, width, height);
        }

        // Al terminar la animación
        if (tiempoMuerte >= 2f) {
            parpadeando = false;

            if (!revivido) {
                revivido = true;
                muerto = false;
                vida = 200;
                maxVida = 200;
                width = 120;
                height = 120;
            } else {
                muertoDelTodo = true;
                muerto = true;
                texturaActual = texturaMuerte;
            }
        }

    }

    public void dibujar(SpriteBatch batch) {
        if (!parpadeando) {
            if (muertoDelTodo) {
                texturaActual = texturaMuerte;
            }
            batch.draw(texturaActual, x, y, width, height);
        }
    }


    // Getters y Setters
    public int getAtaque() { return ataque; }
    public int getVida() { return vida; }
    public int getMaxVida() { return maxVida; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean estaMuerto() { return muerto; }
    public boolean estaMuertoDelTodo() { return muertoDelTodo; }

    public Rectangle getRectangulo() {
        return new Rectangle(x, y, width, height);
    }

    public void dispose() {
        texturaDerecha.dispose();
        texturaIzquierda.dispose();
        texturaMuerte.dispose();
    }
}
