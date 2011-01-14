package org.anarres.cpp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class that returns a list of tokens (each with presence condition),
 * but without any #ifdef tokens, whitespace or such
 *
 * to be extended to mirror the power of Main.java
 *
 * @author kaestner
 *
 */
public class PartialPPLexer {

	public boolean debug = false;

	public List<Token> parse(String code, String folderPath) throws LexerException,
			IOException {
		return parse(new StringLexerSource(code, true), folderPath);
	}

	public List<Token> parseFile(String fileName, String folderPath)
			throws LexerException, IOException {
		return parse(new FileLexerSource(new File(fileName)), folderPath);
	}

	/**
	 *
	 * @param stream stream containing the data to preprocess
	 * @param filePath path of the file represented by the stream
	 * @param folderPath path of the containing folder.
	 * @return
	 * @throws LexerException
	 * @throws IOException
	 */
	public List<Token> parseStream(InputStream stream, String filePath, String folderPath)
			throws LexerException, IOException {
		return parse(new FileLexerSource(stream, filePath), folderPath);
	}

	public List<Token> parse(Source source, String folderPath)
			throws LexerException, IOException {
		Preprocessor pp = new Preprocessor();
		pp.addFeature(Feature.DIGRAPHS);
		pp.addFeature(Feature.TRIGRAPHS);
		pp.addFeature(Feature.LINEMARKERS);
		pp.addWarnings(Warning.allWarnings());
		pp.setListener(new PreprocessorListener(pp) {
			// @Override
			// public void handleWarning(Source source, int line, int column,
			// String msg) throws LexerException {
			// super.handleWarning(source, line, column, msg);
			// throw new LexerException(msg);
			// }
		});
		pp.addMacro("__JCPP__", FeatureExprLib.base());

		// include path
		if (folderPath != null)
			pp.getSystemIncludePath().add(folderPath);

		pp.addInput(source);

		ArrayList<Token> result = new ArrayList<Token>();
		for (;;) {
			Token tok = pp.getNextToken();
			if (tok == null)
				break;
			if (tok.getType() == Token.EOF)
				break;

			if (tok.getType() == Token.INVALID)
				System.err.println("Invalid token: " + tok);
			// throw new LexerException(...)

			if (tok.getType() != Token.P_LINE
					&& tok.getType() != Token.WHITESPACE
					&& !tok.getText().equals("__extension__")
					&& tok.getType() != Token.NL && tok.getType() != Token.P_IF
					&& tok.getType() != Token.CCOMMENT
					&& tok.getType() != Token.CPPCOMMENT
					&& tok.getType() != Token.P_ENDIF
					&& tok.getType() != Token.P_ELIF)
				result.add(tok);
			if (debug)
				System.out.print(tok.getText());
		}
		return result;
	}
}
