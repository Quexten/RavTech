
package box2dLight;

public class DynamicLightMap extends LightMap {

	public DynamicLightMap (RayHandler rayHandler, int fboWidth, int fboHeight) {
		super(rayHandler, fboWidth, fboHeight);
	}

	@Override
	public void dispose () {
		super.dispose();
	}
}
