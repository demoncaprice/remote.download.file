package org.caprice.remote.download.file.torrent.pojo;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class BenValue {

	private final Object value;
	
	public BenValue(Object value) {
		this.value = value;
	}
	
	public String getString() {
		if(this.value instanceof String) {
			return (String) this.value;
		} else if(this.value instanceof byte[]) {
			try {
				return new String((byte[]) this.value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public Map<String, BenValue> getMap() {
		if(this.value instanceof Map) {
			return (Map<String, BenValue>) this.value;
		} else {
			return null;
		}
	}
	
	public List<BenValue> getList() {
		if(this.value instanceof List) {
			return (List<BenValue>) this.value;
		} else {
			return null;
		}
	}
	
}
