package com.ssof.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtFileFilter extends FileFilter {
	/**
	 * L'estensione dei file che si vuole vengano mostrati
	 * nel file chooser, applicando questo filtro.
	 */
	private final String ext;
	
	public ExtFileFilter(String ext){
		this.ext = ext;
	}
	
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String name = f.getName();
		if (name != null) {
			return name.toLowerCase().endsWith(ext.toLowerCase());
		}
		return false;
	}

	public String getDescription() {
		return "File (*" + ext + ")";
	}
}