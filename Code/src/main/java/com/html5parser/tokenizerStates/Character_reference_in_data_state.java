package com.html5parser.tokenizerStates;

import java.util.LinkedList;
import java.util.Queue;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;

public class Character_reference_in_data_state implements ITokenizerState {
	private Queue<Token> reference = new LinkedList<Token>();

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory;
		Token token = null;
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		Queue<Token> result;

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// U+0026 AMPERSAND (&)
		// Switch to the character reference in data state.
		case AMPERSAND:
			// Try to consume the last character reference and then remain in
			// the same state
			result = Tokenizing_character_references
					.getTokenCharactersFromReference(reference, context);

			if (result != null) {
				for (Token tokenResult : result) {
					tokenizerContext.emitCurrentToken(tokenResult);
				}
			}
			factory = TokenizerStateFactory.getInstance();
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.Character_reference_in_data_state));
			break;

		// Switch to the data state.
		// Attempt to consume a character reference, with no additional allowed
		// character.
		// If nothing is returned, emit a U+0026 AMPERSAND character (&) token.
		// Otherwise, emit the character tokens that were returned.
		case LESS_THAN_SIGN:
		case NULL:
		case EOF:
			result = Tokenizing_character_references
					.getTokenCharactersFromReference(reference, context);

			if (result == null)
				tokenizerContext.emitCurrentToken(new Token(
						TokenType.character, 0x0026));
			else
				for (Token tokenResult : result) {
					tokenizerContext.emitCurrentToken(tokenResult);
				}

			factory = TokenizerStateFactory.getInstance();
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Add a token in the stack of token but without emit to try Later to
		// consume a reference
		default:
			token = new Token(TokenType.character, currentChar);
			reference.add(token);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}