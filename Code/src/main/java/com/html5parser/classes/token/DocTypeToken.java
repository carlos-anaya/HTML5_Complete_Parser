package com.html5parser.classes.token;

import com.html5parser.classes.Token;

public class DocTypeToken extends Token {

	String publicIdentifier;// initialized as missing(null)
	String systemIdentifier;// initialized as missing(null)
	boolean forceQuircksFlag = false;

	public DocTypeToken(int _value) {
		super(TokenType.DOCTYPE, _value);
	}

	public DocTypeToken(String _value) {
		super(TokenType.DOCTYPE, _value);
	}

	public String getPublicIdentifier() {
		return publicIdentifier;
	}

	public void setPublicIdentifier(String publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}

	public String getSystemIdentifier() {
		return systemIdentifier;
	}

	public void setSystemIdentifier(String systemIdentifier) {
		this.systemIdentifier = systemIdentifier;
	}

	public boolean isForceQuircksFlag() {
		return forceQuircksFlag;
	}

	public void setForceQuircksFlag(boolean forceQuircksFlag) {
		this.forceQuircksFlag = forceQuircksFlag;
	}
	
	public void appendPublicIdentifier(int value) {
		this.publicIdentifier = this.publicIdentifier.concat(String.valueOf(Character.toChars(value)));
	}
	
	public void appendSystemIdentifier(int value) {
		this.systemIdentifier = this.systemIdentifier.concat(String.valueOf(Character.toChars(value)));
	}
}