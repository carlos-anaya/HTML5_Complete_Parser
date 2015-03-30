package com.html5parser.interfaces;

public interface IStreamPreprocessor {
	public boolean isInvalidCharacter(int codepoint);
	public int processLFAndCRCharacters(int codepoint);
}