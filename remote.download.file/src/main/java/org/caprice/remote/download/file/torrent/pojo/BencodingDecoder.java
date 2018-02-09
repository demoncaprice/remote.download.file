package org.caprice.remote.download.file.torrent.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BencodingDecoder {

	private final InputStream in;
	
	private int data = 0;
	
	private BencodingDecoder(InputStream in) {
		this.in = in;
	}
	
	private int read() throws IOException {
		return this.in.read();
	}
	
	private BenValue start() throws IOException {
		this.data = this.read();
		return this.decode();
	}
	
	private BenValue decode() throws IOException {
		if(-1 == this.data) {
			return null;
		}
		
		if('d' == this.data) {
			return this.decodeMap();
		} else if('i' == this.data) {
			return this.decodeInteger();
		} else if('l' == this.data) {
			return this.decodeList();
		} else if(this.data >= '0' && this.data <= '9') {
			return this.decodeString();
		} else {
			throw new RuntimeException("不支持的byte【" + this.data + "】");
		}
	}
	
	private BenValue decodeMap() throws IOException {
		Map<String, BenValue> map = new HashMap<String, BenValue>();
		this.data = this.read();
		while('e' != this.data) {
			BenValue key = this.decode();
			
			this.data = this.read();
			if('e' == this.data) {
				break;
			}
			BenValue value = this.decode();
			this.data = this.read();
			if('e' == this.data) {
				break;
			}
			
			map.put(key.getString(), value);
		}
		return new BenValue(map);
	}
	
	private BenValue decodeInteger() throws IOException {
		char[] cs = new char[20];
		int i = 0;
		this.data = this.read();
		while('e' != this.data) {
			if(cs.length < (i + 1)) {
				cs = Arrays.copyOf(cs, cs.length * 2);
			}
			
			cs[i] = (char) this.data;
			
			this.data = this.read();
			if('e' == this.data) {
				break;
			}

			i++;
		}
		
		String str = new String(cs, 0, i);
		return new BenValue(new BigInteger(str));
	}
	
	private BenValue decodeList() throws IOException {
		List<BenValue> list = new ArrayList<BenValue>();
		this.data = this.read();
		while('e' != this.data) {
			BenValue value = this.decode();
			this.data = this.read();
			if('e' == this.data) {
				break;
			}
			list.add(value);
		}
		return new BenValue(list);
	}
	
	private BenValue decodeString() throws IOException {
		char lenChar = (char) this.data;
		int len = lenChar - '0';
		
		this.data = this.read();
		if(':' != this.data) {
			int i = 0;
			char[] cs = new char[5];
			cs[i] = lenChar;
			i++;
			cs[i] = (char) this.data;
			i++;
			while(':' != this.data) {
				if(cs.length < (i + 1)) {
					cs = Arrays.copyOf(cs, cs.length * 2);
				}
				this.data = this.read();
				if(':' == this.data) {
					break;
				}
				cs[i] = (char) this.data;
				i++;
			}
			String str = new String(cs, 0, i);
			len = new BigInteger(str).intValue();
		}
		
		byte[] bytes = new byte[len];
		this.in.read(bytes, 0, len);
		return new BenValue(bytes);
	}
	
	public static BenValue decode(InputStream is) throws IOException {
		return new BencodingDecoder(is).start();
	}
	
	public static void main(String[] args) {
		InputStream is = null;
		try {
			is = new FileInputStream(new File("D:\\tools\\test\\3DMGAME-The.Witcher.3.Game.of.the.Year.Edition.v1.31.GOG-3DM.torrent"));
			BencodingDecoder.decode(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
