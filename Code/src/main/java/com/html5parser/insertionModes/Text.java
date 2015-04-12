package com.html5parser.insertionModes;

import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class Text implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {
		// A character token
		// Insert the token's character.
		case character:
			InsertCharacter.run(parserContext, token);
			break;

		// An end-of-file token
		// Parse error.
		// TODO If the current node is a script element, mark the script element
		// as "already started".
		// Pop the current node off the stack of open elements.
		// Switch the insertion mode to the original insertion mode and
		// reprocess the token.
		case end_of_file:
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			//TODO
			parserContext.getOpenElements().pop();
			parserContext.setInsertionMode(parserContext
					.getOriginalInsertionMode());
			parserContext.setFlagReconsumeToken(true);
			break;
		case end_tag:
			// TODO An end tag whose tag name is "script"
			if (token.getValue().equals("script"))
				throw new UnsupportedOperationException();
			// Any other end tag
			// Pop the current node off the stack of open elements.
			// Switch the insertion mode to the original insertion mode.
			else {
				parserContext.getOpenElements().pop();
				parserContext.setInsertionMode(parserContext
						.getOriginalInsertionMode());
			}
			break;
		default:
			break;
		}
		return parserContext;
	}

}
