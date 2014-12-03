package com.ssof.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class TwsFileFilter extends FileFilter {	
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String name = f.getName();
		return (name.endsWith(".tws") || name.endsWith(".TWS"));
	}

	public String getDescription() {
		return "Twitter Statistics File (*.tws, *.TWS)";
	}
}

