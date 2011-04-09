package me.xjunior.remoteworks.gui;

public interface KeyboardInteractioListener {
	public String onInteraction(String error, String name, String instruction, String prompt, boolean echo);
}