package com.html5parser.tokenizerStates;

import com.html5parser.classes.ASCIICharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;

public class RCDATA_less_than_sign_state implements ITokenizerState {
	
	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		tokenizerContext.setCurrentInputCharacter(currentChar);

		ASCIICharacter asciiCharacter = tokenizerContext
				.getCurrentASCIICharacter();

		switch (asciiCharacter) {

		case DASH:
			// Set the temporary buffer to the empty string. Switch to the
			// RCDATA end tag open state.
			tokenizerContext.setTemporaryBuffer("");
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.RAWTEXT_end_tag_open_state));
			break;
		default:
			/*
			 * Switch to the RCDATA state. Emit a U+003C LESS-THAN SIGN
			 * character token.Reconsume the current input character.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.RCDATA_state));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character,
					String.valueOf(0x003C)));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
		}

		context.setTokenizerContext(tokenizerContext);

		return context;

	}

}
