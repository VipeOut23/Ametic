package de.jroeger.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class represents a package of the protocol
 * can be converted to an JSON Object
 * 
 * header - the operation category 
 * content - the specific operation
 * @author jonas
 *
 */
public class ProtocolContainer {
	/*
	 * Protocol constants for the header (topic)
	 */
	public static final int SIGNIN	= 0x1;
	public static final int DC		= 0x2;
	public static final int JOIN	= 0x3;
	public static final int START	= 0x4;
	public static final int WAIT	= 0x5;
	public static final int DO		= 0x6;
	public static final int END		= 0x7;
	public static final int PING	= 0x8;
	public static final int QUICK	= 0x9;
	
	public static final int ERROR	= 0xFF;
	
	/**
	 * saves header information
	 */
	private int header;
	
	/**
	 * saves content information
	 */
	private String content;

	/**
	 * used to construct the Container using an existing JSON string
	 * @param jsonString a jsonString to build from
	 */
	public ProtocolContainer(String jsonString) {
		JSONObject obj;
		try {
			obj = new JSONObject(jsonString);
			this.header = obj.getInt("header");
			this.content = obj.getString("content");
		} catch (JSONException e) {
			this.header 	= ERROR;
			this.content	= "Couldn't resolve package";
		}
		
		//TODO - Validate
		
	}
	
	/**
	 * used for a click
	 * @param header must be the click operation
	 * @param content must contain the information of the clicked field
	 */
	public ProtocolContainer(int header, int content) {
		modify(header, content);
	}
	
	public ProtocolContainer(int header, String content) {
		this.header = header;
		this.content = content;
	}
	
	/**
	 * used to modify attribs with an int as the content
	 * @param header the header
	 * @param content the content as int
	 */
	public void modify(int header, int content) {
		switch(header) {
		case DO:
			this.header = header;
			this.content = String.valueOf(content);
			break;
			
		default:	
		}
	}
	
	/**
	 * used to modify attribs
	 * @param header the header
	 * @param content the content
	 */
	public void modify(int header, String content) {
		this.header = header;
		this.content = content;
	}
	
	/**
	 * converts the attributes to a JSONObject
	 * 
	 * if Exception occours first time -> create Error package
	 * if Exception occours second time -> return null
	 * @return JSONObject
	 */
	public JSONObject toJSONObject() { return toJSONObject(true); }
	private JSONObject toJSONObject(boolean handle) {
		JSONObject out = new JSONObject();
		
		try  {
			//Process header
			if(header == 0) {
				out.put("header", -1);
			}else {
				out.put("header", this.header);
			}
			
			//Process content
			out.put("content", this.content);
		} catch (JSONException e) {
			this.header 	= ERROR;
			this.content	= "Couldn't resolve package";
			
			if(handle)
				return toJSONObject(false);
			else 
				return null; //Worst case
		}
		
		return out;
	}

	/**
	 * used to test if the attribs are valid
	 * @return valid?
	 */
	public boolean isValid() {
		boolean valid = true;
		
		//TODO - improve
		
		if(this.header == 0 || this.header == -1) valid = false;
		
		if(this.content == null) valid = false;
		
		return valid;
	}
	
	public int getHeader() {
		return header;
	}

	public String getContent() {
		return content;
	}
	
	
}
