package com.html5parser.interfaces;

import java.io.InputStream;

import org.w3c.dom.Document;

public interface IParser{
	public Document parse(String htmlString);
	public Document parse(InputStream stream);
}