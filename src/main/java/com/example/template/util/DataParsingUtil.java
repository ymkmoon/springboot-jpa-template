package com.example.template.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.experimental.UtilityClass;

/**
 * DataParsingUtil
 * - 객체 변환 유틸리티
 *
 * @author myungki you
 * @created 2025/08/06
 */
@UtilityClass
public final class DataParsingUtil {

	public static Map<String, Object> toMap(JSONObject jsonobj)  {
		Map<String, Object> map = new HashMap<>();
		@SuppressWarnings("unchecked")
		Iterator<String> keys = jsonobj.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Object value = jsonobj.get(key);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}   
			map.put(key, value);
		}   return map;
	}

	public static List<Object> toList(JSONArray array) {
		List<Object> list = new ArrayList<>();
		for(int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}
			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}   return list;
	}
	
	public static String paramMapToString(Map<String, String[]> paramMap) {
	    return paramMap.entrySet().stream()
	            .map((Map.Entry<String, String[]> entry) -> 
	                String.format("%s -> (%s)", entry.getKey(), String.join(",", entry.getValue()))
	            )
	            .collect(Collectors.joining(", "));
	}
}
