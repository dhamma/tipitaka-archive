package org.tipitaka.archive.legacy;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import org.tipitaka.archive.notes.Note;

/**
 * Created by cmeier on 3/6/16.
 */
// TODO separate into State and NGState
public class State
{
  private String indent = "";

  private int count = 0;

  private int lineCount = 0;

  private int id = 0;

  //private final Writer writer;

  private final LinkedList<String> stack = new LinkedList<>();

  public String getPreviousText() {
    return previousText;
  }

  public void setPreviousText(String text) {
    previousText = text;
  }

  private String previousText;

  protected State() {//Writer writer) {
    //this.writer = writer;
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

  // State appendText(String text) throws IOException {
  //   this.previousText = text;
  //   return append(text);
  // }

  // State appendLine(CharSequence string) throws IOException {
  //   writer.append(indent).append(string).append("\n");
  //   return this;
  // }

  // State appendIndent(CharSequence string) throws IOException {
  //   writer.append(indent).append(string);
  //   return this;
  // }

  // State appendEnd(CharSequence string) throws IOException {
  //   writer.append(string).append("\n");
  //   return this;
  // }

  // State indent() {
  //   indent += "  ";
  //   return this;
  // }

  // State outdent() {
  //   indent = indent.substring(0, indent.length() - 2);
  //   return this;
  // }

  // public State append(CharSequence string) throws IOException {
  //  writer.append(string);
  //  return this;
  //}

  //public void flush() throws IOException {
  //  writer.flush();;
  //}

  //public void close() throws IOException {
  //  writer.close();
  //}
}
