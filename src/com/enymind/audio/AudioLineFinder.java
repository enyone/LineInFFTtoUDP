package com.enymind.audio;

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

/**
 *
 * @author Enymind Oy
 */
public class AudioLineFinder {

  List<Line> inputLines;
  List<Line> outputLines;

  // Find all audio input and output lines
  public AudioLineFinder() {
    this.inputLines = new ArrayList<>();
    this.outputLines = new ArrayList<>();

    Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

    for (Mixer.Info info : mixerInfos) {
      Mixer m = AudioSystem.getMixer(info);

      Line.Info[] lineInfos = m.getSourceLineInfo();
      for (Line.Info lineInfo : lineInfos) {
        try {
          Line line = m.getLine(lineInfo);
          if (true) {
            this.outputLines.add(line);
          }
        } catch (LineUnavailableException lue) {
          System.err.println("Line unavailable: " + lue.getMessage());
        }
      }

      lineInfos = m.getTargetLineInfo();
      for (Line.Info lineInfo : lineInfos) {
        try {
          Line line = m.getLine(lineInfo);
          if (true) {
            this.inputLines.add(line);
          }
        } catch (LineUnavailableException lue) {
          System.err.println("Line unavailable: " + lue.getMessage());
        }
      }
    }
  }

  public List<Line> getInputLines() {
    return inputLines;
  }

  public List<Line> getOutputLines() {
    return outputLines;
  }
}
