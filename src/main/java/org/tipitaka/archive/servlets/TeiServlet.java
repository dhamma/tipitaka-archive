package org.tipitaka.archive.servlets;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tipitaka.archive.BuilderFactory;
import org.tipitaka.archive.Layout;
import org.tipitaka.archive.NGBuilder;
import org.tipitaka.archive.NGVisitor;
import org.tipitaka.archive.TeiNGBuilder;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by cmeier on 3/30/16.
 */
public class TeiServlet extends HttpServlet
{

  private NGVisitor visitor;
  private BuilderFactory<NGBuilder> factory;

  @Override
  public void init() throws ServletException {
    try {
      Layout layout = new ServletLayout(getServletContext());
      factory = new TeiNGBuilder.BuilderFactory(layout);
      visitor = new NGVisitor(factory);
    }
    catch (IOException | XmlPullParserException e) {
      throw new ServletException(e);
    }
  }

  static Pattern PATH = Pattern.compile("^/?([^/]+)(/.*).tei$");

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException
  {
    Matcher matcher = PATH.matcher(req.getServletPath());
    if (!matcher.matches()) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    else {
      resp.setCharacterEncoding("UTF-16");
      resp.setContentType("application/xml");
      resp.setHeader("X-Robots-Tag", "noindex, nofollow");
      visitor.accept(resp.getWriter(),
          factory.script(matcher.group(1)),
          matcher.group(2));
    }
  }
}
