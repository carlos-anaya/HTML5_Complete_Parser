package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;

public class Bogus_DOCTYPE_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();

		switch (tokenizerContext.getCurrentASCIICharacter()) {

		// U+003E GREATER-THAN SIGN (>)
		// Switch to the data state. Emit the current DOCTYPE token.
		case GREATER_THAN_SIGN:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// EOF
		// Switch to the data state. Emit the DOCTYPE token. Reconsume the EOF
		// character.
		case EOF:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Ignore the character.
		default:
			// Ignore the character.
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}