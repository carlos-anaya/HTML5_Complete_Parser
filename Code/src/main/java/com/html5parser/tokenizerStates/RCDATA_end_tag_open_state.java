package com.html5parser.tokenizerStates;

import com.html5parser.SimplestTreeParser.Token;
import com.html5parser.SimplestTreeParser.Token.TokenType;
import com.html5parser.SimplestTreeParser.TokenizerContext;
import com.html5parser.SimplestTreeParser.TreeConstructor;

public class RCDATA_end_tag_open_state implements State {

	public boolean process(TokenizerContext context) {
		boolean reconsumeCharacter = false;
		int currentChar = context.getCurrentChar();
		TreeConstructor treeConstructor = context.getTreeConstructor();

		// U+0041 LATIN CAPITAL LETTER A through to U+005A LATIN CAPITAL LETTER
		// Z
		// Create a new end tag token, set its tag name to the lowercase
		// version of the current input character (add 0x0020 to the character's
		// code point), then switch to the tag name state. (Don't emit the token
		// yet; further details will be filled in before it is emitted.)
		if (currentChar > 64 && currentChar < 91) {
			// Transfrom to the lowercase ASCII
			currentChar += 0x0020;
		}

		// U+0061 LATIN SMALL LETTER A through to U+007A LATIN SMALL LETTER Z
		// Create a new end tag token, set its tag name to the current input
		// character, then switch to the tag name state. (Don't emit the token
		// yet; further details will be filled in before it is emitted.)
		if (currentChar > 96 && currentChar < 123) {
			Token currentToken = new Token(TokenType.end_tag,
					String.valueOf(Character.toChars(currentChar)));
			context.setCurrentToken(currentToken);
			context.setState(new Tag_name_state());
		} else {
			// Anything else
			// Switch to the RCDATA state. Emit a U+003C LESS-THAN SIGN
			// character token and a U+002F SOLIDUS character token. Reconsume
			// the current input character.
			context.setState(new RCDATA_state());
			treeConstructor.processToken(new Token(TokenType.character, String
					.valueOf(0x003C)));
			treeConstructor.processToken(new Token(TokenType.character, String
					.valueOf(0x002F)));
			reconsumeCharacter = true;
		}
		
		return reconsumeCharacter;
	}

}
