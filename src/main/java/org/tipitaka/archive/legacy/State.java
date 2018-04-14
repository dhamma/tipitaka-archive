package org.tipitaka.archive.legacy;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import org.tipitaka.archive.notes.Note;

/**
 * Created by cmeier on 3/6/16.
 */
public class State
{
  private int count = 0;

  private int lineCount = 0;

  private int id = 0;

  private final LinkedList<String> stack = new LinkedList<>();

  public String getPreviousText() {
    return previousText;
  }

  public void setPreviousText(String text) {
    previousText = text;
  }

  private String previousText;

  protected State() {
  }

  public void push(String name) {
    stack.push(name);
  }

  public String pop() {
    return stack.pop();
  }

  public String peek() {
    return stack.peek();
  }

  public String nextNumber() {
    count++;
    return String.valueOf(count);
  }

  public int getId() {
    return id;
  }

  public int nextId() {
    return id++;
  }

  public int getLineNumber() {
    return lineCount;
  }

  public int nextLineNumber() {
    return ++lineCount;
  }
}
