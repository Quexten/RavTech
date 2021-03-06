
package com.quexten.ravtech.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.util.JsonUtil;

public class RenderProperties implements Serializable {

	public static final String LAYER_DEFAULT = "Default";
	public static final String LAYER_LIGHTS = "Lights";
	
	public Color backgroundColor = new Color(0.5f, 0.5f, 0.5f, 1);
	public Color ambientLightColor = new Color(0.1f, 0.1f, 0.1f, 0.5f);
	public Array<String> sortingLayers = new Array<String>();

	public RenderProperties () {
		backgroundColor = Color.WHITE;
		sortingLayers.add(LAYER_DEFAULT);
		sortingLayers.add(LAYER_LIGHTS);
	}

	@Override
	public void write (Json json) {
		json.writeArrayStart("sortingLayers");
		for (int i = 0; i < sortingLayers.size; i++)
			json.writeValue(sortingLayers.get(i));
		json.writeArrayEnd();
		JsonUtil.writeColorToJson(json, backgroundColor, "backgroundColor");
		JsonUtil.writeColorToJson(json, ambientLightColor, "ambientLightColor");
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		sortingLayers.clear();
		JsonValue sortingLayersValue = jsonData.get("sortingLayers");
		JsonValue currentLayerValue = sortingLayersValue.child();
		while (currentLayerValue != null) {
			sortingLayers.add(currentLayerValue.asString());
			currentLayerValue = currentLayerValue.next();
		}
		backgroundColor = JsonUtil.readColorFromJson(jsonData, "backgroundColor");
		ambientLightColor = JsonUtil.readColorFromJson(jsonData, "ambientLightColor");
	}
}
