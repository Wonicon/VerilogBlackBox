package VerilogBlackBox;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

public class App {
  public void transform(String verilogPath) throws Exception {
    Verilog2001Parser parser =
        new Verilog2001Parser(
            new CommonTokenStream(
                new Verilog2001Lexer(
                    new ANTLRInputStream(
                        new FileInputStream(verilogPath)))));
    ParseTree tree = parser.source_text();
    // System.out.println(toStringTree(tree, parser, 0));

    CollectPortInfo collector = new CollectPortInfo();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(collector, tree);
    collector.modules.forEach(m -> System.out.println(new ToChisel(m).process()));
  }

  public static void main(String[] args) throws Exception {
    new App().transform(args[0]);
  }
}
