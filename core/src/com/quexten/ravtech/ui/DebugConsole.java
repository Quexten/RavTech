
package com.quexten.ravtech.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.widget.ScrollableTextArea;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;
import com.kotcrab.vis.ui.widget.VisWindow;

public class DebugConsole extends VisWindow {

	ScrollableTextArea textArea;
	boolean visible;

	public DebugConsole () {
		super("Debug Console");

		textArea = new ScrollableTextArea ("Test");
		textArea.setFocusTraversal(false);
		textArea.setDisabled(false);		

		VisTextField textField = new VisTextField();
		textField.setFocusTraversal(false);
		textField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped (VisTextField textField, char c) {
				if (c == '\n' || c == '\r') {
					com.quexten.ravtech.util.Debug.runScript(textField.getText());
					textField.setText("");
				}
			}
		});
		this.add(new VisScrollPane(textArea)).grow();
		row();
		this.add(textField).growX();
		setSize(600, 300);
		addCloseButton();
		this.fadeOut(0);
	}

	public void log (String tag, String message) {
		//textArea.appendText("[" + tag + "] : " + message + "\n");
	}

	public void logError (String tag, String message) {
		//textArea.appendText("[" + tag + "] : " + message + "\n");
	}

	public void logDebug (String tag, String message) {
		//textArea.appendText("[" + tag + "] : " + message + "\n");
	}

	public void toggleVisible () {
		if (getZIndex() != getParent().getChildren().size - 1 || !isVisible() || !visible) {
			toFront();
			this.fadeIn();
			visible = true;
		} else {
			toBack();
			this.fadeOut();
		}
	}

	/** Fade outs this window, when fade out animation is completed, window is removed from Stage */
	@Override
	public void fadeOut (float time) {
		addAction(Actions.sequence(Actions.fadeOut(time, Interpolation.fade)));
		visible = false;
	}

}
