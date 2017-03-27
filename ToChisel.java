import java.util.stream.Collectors;

class ToChisel {
  private Module module;

  private StringBuilder sb = new StringBuilder();

  ToChisel(Module module) {
    this.module = module;
  }

  String process() {
    sb.append("class ").append(module.name);
    if (!module.params.isEmpty()) {
      String paramList = String.join(", ", module.params.stream().map(p -> p.init.isEmpty() ? p.name : (p.name + ": Int = " + p.init)).collect(Collectors.toList()));
      sb.append("(").append(paramList).append(")");
    }
    sb.append(" extends BlackBox");
    if (!module.params.isEmpty()) {
      String mapList = String.join(", ", module.params.stream().map(p -> "\"" + p.name + "\"" + " -> " + "IntParam(" + p.name + ")").collect(Collectors.toList()));
      sb.append("(Map(").append(mapList).append("))");
    }
    sb.append(" {\n");
    String indent = "  ";
    sb.append(indent).append("val io = IO(new Bundle {\n");
    if (!module.ports.isEmpty()) {
      String portList = String.join("\n", module.ports.stream().map(p -> {
        String type = null;
        if (p.type == PortType.INPUT) type = "Input";
        else if (p.type == PortType.OUTPUT) type = "Output";

        String init = (p.init == null || p.init.isEmpty()) ? "UInt" : String.format("(%s).U", p.init);

        if (p.width.equals("1")) {
          if (p.init == null || p.init.isEmpty()) {
            return String.format("%s%sval %s = %s(Bool())", indent, indent, p.name, type);
          }
          else if (p.init.equals("1")) {
            return String.format("%s%sval %s = %s(true.B)", indent, indent, p.name, type);
          }
          else if (p.init.equals("0")) {
            return String.format("%s%sval %s = %s(false.B)", indent, indent, p.name, type);
          }
          else {
            return String.format("%s%sval %s = %s.U(1.W)", indent, indent, p.name, type);
          }
        }
        else {
          /// TODO remove redundant parentheses.
          return String.format("%s%sval %s = %s(%s((%s).W))", indent, indent, p.name, type, init, p.width);
        }
      }).collect(Collectors.toList()));
      sb.append(portList).append("\n");
    }
    sb.append(indent).append("})\n");
    sb.append("}\n");
    return sb.toString();
  }
}
