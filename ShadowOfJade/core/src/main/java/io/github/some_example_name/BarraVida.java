package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BarraVida {
    private float x, y, ancho, alto;
    private Color colorFondo = Color.DARK_GRAY;
    private Color colorVida = Color.RED;

    public BarraVida(float x, float y, float ancho, float alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    public void render(ShapeRenderer shapeRenderer, int vidaActual, int vidaMaxima) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fondo
        shapeRenderer.setColor(colorFondo);
        shapeRenderer.rect(x, y, ancho, alto);

        // Vida actual
        float vidaPorcentaje = Math.max(0, (float)vidaActual / vidaMaxima);
        shapeRenderer.setColor(colorVida);
        shapeRenderer.rect(x, y, ancho * vidaPorcentaje, alto);

        shapeRenderer.end();
    }
}
