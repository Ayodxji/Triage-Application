package com.joshua.symptom;

public class Symptom {
	private String name;
	private SymptomPriority priority;
	
	public Symptom(String name,SymptomPriority priority) {
		this.name = name;
		this.priority = priority;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SymptomPriority getSymptomPriority() {
		return priority;
	}
	public void setSymptomPriority(SymptomPriority priority) {
		this.priority = priority;
	}
}
