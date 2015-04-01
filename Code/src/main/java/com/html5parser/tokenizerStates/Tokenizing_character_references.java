package com.html5parser.tokenizerStates;

import java.util.LinkedList;
import java.util.Queue;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.constants.NamedCharacterReference;
import com.html5parser.parseError.ParseError;
import com.html5parser.parseError.ParseErrorType;

public class Tokenizing_character_references {

	/**
	 * 
	 * @param referenceTokens
	 * @param context
	 * @return Token. Is null if Not a character reference. No characters are
	 *         consumed, and nothing is returned
	 */
	public static Queue<Token> getTokenCharactersFromReference(
			Queue<Token> referenceTokens, ParserContext context) {
		return getTokenCharactersFromReference(referenceTokens, context, 0);
	}

	/**
	 * 
	 * @param referenceTokens
	 * @param context
	 * @param additionalAllowedCharacter
	 * @return Token. Is null if Not a character reference. No characters are
	 *         consumed, and nothing is returned
	 */
	public static Queue<Token> getTokenCharactersFromReference(
			Queue<Token> referenceTokens, ParserContext context,
			int additionalAllowedCharacter) {
		Queue<Token> queue = new LinkedList<Token>(referenceTokens);
		Queue<Token> result = new LinkedList<Token>();

		if (queue.isEmpty() || queue.peek().getValue() == null) {
			result.add(new Token(TokenType.character, 0x0026)); // return &
			return result;
		}

		Token token = queue.peek();
		String character = token.getValue();

		if (character.codePointAt(0) == additionalAllowedCharacter) {
			result.add(new Token(TokenType.character, 0x0026)); // return &
			return result;
		}

		switch (character.codePointAt(0)) {
		// U+0009 CHARACTER TABULATION (tab)
		case 0x0009:
			result.add(new Token(TokenType.character, 0x0026)); // return &
			result.add(new Token(TokenType.character, 0x0009));
			return result;
			// U+000A LINE FEED (LF)
		case 0x000A:
			result.add(new Token(TokenType.character, 0x0026)); // return &
			result.add(new Token(TokenType.character, 0x000A));
			return result;
			// U+000C FORM FEED (FF)
		case 0x000C:
			result.add(new Token(TokenType.character, 0x0026)); // return &
			result.add(new Token(TokenType.character, 0x000C));
			return result;
			// U+0020 SPACE
		case 0x0020:
			result.add(new Token(TokenType.character, 0x0026)); // return &
			result.add(new Token(TokenType.character, 0x0020));
			return result;
			// U+0026 AMPERSAND
		case 0x0026:
			result.add(new Token(TokenType.character, 0x0026)); // return &
			result.add(new Token(TokenType.character, 0x0026)); // return &
			return result;

			// U+0023 NUMBER SIGN (#
			// Consume the U+0023 NUMBER SIGN.
			// The behavior further depends on the character after the
			// U+0023 NUMBER SIGN:
		case 0x0023:
			queue.poll();// consume #
			if (queue.isEmpty()) {
				result.add(new Token(TokenType.character, 0x0026)); // return &
				result.add(new Token(TokenType.character, 0x0023));
				context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
				return result;
			}
			token = queue.peek();
			character = token.getValue();
			boolean processAsHex = false;
			if (character.equals("X") || character.equals("x")) {
				queue.poll();
				processAsHex = true;
			}
			Token resultToken = getUnicodeCharacterForNumber(queue,
					processAsHex, context);

			if (resultToken == null) {
				result.add(new Token(TokenType.character, 0x0026)); // return &
				result.add(new Token(TokenType.character, 0x0023)); // return #
				if (processAsHex)
					result.add(token);// return X
			} else {
				result.add(resultToken);
			}
			result.addAll(queue);
			return result;
			// break;
		default:
			return result = getNamedCharacterReference(queue, context);
		}

	}

