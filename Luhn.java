import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Luhn {
  private static final int BUFFER_SIZE = 32;
  private static final int MIN_NUMBER_LENGTH = 14;
  private static final int MAX_NUMBER_LENGTH = 16;
  private static final int LEFTMOST_BIT = Integer.MIN_VALUE;
  private static final char OUTPUT_MASK = 'X';

  private static char leftShift(char[] chars, char shiftIn) {
    char first = chars[0];
    System.arraycopy(chars, 1, chars, 0, BUFFER_SIZE - 1);
    chars[BUFFER_SIZE - 1] = shiftIn;
    return first;
  }

  private static boolean inCardNumber(char c) {
    return c == ' ' || c == '-' || Character.isDigit(c);
  }

  private static int getCardNumberMaskInBuffer(char[] buf) {
    int lastNumberSpan = 0, lastNumberStartIndex = 0,
        numberSpan = 0, digitCount = 0, sum = 0;

    // TODO limit search based on previous results
    for (int i = BUFFER_SIZE - 1; i >= 0 && digitCount < MAX_NUMBER_LENGTH; i--) {
      char currChar = buf[i];
      if (inCardNumber(currChar)) {
        numberSpan++;

        if (Character.isDigit(currChar)) {
          digitCount++;
          int digit = Character.getNumericValue(currChar);
          digit <<= ~(digitCount & 1) & 1;
          sum += digit > 9 ? digit - 9 : digit;

          if ((digitCount >= MIN_NUMBER_LENGTH && digitCount <= MAX_NUMBER_LENGTH) && sum % 10 == 0) {
            lastNumberStartIndex = i;
            lastNumberSpan = numberSpan;
          }
        }
        continue;
      }

      numberSpan = digitCount = sum = 0;
    }

    return ~(~0 << lastNumberSpan) << 32 - lastNumberSpan - lastNumberStartIndex;
  }

  public static void main(String[] args) throws IOException {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in), 256);
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out), 256);
    int c = 0, bufferMaskBits = 0, bufidx = 0;

    char[] buf = new char[BUFFER_SIZE];
    int maxBufferLeftOver = stdin.read(buf, 0, BUFFER_SIZE);
    boolean ready = true;
    while ((!ready && bufidx < maxBufferLeftOver) || (c = stdin.read()) != -1) {
      if (!ready)
        c = buf[bufidx++];

      boolean needsMasking = (bufferMaskBits & LEFTMOST_BIT) == LEFTMOST_BIT;

      bufferMaskBits <<= 1;
      char pop = leftShift(buf, (char)c);

      if (needsMasking && Character.isDigit(pop)) {
        out.write(OUTPUT_MASK);
      } else {
        out.write(pop);
      }

      if (Character.isDigit((char)c))
        bufferMaskBits |= getCardNumberMaskInBuffer(buf);

      if (bufidx == maxBufferLeftOver) {
        out.flush();
        c = bufferMaskBits = bufidx = 0;
        maxBufferLeftOver = stdin.read(buf, 0, BUFFER_SIZE);
      }

      ready = stdin.ready();
    }
  }
}
