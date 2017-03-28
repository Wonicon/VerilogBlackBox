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
3. What signal can be detected to use `Clock()`: those whose name appears after `posedge` or `negedge` and contains *clk* (case insensitive).
4. What expression can be transformed: `w'[bdh]xxxxxxx...` or `123456...`. Parameters initialized using other parameters should not be accessed publicly,
but I think it too expensive to figure out constant calculation. Therefore the tool fails to translate them.

## Credit

The `Verilog2001.g4` grammar is obtained from [grammars-v4](https://github.com/antlr/grammars-v4).