	private static Token getUnicodeCharacterForNumber(Queue<Token> characters,
			boolean processAsHex, ParserContext context) {

		// If no characters match the range, then don't consume any characters
		// (and unconsume the U+0023 NUMBER SIGN character and, if appropriate,
		// the X character). This is a parse error; nothing is returned.
		if (characters.isEmpty()
				|| !isDigit(characters.peek().getValue().codePointAt(0),
						processAsHex)) {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			return null;
		}

		Token token = characters.peek();
		String character = token.getValue();
		String reference = "";

		// Consume as many characters as match the range of characters(ASCII hex
		// digits or ASCII digits).
		while (!characters.isEmpty()
				&& isDigit(characters.peek().getValue().codePointAt(0),
						processAsHex)) {
			token = characters.poll();// consume
			character = token.getValue();
			reference = reference.concat(character);
			// codePoint = characters.peek().getValue().codePointAt(0);//peek
			// next char
		}

		// if the next character is a U+003B SEMICOLON, consume that
		// too. If it isn't, there is a parse error.
		if (characters.peek() != null
				&& characters.peek().getValue().equals(";")) {
			token = characters.poll();// consume
		} else {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
		}

		// If one or more characters match the range, then take them all and
		// interpret the string of characters as a number (either
		// hexadecimal or
		// decimal as appropriate)

		Integer referenceNumber = tryParseInt(reference, processAsHex);
		Long referenceNumberLong = tryParseLong(reference, processAsHex);
		if (referenceNumber != null) {
			int unicodeValue = getUnicodeCodePointInt(referenceNumber, context);

			return new Token(TokenType.character, String.valueOf(Character
					.toChars(unicodeValue)));
		} else if (referenceNumberLong != null) {
			long unicodeValue = getUnicodeCodePointLong(referenceNumberLong,
					context);

			return new Token(TokenType.character,
					String.valueOf((char) unicodeValue));
		}
		// If bigger than a long (64 bits) then is greater than 0x10FFFF, then
		// this is a parse error. Return a U+FFFD
		// REPLACEMENT CHARACTER character token.
		else {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			return new Token(TokenType.character, String.valueOf(Character
					.toChars(0xFFFD)));
		}
	}

	private static long getUnicodeCodePointLong(Long referenceNumber,
			ParserContext context) {
		if (referenceNumber > 0x10FFFF) {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			return (long) 0xFFFD;
		}
		if (referenceNumber == 0x1FFFE || referenceNumber == 0x1FFFF
				|| referenceNumber == 0x2FFFE || referenceNumber == 0x2FFFF
				|| referenceNumber == 0x3FFFE || referenceNumber == 0x3FFFF
				|| referenceNumber == 0x4FFFE || referenceNumber == 0x4FFFF
				|| referenceNumber == 0x5FFFE || referenceNumber == 0x5FFFF
				|| referenceNumber == 0x6FFFE || referenceNumber == 0x6FFFF
				|| referenceNumber == 0x7FFFE || referenceNumber == 0x7FFFF
				|| referenceNumber == 0x8FFFE || referenceNumber == 0x8FFFF
				|| referenceNumber == 0x9FFFE || referenceNumber == 0x9FFFF
				|| referenceNumber == 0xAFFFE || referenceNumber == 0xAFFFF
				|| referenceNumber == 0xBFFFE || referenceNumber == 0xBFFFF
				|| referenceNumber == 0xCFFFE || referenceNumber == 0xCFFFF
				|| referenceNumber == 0xDFFFE || referenceNumber == 0xDFFFF
				|| referenceNumber == 0xEFFFE || referenceNumber == 0xEFFFF
				|| referenceNumber == 0xFFFFE || referenceNumber == 0xFFFFF
				|| referenceNumber == 0x10FFFE || referenceNumber == 0x10FFFF)
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
		return referenceNumber;
	}

