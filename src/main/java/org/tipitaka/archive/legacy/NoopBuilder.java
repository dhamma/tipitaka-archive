package org.tipitaka.archive.legacy;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tipitaka.archive.model.Document;

/**
 * Created by cmeier on 3/6/16.
 */
public class NoopBuilder implements Builder
{

  protected final State state;

  protected NoopBuilder(Writer writer) {
    this.state = new State(writer);
  }

  @Override
  public void flush() throws IOException {
    state.flush();
  }

  @Override
  public void close() throws IOException {
    state.close();
  }

  public void documentStart(Document document) throws IOException {
  }

  public void documentEnd() throws IOException {
  }

  public void highlightStart(String name) throws IOException {
  }

  public void highlightEnd() throws IOException {
  }

  public void paraStart(String name, String number) throws IOException {
  }

  public void paraEnd() throws IOException {
  }

  public void noteStart() throws IOException {
  }

  public void text(String text) throws IOException {
  }

  public void noteEnd() throws IOException {
  }

  public void pageBreak(String edition, String number) throws IOException {
  }
}
