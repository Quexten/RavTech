
package com.ravelsoftware.ravtech.dk.zerobrane;

import java.io.File;
import java.io.IOException;

import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.utils.GitHubUpdater;
import com.ravelsoftware.ravtech.dk.ui.utils.UpdateManager;

public class ZeroBraneUtil {

	public static void initialize () {
		UpdateManager.addUpdater("ZeroBraneStudio", new GitHubUpdater("pkulchenko", "ZeroBraneStudio")
			.setDescription("ZeroBrane is the standard Integrated Development\nEnviroment for the RavTech Development Kit.\nIt is released under the MIT License.")
			.setProjectPage("https://studio.zerobrane.com/"));
	}

	public static void openFile (File file) {
		ProcessBuilder b = new ProcessBuilder(
			RavTechDK.getLocalFile("/zbstudio/zbstudio." + RavTechDK.getSystemExecutableEnding()).path(),
			RavTechDK.projectHandle.child("assets").path(), file.getPath());
		try {
			b.start();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
