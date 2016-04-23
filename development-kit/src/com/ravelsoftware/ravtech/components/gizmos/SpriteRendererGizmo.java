
package com.ravelsoftware.ravtech.components.gizmos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.util.EventType;
import com.ravelsoftware.ravtech.util.GeometryUtils;

public class SpriteRendererGizmo extends Gizmo<SpriteRenderer> {

	SpriteRenderer spriteRenderer;
	boolean isGrabbed = false;
	int grabbedPoint = 0;
	Vector2 oldPosition;
	Vector2 trueOldPosition;
	Vector2 oldBounds;
	boolean canEdit = true;
	float ppuX; // Pixels per Unit
	float ppuY; // Pixels per Unit
	int oldSrcWidth;
	int oldSrcHeight;
	int oldSrcX;
	int oldSrcY;
	float closestDst;
	int selectedPoint;

	public SpriteRendererGizmo (SpriteRenderer spriteRenderer) {
		super(spriteRenderer);
		this.spriteRenderer = spriteRenderer;
	}

	@Override
	public void draw (PolygonShapeRenderer renderer,
		boolean selected) {
		if (!canEdit)
			return;
		renderer.setColor(Color.LIGHT_GRAY);
		float rotation = spriteRenderer.getParent().transform
			.getRotation();
		Vector2 middlePosition = spriteRenderer.getParent().transform
			.getPosition().cpy()
			.sub(new Vector2(
				spriteRenderer.originX * (spriteRenderer.width / 2),
				spriteRenderer.originY * (spriteRenderer.height / 2))
					.scl(component.getParent().transform.getLocalScale())
					.rotate(rotation));
		Vector2 tl = middlePosition.cpy()
			.sub(new Vector2(spriteRenderer.width / 2,
				spriteRenderer.height / 2)
					.scl(component.getParent().transform.getLocalScale())
					.rotate(+rotation));
		Vector2 tr = middlePosition.cpy()
			.sub(new Vector2(spriteRenderer.width / 2,
				-spriteRenderer.height / 2)
					.scl(component.getParent().transform.getLocalScale())
					.rotate(+rotation));
		Vector2 br = middlePosition.cpy()
			.add(new Vector2(spriteRenderer.width / 2,
				spriteRenderer.height / 2)
					.scl(component.getParent().transform.getLocalScale())
					.rotate(+rotation));
		Vector2 bl = middlePosition.cpy()
			.add(new Vector2(spriteRenderer.width / 2,
				-spriteRenderer.height / 2)
					.scl(component.getParent().transform.getLocalScale())
					.rotate(+rotation));
		// tl
		Vector2 tlb = tl.cpy().interpolate(bl, 0.25f,
			Interpolation.linear);
		Vector2 tlr = tl.cpy().interpolate(tr, 0.25f,
			Interpolation.linear);
		// tr
		Vector2 trb = tr.cpy().interpolate(br, 0.25f,
			Interpolation.linear);
		Vector2 trl = tr.cpy().interpolate(tl, 0.25f,
			Interpolation.linear);
		// br
		Vector2 brt = br.cpy().interpolate(tr, 0.25f,
			Interpolation.linear);
		Vector2 brl = br.cpy().interpolate(bl, 0.25f,
			Interpolation.linear);
		// bl
		Vector2 blt = bl.cpy().interpolate(tl, 0.25f,
			Interpolation.linear);
		Vector2 blr = bl.cpy().interpolate(br, 0.25f,
			Interpolation.linear);
		renderer.line(tl, tr);
		renderer.line(tr, br);
		renderer.line(br, bl);
		renderer.line(bl, tl);
		// renderer.begin(ShapeType.Line);
		if (selected) {
			renderer.setThickness(4);
			renderer.setColor(Color.YELLOW);
		}
		switch (selectedPoint) {
			case 0:
				renderer.line(tlb, tl);
				renderer.line(tlr, tl);
				break;
			case 1:
				renderer.line(trb, tr);
				renderer.line(trl, tr);
				break;
			case 2:
				renderer.line(brt, br);
				renderer.line(brl, br);
				break;
			case 3:
				renderer.line(blt, bl);
				renderer.line(blr, bl);
				break;
			case 4:
				renderer.line(tr, br);
				break;
			case 5:
				renderer.line(br, bl);
				break;
			case 6:
				renderer.line(bl, tl);
				break;
			case 7:
				renderer.line(tl, tr);
				break;
		}
		renderer.setColor(Color.GRAY);
		// renderer.begin(ShapeType.Line);
		renderer.setThickness(1);
	}

