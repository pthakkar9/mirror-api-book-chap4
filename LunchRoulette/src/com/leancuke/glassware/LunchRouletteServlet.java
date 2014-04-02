package com.leancuke.glassware;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.services.mirror.model.TimelineItem;

@SuppressWarnings("serial")
// START:randomlunch
public class LunchRouletteServlet extends HttpServlet {
	// Old doGet method
	// /** Accepts an HTTP GET request, and writes a random lunch type. */
	// public void doGet(HttpServletRequest req, HttpServletResponse resp)
	// throws IOException, ServletException
	// {
	// resp.setContentType("text/html; charset=utf-8");
	//
	// Map<String, Object> data = new HashMap<String, Object>();
	// data.put("food", LunchRoulette.getRandomLunchOption());
	//
	// String html = LunchRoulette.render(
	// getServletContext(), "web/cuisine.ftl", data);
	// resp.getWriter().append(html);
	// }

	/** Accepts an HTTP GET request, and writes a random lunch type. */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		// LunchRoulette.deleteSimpleTextTimelineItem(req);
		// Ideally inserSimpleTextTimelineItem should be void and we should not
		// accept object here. But, added it for debugging.
//		 TimelineItem timelineitemResp = LunchRoulette
//		 .insertSimpleTextTimelineItem(req);

		
		// LunchRoulette.deleteSimpleTextTimelineItem(req);
		// Ideally insertSimpleHTMLTimelineItem should be void and we should not
		// accept object here. But, added it for debugging.
		ServletContext ctx = getServletContext();
		TimelineItem timelineitemResp = LunchRoulette
				.insertSimpleHTMLTimelineItem(ctx, req);

		// This items are unnecessary. Only for debugging.
		resp.setContentType("text/text");
		resp.getWriter().println("This is just for debugging!");
		resp.getWriter().println(
				"Just Inserted into timeline. Timeline item id is "
						+ timelineitemResp.getId());
		resp.getWriter().println(
				"Timeline item's HTML content is  "
						+ timelineitemResp.getHtml());
		resp.getWriter().println("getText is " + timelineitemResp.getText());
		resp.getWriter().println("getTitle is " + timelineitemResp.getTitle());
		resp.getWriter().println(
				"getUpdated is " + timelineitemResp.getUpdated());
		resp.getWriter().println(
				"getSelfLink is " + timelineitemResp.getSelfLink());

	}
}
