package org.tipitaka.archive;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tipitaka.search.Script;

/**
 * Created by cmeier on 3/6/16.
 */
public class NoopBuilder
    implements Builder
{

  protected final BuilderFactory<?> factory;

  protected  final State state;

  protected NoopBuilder(Writer writer, BuilderFactory<?> factory) {
    this.factory = factory;
    this.state = new State(writer);
  }

  public void flush() throws IOException {
    state.flush();
  }

  protected Builder appendTitle(Map<String, String> breadCrumbs) throws IOException {
    state.append("<title>");
    boolean first = true;
    List<String> parts = new LinkedList<>(breadCrumbs.values());
    Collections.reverse(parts);
    for (String part : parts) {
      if (part != null) {
        if (first) {
          first = false;
        }
        else {
          state.append(" - ");
        }
        state.append(part);
      }
    }
    state.append("</title>");
    return this;
  }

  public void documentStart(Script script, String path) throws IOException {
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