	private static int getUnicodeCodePointInt(int referenceNumber,
			ParserContext context) {
		int unicodeValue = 0;
		switch (referenceNumber) {
		case 0x00:
			unicodeValue = 0xFFFD; // REPLACEMENT CHARACTER
			break;
		case 0x80:
			unicodeValue = 0x20AC; // EURO SIGN (€)
			break;
		case 0x82:
			unicodeValue = 0x201A; // SINGLE LOW-9 QUOTATION MARK (‚)
			break;
		case 0x83:
			unicodeValue = 0x0192; // LATIN SMALL LETTER F WITH HOOK (ƒ)
			break;
		case 0x84:
			unicodeValue = 0x201E; // DOUBLE LOW-9 QUOTATION MARK („)
			break;
		case 0x85:
			unicodeValue = 0x2026; // HORIZONTAL ELLIPSIS (…)
			break;
		case 0x86:
			unicodeValue = 0x2020; // DAGGER (†)
			break;
		case 0x87:
			unicodeValue = 0x2021; // DOUBLE DAGGER (‡)
			break;
		case 0x88:
			unicodeValue = 0x02C6; // MODIFIER LETTER CIRCUMFLEX ACCENT (ˆ)
			break;
		case 0x89:
			unicodeValue = 0x2030; // PER MILLE SIGN (‰)
			break;
		case 0x8A:
			unicodeValue = 0x0160; // LATIN CAPITAL LETTER S WITH CARON (Š)
			break;
		case 0x8B:
			unicodeValue = 0x2039; // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
									// (‹)
			break;
		case 0x8C:
			unicodeValue = 0x0152; // LATIN CAPITAL LIGATURE OE (Œ)
			break;
		case 0x8E:
			unicodeValue = 0x017D; // LATIN CAPITAL LETTER Z WITH CARON (Ž)
			break;
		case 0x91:
			unicodeValue = 0x2018; // LEFT SINGLE QUOTATION MARK (‘)
			break;
		case 0x92:
			unicodeValue = 0x2019; // RIGHT SINGLE QUOTATION MARK (’)
			break;
		case 0x93:
			unicodeValue = 0x201C; // LEFT DOUBLE QUOTATION MARK (“)
			break;
		case 0x94:
			unicodeValue = 0x201D; // RIGHT DOUBLE QUOTATION MARK (”)
			break;
		case 0x95:
			unicodeValue = 0x2022; // BULLET (•)
			break;
		case 0x96:
			unicodeValue = 0x2013; // EN DASH (–)
			break;
		case 0x97:
			unicodeValue = 0x2014; // EM DASH (—)
			break;
		case 0x98:
			unicodeValue = 0x02DC; // SMALL TILDE (˜)
			break;
		case 0x99:
			unicodeValue = 0x2122; // TRADE MARK SIGN (™)
			break;
		case 0x9A:
			unicodeValue = 0x0161; // LATIN SMALL LETTER S WITH CARON (š)
			break;
		case 0x9B:
			unicodeValue = 0x203A; // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
									// (›)
			break;
		case 0x9C:
			unicodeValue = 0x0153; // LATIN SMALL LIGATURE OE (œ)
			break;
		case 0x9E:
			unicodeValue = 0x017E; // LATIN SMALL LETTER Z WITH CARON (ž)
			break;
		case 0x9F:
			unicodeValue = 0x0178; // LATIN CAPITAL LETTER Y WITH DIAERESIS (Ÿ)
		}
		if (unicodeValue != 0) {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			return unicodeValue;
		}

		// Otherwise, if the number is in the range 0xD800 to 0xDFFF or is
		// greater than 0x10FFFF, then this is a parse error. Return a U+FFFD
		// REPLACEMENT CHARACTER character token.

		if ((referenceNumber >= 0xD800 && referenceNumber <= 0xDFFF)
				|| (referenceNumber > 0x10FFFF)) {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			return 0xFFFD;
		}

		// TODO: Otherwise, return a character
		// token for the Unicode character whose code point is that number.
		// Additionally, if the number is in the range 0x0001 to 0x0008, 0x000D
		// to 0x001F, 0x007F to 0x009F, 0xFDD0 to 0xFDEF, or is one of 0x000B,
		// 0xFFFE, 0xFFFF, 0x1FFFE, 0x1FFFF, 0x2FFFE, 0x2FFFF, 0x3FFFE, 0x3FFFF,
		// 0x4FFFE, 0x4FFFF, 0x5FFFE, 0x5FFFF, 0x6FFFE, 0x6FFFF, 0x7FFFE,
		// 0x7FFFF, 0x8FFFE, 0x8FFFF, 0x9FFFE, 0x9FFFF, 0xAFFFE, 0xAFFFF,
		// 0xBFFFE, 0xBFFFF, 0xCFFFE, 0xCFFFF, 0xDFFFE, 0xDFFFF, 0xEFFFE,
		// 0xEFFFF, 0xFFFFE, 0xFFFFF, 0x10FFFE, or 0x10FFFF, then this is a
		// parse error.
		if ((referenceNumber >= 0x0001 && referenceNumber <= 0x0008)
				|| (referenceNumber >= 0x000D && referenceNumber <= 0x001F)
				|| (referenceNumber >= 0x007F && referenceNumber <= 0x009F)
				|| (referenceNumber >= 0xFDD0 && referenceNumber <= 0xFDEF)) {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
		}
		switch (referenceNumber) {
		case 0x000B:
		case 0xFFFE:
		case 0xFFFF:
		case 0x1FFFE:
		case 0x1FFFF:
		case 0x2FFFE:
		case 0x2FFFF:
		case 0x3FFFE:
		case 0x3FFFF:
		case 0x4FFFE:
		case 0x4FFFF:
		case 0x5FFFE:
		case 0x5FFFF:
		case 0x6FFFE:
		case 0x6FFFF:
		case 0x7FFFE:
		case 0x7FFFF:
		case 0x8FFFE:
		case 0x8FFFF:
		case 0x9FFFE:
		case 0x9FFFF:
		case 0xAFFFE:
		case 0xAFFFF:
		case 0xBFFFE:
		case 0xBFFFF:
		case 0xCFFFE:
		case 0xCFFFF:
		case 0xDFFFE:
		case 0xDFFFF:
		case 0xEFFFE:
		case 0xEFFFF:
		case 0xFFFFE:
		case 0xFFFFF:
		case 0x10FFFE:
		case 0x10FFFF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
		}

		return referenceNumber;
	}

