package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;

public class DisparoFuego {
    public float x, y;
    private float dx, dy;
    private float velocidad = 300;
    public float ancho = 16, alto = 16;

    public DisparoFuego(float x, float y, float dirX, float dirY) {
        this.x = x;
        this.y = y;
        float distancia = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        this.dx = (dirX / distancia) * velocidad;
        this.dy = (dirY / distancia) * velocidad;
    }

    public void actualizar(float delta) {
        x += dx * delta;
        y += dy * delta;
    }

    public Rectangle getRectangulo() {
        return new Rectangle(x, y, ancho, alto);
    }
}
