package me.xjunior.remoteworks.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class ScriptSource {
	private String name;
	private Reader reader;
	private File path;
	
	private StringBuffer source = new StringBuffer();
	private ArrayList<ScriptSource> dependencies;
	private ScriptSource parent;
	
	private String _currentLine;
	
	private ArrayList<ScriptParam> params;
	
	public ScriptSource(String source, File path) throws FileNotFoundException {
		reader = new StringReader(source);
		name = "(unnamed)";
		this.path = path;
		dependencies = new ArrayList<ScriptSource>();
		params = new ArrayList<ScriptParam>();
	}
	
	public ScriptSource(File file) throws FileNotFoundException {
		this(file, null);
	}
	
	public ScriptSource(File file, ScriptSource _p) throws FileNotFoundException {
		parent = _p;
		dependencies = parent == null ? new ArrayList<ScriptSource>() : parent.getDependencies();
		params = parent == null ? new ArrayList<ScriptParam>() : parent.getParams();

		reader = new FileReader(file);
		name = file.getName();
		path = new File(file.getParent());
	}

	private void readInputs(BufferedReader reader) throws IOException, ScriptException {
		ScriptParam newparam;
		while (_currentLine != null) {
			if (_currentLine.startsWith("#input")) {
				String[] input = _currentLine.substring(7).split(":");
				
				ScriptParam.Type type;
				if ((type = ScriptParam.Type.parse(input[1])) == null)
					throw new ScriptException("Invalid type " + input[1]);
				
				newparam = new ScriptParam(input[0], type);
				if (!params.contains(newparam))
					params.add(newparam);
			} else
				break;
			_currentLine = reader.readLine();
		}
	}

	private void readDependencies(BufferedReader reader) throws IOException, ScriptException {
		ScriptSource newdep;
		while (_currentLine != null) {
			if (_currentLine.startsWith("#include")) {
				newdep = new ScriptSource(new File(path.getAbsolutePath() + File.separator + _currentLine.substring(9)), this);
				if (!dependsOn(newdep)) {
					newdep.read();
					dependencies.add(newdep);
				}
			} else
				break;
			_currentLine = reader.readLine();
		}
	}
	
	private void readCode(BufferedReader reader) throws IOException {
		while (_currentLine != null) {
			if (_currentLine.trim().length() > 0) {
				source.append(_currentLine).append("\n");
			}
			_currentLine = reader.readLine();
		}
	}
	
	public void read() throws IOException, ScriptException {
		BufferedReader bfr = new BufferedReader(reader);
		_currentLine = bfr.readLine();
		
		readDependencies(bfr);
		readInputs(bfr);
		readCode(bfr);
	}

	public ArrayList<ScriptSource> getDependencies() {
		return dependencies;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSource() {
		return source.toString();
	}
	
	public ArrayList<ScriptParam> getParams() {
		return params;
	}
	
	public boolean equals(ScriptSource other) {
		return this.path.equals(other.path) && this.name.equals(other.name);
	}
	
	public boolean dependsOn(ScriptSource other) {
		return (equals(other) || dependencies.contains(other));
	}
	
	public String toString() {
		return getName();
	}
}
