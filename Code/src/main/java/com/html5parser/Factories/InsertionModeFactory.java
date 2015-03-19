package com.html5parser.Factories;

import com.html5parser.Classes.InsertionMode;
import com.html5parser.InsertionModes.BeforeHTML;
import com.html5parser.InsertionModes.Initial;
import com.html5parser.Interfaces.IInsertionMode;

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
			// case Character_reference_in_data_state:
			// case Tag_open_state:
		case before_html:
			return new BeforeHTML();
		default:
			throw new RuntimeException("No state type: " + insertionMode.name());
		}
	}
}
