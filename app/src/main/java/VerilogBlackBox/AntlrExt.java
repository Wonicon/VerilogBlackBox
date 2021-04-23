package VerilogBlackBox;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

/**
 * Helper functions to extend ANTLR original functionality.
 */
public class AntlrExt {
  /**
   * Print parsing tree with assigned indent style and ranges of syntax nodes in the source code.
   * @param tree The parsing tree to display.
   * @param parser The corresponding parser to provide information.
   * @param indentSymbol The string used to display one level of indent.
   * @return The parsing tree formatted text.
   */
  public static String toStringTree(ParseTree tree, Parser parser, String indentSymbol) {
    return toStringTree(tree, parser, indentSymbol, 0);
  }

  private static class NodeRange {
    final int startLine, startCol, endLine, endCol;
    NodeRange(ParseTree tree, Parser parser) {
      Interval interval = tree.getSourceInterval();  // byte range
      Token firstToken = parser.getTokenStream().get(interval.a);
      Token lastToken = parser.getTokenStream().get(interval.b);
      startLine = firstToken.getLine();
      startCol = firstToken.getCharPositionInLine() + 1;
      endLine = lastToken.getLine();
      endCol = lastToken.getCharPositionInLine() + lastToken.getText().length();
    }
  }

  private static String toStringTree(ParseTree tree, Parser parser, String indentSymbol, int indent) {
    String s = Utils.escapeWhitespace(Trees.getNodeText(tree, parser), false);
    NodeRange rng = new NodeRange(tree, parser);
    StringBuilder buf = new StringBuilder();
    buf.append(String.format("%s @(%d, %d) -> (%d, %d)\n",s, rng.startLine, rng.startCol, rng.endLine, rng.endCol));

    if (tree.getChildCount() != 0) {
      for (int i = 0; i < tree.getChildCount(); i++) {
        for (int j = 0; j < indent + 1; j++) {
          buf.append(indentSymbol);
        }
        buf.append(toStringTree(tree.getChild(i), parser, indentSymbol, indent + 1));
      }
    }

    return buf.toString();
  }
}
