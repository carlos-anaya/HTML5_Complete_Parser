package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Comment_start_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// "-" (U+002D)
		// Switch to the comment start dash state.
		case HYPHEN_MINUS:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Comment_start_dash_state));
			break;

		// U+0000 NULL
		// Parse error. Append a U+FFFD REPLACEMENT CHARACTER character to the
		// comment token's data. Switch to the comment state.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.getCurrentToken().appendValue(0xFFFD);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Comment_state));
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Parse error. Switch to the data state. Emit the comment token.
		case GREATER_THAN_SIGN:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// EOF
		// Parse error. Switch to the data state. Emit the comment token.
		// Reconsume the EOF character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Append the current input character to the comment token's data.
		// Switch to the comment state.
		default:
			tokenizerContext.getCurrentToken().appendValue(currentChar);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Comment_state));
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}