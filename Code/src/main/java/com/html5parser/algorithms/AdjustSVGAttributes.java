package com.html5parser.algorithms;

import com.html5parser.classes.Token;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.constants.SVGAttributesTable;

public class AdjustSVGAttributes {

	public static Token run(TagToken token) {
		for (Attribute att : token.getAttributes()) {
			String value = SVGAttributesTable.TABLE.get(att);
			if (value!=null){
				att.setName(value);
			}
		}
		return token;
	}
}
