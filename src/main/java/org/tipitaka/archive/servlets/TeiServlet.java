package org.tipitaka.archive.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tipitaka.archive.Layout;
import org.tipitaka.archive.NGBuilderFactory;
import org.tipitaka.archive.TeiNGBuilder;

/**
 * Created by cmeier on 3/30/16.
 */
public class TeiServlet extends VisitorServlet
{

  protected NGBuilderFactory createFactory(Layout layout) throws IOException {
    return new TeiNGBuilder.BuilderFactory(layout);
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException
  {
    resp.setCharacterEncoding("UTF-16");
    resp.setContentType("application/xml");
    resp.setHeader("X-Robots-Tag", "noindex, nofollow");
    getVisitor().accept(resp.getWriter(), req.getServletPath(), null);
  }
}
