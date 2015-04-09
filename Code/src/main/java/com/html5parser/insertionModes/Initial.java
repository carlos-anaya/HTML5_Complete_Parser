package com.html5parser.insertionModes;

import com.html5parser.algorithms.AppropiatePlaceForInsertingANode;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;

public class Initial implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		
		switch (token.getType()) {
		/* A character token that is one of U+0009 CHARACTER TABULATION, "LF"
		 * (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		 *Ignore the token.
		 */
		case character:
			int currentChar = (int) token.getValue().charAt(0);
			if ((currentChar == 0x0009 || currentChar == 0x000A
					|| currentChar == 0x000C || currentChar == 0x000D || currentChar == 0x0020))
				return parserContext;
			break;
		case comment:
			//Insert a comment as the last child of the Document object.
			InsertComment.run(parserContext, token);
			throw new UnsupportedOperationException();

		// A comment token
		// Append a Comment node to the Document object with the data attribute
		// set to the data given in the comment token.
		case DOCTYPE :
			throw new UnsupportedOperationException();
		

			// Anything else
			// TODO If the document is not an iframe srcdoc document, then this
			// is a
			// parse error; set the Document to quirks mode.
			// In any case, switch the insertion mode to "before html", then
			// reprocess the current token.
		default:
			
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.before_html));
			parserContext.setFlagReconsumeToken(true);
		}
		return parserContext;
	}

}
