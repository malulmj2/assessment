package com.perago.techtest;

public enum DiffAction {
	CREATED("Create"),UPDATED("Update"),DELETED("Delete");
	private String display;
	private DiffAction(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}
}
