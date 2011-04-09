package me.xjunior.remoteworks.script;

import java.util.Stack;

public class ScriptRunner {
	private Stack<ScriptSource> dependencies;
	
	public ScriptRunner(ScriptSource source) {
		dependencies.push(source);
	}
}
