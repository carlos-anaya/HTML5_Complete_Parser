package com.html5parser.insertionModes;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class BeforeHTML implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {
		// TODO Before html . Is not finished

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {

		// A DOCTYPE token
		// Parse error. Ignore the token.
		case DOCTYPE:
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			break;

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


			// An end tag whose tag name is one of: "head", "body", "html", "br"
			// Act as described in the "anything else" entry below.
			// Any other end tag
			// Parse error. Ignore the token.
		case end_tag:
			if(token.getValue().equals("head"));
			// Anything else
		case end_of_file:
		default:
			Document doc = parserContext.getDocument();
			Element html = doc.createElement("html");
			doc.appendChild(html);
			Stack<Element> stackOpenElements = parserContext.getOpenElements();
			stackOpenElements.push(html);

			
			// TODO delete this, it just simulates next state
			Token headT = new Token(TokenType.start_tag, "head");
			Element head = InsertAnHTMLElement.run(parserContext, token);
			parserContext.setHeadElementPointer(head);

			// TODO uncomment
			/*
			 * parserContext.setInsertionMode(factory
			 * .getInsertionMode(InsertionMode.before_head));
			 * parserContext.setFlagReconsumeToken(true);
			 */
			break;
		}

		parserContext.setFlagStopParsing(true);// TODO remove this to allow
												// continue building the tree
		return parserContext;
	}

}
