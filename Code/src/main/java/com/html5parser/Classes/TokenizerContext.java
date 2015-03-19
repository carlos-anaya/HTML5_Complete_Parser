package com.html5parser.Classes;

import java.util.LinkedList;
import java.util.Queue;

import com.html5parser.Interfaces.ITokenizerState;
import com.html5parser.TokenizerStates.Data_state;

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
}
