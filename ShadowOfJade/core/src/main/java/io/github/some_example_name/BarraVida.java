package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BarraVida {
    private int vidaActual, vidaMaxima;
    private float x, y, ancho, alto;
    private Color color;

    public BarraVida(int vidaActual, int vidaMaxima, float x, float y, float ancho, float alto, Color color) {
        this.vidaActual = vidaActual;
        this.vidaMaxima = vidaMaxima;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.color = color;
    }

    public void dibujar(ShapeRenderer sr) {
        sr.setColor(Color.DARK_GRAY);
        sr.rect(x, y, ancho, alto);
        sr.setColor(color);
        sr.rect(x, y, Math.max(0, (vidaActual / (float)vidaMaxima) * ancho), alto);
    }
}