	private static Integer tryParseInt(String value, Boolean isHex) {
		Integer retVal;
		try {
			retVal = isHex ? Integer.parseInt(value, 16) : Integer
					.parseInt(value);
			retVal = retVal < 0 ? Integer.parseInt(value) : retVal;
		} catch (NumberFormatException nfe) {
			retVal = null;
		}
		return retVal;
	}

	private static Long tryParseLong(String value, Boolean isHex) {
		Long retVal;
		try {
			retVal = isHex ? Long.parseLong(value, 16) : Long.parseLong(value);
			retVal = retVal < 0 ? Long.parseLong(value) : retVal;
		} catch (NumberFormatException nfe) {
			retVal = null;
		}
		return retVal;
	}

	/**
	 * if match the range of characters (ASCII hex digits or ASCII digits).
	 * 
	 * @param codePoint
	 * @param processAsHex
	 * @return
	 */
	private static boolean isDigit(int codePoint, boolean processAsHex) {
		return processAsHex ? isHexDigit(codePoint) : isDecDigit(codePoint);
	}

	private static boolean isHexDigit(int codePoint) {
		return ((codePoint >= 0x0030 && codePoint <= 0x0039)
				|| (codePoint >= 0x0041 && codePoint <= 0x005A) || (codePoint >= 0x0061 && codePoint <= 0x0066));
	}

	private static boolean isDecDigit(int codePoint) {
		return (codePoint >= 0x0030 && codePoint <= 0x0039);
	}

	private static boolean isAlphanumeric(int codePoint) {
		return isDecDigit(codePoint) || isUpercasseAscii(codePoint)
				|| isLowercaseAscii(codePoint);
	}

	private static boolean isUpercasseAscii(int codePoint) {
		return (codePoint >= 0x0041 && codePoint <= 0x005A);
	}

	private static boolean isLowercaseAscii(int codePoint) {
		return (codePoint >= 0x0061 && codePoint <= 0x007A);
	}

