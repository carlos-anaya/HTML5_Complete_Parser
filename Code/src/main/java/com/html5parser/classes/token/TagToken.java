package com.html5parser.classes.token;

import java.util.ArrayList;
import java.util.List;

import com.html5parser.classes.Token;

public class TagToken extends Token {

	boolean flagSelfClosingTag = false;
	List<Attribute> attributes = new ArrayList<TagToken.Attribute>();
	Attribute lasttAttribute;

	public TagToken(TokenType _type, int _value) {
		super(_type, _value);
	}

	public TagToken(TokenType _type, String _value) {
		super(_type, _value);
	}

	public boolean isFlagSelfClosingTag() {
		return flagSelfClosingTag;
	}

	public void setFlagSelfClosingTag(boolean value) {
		this.flagSelfClosingTag = value;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Attribute createAttribute(String name) {
		lasttAttribute = new Attribute(name, "");
		attributes.add(lasttAttribute);
		return lasttAttribute;
	}

	public Attribute createAttribute(int name) {
		return createAttribute(String.valueOf(Character.toChars(name)));
	}

	public void appendCharacterInNameInLastAttribute(String character) {
		appendCharacterInName(lasttAttribute, character);
	}

	public void appendCharacterInNameInLastAttribute(int character) {
		appendCharacterInName(lasttAttribute,
				String.valueOf(Character.toChars(character)));
	}

	public void appendCharacterInName(Attribute attribute, String character) {
		attribute.appendCharacterToName(character);
	}

	public void appendCharacterInValueInLastAttribute(String character) {
		appendCharacterInValue(lasttAttribute, character);
	}

	public void appendCharacterInValueInLastAttribute(int character) {
		appendCharacterInValue(lasttAttribute,
				String.valueOf(Character.toChars(character)));
	}

	public void appendCharacterInValue(Attribute attribute, String character) {
		attribute.appendCharacterToValue(character);
	}

	public class Attribute {
		String name;
		String value;
		String localName;
		String prefix;
		String namespace;

		public Attribute(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void appendCharacterToName(String character) {
			this.name = name.concat(character);
		}

		public void appendCharacterToValue(String character) {
			this.value = value.concat(character);
		}

		public String getLocalName() {
			return localName;
		}

		public void setLocalName(String localName) {
			this.localName = localName;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

	}
}