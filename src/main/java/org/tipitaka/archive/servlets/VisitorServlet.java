package org.tipitaka.archive.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.tipitaka.archive.Layout;
import org.tipitaka.archive.NGBuilderFactory;
import org.tipitaka.archive.NGVisitor;
import org.tipitaka.archive.Visitor;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by cmeier on 3/30/16.
 */
public abstract class VisitorServlet
    extends HttpServlet
{

  private Visitor visitor;

  @Override
  public void init() throws ServletException {
    try {
      Layout layout = new ServletLayout(getServletContext());
      visitor = createVisitor(createFactory(layout));
    }
    catch (IOException | XmlPullParserException e) {
      throw new ServletException(e);
    }
  }

  protected Visitor createVisitor(NGBuilderFactory factory) throws XmlPullParserException {
    return new NGVisitor(factory);
  }

  protected Visitor getVisitor() {
    return visitor;
  }

  abstract protected NGBuilderFactory createFactory(Layout layout) throws IOException;

}
