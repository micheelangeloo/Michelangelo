package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemigo {
    private Texture textura;
    private float x, y;
    private float ancho = 64f, alto = 64f;
    private int vida = 150;
    private int ataque = 20;
    private Rectangle hitbox;

    public Enemigo(String rutaImagen, float x, float y) {
        this.textura = new Texture("rancor_left.png");
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle(x, y, ancho, alto);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textura, x, y, ancho, alto);
    }

    public void recibirDanio(int cantidad) {
        vida -= cantidad;
        if (vida < 0) vida = 0;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public void actualizar() {
        hitbox.setPosition(x, y);
    }

    public void dispose() {
        textura.dispose();
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getVida() {
        return vida;
    }

    public int getAtaque() {
        return ataque;
    }
}
