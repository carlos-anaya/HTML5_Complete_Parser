package com.html5parser.factories;

import com.html5parser.classes.InsertionMode;
import com.html5parser.insertionModes.BeforeHTML;
import com.html5parser.insertionModes.Initial;
import com.html5parser.interfaces.IInsertionMode;

public class InsertionModeFactory {
	
	private static InsertionModeFactory factory;

	private InsertionModeFactory() {
	};

	public static InsertionModeFactory getInstance() {
		if (factory == null) {
			factory = new InsertionModeFactory();
		}
		return factory;
	}

	public IInsertionMode getInsertionMode(InsertionMode insertionMode) {
		switch (insertionMode) {
		case initial:
			return new Initial();
		case before_html:
			return new BeforeHTML();
		default:
			throw new RuntimeException("No state type: " + insertionMode.name());
		}
	}
}
