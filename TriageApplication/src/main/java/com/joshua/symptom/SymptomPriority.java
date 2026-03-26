package com.joshua.symptom;

public enum SymptomPriority {
	EMERGENCY(5),
	IMMEDIATE(4),
	URGENT(3),
	SEMIURGENT(2),
	NONURGENT(1);
	
	private final int urgency;
	
	SymptomPriority(int urgency) {
		this.urgency = urgency;
	}
	public int getUrgencyValue() {
		return urgency;
	}
}
