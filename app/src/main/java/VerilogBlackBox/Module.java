package VerilogBlackBox;

import java.util.ArrayList;

enum PortDir { INPUT, OUTPUT, INOUT, NA }

class Interface {
  final String name;
  final String init;
  Interface(String name, String init) {
    this.name = name;
    this.init = init;
  }

  protected final String init() {
    return ((init == null || init.isEmpty()) ? "" : (" = " + init));
  }

  @Override
  public String toString() {
    return name + init();
  }
}

class Port extends Interface {
  final PortDir dir;
  final String width;
  private boolean isClock;

  boolean isClock() {
    return isClock;
  }

  Port(PortDir dir, String name, String width, String init) {
    super(name, init);
    this.dir = dir;
    this.width = width;
    this.isClock = false;
  }

  void toClock() {
    isClock = true;
  }

  @Override
  public String toString() {
    return name + ": " + dir + init();
  }
}

class Parameter extends Interface {
  Parameter(String name, String init) {
    super(name, init);
  }
}

class Module {
  final ArrayList<Port> ports = new ArrayList<>();
  final ArrayList<Parameter> params = new ArrayList<>();
  final String name;

  Module(String name) {
    this.name = name;
  }
}
