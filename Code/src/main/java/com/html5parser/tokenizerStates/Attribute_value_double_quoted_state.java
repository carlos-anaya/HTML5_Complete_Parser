package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Attribute_value_double_quoted_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// U+0022 QUOTATION MARK (")
		// Switch to the after attribute value (quoted) state.
		case QUOTATION_MARK:
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.After_attribute_value_quoted_state));
			break;

		// U+0026 AMPERSAND (&)
		// Switch to the character reference in attribute value state, with the
		// additional allowed character being "'" (U+0027).
		case AMPERSAND:
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.Character_reference_in_attribute_value_state));
			tokenizerContext.setNextInputCharacter(0x0027);
			break;

		// U+0000 NULL
		// Parse error. Append a U+FFFD REPLACEMENT CHARACTER character to the
		// current attribute's value.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			((TagToken) tokenizerContext.getCurrentToken())
					.appendCharacterInValueInLastAttribute(0xFFFD);
			break;

		// EOF
		// Parse error. Switch to the data state. Reconsume the EOF
		// character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Append the current input character to the current attribute's
		// value.
		default:
			((TagToken) tokenizerContext.getCurrentToken())
					.appendCharacterInValueInLastAttribute(currentChar);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}