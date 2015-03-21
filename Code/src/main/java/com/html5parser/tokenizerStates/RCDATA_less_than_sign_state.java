package com.html5parser.tokenizerStates;

import com.html5parser.SimplestTreeParser.Token;
import com.html5parser.SimplestTreeParser.Token.TokenType;
import com.html5parser.SimplestTreeParser.TokenizerContext;
import com.html5parser.SimplestTreeParser.TreeConstructor;

public class RCDATA_less_than_sign_state implements State {

	public boolean process(TokenizerContext context) {
		boolean reconsumeCharacter = false;
		int currentChar = context.getCurrentChar();
		TreeConstructor treeConstructor = context.getTreeConstructor();
		switch (currentChar) {
		// "/" (U+002F)
		// Set the temporary buffer to the empty string. Switch to the RCDATA
		// end tag open state.
		case 0x002F:
			context.setState(new RCDATA_end_tag_open_state());
			break;

		// Anything else
		// Switch to the RCDATA state. Emit a U+003C LESS-THAN SIGN character
		// token. Reconsume the current input character.
		default:
			context.setState(new RCDATA_state());
			treeConstructor.processToken(new Token(TokenType.character, String
					.valueOf(0x003C)));
			reconsumeCharacter = true;
			break;
		}
		
		return reconsumeCharacter;
	}

}
