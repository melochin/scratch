package scratch.support;

public class Result<T> {
	
	private T data;
	
	private String error;

	public Result(T data) {
		this.data = data;
	}
	
	public Result(String error) {
		this.error = error;
	}
	
	public Result(T data, String error) {
		this.data = data;
		this.error = error;
	}
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
