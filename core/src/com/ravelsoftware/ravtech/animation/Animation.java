
package com.ravelsoftware.ravtech.animation;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.components.Animator;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;

public class Animation implements Json.Serializable {

	public Animator animator;
	public Array<Timeline> timelines = new Array<Timeline>();
	int currentTime = 0;
	private int length = 1000;

	public Animation () {
	}

	public Animation (GameObject object) {
	}

	public void update (float deltaTime) {
		setTime(currentTime + Math.round(deltaTime * 1000));
	}

	public void setTime (int time) {
		currentTime = time;
		if (currentTime > getLength())
			currentTime = currentTime - getLength();
		if (currentTime < 0)
			currentTime = getLength();
		for (int i = 0; i < timelines.size; i++)
			timelines.get(i).update(currentTime);
	}

	public int getTime () {
		return currentTime;
	}

	public Timeline getTimeline (GameComponent gameComponent, int id) {
		for (int i = 0; i < timelines.size; i++)
			if (timelines.get(i).component == gameComponent && timelines.get(i).variableId == id)
				return timelines.get(i);
		return null;
	}

	public void removeKeysAtTime (int time) {
		for (int i = 0; i < timelines.size; i++)
			timelines.get(i).removeKeyAtTime(time);
	}

	@Override
	public void write (Json json) {
		json.writeValue("timelines", timelines);
		json.writeValue("animationLength", getLength());
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		for (int i = 0; i < jsonData.get("timelines").size; i++) {
			Timeline timeline = new Timeline();
			timeline.animator = animator;
			timeline.read(json, jsonData.get("timelines").get(i));
			timeline.animation = this;
			timelines.add(timeline);
		}
		if (jsonData.has("animationLength"))
			setLength(jsonData.getInt("animationLength"));
	}

	public void setLength (Integer valueOf) {
		length = valueOf;
	}

	public int getLength () {
		return length;
	}

	public void setLength (int length) {
		this.length = length;
	}
}
