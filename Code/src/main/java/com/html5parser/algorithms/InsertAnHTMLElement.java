package com.html5parser.algorithms;

import org.w3c.dom.Element;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.constants.Namespace;

public class InsertAnHTMLElement {

	public static Element run(ParserContext context, Token token) {
		return InsertForeignElement.run(context, token, Namespace.HTML);
	}
}
