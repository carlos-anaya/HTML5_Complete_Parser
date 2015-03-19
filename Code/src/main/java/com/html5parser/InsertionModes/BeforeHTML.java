package com.html5parser.InsertionModes;

import com.html5parser.Classes.ParserContext;
import com.html5parser.Interfaces.IInsertionMode;

public class BeforeHTML implements IInsertionMode{

	public ParserContext process(ParserContext context) {
		// TODO Auto-generated method stub
		context.setFlagStopParsing(true);
		return context;
	}

}
