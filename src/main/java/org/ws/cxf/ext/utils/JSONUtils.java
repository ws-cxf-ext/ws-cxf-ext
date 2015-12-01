package org.ws.cxf.ext.utils;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ws.cxf.ext.Constants;
import org.ws.cxf.ext.exception.CxfExtraTechnicalException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe utilitaire pour encoder/décoder du JSON
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class JSONUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);

	/**
	 * Convertir une Map en JSON
	 * 
	 * @param map
	 * @return String
	 */
	public static String map2json(Map<?, ?> map) {
		if (!isNotEmpty(map)) {
			return Constants.EMPTY_STRING;
		}

		String rtn = Constants.EMPTY_STRING;

		try {
			ObjectMapper mapper = new ObjectMapper();
			rtn = mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			new CxfExtraTechnicalException(e);
		}

		return rtn;
	}

	/**
	 * Convertir un objet en JSON
	 * 
	 * @param obj
	 * @param clazz
	 * @return String
	 */
	public static String objectTojson(Object obj, Class<?> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(clazz.cast(obj));
		} catch (JsonProcessingException e) {
			new CxfExtraTechnicalException(e);
		}

		return json;
	}

	/**
	 * Convertir une Liste en JSON
	 * 
	 * @param list
	 * @return String
	 */
	public static String list2json(List<? extends List<String>> list) {
		if (!isNotEmpty(list)) {
			return Constants.EMPTY_STRING;
		}

		String rtn = Constants.EMPTY_STRING;

		try {
			ObjectMapper mapper = new ObjectMapper();
			rtn = mapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			new CxfExtraTechnicalException(e);
		}

		return rtn;
	}

	/**
	 * Convertir une Liste en JSON
	 * 
	 * @param list
	 * @return String
	 */
	public static String resultset2json(List<Object[]> list) {
		if (!isNotEmpty(list)) {
			return Constants.EMPTY_JSON;
		}

		String rtn = Constants.EMPTY_JSON;

		try {
			ObjectMapper mapper = new ObjectMapper();
			rtn = mapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			new CxfExtraTechnicalException(e);
		}

		return rtn;
	}

	/**
	 * Convertir une chaîne Json en map
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, String> json2map(String json) {
		Map<String, String> rtn = null;

		if (isNotEmpty(json)) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				rtn = mapper.readValue(json, new TypeReference<HashMap<String, String>>() {
				});
			} catch (Exception e) {
				new CxfExtraTechnicalException(e);
			}
		}

		return rtn;
	}

	/**
	 * Convert JSON to map.
	 * 
	 * @param json
	 * @return Map<String, String>
	 */
	public static Map<String, String> json2mapQuietly(String json) {
		Map<String, String> rtn = null;

		if (isNotEmpty(json)) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				rtn = mapper.readValue(json, new TypeReference<HashMap<String, String>>() {
				});
			} catch (Exception e) {
				LOGGER.error("Parsing error", e);
			}
		}

		return rtn;
	}

	/**
	 * Classe statique
	 */
	private JSONUtils() {
	}
}
