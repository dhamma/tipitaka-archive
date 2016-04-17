package org.tipitaka.archive.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tipitaka.archive.DirectoryNGVisitor;
import org.tipitaka.archive.Layout;
import org.tipitaka.archive.NGBuilderFactory;
import org.tipitaka.archive.Visitor;
import org.tipitaka.archive.XHtmlNGBuilder;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by cmeier on 3/30/16.
 */
public class XHtmlServlet
    extends VisitorServlet
{

  protected NGBuilderFactory createFactory(Layout layout) throws IOException {
    return new XHtmlNGBuilder.BuilderFactory(layout);
  }

  @Override
  protected Visitor createVisitor(final NGBuilderFactory factory) throws XmlPullParserException {
    return new DirectoryNGVisitor(factory);
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException
  {
    resp.setCharacterEncoding("UTF-8");
    resp.setContentType("text/html");
    //if (req.getServletPath().endsWith("index.html")) {
    //  getServletContext().getNamedDispatcher("default").forward(req, resp);
    //}
    //else {
      getVisitor().accept(resp.getWriter(), req.getServletPath(), req.getParameter("version"));
    //}
  }
}
