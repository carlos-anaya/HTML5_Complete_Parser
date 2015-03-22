package com.html5parser.tokenizerStates;

import com.html5parser.classes.ASCIICharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class RCDATA_state implements ITokenizerState {
	
	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();

		int currentChar = tokenizerContext.getCurrentInputCharacter();
		ASCIICharacter asciiCharacter = tokenizerContext
				.getCurrentASCIICharacter();

		switch (asciiCharacter) {

		case AMPERSAND:
			// Switch to the character reference in RCDATA state.
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.Character_reference_in_RCDATA_state));
			break;
		case LESS_THAN_SIGN:
			// Switch to the RCDATA less-than sign state.
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.RCDATA_less_than_sign_state));
			break;
		case NULL:
			// Parse error. Emit a U+FFFD REPLACEMENT CHARACTER character token.
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.emitCurrentToken(new Token(TokenType.character,
					String.valueOf(0xFFFD)));
			break;
		case EOF:
			// Emit an end-of-file token.
			tokenizerContext.emitCurrentToken(new Token(TokenType.character,
					null));
			break;
		default:
			// Emit the current input character as a character token.
			tokenizerContext.emitCurrentToken(new Token(TokenType.character,
					String.valueOf(currentChar)));
			break;
		}

		context.setTokenizerContext(tokenizerContext);

		return context;
	}

}
