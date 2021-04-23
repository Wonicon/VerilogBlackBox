package VerilogBlackBox;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ToChisel {
  private Module module;

  private StringBuilder sb = new StringBuilder();

  ToChisel(Module module) {
    this.module = module;
  }

  private void log(String fmt, Object... args) {
    System.err.printf(fmt + "\n", args);
  }

  private RadixPattern[] radixPatterns = {
      new RadixPattern(Pattern.compile("\\d+'h([0-9a-zA-F]+)"), 16),
      new RadixPattern(Pattern.compile("\\d+'b([01]+)"), 2),
      new RadixPattern(Pattern.compile("\\d+'d([0-9]+)"),10),
      new RadixPattern(Pattern.compile("(\\d+)"), 10)
  };

  private static class ChiselParam {
    final String name;
    final String init;
    ChiselParam(String name, String init) {
      this.name = name;
      this.init = init;
    }
  }

  private Stream<ChiselParam> paramFilterMapper(Parameter param) {
    if (param.init.isEmpty()) {
      return Stream.of(new ChiselParam(param.name, param.init));
    }
    for (RadixPattern rp : radixPatterns) {
      Matcher m = rp.pattern.matcher(param.init);
      if (m.matches()) {
        return Stream.of(new ChiselParam(param.name, "BigInt(\"" + m.group(1) + "\", " + rp.radix + ")"));
      }
    }
    log("reject parameter assignment due to dependency or complexity: " + param.name + " = " + param.init);
    return Stream.empty();
  }

  private String chiselParamFormatter(ChiselParam p) {
    // Expose parameters by default.
    return "val " + p.name + ": BigInt" + (p.init.isEmpty() ? "" : (" = " + p.init));
  }

  private String blackBoxMapFormatter(ChiselParam p) {
    return String.format("\"%s\" -> IntParam(%s)", p.name, p.name);
  }

  String process() {
    sb.append("class ").append(module.name);
    List<ChiselParam> params = module.params.stream().flatMap(this::paramFilterMapper).collect(Collectors.toList());
    if (params.size() > 0) {
      String paramList = String.join(", ", params.stream().map(this::chiselParamFormatter).collect(Collectors.toList()));
      sb.append("(").append(paramList).append(")");
    }
    sb.append(" extends BlackBox");
    if (params.size() > 0) {
      String mapList = String.join(", ", params.stream().map(this::blackBoxMapFormatter).collect(Collectors.toList()));
      sb.append("(Map(").append(mapList).append("))");
    }
    sb.append(" {\n");
    String indent = "  ";
    sb.append(indent).append("val io = IO(new Bundle {\n");
    if (!module.ports.isEmpty()) {
      String portList = String.join("\n", module.ports.stream().map(p -> {
        String dir = null;
        if (p.dir == PortDir.INPUT) dir = "Input";
        else if (p.dir == PortDir.OUTPUT) dir = "Output";

        String init = (p.init == null || p.init.isEmpty()) ? "UInt" : String.format("(%s).U", p.init);

        if (p.width.equals("1")) {
          if (p.isClock()) {
            return String.format("%s%sval %s = %s(Clock())", indent, indent, p.name, dir);
          }
          else if (p.init == null || p.init.isEmpty()) {
            return String.format("%s%sval %s = %s(Bool())", indent, indent, p.name, dir);
          }
          else if (p.init.equals("1")) {
            return String.format("%s%sval %s = %s(true.B)", indent, indent, p.name, dir);
          }
          else if (p.init.equals("0")) {
            return String.format("%s%sval %s = %s(false.B)", indent, indent, p.name, dir);
          }
          else {
            return String.format("%s%sval %s = %s(%s(1.W))", indent, indent, p.name, dir, init);
          }
        }
        else {
          /// TODO remove redundant parentheses.
          return String.format("%s%sval %s = %s(%s((%s).toInt.W))", indent, indent, p.name, dir, init, p.width);
        }
      }).collect(Collectors.toList()));
      sb.append(portList).append("\n");
    }
    sb.append(indent).append("})\n");
    sb.append("}\n");
    return sb.toString();
  }
}
