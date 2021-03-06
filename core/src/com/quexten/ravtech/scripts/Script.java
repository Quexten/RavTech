
package com.quexten.ravtech.scripts;

import com.badlogic.gdx.utils.ObjectMap;

public abstract class Script {

	public abstract void loadChunk (String chunk);

	public abstract void init ();

	public abstract void update ();

	public abstract void setEnviroment (ObjectMap<String, Object> values);

	public abstract boolean isLoaded ();

	public abstract Object callFunction (String name, Object[] args);

	public abstract Object getVariable (String name);
}
