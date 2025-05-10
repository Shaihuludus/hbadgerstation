package io.maddsoft.hbadgerstation.gui.details;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PrintableTextPath {

  private String text;
  private String path;

  public String toString(){
    return text;
  }
}
