package com.html5parser.parseError;

public enum ParseErrorType {	
	UnexpectedInputCharacter,
	UnexpectedToken,
	DuplicatedAttributeName,
	EndTagWithAttributes,
	EndTagWithSelfClosingFlag
}