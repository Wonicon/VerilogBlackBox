package VerilogBlackBox;

import java.util.regex.Pattern;

public class RadixPattern {
  public final Pattern pattern;
  public final int radix;
  public RadixPattern(Pattern pattern, int radix) {
    this.pattern = pattern;
    this.radix = radix;
  }
}
