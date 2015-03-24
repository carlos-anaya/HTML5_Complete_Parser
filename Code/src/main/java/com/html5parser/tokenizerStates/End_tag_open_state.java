package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class End_tag_open_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		Token token = null;
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// U+003E GREATER-THAN SIGN (>)
		// Parse error. Switch to the data state.
		case GREATER_THAN_SIGN:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			break;

		// EOF
		// Parse error. Switch to the data state. Emit a U+003C LESS-THAN
		// SIGN character token and a U+002F SOLIDUS character token.
		// Reconsume the EOF character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			token = new Token(TokenType.character, 0x003C);
			tokenizerContext.emitCurrentToken(token);
			token = new Token(TokenType.character, 0x002F);
			tokenizerContext.emitCurrentToken(token);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// U+0041 LATIN CAPITAL LETTER A through to U+005A LATIN CAPITAL LETTER
		// Z
		// Create a new end tag token, set its tag name to the lowercase
		// version of the current input character (add 0x0020 to the character's
		// code point), then switch to the tag name state. (Don't emit the token
		// yet; further details will be filled in before it is emitted.)
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;

			// U+0061 LATIN SMALL LETTER A through to U+007A LATIN SMALL LETTER
			// Z Create a new end tag token, set its tag name to the current
			// input character, then switch to the tag name state. (Don't emit
			// the token yet; further details will be filled in before it is
			// emitted.)
		case LATIN_SMALL_LETTER:
			Token currentToken = new TagToken(TokenType.end_tag, currentChar);
			tokenizerContext.setCurrentToken(currentToken);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Tag_name_state));
			break;
		// Anything else
		// Parse error. Switch to the bogus comment state.
		default:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Bogus_comment_state));

			// Create a comment token for the Bogus_comment_state
			Token commentToken = new Token(TokenType.comment, currentChar);
			tokenizerContext.setCurrentToken(commentToken);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}