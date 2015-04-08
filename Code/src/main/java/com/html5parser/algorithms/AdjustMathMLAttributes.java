package com.html5parser.algorithms;

import com.html5parser.classes.Token;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;

public class AdjustMathMLAttributes {

	public static Token run(TagToken token) {
		for (Attribute att : token.getAttributes()) {
			if (att.getName().equals("definitionurl"))
				att.setName("definitionURL");
		}
		return token;
	}
}
