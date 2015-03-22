package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Before_attribute_value_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// "tab" (U+0009)
		// "LF" (U+000A)
		// "FF" (U+000C)
		// U+0020 SPACE
		// Ignore the character.
		case TAB:
		case LF:
		case FF:
		case SPACE:
			break;
		// U+0022 QUOTATION MARK (")
		// Switch to the attribute value (double-quoted) state.
		case QUOTATION_MARK:
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.Attribute_value_double_quoted_state));
			break;
		// U+0026 AMPERSAND (&)
		// Switch to the attribute value (unquoted) state. Reconsume the current
		// input character.
		case AMPERSAND:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Attribute_value_unquoted_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
		// "'" (U+0027)
		// Switch to the attribute value (single-quoted) state.
		case APOSTROPHE:
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.Attribute_value_single_quoted_state));
			break;

		// U+0041 LATIN CAPITAL LETTER A through to U+005A LATIN CAPITAL LETTER
		// Z
		// Start a new attribute in the current tag token. Set that attribute's
		// name to the lowercase version of the current input character (add
		// 0x0020 to the character's code point), and its value to the empty
		// string. Switch to the attribute name state.
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;
			((TagToken) tokenizerContext.getCurrentToken())
					.createAttribute(currentChar);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Attribute_name_state));
			break;

		// U+0000 NULL
		// Parse error. Append a U+FFFD REPLACEMENT CHARACTER character to the
		// current attribute's value. Switch to the attribute value (unquoted)
		// state.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			((TagToken) tokenizerContext.getCurrentToken())
					.appendCharacterInValueInLastAttribute(0xFFFD);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Attribute_value_unquoted_state));
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Parse error. Switch to the data state. Emit the current tag token.
		case GREATER_THAN_SIGN:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(false);
			// EOF
			// Parse error. Switch to the data state. Reconsume the EOF
			// character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// U+003C LESS-THAN SIGN (<)
		// "=" (U+003D)
		// "`" (U+0060)
		// Parse error. Treat it as per the "anything else" entry below.
		case LESS_THAN_SIGN:
		case EQUALS_SIGN:
		case GRAVE_ACCENT:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			// Anything else
			// Append the current input character to the current attribute's
			// value. Switch to the attribute value (unquoted) state.
		default:
			((TagToken) tokenizerContext.getCurrentToken())
					.appendCharacterInValueInLastAttribute(currentChar);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Attribute_value_unquoted_state));
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}