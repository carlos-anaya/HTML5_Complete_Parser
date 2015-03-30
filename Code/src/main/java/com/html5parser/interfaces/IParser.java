package com.html5parser.interfaces;

import java.io.InputStream;

import org.w3c.dom.Document;

import com.html5parser.classes.ParserContext;

public interface IParser{
	public Document parse(String htmlString);
	public Document parse(InputStream stream);
	
	public void printTokens(ParserContext parserContext);
	public ParserContext tokenize(ParserContext parserContext, String string);
}