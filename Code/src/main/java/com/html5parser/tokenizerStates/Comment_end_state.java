package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Comment_end_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// U+003E GREATER-THAN SIGN (>)
		// Switch to the data state. Emit the comment token.
		case GREATER_THAN_SIGN:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// U+0000 NULL
		// Parse error. Append two U+002D HYPHEN-MINUS characters (-) and a
		// U+FFFD REPLACEMENT CHARACTER character to the comment token's data.
		// Switch to the comment state.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.getCurrentToken().appendValue(0x002D);
			tokenizerContext.getCurrentToken().appendValue(0x002D);
			tokenizerContext.getCurrentToken().appendValue(0xFFFD);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Comment_state));
			break;

		// U+0021 EXCLAMATION MARK (!)
		// Parse error. Switch to the comment end bang state.
		case EXCLAMATION_MARK:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Comment_end_bang_state));
			break;

		// "-" (U+002D)
		// Parse error. Append a U+002D HYPHEN-MINUS character (-) to the
		// comment token's data.
		case HYPHEN_MINUS:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.getCurrentToken().appendValue(0x002D);
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
		// Parse error. Append two U+002D HYPHEN-MINUS characters (-) and the
		// current input character to the comment token's data. Switch to the
		// comment state.
		default:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.getCurrentToken().appendValue(0x002D);
			tokenizerContext.getCurrentToken().appendValue(0x002D);
			tokenizerContext.getCurrentToken().appendValue(currentChar);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Comment_state));
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}
