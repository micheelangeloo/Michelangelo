package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class Jugador {
    private float x, y;
    private float speed;
    private int vida, ataque;
    private Texture texture;
    private String direccion = "right";
    private float tiempoGolpe = 1f;
    private float animTimer = 0f;
    private float dx, dy;
    private String rutaSprite = "jade_normal.png";

    public Jugador(float x, float y, float speed, int vida, Texture texture) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.vida = vida;
        this.ataque = 10;
        this.texture = texture;
    }

    public void procesarEntrada(float delta) {
        dx = 0;
        dy = 0;
        boolean moviendo = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += speed * delta;
            direccion = "up";
            moviendo = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= speed * delta;
            direccion = "down";
            moviendo = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= speed * delta;
            direccion = "left";
            moviendo = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += speed * delta;
            direccion = "right";
            moviendo = true;
        }

        if (moviendo && animTimer >= 0.1f) {
            String nuevaRuta = "jade_" + direccion + ".png";
            if (!nuevaRuta.equals(rutaSprite)) {
                rutaSprite = nuevaRuta;
                texture.dispose();
                texture = new Texture(rutaSprite);
            }
            animTimer = 0f;
        }

        if (!moviendo && !rutaSprite.equals("jade_normal.png")) {
            rutaSprite = "jade_normal.png";
            texture.dispose();
            texture = new Texture(rutaSprite);
            animTimer = 0f;
        }
    }

    public void mover() {
        x += dx;
        y += dy;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(texture, x, y, 100, 100);
    }

    public Polygon getPoligonoFuturo() {
        return new Polygon(new float[]{
            x + dx, y + dy,
            x + dx + 64, y + dy,
            x + dx + 64, y + dy + 64,
            x + dx, y + dy + 64
        });
    }

    public Rectangle getRectangulo() {
        return new Rectangle(x, y, 64, 64);
    }

    public String getDireccion() {
        return direccion;
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void recibirDanio(int cantidad) {
        vida -= cantidad;
        if (vida < 0) vida = 0;
        tiempoGolpe = 0;
    }

    public boolean puedeSerGolpeado() {
        return tiempoGolpe >= 1f;
    }

    public void incrementarTiempoGolpe(float delta) {
        tiempoGolpe += delta;
    }

    public int getVida() {
        return vida;
    }

    public int getMaxVida() {
        return 100;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public void actualizarAnimacion(float delta) {
        animTimer += delta;
    }

    public void dispose() {
        texture.dispose();
    }
}
