// Generated from /home/frankie/Dropbox/ETH/INFK/2018 FS/Compiler Design/compiler-design/HW2/src/cd/frontend/parser/Javali.g4 by ANTLR 4.7
package cd.frontend.parser;

	// Java header
	 //package cd.frontend.parser; // TODO: Uncomment before commit!!

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JavaliLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, PrimitiveType=38, 
		Integer=39, Boolean=40, Identifier=41, COMMENT=42, LINE_COMMENT=43, WS=44, 
		ErrorCharacter=45;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
		"T__33", "T__34", "T__35", "T__36", "PrimitiveType", "Integer", "Decimal", 
		"Hex", "Boolean", "Identifier", "Letter", "Digit", "HexDigit", "COMMENT", 
		"LINE_COMMENT", "WS", "ErrorCharacter"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'class'", "'extends'", "'{'", "'}'", "','", "';'", "'void'", "'('", 
		"')'", "'='", "'write'", "'writeln'", "'if'", "'else'", "'while'", "'return'", 
		"'new'", "'['", "']'", "'read'", "'.'", "'this'", "'+'", "'-'", "'!'", 
		"'*'", "'/'", "'%'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&&'", 
		"'||'", "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, "PrimitiveType", "Integer", "Boolean", "Identifier", "COMMENT", 
		"LINE_COMMENT", "WS", "ErrorCharacter"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public JavaliLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Javali.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2/\u0148\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3"+
		"\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3"+
		"\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25\3"+
		"\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3"+
		"\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 \3!\3!\3!\3"+
		"\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\5\'\u00f1\n\'\3(\3(\5(\u00f5\n(\3)\3)\3)\7)\u00fa"+
		"\n)\f)\16)\u00fd\13)\5)\u00ff\n)\3*\3*\3*\3*\5*\u0105\n*\3*\6*\u0108\n"+
		"*\r*\16*\u0109\3+\3+\3+\3+\3+\3+\3+\3+\3+\5+\u0115\n+\3,\3,\3,\7,\u011a"+
		"\n,\f,\16,\u011d\13,\3-\3-\3.\3.\3/\3/\5/\u0125\n/\3\60\3\60\3\60\3\60"+
		"\7\60\u012b\n\60\f\60\16\60\u012e\13\60\3\60\3\60\3\60\3\60\3\60\3\61"+
		"\3\61\3\61\3\61\7\61\u0139\n\61\f\61\16\61\u013c\13\61\3\61\3\61\3\62"+
		"\6\62\u0141\n\62\r\62\16\62\u0142\3\62\3\62\3\63\3\63\3\u012c\2\64\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"C#E$G%I&K\'M(O)Q\2S\2U*W+Y\2[\2]\2_,a-c.e/\3\2\6\4\2C\\c|\4\2CHch\4"+
		"\2\f\f\17\17\5\2\13\f\17\17\"\"\2\u014f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
		"\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2"+
		"\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2"+
		"A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3"+
		"\2\2\2\2O\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2"+
		"\2\2e\3\2\2\2\3g\3\2\2\2\5m\3\2\2\2\7u\3\2\2\2\tw\3\2\2\2\13y\3\2\2\2"+
		"\r{\3\2\2\2\17}\3\2\2\2\21\u0082\3\2\2\2\23\u0084\3\2\2\2\25\u0086\3\2"+
		"\2\2\27\u0088\3\2\2\2\31\u008e\3\2\2\2\33\u0096\3\2\2\2\35\u0099\3\2\2"+
		"\2\37\u009e\3\2\2\2!\u00a4\3\2\2\2#\u00ab\3\2\2\2%\u00af\3\2\2\2\'\u00b1"+
		"\3\2\2\2)\u00b3\3\2\2\2+\u00b8\3\2\2\2-\u00ba\3\2\2\2/\u00bf\3\2\2\2\61"+
		"\u00c1\3\2\2\2\63\u00c3\3\2\2\2\65\u00c5\3\2\2\2\67\u00c7\3\2\2\29\u00c9"+
		"\3\2\2\2;\u00cb\3\2\2\2=\u00cd\3\2\2\2?\u00d0\3\2\2\2A\u00d2\3\2\2\2C"+
		"\u00d5\3\2\2\2E\u00d8\3\2\2\2G\u00db\3\2\2\2I\u00de\3\2\2\2K\u00e1\3\2"+
		"\2\2M\u00f0\3\2\2\2O\u00f4\3\2\2\2Q\u00fe\3\2\2\2S\u0104\3\2\2\2U\u0114"+
		"\3\2\2\2W\u0116\3\2\2\2Y\u011e\3\2\2\2[\u0120\3\2\2\2]\u0124\3\2\2\2_"+
		"\u0126\3\2\2\2a\u0134\3\2\2\2c\u0140\3\2\2\2e\u0146\3\2\2\2gh\7e\2\2h"+
		"i\7n\2\2ij\7c\2\2jk\7u\2\2kl\7u\2\2l\4\3\2\2\2mn\7g\2\2no\7z\2\2op\7v"+
		"\2\2pq\7g\2\2qr\7p\2\2rs\7f\2\2st\7u\2\2t\6\3\2\2\2uv\7}\2\2v\b\3\2\2"+
		"\2wx\7\177\2\2x\n\3\2\2\2yz\7.\2\2z\f\3\2\2\2{|\7=\2\2|\16\3\2\2\2}~\7"+
		"x\2\2~\177\7q\2\2\177\u0080\7k\2\2\u0080\u0081\7f\2\2\u0081\20\3\2\2\2"+
		"\u0082\u0083\7*\2\2\u0083\22\3\2\2\2\u0084\u0085\7+\2\2\u0085\24\3\2\2"+
		"\2\u0086\u0087\7?\2\2\u0087\26\3\2\2\2\u0088\u0089\7y\2\2\u0089\u008a"+
		"\7t\2\2\u008a\u008b\7k\2\2\u008b\u008c\7v\2\2\u008c\u008d\7g\2\2\u008d"+
		"\30\3\2\2\2\u008e\u008f\7y\2\2\u008f\u0090\7t\2\2\u0090\u0091\7k\2\2\u0091"+
		"\u0092\7v\2\2\u0092\u0093\7g\2\2\u0093\u0094\7n\2\2\u0094\u0095\7p\2\2"+
		"\u0095\32\3\2\2\2\u0096\u0097\7k\2\2\u0097\u0098\7h\2\2\u0098\34\3\2\2"+
		"\2\u0099\u009a\7g\2\2\u009a\u009b\7n\2\2\u009b\u009c\7u\2\2\u009c\u009d"+
		"\7g\2\2\u009d\36\3\2\2\2\u009e\u009f\7y\2\2\u009f\u00a0\7j\2\2\u00a0\u00a1"+
		"\7k\2\2\u00a1\u00a2\7n\2\2\u00a2\u00a3\7g\2\2\u00a3 \3\2\2\2\u00a4\u00a5"+
		"\7t\2\2\u00a5\u00a6\7g\2\2\u00a6\u00a7\7v\2\2\u00a7\u00a8\7w\2\2\u00a8"+
		"\u00a9\7t\2\2\u00a9\u00aa\7p\2\2\u00aa\"\3\2\2\2\u00ab\u00ac\7p\2\2\u00ac"+
		"\u00ad\7g\2\2\u00ad\u00ae\7y\2\2\u00ae$\3\2\2\2\u00af\u00b0\7]\2\2\u00b0"+
		"&\3\2\2\2\u00b1\u00b2\7_\2\2\u00b2(\3\2\2\2\u00b3\u00b4\7t\2\2\u00b4\u00b5"+
		"\7g\2\2\u00b5\u00b6\7c\2\2\u00b6\u00b7\7f\2\2\u00b7*\3\2\2\2\u00b8\u00b9"+
		"\7\60\2\2\u00b9,\3\2\2\2\u00ba\u00bb\7v\2\2\u00bb\u00bc\7j\2\2\u00bc\u00bd"+
		"\7k\2\2\u00bd\u00be\7u\2\2\u00be.\3\2\2\2\u00bf\u00c0\7-\2\2\u00c0\60"+
		"\3\2\2\2\u00c1\u00c2\7/\2\2\u00c2\62\3\2\2\2\u00c3\u00c4\7#\2\2\u00c4"+
		"\64\3\2\2\2\u00c5\u00c6\7,\2\2\u00c6\66\3\2\2\2\u00c7\u00c8\7\61\2\2\u00c8"+
		"8\3\2\2\2\u00c9\u00ca\7\'\2\2\u00ca:\3\2\2\2\u00cb\u00cc\7>\2\2\u00cc"+
		"<\3\2\2\2\u00cd\u00ce\7>\2\2\u00ce\u00cf\7?\2\2\u00cf>\3\2\2\2\u00d0\u00d1"+
		"\7@\2\2\u00d1@\3\2\2\2\u00d2\u00d3\7@\2\2\u00d3\u00d4\7?\2\2\u00d4B\3"+
		"\2\2\2\u00d5\u00d6\7?\2\2\u00d6\u00d7\7?\2\2\u00d7D\3\2\2\2\u00d8\u00d9"+
		"\7#\2\2\u00d9\u00da\7?\2\2\u00daF\3\2\2\2\u00db\u00dc\7(\2\2\u00dc\u00dd"+
		"\7(\2\2\u00ddH\3\2\2\2\u00de\u00df\7~\2\2\u00df\u00e0\7~\2\2\u00e0J\3"+
		"\2\2\2\u00e1\u00e2\7p\2\2\u00e2\u00e3\7w\2\2\u00e3\u00e4\7n\2\2\u00e4"+
		"\u00e5\7n\2\2\u00e5L\3\2\2\2\u00e6\u00e7\7d\2\2\u00e7\u00e8\7q\2\2\u00e8"+
		"\u00e9\7q\2\2\u00e9\u00ea\7n\2\2\u00ea\u00eb\7g\2\2\u00eb\u00ec\7c\2\2"+
		"\u00ec\u00f1\7p\2\2\u00ed\u00ee\7k\2\2\u00ee\u00ef\7p\2\2\u00ef\u00f1"+
		"\7v\2\2\u00f0\u00e6\3\2\2\2\u00f0\u00ed\3\2\2\2\u00f1N\3\2\2\2\u00f2\u00f5"+
		"\5Q)\2\u00f3\u00f5\5S*\2\u00f4\u00f2\3\2\2\2\u00f4\u00f3\3\2\2\2\u00f5"+
		"P\3\2\2\2\u00f6\u00ff\7\62\2\2\u00f7\u00fb\4\63;\2\u00f8\u00fa\5[.\2\u00f9"+
		"\u00f8\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2"+
		"\2\2\u00fc\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fe\u00f6\3\2\2\2\u00fe"+
		"\u00f7\3\2\2\2\u00ffR\3\2\2\2\u0100\u0101\7\62\2\2\u0101\u0105\7z\2\2"+
		"\u0102\u0103\7\62\2\2\u0103\u0105\7Z\2\2\u0104\u0100\3\2\2\2\u0104\u0102"+
		"\3\2\2\2\u0105\u0107\3\2\2\2\u0106\u0108\5]/\2\u0107\u0106\3\2\2\2\u0108"+
		"\u0109\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010aT\3\2\2\2"+
		"\u010b\u010c\7h\2\2\u010c\u010d\7c\2\2\u010d\u010e\7n\2\2\u010e\u010f"+
		"\7u\2\2\u010f\u0115\7g\2\2\u0110\u0111\7v\2\2\u0111\u0112\7t\2\2\u0112"+
		"\u0113\7w\2\2\u0113\u0115\7g\2\2\u0114\u010b\3\2\2\2\u0114\u0110\3\2\2"+
		"\2\u0115V\3\2\2\2\u0116\u011b\5Y-\2\u0117\u011a\5Y-\2\u0118\u011a\5[."+
		"\2\u0119\u0117\3\2\2\2\u0119\u0118\3\2\2\2\u011a\u011d\3\2\2\2\u011b\u0119"+
		"\3\2\2\2\u011b\u011c\3\2\2\2\u011cX\3\2\2\2\u011d\u011b\3\2\2\2\u011e"+
		"\u011f\t\2\2\2\u011fZ\3\2\2\2\u0120\u0121\4\62;\2\u0121\\\3\2\2\2\u0122"+
		"\u0125\5[.\2\u0123\u0125\t\3\2\2\u0124\u0122\3\2\2\2\u0124\u0123\3\2\2"+
		"\2\u0125^\3\2\2\2\u0126\u0127\7\61\2\2\u0127\u0128\7,\2\2\u0128\u012c"+
		"\3\2\2\2\u0129\u012b\13\2\2\2\u012a\u0129\3\2\2\2\u012b\u012e\3\2\2\2"+
		"\u012c\u012d\3\2\2\2\u012c\u012a\3\2\2\2\u012d\u012f\3\2\2\2\u012e\u012c"+
		"\3\2\2\2\u012f\u0130\7,\2\2\u0130\u0131\7\61\2\2\u0131\u0132\3\2\2\2\u0132"+
		"\u0133\b\60\2\2\u0133`\3\2\2\2\u0134\u0135\7\61\2\2\u0135\u0136\7\61\2"+
		"\2\u0136\u013a\3\2\2\2\u0137\u0139\n\4\2\2\u0138\u0137\3\2\2\2\u0139\u013c"+
		"\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u013d\3\2\2\2\u013c"+
		"\u013a\3\2\2\2\u013d\u013e\b\61\2\2\u013eb\3\2\2\2\u013f\u0141\t\5\2\2"+
		"\u0140\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0140\3\2\2\2\u0142\u0143"+
		"\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\b\62\2\2\u0145d\3\2\2\2\u0146"+
		"\u0147\13\2\2\2\u0147f\3\2\2\2\20\2\u00f0\u00f4\u00fb\u00fe\u0104\u0109"+
		"\u0114\u0119\u011b\u0124\u012c\u013a\u0142\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}