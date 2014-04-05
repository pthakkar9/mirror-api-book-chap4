package com.leancuke.glassware;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.util.DateTime;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
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
			Map<String, ? extends Object> data) throws IOException,
			ServletException {
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

	public static void insertSimpleMenuItems(TimelineItem t) {

		t.setMenuItems(new LinkedList<MenuItem>());

		t.getMenuItems().add(new MenuItem().setAction("READ_ALOUD"));
		t.getMenuItems().add(new MenuItem().setAction("DELETE"));

	}

	public static TimelineItem insertMultiHTMLTimelineItem(ServletContext ctx,
			HttpServletRequest req) throws IOException, ServletException {

		// gets mirror object from MirrorUtils file
		String bundleId = "Item Id" + UUID.randomUUID();
		String userId = SessionUtils.getUserId(req);
		Mirror mirror = MirrorUtils.getMirror(req);
		Timeline timeline = mirror.timeline();

		String cuisine1 = getRandomLunchOption();
		Map<String, String> data = Collections.singletonMap("food", cuisine1);
		String html1 = render(ctx, "glass/cuisine.ftl", data);
		TimelineItem timelineitem1 = new TimelineItem()
				.setHtml(html1)
				.setUpdated(new DateTime(new Date()))
				.setTitle("pthakkar9")
				.setBundleId(bundleId)
				.setIsPinned(true)
				.setSpeakableText(
						"Parva says You should eat " + cuisine1 + " for lunch")
				.setIsBundleCover(true);

		insertSimpleMenuItems(timelineitem1);

		// This is not DRY. I should move it to some function
		String cuisine2 = getRandomLunchOption();
		String html2 = render(ctx, "glass/cuisine.ftl", data);
		TimelineItem timelineitem2 = new TimelineItem()
				.setHtml(html2)
				.setUpdated(new DateTime(new Date()))
				.setTitle("pthakkar9")
				.setBundleId(bundleId)
				.setSpeakableText(
						"Parva says You should eat " + cuisine2 + " for lunch");
		insertSimpleMenuItems(timelineitem2);

		// This is not DRY. I should move it to some function
		String cuisine3 = getRandomLunchOption();
		String html3 = render(ctx, "glass/cuisine.ftl", data);
		TimelineItem timelineitem3 = new TimelineItem()
				.setHtml(html3)
				.setUpdated(new DateTime(new Date()))
				.setTitle("pthakkar9")
				.setBundleId(bundleId)
				.setSpeakableText(
						"Parva says You should eat " + cuisine3 + " for lunch");
		insertSimpleMenuItems(timelineitem3);

		TimelineItem timelineitemResp;

		if (getLunchRoutellteId(userId) == null) {
			timelineitemResp = timeline.insert(timelineitem1).execute();
			timelineitemResp = timeline.insert(timelineitem2).execute();
			timelineitemResp = timeline.insert(timelineitem3).execute();

		} else {
			timelineitemResp = timeline.patch(getLunchRoutellteId(userId),
					timelineitem1).execute();
			timelineitemResp = timeline.patch(getLunchRoutellteId(userId),
					timelineitem2).execute();
			timelineitemResp = timeline.patch(getLunchRoutellteId(userId),
					timelineitem3).execute();

		}

		setLunchRoutellteId(userId, timelineitemResp.getId());
		return timelineitemResp;

	}

	public static TimelineItem insertSimpleHTMLTimelineItem(ServletContext ctx,
			HttpServletRequest req) throws IOException, ServletException {
		// gets mirror object from MirrorUtils file
		String userId = SessionUtils.getUserId(req);
		Mirror mirror = MirrorUtils.getMirror(req);
		Timeline timeline = mirror.timeline();

		String cuisine = getRandomLunchOption();
		Map<String, String> data = Collections.singletonMap("food", cuisine);
		String html = render(ctx, "glass/cuisine.ftl", data);
		TimelineItem timelineitem = new TimelineItem()
				.setHtml(html)
				.setUpdated(new DateTime(new Date()))
				.setTitle("pthakkar9")
				.setSpeakableText(
						"Parva says You should eat " + cuisine + " for lunch");

		insertSimpleMenuItems(timelineitem);

		TimelineItem timelineitemResp;

		if (getLunchRoutellteId(userId) == null) {
			timelineitemResp = timeline.insert(timelineitem).execute();
		} else {
			timelineitemResp = timeline.patch(getLunchRoutellteId(userId),
					timelineitem).execute();

		}

		setLunchRoutellteId(userId, timelineitemResp.getId());
		return timelineitemResp;
	}

	public static TimelineItem insertSimpleTextTimelineItem(
			HttpServletRequest req) throws IOException {
		// gets mirror object from MirrorUtils file
		String userId = SessionUtils.getUserId(req);
		Mirror mirror = MirrorUtils.getMirror(req);

		Timeline timeline = mirror.timeline();
		TimelineItem timelineitem = new TimelineItem()
				.setText(LunchRoulette.getRandomLunchOption())
				.setUpdated(new DateTime(new Date())).setTitle("pthakkar9");

		TimelineItem timelineitemResp;

		if (getLunchRoutellteId(userId) == null) {
			timelineitemResp = timeline.insert(timelineitem).execute();
		} else {
			try {
				timelineitemResp = timeline.patch(getLunchRoutellteId(userId),
						timelineitem).execute();
			} catch (Exception e) {
				// I sometimes get server error when timeline item id exists in
				// datastore, but is expired on google outh2 framework. So, I
				// should change try/catch to if condition that checks for
				// expiration. I yet don't know how to do it, so I surrounded it
				// by try/catch with an assumption that whenever expired item id
				// is found, it'll try to path and will find exception. With
				// this exception found, it'll go to catch section and then try
				// to insert a new timeline card, which is excactly what I want.
				timelineitemResp = timeline.insert(timelineitem).execute();
			}

		}

		setLunchRoutellteId(userId, timelineitemResp.getId());
		return timelineitemResp;
	}

	// Deprecated function. Combined with addSimpleTextTimelineItem
	// public static TimelineItem updateSimpleTextTimelineItem(
	// HttpServletRequest req) throws IOException {
	// // gets mirror object from MirrorUtils file
	// String userId = SessionUtils.getUserId(req);
	// Mirror mirror = MirrorUtils.getMirror(req);
	//
	// Timeline timeline = mirror.timeline();
	// TimelineItem timelineitem = new TimelineItem().setText(
	// getRandomLunchOption()).setUpdated(new DateTime(new Date()));
	//
	// TimelineItem timelineitemResp = timeline.patch(
	// getLunchRoutellteId(userId), timelineitem).execute();
	//
	// return timelineitemResp;
	// }

	public static void deleteSimpleTextTimelineItem(HttpServletRequest req)
			throws IOException {
		// gets mirror object from MirrorUtils file
		String userId = SessionUtils.getUserId(req);
		Mirror mirror = MirrorUtils.getMirror(req);

		Timeline timeline = mirror.timeline();

		if (getLunchRoutellteId(userId) == null) {
			return;
		} else {
			timeline.delete(getLunchRoutellteId(userId)).execute();

		}

		// TODO: Delete data from datastore too. Unless this is done, this
		// method cannot be used.

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

	private static String getLunchRoutellteId(String userId) {
		com.google.appengine.api.datastore.DatastoreService store = DatastoreServiceFactory
				.getDatastoreService();
		com.google.appengine.api.datastore.Key key = KeyFactory.createKey(
				LunchRoulette.class.getSimpleName(), userId);
		Entity entity;
		try {
			entity = store.get(key);
		} catch (EntityNotFoundException e) {
			return null;
		}
		return (String) entity.getProperty("lastId");

	}
}
