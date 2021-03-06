
package com.quexten.ravtech.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.quexten.ravtech.graphics.RavCamera;

public class PolygonShapeRenderer extends PolygonSpriteBatch {

	Texture texture;
	int thickness = 1;
	RavCamera camera;

	public PolygonShapeRenderer (RavCamera camera) {
		this.camera = camera;
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB565);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		texture = new Texture(pixmap);
		pixmap.dispose();
	}

	void drawRect (float x, float y, float width, float height, int thickness) {
		draw(texture, x, y, width, thickness);
		draw(texture, x, y, thickness, height);
		draw(texture, x, y + height - thickness, width, thickness);
		draw(texture, x + width - thickness, y, thickness, height);
	}

	public void line (float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;

		float angle = (float)Math.toDegrees(3.14159f + Math.atan2(dy, dx));
		float length = (float)Math.sqrt(dx * dx + dy * dy);

		draw(texture, x1, y1, 0.0f, 0.0f, length, thickness * camera.zoom, 1, 1, angle, 0, 0, 1, 1, false, false);
	}

	public void line (Vector2 tl, Vector2 tr) {
		line(tl.x, tl.y, tr.x, tr.y);
	}

	/** Draws a polyline in the x/y plane using {@link ShapeType#Line}. The vertices must contain at least 2 points (4 floats
	 * x,y). */
	public void polyline (float[] vertices, int offset, int count) {
		if (count < 4)
			throw new IllegalArgumentException("Polylines must contain at least 2 points.");
		if (count % 2 != 0)
			throw new IllegalArgumentException("Polylines must have an even number of vertices.");

		for (int i = offset, n = offset + count - 2; i < n; i += 2) {
			float x1 = vertices[i];
			float y1 = vertices[i + 1];

			float x2;
			float y2;

			x2 = vertices[i + 2];
			y2 = vertices[i + 3];
			line(x1, y1, x2, y2);
		}
	}

	/** @see #polyline(float[], int, int) */
	public void polyline (float[] vertices) {
		polyline(vertices, 0, vertices.length);
	}

	public void drawCone (float x, float y, float rotation, float degrees, float length) {
		int indexcount = 360;
		float[] indicies = new float[indexcount * 2];
		for (int i = 0; i < indexcount; i++) {
			float offset = degrees * (i / (indexcount - 1f));
			Vector2 endpoint = new Vector2(x + (float)Math.cos(Math.toRadians(rotation + offset)) * length,
				y + (float)Math.sin(Math.toRadians(rotation + offset)) * length);
			indicies[2 * i] = endpoint.x;
			indicies[2 * i + 1] = endpoint.y;
		}
		polyline(indicies);
	}

	public void box (float x, float y, float viewportWidth, float viewportHeight) {
		line(x - viewportWidth / 2, y - viewportHeight / 2, x + viewportWidth / 2, y - viewportHeight / 2);
		line(x - viewportWidth / 2, y + viewportHeight / 2, x + viewportWidth / 2, y + viewportHeight / 2);
		line(x - viewportWidth / 2, y - viewportHeight / 2, x - viewportWidth / 2, y + viewportHeight / 2);
		line(x + viewportWidth / 2, y - viewportHeight / 2, x + viewportWidth / 2, y + viewportHeight / 2);
	}

	public void setThickness (int thickness) {
		this.thickness = thickness;
	}

	@Override
	public void dispose () {
		texture.dispose();
	}

	@Override
	public void begin () {
		this.setProjectionMatrix(camera.combined);
		super.begin();
	}

	public void circle (float x, float y, float radius, int lineCount) {
		float lastX = 0;
		float lastY = 1;
		for (int i = 0; i < lineCount + 1; i++) {
			float currentX = (float)Math.sin(Math.toRadians(i * 360f / lineCount)) * radius;
			float currentY = (float)Math.cos(Math.toRadians(i * 360f / lineCount)) * radius;
			this.line(x + lastX, y + lastY, x + currentX, y + currentY);
			lastX = currentX;
			lastY = currentY;
		}
	}

	public void circle (Vector2 position, float radius, int lineCount) {
		circle(position.x, position.y, radius, lineCount);
	}
}