	@Override
	public float input (float x, float y, int button, int eventType) {
		float rotation = spriteRenderer.getParent().transform
			.getRotation();
		Vector2 middlePosition = spriteRenderer.getParent().transform
			.getPosition().cpy()
			.sub(new Vector2(
				spriteRenderer.originX * (spriteRenderer.width / 2),
				spriteRenderer.originY * (spriteRenderer.height / 2))
					.scl(component.getParent().transform.getLocalScale())
					.rotate(rotation));
		Vector2 mousePosition = new Vector2(x, y);
		switch (eventType) {
			case EventType.MouseMoved:
				Vector2 tl = middlePosition.cpy()
					.sub(new Vector2(spriteRenderer.width / 2,
						spriteRenderer.height / 2).scl(
							component.getParent().transform.getLocalScale())
						.rotate(+rotation));
				Vector2 tr = middlePosition.cpy()
					.sub(new Vector2(spriteRenderer.width / 2,
						-spriteRenderer.height / 2).scl(
							component.getParent().transform.getLocalScale())
						.rotate(+rotation));
				Vector2 br = middlePosition.cpy()
					.add(new Vector2(spriteRenderer.width / 2,
						spriteRenderer.height / 2).scl(
							component.getParent().transform.getLocalScale())
						.rotate(+rotation));
				Vector2 bl = middlePosition.cpy()
					.add(new Vector2(spriteRenderer.width / 2,
						-spriteRenderer.height / 2).scl(
							component.getParent().transform.getLocalScale())
						.rotate(+rotation));
				closestDst = Float.MAX_VALUE;
				Array<Vector2> positions = new Array<Vector2>();
				positions.add(tl);
				positions.add(tr);
				positions.add(br);
				positions.add(bl);
				float camFactor = RavTech.sceneHandler.worldCamera.zoom
					* 20f;
				float lDst = GeometryUtils.isInBoundingBox(tl, tr,
					mousePosition, camFactor)
						? GeometryUtils.dstFromLine(tl, tr, mousePosition)
						: Float.MAX_VALUE;
				float tDst = GeometryUtils.isInBoundingBox(tr, br,
					mousePosition, camFactor)
						? GeometryUtils.dstFromLine(tr, br, mousePosition)
						: Float.MAX_VALUE;
				float rDst = GeometryUtils.isInBoundingBox(br, bl,
					mousePosition, camFactor)
						? GeometryUtils.dstFromLine(br, bl, mousePosition)
						: Float.MAX_VALUE;
				float bDst = GeometryUtils.isInBoundingBox(bl, tl,
					mousePosition, camFactor)
						? GeometryUtils.dstFromLine(bl, tl, mousePosition)
						: Float.MAX_VALUE;
				if (closestDst > tDst) {
					selectedPoint = 4;
					closestDst = tDst;
				}
				if (closestDst > rDst) {
					selectedPoint = 5;
					closestDst = rDst;
				}
				if (closestDst > bDst) {
					selectedPoint = 6;
					closestDst = bDst;
				}
				if (closestDst > lDst) {
					selectedPoint = 7;
					closestDst = lDst;
				}
				for (int i = 0; i < positions.size; i++)
					if (camFactor > positions.get(i).dst(mousePosition)) {
						closestDst = positions.get(i).dst(mousePosition);
						selectedPoint = i;
					}
				if (closestDst > camFactor)
					return -1f;
				break;
			case EventType.MouseDown:
				ppuX = spriteRenderer.srcWidth / spriteRenderer.width;
				ppuY = spriteRenderer.srcHeight / spriteRenderer.height;
				oldSrcWidth = spriteRenderer.srcWidth;
				oldSrcHeight = spriteRenderer.srcHeight;
				oldSrcX = spriteRenderer.srcX;
				oldSrcY = spriteRenderer.srcY;
				oldPosition = spriteRenderer.getParent().transform
					.getPosition().cpy()
					.sub(new Vector2(
						spriteRenderer.originX * (spriteRenderer.width / 2),
						spriteRenderer.originY
							* (spriteRenderer.height / 2))
								.scl(component.getParent().transform
									.getLocalScale())
								.rotate(rotation));
				middlePosition = oldPosition;
				oldPosition = spriteRenderer.getParent().transform
					.getPosition().cpy()
					.sub(new Vector2(
						spriteRenderer.originX * (spriteRenderer.width / 2),
						spriteRenderer.originY
							* (spriteRenderer.height / 2))
								.scl(component.getParent().transform
									.getLocalScale())
								.rotate(rotation));
				trueOldPosition = spriteRenderer.getParent().transform
					.getPosition().cpy()
					.sub(new Vector2(
						spriteRenderer.originX * spriteRenderer.width,
						spriteRenderer.originY * spriteRenderer.height)
							.rotate(0));
				oldBounds = new Vector2(spriteRenderer.width,
					spriteRenderer.height);
				isGrabbed = true;
				grabbedPoint = selectedPoint;
				return -1f;
			case EventType.MouseDrag:
				Vector2 antiScale = new Vector2(
					1f / component.getParent().transform.getLocalScale().x,
					1f / component.getParent().transform
						.getLocalScale().y);
				if (isGrabbed)
					switch (grabbedPoint) {
						case 0: // tl
							changeBounds(
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).x,
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).y,
								false, false);
							break;
						case 1: // TOP-LEFT
							changeBounds(
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).x,
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).y,
								false, true);
							break;
						case 2: // br
							changeBounds(
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).x,
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).y,
								true, true);
							break;
						case 3: // bl
							changeBounds(
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).x,
								mousePosition.cpy().sub(oldPosition)
									.scl(antiScale).rotate(
										-spriteRenderer.getParent().transform
											.getRotation()).y,
								true, false);
							break;
						case 4: // t
							changeHeight(mousePosition.sub(oldPosition)
								.scl(antiScale)
								.rotate(-spriteRenderer.getParent().transform
									.getRotation()).y,
								true);
							break;
						case 5: // r
							changeWidth(mousePosition.sub(oldPosition)
								.scl(antiScale)
								.rotate(-spriteRenderer.getParent().transform
									.getRotation()).x,
								true);
							break;
						case 6: // b
							changeHeight(mousePosition.sub(oldPosition)
								.scl(antiScale)
								.rotate(-spriteRenderer.getParent().transform
									.getRotation()).y,
								false);
							break;
						case 7: // l
							changeWidth(mousePosition.sub(oldPosition)
								.scl(antiScale)
								.rotate(-spriteRenderer.getParent().transform
									.getRotation()).x,
								false);
							break;
						case 8:
							Vector2 subPosition = mousePosition
								.sub(spriteRenderer.getParent().transform
									.getPosition().cpy());
							spriteRenderer.originX = -subPosition.x
								/ spriteRenderer.width * 2;
							spriteRenderer.originY = -subPosition.y
								/ spriteRenderer.height * 2;
							break;
					}
				return -1f;
			case EventType.MouseUp:
				isGrabbed = false;
				break;
		}
		return closestDst;
	}

	private void changeWidth (float width, boolean changeRight) {
		width = (changeRight ? 1f : -1f) * (width - oldBounds.x * 0.5f)
			+ oldBounds.x * (changeRight ? 1 : 0);
		Vector2 addPosition = new Vector2(
			changeRight ? width + -oldBounds.x : oldBounds.x - width, 0);
		spriteRenderer.originX = -trueOldPosition.cpy().add(addPosition)
			.sub(spriteRenderer.getParent().transform.getPosition()
				.cpy()).x
			/ width;
		spriteRenderer.originY = spriteRenderer.originY; // -trueOldPosition.cpy().add(addPosition).sub(spriteRenderer.getParent().transform.getPosition().cpy()).y
																			// /
																			// (spriteRenderer.height
																			// );
		spriteRenderer.width = width;
		if (spriteRenderer.uWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcWidth = (int)(ppuX * width);
			if (!changeRight)
				spriteRenderer.srcX = oldSrcX + oldSrcWidth
					- spriteRenderer.srcWidth;
		}
	}

	private void changeHeight (float height, boolean changeTop) {
		height = (changeTop ? 1 : -1f) * (height - oldBounds.y * 0.5f)
			+ oldBounds.y * (changeTop ? 1 : 0);
		Vector2 addPosition = new Vector2(0,
			changeTop ? height + -oldBounds.y : oldBounds.y - height);
		spriteRenderer.originX = spriteRenderer.originX;
		spriteRenderer.originY = -trueOldPosition.cpy().add(addPosition)
			.sub(spriteRenderer.getParent().transform.getPosition()
				.cpy()).y
			/ height;
		spriteRenderer.height = height;
		if (spriteRenderer.vWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcHeight = (int)(ppuY * height);
			if (changeTop)
				spriteRenderer.srcY = oldSrcY + oldSrcHeight
					- spriteRenderer.srcHeight;
		}
	}

	private void changeBounds (float width, float height,
		boolean changeRight, boolean changeTop) {
		width = (changeRight ? 1f : -1f) * (width - oldBounds.x * 0.5f)
			+ oldBounds.x * (changeRight ? 1 : 0);
		Vector2 addPosition = new Vector2(
			changeRight ? width + -oldBounds.x : oldBounds.x - width, 0);
		spriteRenderer.originX = -trueOldPosition.cpy().add(addPosition)
			.sub(spriteRenderer.getParent().transform.getPosition()
				.cpy()).x;
		spriteRenderer.originY = trueOldPosition.cpy().add(addPosition)
			.sub(spriteRenderer.getParent().transform.getPosition()
				.cpy()).y;
		height = (changeTop ? 1 : -1f) * (height - oldBounds.y * 0.5f)
			+ oldBounds.y * (changeTop ? 1 : 0);
		Vector2 addPosition2 = new Vector2(0,
			changeTop ? height + -oldBounds.y : oldBounds.y - height);
		Vector2 newPosition = new Vector2(spriteRenderer.originX,
			spriteRenderer.originY);
		spriteRenderer.originX = newPosition.cpy().add(addPosition2).x
			/ width;
		spriteRenderer.originY = -newPosition.cpy().add(addPosition2).y
			/ height;
		spriteRenderer.width = width;
		spriteRenderer.height = height;
		if (spriteRenderer.uWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcWidth = (int)(ppuX * width);
			if (!changeRight)
				spriteRenderer.srcX = oldSrcX + oldSrcWidth
					- spriteRenderer.srcWidth;
		}
		if (spriteRenderer.vWrap.equals(TextureWrap.Repeat)) {
			spriteRenderer.srcHeight = (int)(ppuY * height);
			if (changeTop)
				spriteRenderer.srcY = oldSrcY + oldSrcHeight
					- spriteRenderer.srcHeight;
		}
	}

	@Override
	public boolean isInBoundingBox (Vector2 coord) {
		float rotation = spriteRenderer.getParent().transform
			.getRotation();
		Vector2 middlePosition = spriteRenderer.getParent().transform
			.getPosition().cpy()
			.sub(new Vector2(
				spriteRenderer.originX * (spriteRenderer.width / 2),
				spriteRenderer.originY * (spriteRenderer.height / 2))
					.rotate(rotation));
		Vector2 bl = middlePosition.cpy()
			.sub(new Vector2(spriteRenderer.width / 2,
				spriteRenderer.height / 2).rotate(+rotation));
		Vector2 tr = middlePosition.cpy()
			.add(new Vector2(spriteRenderer.width / 2,
				spriteRenderer.height / 2).rotate(+rotation));
		return GeometryUtils.isInBoundingBox(bl, tr, coord, 0);
	}

}
