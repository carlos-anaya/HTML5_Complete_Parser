package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class CData_section_state implements ITokenizerState {

	private int bracketCount = 0;

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		// Consume every character up to the next occurrence of the three
		// character sequence U+005D RIGHT SQUARE BRACKET U+005D RIGHT SQUARE
		// BRACKET U+003E GREATER-THAN SIGN (]]>), or the end of the file (EOF),
		// whichever comes first. Emit a series of character tokens consisting
		// of all the characters consumed except the matching three character
		// sequence at the end (if one was found before the end of the file).

		switch (tokenizerContext.getCurrentASCIICharacter()) {

		case EOF:
			while (bracketCount>0){
				tokenizerContext.emitCurrentToken(new Token(
						TokenType.character, 0x005D));
				bracketCount--;
			}
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		case RIGHT_SQUARE_BRACKET:
			bracketCount++;
//			if (bracketCount == 2) {
//				tokenizerContext.emitCurrentToken(new Token(
//						TokenType.character, 0x005D));
//				bracketCount=1;
//			}
			break;

		case GREATER_THAN_SIGN:
			if (bracketCount >= 2) {
				tokenizerContext.setNextState(factory
						.getState(TokenizerState.Data_state));
				bracketCount=bracketCount-2;
				while (bracketCount>0){
					tokenizerContext.emitCurrentToken(new Token(
							TokenType.character, 0x005D));
					bracketCount--;
				}
			} else {
				while (bracketCount>0){
					tokenizerContext.emitCurrentToken(new Token(
							TokenType.character, 0x005D));
					bracketCount--;
				}
				// emit >
				tokenizerContext.emitCurrentToken(new Token(
						TokenType.character, 0x003E));
				bracketCount = 0;
			}
			break;

		// Anything else
		default:
			while (bracketCount>0){
				tokenizerContext.emitCurrentToken(new Token(
						TokenType.character, 0x005D));
				bracketCount--;
			}
			tokenizerContext.emitCurrentToken(new Token(TokenType.character,
					currentChar));
			bracketCount = 0;
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}