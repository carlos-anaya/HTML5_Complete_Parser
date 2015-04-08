package com.html5parser.parseError;

import com.html5parser.classes.ParserContext;

public class ParseError {

	private String message = "";

	public ParseError(ParseErrorType type, ParserContext context) {

		int currentChar;

		switch (type) {
		case InvalidInputCharacter:
			currentChar = context.getTokenizerContext()
					.getCurrentInputCharacter();
			this.message = "Invalid input character: "
					+ String.valueOf(Character.toChars(currentChar)) + " ("
					+ currentChar + ") while preprocessing the stream.";
			break;
		case UnexpectedInputCharacter:
			currentChar = context.getTokenizerContext()
					.getCurrentInputCharacter();
			this.message = "Unexpected character: "
					+ (currentChar != -1 ? String.valueOf(Character
							.toChars(currentChar)) : "EOF") + " ("
					+ currentChar + ") at "
					+ context.getTokenizerContext().getNextState();
			break;
		case UnexpectedToken:
			this.message = "Unexpected token: "
					+ context.getTokenizerContext().getCurrentToken().getType()
					+ " value: "
					+ context.getTokenizerContext().getCurrentToken()
							.getValue() + " at " + context.getInsertionMode();
			break;
		case EndTagWithAttributes:
			this.message = "End tag token emitted with attributes.";
			break;
		case EndTagWithSelfClosingFlag:
			this.message = "End tag token emitted with its self-closing flag set.";
			break;
		case StartTagWithSelfClosingFlag:
			this.message = "Start tag token emitted with its self-closing flag set and not acknowledged.";
			break;
		default:
			break;

		}
	}

	public ParseError(ParseErrorType type, String error) {
		switch (type) {
		case DuplicatedAttributeName:
			this.message = "Duplicated attribute name :" + error;
			break;
		default:
			this.message = error;
			break;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}