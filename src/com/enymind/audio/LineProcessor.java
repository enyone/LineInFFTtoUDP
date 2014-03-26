package com.enymind.audio;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import java.nio.ByteBuffer;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Enymind Oy
 */
public class LineProcessor implements Runnable {

  FFTInterruptable parent;
  TargetDataLine line;
  int bufferLen;

  public LineProcessor(FFTInterruptable parent, TargetDataLine line, int bufferLen) {
    this.parent = parent;
    this.line = line;
    this.bufferLen = bufferLen;
  }

  public static void decode(byte[] input, double[] output) {
    assert input.length == 2 * output.length;
    for (int i = 0; i < output.length; i++) {
      output[i] = (short) (((0xFF & input[2 * i + 1]) << 8) | (0xFF & input[2 * i]));
      output[i] /= Short.MAX_VALUE;
    }
  }

  @Override
  public void run() {
    System.out.println("Starting thread with buffer size of: " + this.bufferLen);

    int bytesRead = 0;
    byte[] byteData = new byte[this.bufferLen * line.getFormat().getSampleSizeInBits() / 8];
    double[] doubleData = new double[this.bufferLen];
    double[] spectrum = new double[this.bufferLen / 2];
    DoubleFFT_1D fft = new DoubleFFT_1D(this.bufferLen);

    while (!Thread.interrupted()) {
      try {
        bytesRead = 0;
        while (bytesRead < byteData.length) {
          int currentBytes = line.read(byteData, bytesRead, byteData.length - bytesRead);
          if (currentBytes < 0) {
            break;
          }
          bytesRead += currentBytes;
        }
      } catch (IllegalArgumentException iae) {
        System.err.println("Invalid buffer size (" + byteData.length + ") when reading line: " + iae.getMessage());
        System.exit(1);
      }

      this.decode(byteData, doubleData);

      fft.realForward(doubleData);

      for (int k = 0; k < (doubleData.length / 2) - 1; k++) {
        spectrum[k] = Math.sqrt(Math.pow(doubleData[2 * k], 2) + Math.pow(doubleData[2 * k + 1], 2));
      }

      this.parent.fftInterrupt(spectrum);

      try {
        Thread.sleep(10);
      } catch (InterruptedException ie) {
      }
    }
  }
}
