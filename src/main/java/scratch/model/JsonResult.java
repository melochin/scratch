package scratch.model;

import java.util.HashMap;

@SuppressWarnings("serial")
public class JsonResult extends HashMap<String, Object>{
	
	private final static String SUCCESS = "success";
	
	private final static String ERROR = "error";
	
	private final static String VALIDATE = "validate";
	
	public JsonResult setSuccess(boolean success) {
		this.put(SUCCESS, success);
		return this;
	}
	
	public JsonResult setError(String error) {
		this.put(ERROR, error);
		return this;
	}
	
	public JsonResult setValidate(boolean validate) {
		this.put(VALIDATE, validate);
		return this;
	}
	
}
