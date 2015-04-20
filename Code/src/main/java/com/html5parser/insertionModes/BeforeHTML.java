package com.html5parser.insertionModes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.algorithms.CreateAnElementForAToken;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.constants.Namespace;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class BeforeHTML implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		Document doc = parserContext.getDocument();
		TokenType tokenType = token.getType();

		/*
		 * A DOCTYPE token Parse error. Ignore the token.
		 */
		if (tokenType == TokenType.DOCTYPE) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			return parserContext;
		}
		/*
		 * comment tokenInsert a comment as the last child of the Document
		 * object.
		 */
		else if (tokenType == TokenType.comment) {
			InsertComment
					.run(parserContext, token, parserContext.getDocument());
		}
		// A character token that is one of U+0009 CHARACTER TABULATION,
		// "LF"(U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		// Ignore the token.
		else if (token.isSpaceCharacter()) {
			return parserContext;
		}
		/*
		 * A start tag whose tag name is "html" Create an element for the token
		 * in the HTML namespace, with the Document as the intended parent.
		 * Append it to the Document object. Put this element in the stack of
		 * open elements.
		 * 
		 * TODO If the Document is being loaded as part of navigation of a
		 * browsing context, then: if the newly created element has a manifest
		 * attribute whose value is not the empty string, then resolve the value
		 * of that attribute to an absolute URL, relative to the newly created
		 * element, and if that is successful, run the application cache
		 * selection algorithm with the result of applying the URL serializer
		 * algorithm to the resulting parsed URL with the exclude fragment flag
		 * set; otherwise, if there is no such attribute, or its value is the
		 * empty string, or resolving its value fails, run the application cache
		 * selection algorithm with no manifest. The algorithm must be passed
		 * the Document object.
		 * 
		 * Switch the insertion mode to "before head".
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("html")) {
			Element element = CreateAnElementForAToken.run(doc, Namespace.HTML,
					token, parserContext);
			doc.appendChild(element);
			parserContext.getOpenElements().push(element);
			// TODO
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.before_head));
			return parserContext;
		}
		/*
		 * An end tag whose tag name is one of: "head", "body", "html", "br" Act
		 * as described in the "anything else" entry below. Any other end tag
		 * Parse error. Ignore the token.
		 */
		else if (tokenType == TokenType.end_tag
				&& !(token.getValue().equals("head")
						|| token.getValue().equals("body")
						|| token.getValue().equals("html") || token.getValue()
						.equals("br"))) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			return parserContext;
		}
		/*
		 * Anything else Create an html element whose ownerDocument is the
		 * Document object. Append it to the Document object. Put this element
		 * in the stack of open elements.
		 * 
		 * TODO If the Document is being loaded as part of navigation of a
		 * browsing context, then: run the application cache selection algorithm
		 * with no manifest, passing it the Document object.
		 * 
		 * Switch the insertion mode to "before head", then reprocess the token.
		 */
		else {
			Element html = doc.createElementNS(Namespace.HTML, "html");
			doc.appendChild(html);
			parserContext.getOpenElements().push(html);
			// TODO
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.before_head));
			parserContext.setFlagReconsumeToken(true);
			return parserContext;
		}

		return parserContext;
		// switch (token.getType()) {
		//
		//
		// case end_tag:
		// if (token.getValue().equals("head"))
		// ;
		// // Anything else
		// case end_of_file:
		// default:
		// Document doc = parserContext.getDocument();
		// Element html = doc.createElement("html");
		// doc.appendChild(html);
		// Stack<Element> stackOpenElements = parserContext.getOpenElements();
		// stackOpenElements.push(html);
		//
		// // TODO delete this, it just simulates next state
		// Token headT = new Token(TokenType.start_tag, "head");
		// Element head = InsertAnHTMLElement.run(parserContext, token);
		// parserContext.setHeadElementPointer(head);

		// TODO uncomment
		/*
		 * parserContext.setInsertionMode(factory
		 * .getInsertionMode(InsertionMode.before_head));
		 * parserContext.setFlagReconsumeToken(true);
		 */
		// break;
		// }
		//
		// parserContext.setFlagStopParsing(true);// TODO remove this to allow
		// // continue building the tree
	}
}
