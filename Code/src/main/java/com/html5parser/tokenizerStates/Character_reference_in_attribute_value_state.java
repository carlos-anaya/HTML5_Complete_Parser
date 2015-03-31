package com.html5parser.tokenizerStates;

import java.util.LinkedList;
import java.util.Queue;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.token.TagToken;
import com.html5parser.constants.NamedCharacterReference;
import com.html5parser.parseError.ParseErrorType;

public class Character_reference_in_attribute_value_state {
	protected Queue<Token> reference = new LinkedList<Token>();
	protected boolean parsingCharacterReference = false;

	protected void attemptToConsumeReference(ParserContext context,
			TokenizerContext tokenizerContext) {
		Queue<Token> result = Tokenizing_character_references
				.getTokenCharactersFromReference(reference, context);

		if (result != null) {
			for (Token tokenResult : result) {
				((TagToken) tokenizerContext.getCurrentToken())
						.appendCharacterInValueInLastAttribute(tokenResult
								.getValue());
			}
		}
		parsingCharacterReference = false;
		reference = new LinkedList<Token>();
	}

	// If the character reference is being consumed as part of an attribute, and
	// the last character matched is not a ";" (U+003B) character, and the next
	// character is either a "=" (U+003D) character or in the range ASCII
	// digits, uppercase ASCII letters, or lowercase ASCII letters, then, for
	// historical reasons, all the characters that were matched after the U+0026
	// AMPERSAND character (&) must be unconsumed, and nothing is returned.

	// If the possible reference is numerical then try to match,
	// If not numerical, try to match the whole input as a reference and add a
	// parse error because it doesn't end with ;
	// If no match reference add the input to the value of the attribute
	protected void attemptToConsumeReferenceInAttribute(
			TokenizerContext tokenizerContext, ParserContext context) {
		if (parsingCharacterReference) {
			if (!reference.isEmpty() && reference.peek().getValue().equals("#"))
				attemptToConsumeReference(context, tokenizerContext);
			else {
				String original = "";
				// ((TagToken) tokenizerContext.getCurrentToken())
				// .appendCharacterInValueInLastAttribute(0X0026);
				for (Token token : reference)
					original = original.concat(token.getValue());
				// ((TagToken) tokenizerContext.getCurrentToken())
				// .appendCharacterInValueInLastAttribute(token
				// .getValue());

				int[] values = NamedCharacterReference.MAP.get(original.replace(";", ""));
				if (values == null) {
					((TagToken) tokenizerContext.getCurrentToken())
							.appendCharacterInValueInLastAttribute(0x0026);
					((TagToken) tokenizerContext.getCurrentToken())
							.appendCharacterInValueInLastAttribute(original);
				} else {
					for (int value : values)
						((TagToken) tokenizerContext.getCurrentToken())
								.appendCharacterInValueInLastAttribute(value);
					context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
				}

			}
		}
	}
}