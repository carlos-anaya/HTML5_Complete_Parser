package com.html5parser.parser;

import com.html5parser.interfaces.IStreamPreprocessor;

public class StreamPreprocessor implements IStreamPreprocessor {
	private boolean isPreviuosCharacterCR = false;

	public boolean isInvalidCharacter(int codepoint) {
		// Any occurrences of any characters in the ranges U+0001 to U+0008,
		// U+000E to U+001F, U+007F to U+009F, U+FDD0 to U+FDEF, and characters
		// U+000B, U+FFFE, U+FFFF, U+1FFFE, U+1FFFF, U+2FFFE, U+2FFFF, U+3FFFE,
		// U+3FFFF, U+4FFFE, U+4FFFF, U+5FFFE, U+5FFFF, U+6FFFE, U+6FFFF,
		// U+7FFFE, U+7FFFF, U+8FFFE, U+8FFFF, U+9FFFE, U+9FFFF, U+AFFFE,
		// U+AFFFF, U+BFFFE, U+BFFFF, U+CFFFE, U+CFFFF, U+DFFFE, U+DFFFF,
		// U+EFFFE, U+EFFFF, U+FFFFE, U+FFFFF, U+10FFFE, and U+10FFFF are parse
		// errors. These are all control characters or permanently undefined
		// Unicode characters (noncharacters).

		if ((codepoint >= 0x0001 && codepoint <= 0x0008)
				|| (codepoint >= 0x000E && codepoint <= 0x001F)
				|| (codepoint >= 0x007F && codepoint <= 0x009F)
				|| (codepoint >= 0xFDD0 && codepoint <= 0xFDEF))
			return true;
		if (codepoint == 0x000B || codepoint == 0xFFFE || codepoint == 0xFFFF
				|| codepoint == 0x1FFFE || codepoint == 0x1FFFF
				|| codepoint == 0x2FFFE || codepoint == 0x2FFFF
				|| codepoint == 0x3FFFE || codepoint == 0x3FFFF
				|| codepoint == 0x4FFFE || codepoint == 0x4FFFF
				|| codepoint == 0x5FFFE || codepoint == 0x5FFFF
				|| codepoint == 0x6FFFE || codepoint == 0x6FFFF
				|| codepoint == 0x7FFFE || codepoint == 0x7FFFF
				|| codepoint == 0x8FFFE || codepoint == 0x8FFFF
				|| codepoint == 0x9FFFE || codepoint == 0x9FFFF
				|| codepoint == 0xAFFFE || codepoint == 0xAFFFF
				|| codepoint == 0xBFFFE || codepoint == 0xBFFFF
				|| codepoint == 0xCFFFE || codepoint == 0xCFFFF
				|| codepoint == 0xDFFFE || codepoint == 0xDFFFF
				|| codepoint == 0xEFFFE || codepoint == 0xEFFFF
				|| codepoint == 0xFFFFE || codepoint == 0xFFFFF
				|| codepoint == 0x10FFFE || codepoint == 0x10FFFF)
			return true;

		return false;
	}

	public int processLFAndCRCharacters(int codepoint) {
		// "CR" (U+000D) characters and "LF" (U+000A) characters are treated
		// specially. All CR characters must be converted to LF characters, and
		// any LF characters that immediately follow a CR character must be
		// ignored. Thus, newlines in HTML DOMs are represented by LF
		// characters, and there are never any CR characters in the input to the
		// tokenization stage.

		// if previous char was CR and current LF, change LF for CR and ignore
		if (isPreviuosCharacterCR && codepoint == 0x000A)
			return 0x000D;

		// replace CR for LF
		if (codepoint == 0x000D) {
			isPreviuosCharacterCR = true;
			return 0x000A;
		}
		isPreviuosCharacterCR = false;

		return codepoint;
	}
}
