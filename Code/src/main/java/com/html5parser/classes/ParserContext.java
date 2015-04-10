package com.html5parser.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.insertionModes.Initial;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseError;
import com.html5parser.parseError.ParseErrorType;

public class ParserContext {

	// TODO extra validation when emit tokens. see tokenization in spec

	/*
	 * Tokenizer context
	 */
	private TokenizerContext tokenizerContext = new TokenizerContext();

	/*
	 * Insertion modes
	 */
	private IInsertionMode insertionMode = new Initial();
	private IInsertionMode originalInsertionMode;
	private IInsertionMode currentTemplateInsertionMode;

	/*
	 * Stacks
	 */
	private Stack<Element> openElements = new Stack<Element>();
	private Stack<ParseError> parseErrors = new Stack<ParseError>();
	private Stack<IInsertionMode> templateInsertionModes = new Stack<IInsertionMode>();

	/*
	 * Flags
	 */
	private boolean flagScripting = false;
	private boolean flagForceQuirks = false;
	private boolean flagParserPause = false;
	private boolean flagFramesetOk = false;
	private boolean flagStopParsing = false;
	private boolean flagReconsumeToken = false;
	private boolean flagFosterParenting = false;
	private boolean flagHTMLFragmentParser = false;

	/*
	 * Others
	 */
	private ArrayList<Element> activeFormattingElements = new ArrayList<Element>();
	private Element currentNode;
	private Element adjustedCurrentNode;
	private Element headElementPointer;
	private Element formElementPointer;

	/*
	 * Document
	 */
	Document doc;

	public TokenizerContext getTokenizerContext() {
		return tokenizerContext;
	}

	public void setTokenizerContext(TokenizerContext value) {
		this.tokenizerContext = value;
	}

	public IInsertionMode getInsertionMode() {
		return insertionMode;
	}

	public void setInsertionMode(IInsertionMode value) {
		this.insertionMode = value;
	}

	/*
	 * public IInsertionMode getInsertionMode() { return IInsertionMode; }
	 * 
	 * public void setInsertionMode(IInsertionMode IInsertionMode) {
	 * this.insertionMode = IInsertionMode; }
	 */
	public IInsertionMode getOriginalInsertionMode() {
		return originalInsertionMode;
	}

	public void setOriginalInsertionMode(IInsertionMode originalInsertionMode) {
		this.originalInsertionMode = originalInsertionMode;
	}

	public IInsertionMode getCurrentTemplateInsertionMode() {
		return currentTemplateInsertionMode;
	}

	public void setCurrentTemplateInsertionMode(
			IInsertionMode currentTemplateInsertionMode) {
		this.currentTemplateInsertionMode = currentTemplateInsertionMode;
	}

	public Stack<Element> getOpenElements() {
		return openElements;
	}

	public void setOpenElements(Stack<Element> openElements) {
		this.openElements = openElements;
	}

	public Stack<ParseError> getParseErrors() {
		return parseErrors;
	}

	public void setParseErrors(Stack<ParseError> parseErrors) {
		this.parseErrors = parseErrors;
	}

	public void addParseErrors(ParseErrorType parseErrorType) {
		parseErrors.push(new ParseError(parseErrorType, this));
	}

	public void addParseErrors(ParseErrorType parseErrorType, String message) {
		parseErrors.push(new ParseError(parseErrorType, message));
	}

	public Stack<IInsertionMode> getTemplateInsertionModes() {
		return templateInsertionModes;
	}

	public void setTemplateInsertionModes(
			Stack<IInsertionMode> templateInsertionModes) {
		this.templateInsertionModes = templateInsertionModes;
	}

	public boolean isFlagScripting() {
		return flagScripting;
	}

	public void setFlagScripting(boolean flagScripting) {
		this.flagScripting = flagScripting;
	}

	public boolean isFlagForceQuirks() {
		return flagForceQuirks;
	}

	public void setFlagForceQuirks(boolean flagForceQuirks) {
		this.flagForceQuirks = flagForceQuirks;
	}

	public boolean isFlagParserPause() {
		return flagParserPause;
	}

	public void setFlagParserPause(boolean flagParserPause) {
		this.flagParserPause = flagParserPause;
	}

	public boolean isFlagFramesetOk() {
		return flagFramesetOk;
	}

	public void setFlagFramesetOk(boolean flagFramesetOk) {
		this.flagFramesetOk = flagFramesetOk;
	}

	public ArrayList<Element> getActiveFormattingElements() {
		return activeFormattingElements;
	}

	public void setActiveFormattingElements(
			ArrayList<Element> activeFormattingElements) {
		this.activeFormattingElements = activeFormattingElements;
	}

	public Element getCurrentNode() {
		return openElements.peek();
	}

	public void setCurrentNode(Element currentNode) {
		this.currentNode = currentNode;
	}

	public Element getAdjustedCurrentNode() {
		return adjustedCurrentNode;
	}

	public void setAdjustedCurrentNode(Element adjustedCurrentNode) {
		this.adjustedCurrentNode = adjustedCurrentNode;
	}

	public Element getHeadElementPointer() {
		return headElementPointer;
	}

	public void setHeadElementPointer(Element headElementPointer) {
		this.headElementPointer = headElementPointer;
	}

	public Element getFormElementPointer() {
		return formElementPointer;
	}

	public void setFormElementPointer(Element formElementPointer) {
		this.formElementPointer = formElementPointer;
	}

	public boolean isFlagStopParsing() {
		return flagStopParsing;
	}

	public void setFlagStopParsing(boolean flagStopParsing) {
		this.flagStopParsing = flagStopParsing;
	}

	public boolean isFlagReconsumeToken() {
		return flagReconsumeToken;
	}

	public void setFlagReconsumeToken(boolean flagReconsumeToken) {
		this.flagReconsumeToken = flagReconsumeToken;
	}

	public Document getDocument() {
		return doc;
	}

	public void setDocument(Document doc) {
		this.doc = doc;
	}

	// Remove duplicate attributes and generate parse errors
	public void validateAttributeNames() {
		List<Attribute> attributes = ((TagToken) this.tokenizerContext
				.getCurrentToken()).getAttributes();
		final List<Attribute> setToReturn = new ArrayList<>();
		final Set<String> set1 = new HashSet<String>();

		for (Attribute att : attributes) {
			if (set1.add(att.getName())) {
				setToReturn.add(att);
			} else {
				this.parseErrors.push(new ParseError(
						ParseErrorType.DuplicatedAttributeName, att.getName()));
			}
		}
		((TagToken) this.tokenizerContext.getCurrentToken())
				.setAttributes(setToReturn);
	}

	public boolean isFlagFosterParenting() {
		return flagFosterParenting;
	}

	public void setFlagFosterParenting(boolean flagFosterParenting) {
		this.flagFosterParenting = flagFosterParenting;
	}
	
	public boolean openElementsContain(String elementName){
		int n = openElements.size();
		boolean flag = false;
		for (int i = 0; i < n; i++) {
			Element element = openElements.pop();
			if (element.getNodeName().equals(elementName)) {
				flag = true;
			}
		}
		return flag;
	}

	public boolean isFlagHTMLFragmentParser() {
		return flagHTMLFragmentParser;
	}

	public void setFlagHTMLFragmentParser(boolean flagHTMLFragmentParser) {
		this.flagHTMLFragmentParser = flagHTMLFragmentParser;
	}
	
	
}
