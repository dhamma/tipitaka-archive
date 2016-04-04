package org.tipitaka.archive.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tipitaka.archive.Layout;
import org.tipitaka.archive.NGBuilderFactory;
import org.tipitaka.archive.NGVisitor;
import org.tipitaka.archive.TeiNGBuilder;
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
      visitor = new NGVisitor(createFactory(layout));
    }
    catch (IOException | XmlPullParserException e) {
      throw new ServletException(e);
    }
  }

  protected Visitor getVisitor() {
    return visitor;
  }

  abstract protected NGBuilderFactory createFactory(Layout layout) throws IOException;

}
