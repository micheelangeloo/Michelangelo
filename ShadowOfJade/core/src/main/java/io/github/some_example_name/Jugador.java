package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Jugador {
    private Texture textura;
    private float x, y;
    private float velocidad = 200f;
    private float ancho = 64f, alto = 64f;
    private Rectangle hitbox;

    public Jugador(String rutaImagen, float x, float y){
        this.textura = new Texture(rutaImagen);
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle(x, y, ancho, alto);
    }

    public void actualizar() {
        hitbox.setPosition(x, y);
    }

    public void mover(float dx, float dy) {
        x += dx;
        y += dy;
        hitbox.setPosition(x, y);
    }

    public void render(SpriteBatch batch){
        batch.draw(textura, x, y, ancho, alto);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void dispose(){
        textura.dispose();
    }
}

