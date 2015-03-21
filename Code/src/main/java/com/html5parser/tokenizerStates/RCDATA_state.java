package com.html5parser.tokenizerStates;

import com.html5parser.SimplestTreeParser.ParserStacks;
import com.html5parser.SimplestTreeParser.Token;
import com.html5parser.SimplestTreeParser.Token.TokenType;
import com.html5parser.SimplestTreeParser.TokenizerContext;
import com.html5parser.SimplestTreeParser.TreeConstructor;

public class RCDATA_state implements State {

	public boolean process(TokenizerContext context) {
		boolean reconsumeCharacter = false;
		int currentChar = context.getCurrentChar();
		TreeConstructor treeConstructor = context.getTreeConstructor();
		switch (currentChar) {
		// U+0026 AMPERSAND (&)
		// Switch to the character reference in RCDATA state.
		case 0x0026:
			// nextState = new Character_reference_in_data_state();
			ParserStacks.parseErrors
					.push("AMPERSAND (&) Character encountered.");
			context.setState(new Error_state());
			break;

		// U+003C LESS-THAN SIGN (<)
		// Switch to the RCDATA less-than sign state.
		case 0x003C:
			context.setState(new RCDATA_less_than_sign_state());
			break;

		// U+0000 NULL
		// Parse error. Emit a U+FFFD REPLACEMENT CHARACTER character token.
		case 0x0000:
			ParserStacks.parseErrors.push("NULL Character encountered.");
			treeConstructor.processToken(new Token(TokenType.character, String
					.valueOf(Character.toChars(0xFFFD))));
			break;

		// EOF
		// Emit an end-of-file token.
		case -1:
			treeConstructor
					.processToken(new Token(TokenType.end_of_file, null));
			break;

		// Anything else
		// Emit the current input character as a character token.
		default:
			treeConstructor.processToken(new Token(TokenType.character, String
					.valueOf(Character.toChars(currentChar))));
			break;
		}
		
		return reconsumeCharacter;
	}

}