	private static Queue<Token> getNamedCharacterReference(Queue<Token> queue,
			ParserContext context) {
		StringBuilder buffer = new StringBuilder();
		int[] values = null;
		int charsConsumed = 0;
		for (Token token : queue) {
			buffer.append(token.getValue());
			int[] res = NamedCharacterReference.MAP.get(buffer.toString());
			if (res != null) {
				values = res;
				charsConsumed = buffer.length();
			}
		}

		Queue<Token> result = new LinkedList<Token>();

		// If no match can be made, then no characters are consumed, and nothing
		// is returned. In this case, if the characters after the U+0026
		// AMPERSAND character (&) consist of a sequence of one or more
		// alphanumeric ASCII characters followed by a U+003B SEMICOLON
		// character (;), then this is a parse error.
		if (values == null) {
			result.add(new Token(TokenType.character, 0x0026));
			// for (Token token : queue)
			// result.add(token);
			result.addAll(queue);
			// if empty entity then no parse errors
			if (buffer.toString().equals(";"))
				return result;
			for (int i = 0; i < buffer.length(); i++) {
				int codePoint = buffer.codePointAt(i);
				if (!isAlphanumeric(codePoint)) {
					if (codePoint != 59)
						return result;
				}
			}
			if (buffer.toString().endsWith(";")) {
				context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			}
			return result;
		} else {
			for (int value : values) {
				result.add(new Token(TokenType.character, value));
			}
			while (charsConsumed > 0) {
				queue.poll();
				charsConsumed--;
			}
			result.addAll(queue);// add tokens not processed
		}
		if (!buffer.toString().endsWith(";")) {
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
		}

		return result;
	}

	public static void main(String[] args) {
		Queue<Token> queue = new LinkedList<Token>(), result;
		ParserContext pc = new ParserContext();
		// pc.getTokenizerContext().addTokenWithoutEmit(new
		// Token(TokenType.character, "#"));
		// pc.getTokenizerContext().addTokenWithoutEmit(new
		// Token(TokenType.character, "x"));
		// pc.getTokenizerContext().addTokenWithoutEmit(new
		// Token(TokenType.character, "1"));
		// pc.getTokenizerContext().addTokenWithoutEmit(new
		// Token(TokenType.character, "0"));
		// pc.getTokenizerContext().addTokenWithoutEmit(new
		// Token(TokenType.character, "F"));

		// queue.add(new Token(TokenType.character, "#"));
		// queue.add(new Token(TokenType.character, "x"));
		// queue.add(new Token(TokenType.character, "1"));
		// queue.add(new Token(TokenType.character, "0"));
		// queue.add(new Token(TokenType.character, "F"));

		queue.add(new Token(TokenType.character, "E"));
		queue.add(new Token(TokenType.character, "a"));
		queue.add(new Token(TokenType.character, "a"));
		queue.add(new Token(TokenType.character, "u"));
		queue.add(new Token(TokenType.character, "t"));
		queue.add(new Token(TokenType.character, "e"));
		queue.add(new Token(TokenType.character, ";"));

		// queue = pc.getTokenizerContext().getTokens();
		result = Tokenizing_character_references
				.getTokenCharactersFromReference(queue, pc);
		System.out.println(result.poll().getValue().toString());

		printTokens(pc);
	}

	private static void printTokens(ParserContext parserContext) {
		System.out.println("*** TOKENS ***\n");
		for (Token token : parserContext.getTokenizerContext().getTokens()) {
			switch (token.getType()) {
			case end_of_file:
				System.out.println("EOF");
				break;
			case character:
			case comment:
				System.out.println(token.getType() + " : " + token.getValue());
				break;
			case DOCTYPE:
				DocTypeToken docTypeToken = (DocTypeToken) token;
				System.out.println(docTypeToken.getType() + " : "
						+ docTypeToken.getValue() + " public id. "
						+ docTypeToken.getPublicIdentifier() + " system id. "
						+ docTypeToken.getSystemIdentifier()
						+ " force-quirks flag "
						+ docTypeToken.isForceQuircksFlag());
				break;
			case end_tag:
			case start_tag:
				TagToken tagToken = (TagToken) token;
				System.out.println(tagToken.getType() + " : "
						+ tagToken.getValue() + " self-closing flag "
						+ tagToken.isFlagSelfClosingTag() + " attributes: ");
				for (Attribute att : tagToken.getAttributes()) {
					System.out.println(att.getName() + " : " + att.getValue());
				}
				break;
			default:
				System.out.println("Error");
				break;
			}

		}

		System.out.println("\n\n*** ERRORS ***\n");

		for (ParseError error : parserContext.getParseErrors()) {
			System.out.println(error.getMessage());
		}
	}
}