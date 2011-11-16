
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Luhn {
  private static final int BUFFER_SIZE = 32;
  private static final int MIN_NUMBER_LENGTH = 14;
  private static final int MAX_NUMBER_LENGTH = 16;
  private static final int LEFTMOST_BIT = Integer.MIN_VALUE;
  private static final char OUTPUT_MASK = 'X';
  
  private static char leftShift(char[] chars, char shiftIn) {
    char first = chars[0];
    for (int i = 1; i < BUFFER_SIZE; i++) {
      chars[i-1] = chars[i];
    }
    chars[BUFFER_SIZE - 1] = shiftIn;
    return first;
  }
  
  private static boolean inCardNumber(char c) {
    return c == ' ' || c == '-' || Character.isDigit(c);
  }
  
  private static int getCardNumberInBufferMask(char[] buf) {
    int lastNumberSpan = 0, lastNumberStartIndex = -1,
        numberSpan = 0, digitCount = 0, sum = 0;
    
    // TODO limit search based on previous results
    for (int i = BUFFER_SIZE-1 ; i >=0 && digitCount < MAX_NUMBER_LENGTH;i--) {
      char currChar = buf[i];
      
      if (inCardNumber(currChar)) {
        numberSpan++;
        
        if (Character.isDigit(currChar)) {
          digitCount++;
          int digit = Character.getNumericValue(currChar);
          digit <<= digitCount % 2 == 0 ? 1 : 0;
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
    
    if (lastNumberStartIndex != -1) {
      return (~0 << (32 - lastNumberSpan)) >>> lastNumberStartIndex;
    }
    
    return 0;
  }
  
  public static void main(String[] args) throws IOException {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    int c = 0, bufferMaskBits = 0;

    char[] buf = new char[BUFFER_SIZE];
    
    while ((c = stdin.read()) != -1) {
      boolean needsMasking = (bufferMaskBits & LEFTMOST_BIT) == LEFTMOST_BIT;
      
      // rotate buffer
      bufferMaskBits <<= 1;
      char pop = leftShift(buf, (char) c);

      if (pop != '\0') { // cheat to skip initial array
        if (needsMasking && Character.isDigit(pop)) {
          System.out.print(OUTPUT_MASK);
        } else {
          System.out.print(pop);
        }
      }

      bufferMaskBits |= getCardNumberInBufferMask(buf);
    }

    // TODO mask
    System.out.print(buf);
  }

}
