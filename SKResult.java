package utils;


/**
 * 
 * @author zhouy
 *
 */
public class SKResult {

	// 状态
	private boolean result;
	// 信息
	private String respMsg;
	// 对象
	private Object obj;
	
	public SKResult(boolean result, String respMsg) {
		super();
		this.result = result;
		this.respMsg = respMsg;
	}
	
	public SKResult(boolean result, String respMsg, Object obj) {
		super();
		this.result = result;
		this.respMsg = respMsg;
		this.obj = obj;
	}
	
	public boolean isResult() {
		return result;
	}


	public void setResult(boolean result) {
		this.result = result;
	}


	public String getRespMsg() {
		return respMsg;
	}


	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}


	public Object getObj() {
		return obj;
	}


	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public  String toString() {
		return "SKResult [result=" + result + ", respMsg=" + respMsg + ", obj="
				+ obj + "]";
	}
	
	
}


