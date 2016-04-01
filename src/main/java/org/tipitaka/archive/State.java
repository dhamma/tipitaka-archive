package org.tipitaka.archive;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import org.tipitaka.archive.Notes.Note;

/**
 * Created by cmeier on 3/6/16.
 */
class State
{
  private int count = 0;

  private int lineCount = 0;

  private int id = 0;

  private final Writer writer;

  private final LinkedList<String> stack = new LinkedList<>();

  public String getPreviousText() {
    return previousText;
  }

  public void setPreviousText(String text) {
    previousText = text;
  }

  private String previousText;

  State(Writer writer) {
    this.writer = writer;
  }

  void push(String name) {
    stack.push(name);
  }

  // TODO move into specialist State
  LinkedList<Note> notes;

  String pop() {
    return stack.pop();
  }

  String nextNumber() {
    count++;
    return String.valueOf(count);
  }

  int getId() {
    return id;
  }

  String nextId() {
    return String.valueOf(id++);
  }

  String getLineNumber() {
    return String.valueOf(lineCount);
  }

  String nextLineNumber() {
    lineCount++;
    return String.valueOf(lineCount);
  }

  Writer appendText(String text) throws IOException {
    this.previousText = text;
    return append(text);
  }

  Writer append(CharSequence string) throws IOException {
    return writer.append(string);
  }

  public String peek() {
    return stack.peek();
  }

  public void flush() throws IOException {
    writer.flush();;
  }

  public void close() throws IOException {
    writer.close();
  }
}
