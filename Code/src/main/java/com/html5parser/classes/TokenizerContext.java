package com.html5parser.classes;

import java.util.LinkedList;
import java.util.Queue;

import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.tokenizerStates.Data_state;

public class TokenizerContext {

	private ITokenizerState nextState = new Data_state();
	private Queue<Token> tokens = new LinkedList<Token>();

	private String temporaryBuffer;
	private Token currentToken = null;

	private int currentInputCharacter = 0;
	private int nextInputCharacter = 0;

	// reconsume the current character and not read next
	private boolean flagReconsumeCurrentInputCharacter = false;
	// emit the token so it is consumed by the tree constructor
	private boolean flagEmitToken = false;

	public ITokenizerState getNextState() {
		return nextState;
	}

	public void setNextState(ITokenizerState value) {
		this.nextState = value;
	}

	public String getTemporaryBuffer() {
		return temporaryBuffer;
	}

	public void setCurrentToken(Token value) {
		this.tokens.add(value);
	}

	public Token getCurrentToken() {
		return currentToken;
	}

	public Token pollCurrentToken() {
		currentToken = tokens.poll();
		return currentToken;
	}

	public Queue<Token> getTokens() {
		return tokens;
	}

	public void setTemporaryBuffer(String value) {
		this.temporaryBuffer = value;
	}

	public int getCurrentInputCharacter() {
		return currentInputCharacter;
	}

	public void setCurrentInputCharacter(int value) {
		this.currentInputCharacter = value;
	}

	public int getNextInputCharacter() {
		return nextInputCharacter;
	}

	public void setNextInputCharacter(int value) {
		this.nextInputCharacter = value;
	}

	public boolean isFlagReconsumeCurrentInputCharacter() {
		return flagReconsumeCurrentInputCharacter;
	}

	public void setFlagReconsumeCurrentInputCharacter(boolean value) {
		this.flagReconsumeCurrentInputCharacter = value;
	}

	public boolean isFlagEmitToken() {
		return flagEmitToken;
	}

	public void setFlagEmitToken(boolean value) {
		this.flagEmitToken = value;
	}

	public void emitCurrentToken(Token value) {
		setCurrentToken(value);
		setFlagEmitToken(true);
	}
	public void emitCurrentToken() {
		setCurrentToken(currentToken);
		setFlagEmitToken(true);
	}

	public ASCIICharacter getCurrentASCIICharacter() {

		int currentChar = this.currentInputCharacter;

		if (currentChar > 64 && currentChar < 91)
			return ASCIICharacter.LATIN_CAPITAL_LETTER;
		else if (currentChar > 96 && currentChar < 123)
			return ASCIICharacter.LATIN_SMALL_LETTER;

		switch (currentChar) {
		case 0x0026:
			return ASCIICharacter.AMPERSAND;
		case 0x0000:			
			return ASCIICharacter.NULL;
		case 0x003E:
			return ASCIICharacter.GREATER_THAN_SIGN;
		case 0x003C:			
			return ASCIICharacter.LESS_THAN_SIGN;
		case 0x0020:
			return ASCIICharacter.SPACE;
		case 0x0009:			
			return ASCIICharacter.TAB;
		case 0x000A:
			return ASCIICharacter.LF;
		case 0x000C:			
			return ASCIICharacter.FF;
		case 0x002F:
			return ASCIICharacter.DASH;
		case 0x0021:			
			return ASCIICharacter.EXCLAMATION_MARK;
		case -1:
			return ASCIICharacter.EOF;
		case 0x002D:			
			return ASCIICharacter.HYPHEN_MINUS;
		case 0x0022:
			return ASCIICharacter.QUOTATION_MARK;
		case 0X0027:			
			return ASCIICharacter.APOSTROPHE;
		case 0X003D:			
			return ASCIICharacter.EQUALS_SIGN;
		default:
			return ASCIICharacter.OTHER;
		}
	}
}
