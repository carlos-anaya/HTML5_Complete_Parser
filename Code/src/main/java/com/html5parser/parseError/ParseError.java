package com.html5parser.parseError;

import com.html5parser.classes.ParserContext;

public class ParseError {
		
	String message="";	
	
	public ParseError(ParseErrorType type, ParserContext context){
		
		switch(type){
		
		case UnexpectedInputCharacter:
			message = "Unexpected character: "+ context.getTokenizerContext().getCurrentInputCharacter() 
				+ " at "+ context.getTokenizerContext().getNextState();
			
		case UnexpectedToken:
			message = "Unexpected token: "+ context.getTokenizerContext().getCurrentToken().getType()
				+ " value: "+ context.getTokenizerContext().getCurrentToken().getValue()
				+ " at "+ context.getInsertionMode();
			
		}
			
		
	}

}
