package com.leancuke.glassware;

import java.io.IOException;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.TimelineItem;
import com.leancuke.glassware.auth.AuthUtils;

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

		String userId = SessionUtils.getUserId(req);
		Credential credential = AuthUtils.getCredential(userId);

		Mirror mirror = new Mirror.Builder(new UrlFetchTransport(),
				new JacksonFactory(), credential).setApplicationName(
				"pthakkar Lunch Roulette").build();

		Timeline timeline = mirror.timeline();
		TimelineItem timelineitem = new TimelineItem()
				.setText("Hello Parva, This is your app talking from mirror api server");

		timeline.insert(timelineitem).execute();
		
		resp.setContentType("text/html");
		resp.getWriter().print("<p>Just Inserted into timeline</p>");

	}

}
