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
  private String indent = "";

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

  State appendText(String text) throws IOException {
    this.previousText = text;
    return append(text);
  }

  State appendLine(CharSequence string) throws IOException {
    writer.append(indent).append(string).append("\n");
    return this;
  }

  State appendIndent(CharSequence string) throws IOException {
    writer.append(indent).append(string);
    return this;
  }

  State appendEnd(CharSequence string) throws IOException {
    writer.append(string).append("\n");
    return this;
  }

  State indent() {
    indent += "  ";
    return this;
  }

  State outdent() {
    indent = indent.substring(0, indent.length() - 2);
    return this;
  }

  State append(CharSequence string) throws IOException {
    writer.append(string);
    return this;
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
