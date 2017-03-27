# Convert Verilog Module Definition to Chisel BlackBox

This tool utilizes ANTLR4 to parse Verilog2001 and collects parameter and port information
to create a chisel3 `BlackBox` definition, which can be used when hybriding chisel3 and Verilog code.

## How To Build

```
$ # In the project root dir
$ wget http://www.antlr.org/download/antlr-4.6-complete.jar
$ java -cp antlr-4.6-complete.jar Verilog2001.g4
$ javac *.java
$ java -x .
```

## How To Use

```
$ java -cp . Main some.v >> BlackBox.scala
```

## Limitation

1. This tool cannot handle marco. Use **iverilog** to pre-process Verilog sources.
2. Some implementation of ANTLR-4.6 api using a older version of this lib, so it may display warning while running.
I do not close it in code, so redirecting the stdout to other place is a work-around.

## Credit

The `Verilog2001.g4` grammar is found at [grammars-v4](https://github.com/antlr/grammars-v4).