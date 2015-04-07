package com.html5parser.parseError;

public enum ParseErrorType {	
	InvalidInputCharacter,
	UnexpectedInputCharacter,
	UnexpectedToken,
	DuplicatedAttributeName,
	EndTagWithAttributes,
	EndTagWithSelfClosingFlag,
	InvalidNamespace
}