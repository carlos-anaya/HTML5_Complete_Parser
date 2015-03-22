package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Attribute_value_unquoted_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// "tab" (U+0009)
		// "LF" (U+000A)
		// "FF" (U+000C)
		// U+0020 SPACE
		// Switch to the before attribute name state..
		case TAB:
		case LF:
		case FF:
		case SPACE:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Before_attribute_name_state));
			break;

		// U+0026 AMPERSAND (&)
		// Switch to the character reference in attribute value state, with the
		// additional allowed character being U+003E GREATER-THAN SIGN (>).
		case AMPERSAND:
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.Character_reference_in_attribute_value_state));
			tokenizerContext.setNextInputCharacter(0x003E);
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Switch to the data state. Emit the current tag token.
		case GREATER_THAN_SIGN:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
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

		// U+0022 QUOTATION MARK (")
		// "'" (U+0027)
		// U+003C LESS-THAN SIGN (<)
		// "=" (U+003D)
		// "`" (U+0060)
		// Parse error. Treat it as per the "anything else" entry below.
		case QUOTATION_MARK:
		case APOSTROPHE:
		case LESS_THAN_SIGN:
		case EQUALS_SIGN:
		case GRAVE_ACCENT:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
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