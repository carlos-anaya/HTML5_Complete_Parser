package com.html5parser.Factories;

import com.html5parser.Classes.TokenizerState;
import com.html5parser.Interfaces.ITokenizerState;
import com.html5parser.TokenizerStates.Data_state;

public class TokenizerStateFactory {

	private static TokenizerStateFactory factory;

	private TokenizerStateFactory() {
	};

	public static TokenizerStateFactory getInstance() {
		if (factory == null) {
			factory = new TokenizerStateFactory();
		}
		return factory;
	}

	public ITokenizerState getState(TokenizerState stateType) {
		switch (stateType) {
		case Data_state:
			return new Data_state();
		case Character_reference_in_data_state:
		case Tag_open_state:
		default:
			throw new RuntimeException("No state type: " + stateType.name());
		}
	}
}
