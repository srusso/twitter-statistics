package com.ssof.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class DirFileFilter extends FileFilter {
	public boolean accept(File f) {
		return f.isDirectory();
	}

	public String getDescription() {
		return "Directories";
	}
}
