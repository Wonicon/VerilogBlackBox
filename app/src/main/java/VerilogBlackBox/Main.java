import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

public class Main {
  public static void main(String[] args) throws Exception {
    Verilog2001Parser parser =
        new Verilog2001Parser(
            new CommonTokenStream(
                new Verilog2001Lexer(
                    new ANTLRInputStream(
                        new FileInputStream(args[0])))));
    ParseTree tree = parser.source_text();
    // System.out.println(toStringTree(tree, parser, 0));

    CollectPortInfo collector = new CollectPortInfo();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(collector, tree);
    collector.modules.forEach(m -> System.out.println(new ToChisel(m).process()));
  }
}
