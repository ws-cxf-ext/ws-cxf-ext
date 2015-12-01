package org.ws.cxf.ext.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.ws.cxf.ext.exception.CxfExtraTechnicalException;
import org.ws.cxf.ext.utils.others.QueryStringBuilder;

/**
 * Utils class for HTTP methods.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class HTTPUtils {

	public static final String HTTP_QUERY_SEPARATOR = "&|\\?";

	/**
	 * Utils class : private constructor.
	 */
	private HTTPUtils() {

	}

	/**
	 * Launching HTTP GET request.
	 * 
	 * @param urlToRead
	 * @return String
	 */
	public static final String httpGet(String urlToRead) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		StringBuilder result = new StringBuilder();

		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
		} catch (IOException e) {
			throw new CxfExtraTechnicalException(e);
		}

		return result.toString();
	}

	/**
	 * Building queries.
	 * 
	 * @param data
	 * @return String
	 */
	public static String httpBuildQuery(Map<String, String> data) {
		QueryStringBuilder builder = new QueryStringBuilder();

		for (Entry<String, String> pair : data.entrySet()) {
			builder.addQueryParameter(pair.getKey(), pair.getValue());
		}

		try {
			return builder.encode("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new CxfExtraTechnicalException(e);
		}
	}

	/**
	 * Getting query as Map.
	 * 
	 * @param query
	 * @param separator
	 * @return Map<String, String>
	 */
	public static Map<String, String> getQueryMap(String query, String separator) {
		Map<String, String> map = new HashMap<String, String>();

		if (!StringUtils.isEmpty(query)) {
			Pattern pattern = Pattern.compile("([^=]*)(?:=(.*))?");
			String[] params = query.split(separator);
			for (String param : params) {
				Matcher m = pattern.matcher(param);
				if (m.matches() && !StringUtils.isEmpty(m.group(2))) {
					map.put(m.group(1), m.group(2));
				}
			}
		}

		return map;
	}

	/**
	 * Getting query as Map.
	 * 
	 * @param query
	 * @return Map<String, String>
	 */
	public static Map<String, String> getQueryMap(String query) {
		return getQueryMap(query, HTTP_QUERY_SEPARATOR);
	}
}
