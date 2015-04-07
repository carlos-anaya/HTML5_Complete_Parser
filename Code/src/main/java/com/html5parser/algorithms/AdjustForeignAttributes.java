package com.html5parser.algorithms;

import com.html5parser.classes.Token;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.constants.ForeignAttributesTable;

public class AdjustForeignAttributes {

	public static Token run(TagToken token) {
		for (Attribute att : token.getAttributes()) {
			String[] values = ForeignAttributesTable.TABLE.get(att);
			if (values!=null){
				att.setPrefix(values[0]);
				att.setLocalName(values[1]);
				att.setNamespace(values[2]);
			}
				
		}
		return token;
	}
}
