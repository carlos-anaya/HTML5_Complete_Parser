package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Attribute_value_single_quoted_state extends Character_reference_in_attribute_value_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// "'" (U+0027)
		// Switch to the after attribute value (quoted) state.
		case APOSTROPHE:
			if (parsingCharacterReference)
				attemptToConsumeReference(context, tokenizerContext);
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.After_attribute_value_quoted_state));
			break;

		// U+0026 AMPERSAND (&)
		// Switch to the character reference in attribute value state, with the
		// additional allowed character being U+0022 QUOTATION MARK (").
		case AMPERSAND:
//			tokenizerContext
//					.setNextState(factory
//							.getState(TokenizerState.Character_reference_in_attribute_value_state));
//			tokenizerContext.setNextInputCharacter(0x0022);
			if (parsingCharacterReference)
				attemptToConsumeReference(context, tokenizerContext);
			parsingCharacterReference = true;
			break;

		// U+0000 NULL
		// Parse error. Append a U+FFFD REPLACEMENT CHARACTER character to the
		// current attribute's value.
		case NULL:
			if (parsingCharacterReference)
				attemptToConsumeReference(context, tokenizerContext);
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			((TagToken) tokenizerContext.getCurrentToken())
					.appendCharacterInValueInLastAttribute(0xFFFD);
			break;

		// EOF
		// Parse error. Switch to the data state. Reconsume the EOF
		// character.
		case EOF:
			if (parsingCharacterReference)
				attemptToConsumeReference(context, tokenizerContext);
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Append the current input character to the current attribute's
		// value.
		default:
			if (!parsingCharacterReference) {
				((TagToken) tokenizerContext.getCurrentToken())
						.appendCharacterInValueInLastAttribute(currentChar);
			} else {
				reference.add(new Token(TokenType.character, currentChar));
			}
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}