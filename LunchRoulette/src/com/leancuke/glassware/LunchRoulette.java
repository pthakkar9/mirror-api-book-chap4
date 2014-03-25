package com.leancuke.glassware;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class LunchRoulette {

	public static String getRandomLunchOption() {

		String[] lunchOptions = { "Burger", "Taco", "Pizza", "Sub", "Salad",
				"Paneer Makhni", "Chips 'n Salsa", "Just Coffee and Apple!" };
		int randomChoice = new Random().nextInt(lunchOptions.length);
		return lunchOptions[randomChoice];
	}

	/**
	 * Render the HTML template with the given data
	 * 
	 * @param resp
	 * @param data
	 * @throws IOException
	 * @throws ServletException
	 */
	// NOTE: If you're having trouble finding Freemarker code in your Eclipse
	// project, don't forget to add the JAR to your "Java Build Path" Libraries
	// via project "Properties".
	// START:render
	public static String render(ServletContext ctx, String template,
			Map<String, Object> data) throws IOException, ServletException {
		Configuration config = new Configuration();
		config.setServletContextForTemplateLoading(ctx, "WEB-INF/views");
		config.setDefaultEncoding("UTF-8");
		Template ftl = config.getTemplate(template);
		try {
			// use the data to render the template to the servlet output
			StringWriter writer = new StringWriter();
			ftl.process(data, writer);
			return writer.toString();
		} catch (TemplateException e) {
			throw new ServletException("Problem while processing template", e);
		}
	}

	// END:render

	public static TimelineItem insertSimpleTextTimelineItem(
			HttpServletRequest req) throws IOException {
		// gets mirror object from MirrorUtils file
		String userId = SessionUtils.getUserId(req);
		Mirror mirror = MirrorUtils.getMirror(req);

		Timeline timeline = mirror.timeline();
		TimelineItem timelineitem = new TimelineItem().setText(
				LunchRoulette.getRandomLunchOption()).setTitle("pthakkar9");

		TimelineItem timelineitemResp = timeline.insert(timelineitem).execute();
		setLunchRoutellteId(userId, timelineitemResp.getId());
		return timelineitemResp;
	}

	private static void setLunchRoutellteId(String userId, String id) {
		com.google.appengine.api.datastore.DatastoreService store = DatastoreServiceFactory
				.getDatastoreService();
		com.google.appengine.api.datastore.Key key = KeyFactory.createKey(
				LunchRoulette.class.getSimpleName(), userId);
		Entity entity = new Entity(key);
		entity.setProperty("lastId", id);
		store.put(entity);

	}
}
