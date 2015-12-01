/*
 * Copyright 2009 Vladimir Ritz Bossicard
 *
 * This file is part of WorkingOnIt.
 *
 * WorkingOnIt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Version     : $Revision: 148 $
 * Last edit   : $Date: 2015-09-09 12:40:24 +0200 (Wed, 09 Sep 2015) $
 * Last editor : $Author: ccougour $
 */
package org.ws.cxf.ext.utils.others;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manage and generate the query string part of an URL.
 * <p>
 *
 * <pre>
 * QueryStringBuilder builder = new QueryStringBuilder(&quot;/test.jsp&quot;).addQueryParameter(&quot;english&quot;, &quot;hello world&quot;).addQueryParameter(&quot;german&quot;, &quot;gruezi&quot;);
 * String query = builder.encode();
 * </pre>
 * 
 * returns <code>"/test.jsp?english=hello%20world&german=gruezi"</code>
 *
 * <p>
 * <b>Dependendies</b>
 * <ul>
 * <li>Apache commons-httpclient, 3.1</li>
 * </ul>
 *
 * @author Vladimir Ritz Bossicard
 */
public class QueryStringBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryStringBuilder.class);

	/**
	 * Default encoding used when no encoding is defined as parameter. The
	 * encoding is retrieved via <code>Charset.defaultCharset().name()</code>.
	 */
	public static final String DEFAULT_ENCODING = Charset.defaultCharset().name();

	/*
	 * The fields are marked as protected to let extending classes define
	 * additional functionalities that are proper to their usage.
	 */

	protected String base;
	protected String sessionid;
	protected Map<String, NameValuePair> parameters = new HashMap<String, NameValuePair>();
	protected boolean override = true;

	/**
	 * Creates an empty <code>QueryStringBuilder</code>.
	 * <p>
	 * This constructor is equivalent to a call to the three-arguments
	 * constructor with a <code>null</code>, <code>null</code> and
	 * <code>true</code> arguments.
	 *
	 * @see #QueryStringBuilder(String, String, boolean)
	 */
	public QueryStringBuilder() {
		this(null, null, true);
	}

	/**
	 * Creates a <code>QueryStringBuilder</code> from the <code>String</code>
	 * representation.
	 * <p>
	 * This constructor is equivalent to a call to the three-arguments
	 * constructor with a <code>spec</code>, <code>null</code> and
	 * <code>true</code> arguments.
	 *
	 * @param spec
	 *            the <code>String</code> to parse.
	 * @see #QueryStringBuilder(String, String, boolean)
	 */
	public QueryStringBuilder(final String spec) {
		this(spec, null, true);
	}

	/**
	 * Creates a <code>QueryStringBuilder</code> from the <code>String</code>
	 * representation with automatic overriding of the parameters.
	 * <p>
	 * This constructor is equivalent to a call to the three-arguments
	 * constructor with a <code>spec</code>, <code>DEFAULT_ENCODING</code> and
	 * <code>override</code> arguments.
	 *
	 * @param spec
	 *            the <code>String</code> to parse.
	 * @param override
	 *            <code>true</code> if already defined parameters will be
	 *            overridden by newly defined ones.
	 * @see #QueryStringBuilder(String, String, boolean)
	 */
	public QueryStringBuilder(final String spec, final boolean override) {
		this(spec, DEFAULT_ENCODING, override);
	}

	/**
	 * Creates a <code>QueryStringBuilder</code> from the <code>String</code>
	 * representation with automatic overriding of the parameters.
	 * <p>
	 * The string will be parsed and the query parameters will be decoded and
	 * added to the list of parameters.
	 *
	 * @param spec
	 *            the <code>String</code> to parse.
	 * @param enc
	 *            The name of a supported character encoding
	 * @param override
	 *            <code>true</code> if already defined parameters will be
	 *            overridden by newly defined ones.
	 */
	public QueryStringBuilder(final String spec, final String enc, final boolean override) {
		this.override = override;
		if (spec != null) {
			String tmp = spec.trim();
			int pos = spec.indexOf('?');
			if (pos < 0) {
				this.base = tmp;
			} else {
				this.base = tmp.substring(0, pos);
				for (StringTokenizer tokenizer = new StringTokenizer(tmp.substring(pos + 1), "&"); tokenizer.hasMoreTokens();) {
					try {
						String token = tokenizer.nextToken().trim();
						int pos2 = token.indexOf('=');
						addQueryParameter(token.substring(0, pos2), enc == null ? URLDecoder.decode(token.substring(pos2 + 1), DEFAULT_ENCODING) : URLDecoder.decode(token.substring(pos2 + 1), enc));
					} catch (UnsupportedEncodingException e) {
						// TODO should the exception bubble?
						LOGGER.warn("Unsupported encoding {}", DEFAULT_ENCODING, e);
					}
				}
			}
		}
	}

	/**
	 * Returns the base of the URL.
	 */
	public String getBase() {
		return this.base;
	}

	/**
	 * Adds a parameter to the list of already defined parameters. Already
	 * existing parameters are overridden depending on the how the
	 * <code>QueryStringBuilder</code> object was created.
	 *
	 * @param param
	 *            the parameter to add.
	 *
	 * @see #QueryStringBuilder(String, String, boolean)
	 * @see #QueryStringBuilder(String, boolean)
	 */
	public QueryStringBuilder addQueryParameter(final NameValuePair param) {
		if (param != null && (!this.parameters.containsKey(param.getName()) || this.override)) {
			this.parameters.put(param.getName(), param);
		}
		return this;
	}

	/**
	 * Adds a parameter to the list of already defined parameters. Already
	 * existing parameters are overridden depending on the how the
	 * <code>QueryStringBuilder</code> object was created.
	 * <p>
	 *
	 * A value is considered as <i>invalid</i> if it is:
	 * <ul>
	 * <li><code>null</code> values
	 * <li>empty values
	 * <li>blank values
	 * </ul>
	 * Parameters with invalid values will <i>not</i> be added.
	 *
	 * @param name
	 *            the name of the parameter
	 * @param value
	 *            the value of the parameter. The <code>toString</code> method
	 *            will be invoked to retrieve the parameter's <i>real</i> value.
	 * @param defaultValue
	 *            the default value used in case the passed value argument is
	 *            considered as invalid.
	 *
	 * @see #addQueryParameter(NameValuePair)
	 */
	public QueryStringBuilder addQueryParameter(final String name, final Object value, final String defaultValue) {
		String tmp = value == null || value.toString().trim().length() == 0 ? defaultValue : value.toString().trim();
		return addQueryParameter(new NameValuePair(name, tmp));
	}

	/**
	 * Invokes {@link #addQueryParameter(String, Object, String)} with a
	 * <code>null</code> defaultValue.
	 *
	 * #see {@link #addQueryParameter(String, Object, String)}
	 */
	public QueryStringBuilder addQueryParameter(final String name, final String value) {
		return addQueryParameter(name, value, null);
	}

	/**
	 * Invokes {@link #addQueryParameter(NameValuePair)} with a
	 * <code>String</code> converted value.
	 */
	public QueryStringBuilder addQueryParameter(final String name, final int value) {
		return addQueryParameter(new NameValuePair(name, String.valueOf(value)));
	}

	/**
	 * Invokes {@link #addQueryParameter(NameValuePair)} with a
	 * <code>String</code> converted value.
	 */
	public QueryStringBuilder addQueryParameter(final String name, final float value) {
		return addQueryParameter(new NameValuePair(name, String.valueOf(value)));
	}

	/**
	 * Invokes {@link #addQueryParameter(NameValuePair)} with a
	 * <code>String</code> converted value.
	 */
	public QueryStringBuilder addQueryParameter(final String name, final long value) {
		return addQueryParameter(new NameValuePair(name, String.valueOf(value)));
	}

	/**
	 * Invokes {@link #addQueryParameter(NameValuePair)} with a
	 * <code>String</code> converted value.
	 */
	public QueryStringBuilder addQueryParameter(final String name, final boolean value) {
		return addQueryParameter(new NameValuePair(name, String.valueOf(value)));
	}

	/**
	 * Returns <code>true</code> if the parameter with the given name has
	 * already been defined.
	 *
	 * @param name
	 *            the name of the parameter
	 */
	public boolean containsParameter(final String name) {
		return this.parameters.containsKey(name);
	}

	/**
	 * Add the <code>sessionid</code> to the url.
	 *
	 * @param sessionid
	 *            the session id
	 */
	public QueryStringBuilder addSessionId(String sessionid) {
		this.sessionid = sessionid;
		return this;
	}

	/**
	 * Returns the {@link NameValuePair} parameter associated with the given
	 * name.
	 *
	 * @param name
	 *            the name of the parameter
	 */
	public NameValuePair getQueryParameter(final String name) {
		return this.parameters.get(name);
	}

	/**
	 * Invokes {@link #encode(String)} with a <code>DEFAULT_ENCODING</code>
	 * encoding.
	 *
	 * @see URLEncoder#encode(String)
	 */
	public String encode() {
		try {
			return encode(DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// according to Sun, the system should always have the platform
			// default so this exception should never be thrown
			LOGGER.warn("Unsupported encoding {}", DEFAULT_ENCODING, e);
		}
		return null;
	}

	/**
	 * Returns the encoded URL <code>String</code>.
	 *
	 * @param enc
	 *            The name of a supported character encoding
	 *
	 * @see URLEncoder#encode(String, String)
	 * @exception UnsupportedEncodingException
	 *                If the named encoding is not supported
	 */
	public String encode(final String enc) throws UnsupportedEncodingException {
		StringBuilder buffer = new StringBuilder();
		if (this.base != null) {
			buffer.append(this.base);
		}
		if (this.sessionid != null) {
			buffer.append(";jsessionid=" + this.sessionid);
		}
		if (!this.parameters.isEmpty()) {
			buffer.append("?");
			for (Iterator<Map.Entry<String, NameValuePair>> iter = this.parameters.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, NameValuePair> entry = iter.next();

				String value = entry.getValue().getValue();
				if (value != null && value.trim().length() > 0) {
					String encoded = enc == null ? URLEncoder.encode(value, DEFAULT_ENCODING) : URLEncoder.encode(value, enc);
					buffer.append(entry.getKey()).append("=").append(encoded);
					buffer.append("&");
				}
			}
			// remove the last "&", not very elegant but it works
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}

}
