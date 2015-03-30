package com.html5parser.tokenizerStates;

import java.util.LinkedList;
import java.util.Queue;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.token.TagToken;

public class Character_reference_in_attribute_value_state  {
	protected Queue<Token> reference = new LinkedList<Token>();
	protected boolean parsingCharacterReference = false;

	protected void attemptToConsumeReference(ParserContext context,
			TokenizerContext tokenizerContext) {
		Queue<Token> result = Tokenizing_character_references
				.getTokenCharactersFromReference(reference, context);

		if (result != null) {
			for (Token tokenResult : result) {
				((TagToken) tokenizerContext.getCurrentToken())
				.appendCharacterInValueInLastAttribute(tokenResult.getValue());
			}
		}
		parsingCharacterReference = false;
		reference = new LinkedList<Token>();
	}
	
}