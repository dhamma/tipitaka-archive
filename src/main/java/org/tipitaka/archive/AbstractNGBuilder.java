package org.tipitaka.archive;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by cmeier on 3/6/16.
 */
public abstract class AbstractNGBuilder implements NGBuilder
{

  protected  final State state;

  protected final BuilderFactory factory;

  protected AbstractNGBuilder(Writer writer, BuilderFactory factory) {
    this.state = new State(writer);
    this.factory = factory;
  }

  @Override
  public void flush() throws IOException {
    state.flush();
  }

  @Override
  public void close() throws IOException {
    state.close();
  }

  @Override
  public void text(String text) throws IOException {
    state.append(text);
  }
}
