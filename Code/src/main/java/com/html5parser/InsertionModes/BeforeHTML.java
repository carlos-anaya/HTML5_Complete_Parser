package com.html5parser.InsertionModes;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.Classes.InsertionMode;
import com.html5parser.Classes.ParserContext;
import com.html5parser.Classes.Token;
import com.html5parser.Classes.Token.TokenType;
import com.html5parser.Factories.InsertionModeFactory;
import com.html5parser.Interfaces.IInsertionMode;
import com.html5parser.algorithms.ElementInsertionAlgorithm;

public class BeforeHTML implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {
		// TODO Before html . Is not finished

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {

		// A DOCTYPE token
		// Parse error. Ignore the token.
		case DOCTYPE:
			throw new UnsupportedOperationException();

			// A comment token
			// Append a Comment node to the Document object with the data
			// attribute
			// set to the data given in the comment token.
		case comment:
			throw new UnsupportedOperationException();

			// A character token that is one of U+0009 CHARACTER TABULATION,
			// "LF"
			// (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
			// Ignore the token.
		case character:
			int currentChar = (int) token.getValue().charAt(0);
			if ((currentChar == 0x0009 || currentChar == 0x000A
					|| currentChar == 0x000C || currentChar == 0x000D || currentChar == 0x0020))
				return parserContext;
			break;

		case start_tag:
			throw new UnsupportedOperationException();

		case end_tag:
			throw new UnsupportedOperationException();

			// Anything else
		case end_of_file:
		default:
			Document doc = parserContext.getDocument();
			Element html = doc.createElement("html");
			doc.appendChild(html);
			Stack<Element> stackOpenElements = parserContext.getOpenElements();
			stackOpenElements.push(html);
			
			//TODO delete this, it just simulates next state
			Token headT = new Token(TokenType.start_tag, "head");
			Element head = ElementInsertionAlgorithm.insertHTMLElement(parserContext, headT);
			parserContext.setHeadElementPointer(head);
			
			
			//TODO uncomment
			/*parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.before_head));
			  parserContext.setFlagReconsumeToken(true);
			 */
			break;
		}

		parserContext.setFlagStopParsing(true);// TODO remove this to allow
												// continue building the tree
		return parserContext;
	}

}
