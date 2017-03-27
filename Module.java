import java.util.ArrayList;

enum PortType { INPUT, OUTPUT, INOUT, PARAMETER, NA }

/**
 * We use the same class to describe ports and parameters.
 */
class Port {
  final PortType type;
  final String name;
  final String width;
  final String init;

  Port(PortType t, String n, String w, String i) {
    type = t;
    name = n;
    width = w;
    init = i;
  }

  @Override public String toString() {
    return name + ": " + type + " = " + init;
  }
}

class Module {
  final ArrayList<Port> ports = new ArrayList<>();
  final ArrayList<Port> params = new ArrayList<>();
  final String name;

  Module(String name) {
    this.name = name;
  }
}
