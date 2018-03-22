package bean.kitchenmanage.mymsg;


public class S2CMsg {
 
	private String msgRoomName;//房间名
	private String msgTableName;//桌位名
	private String msgType;//呼叫类型
	private String msgStartTime;//呼叫时间
	private String msgWaiter;//服务员
	
	private String msgCkTime;//应答时间
	private String msgEndTime;//处理结束时间
	private String msgTimes;//应答用时
	
	private String isvalide;//是否有效
	
 
	public String getMsgRoomName() {
		return msgRoomName;
	}


	public void setMsgRoomName(String msgRoomName) {
		this.msgRoomName = msgRoomName;
	}


	public String getMsgTableName() {
		return msgTableName;
	}


	public void setMsgTableName(String msgTableName) {
		this.msgTableName = msgTableName;
	}


	public String getMsgType() {
		return msgType;
	}


	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}


	public String getMsgStartTime() {
		return msgStartTime;
	}


	public void setMsgStartTime(String msgStartTime) {
		this.msgStartTime = msgStartTime;
	}


	public String getMsgWaiter() {
		return msgWaiter;
	}


	public void setMsgWaiter(String msgWaiter) {
		this.msgWaiter = msgWaiter;
	}


	public String getMsgCkTime() {
		return msgCkTime;
	}


	public void setMsgCkTime(String msgCkTime) {
		this.msgCkTime = msgCkTime;
	}


	public String getMsgEndTime() {
		return msgEndTime;
	}


	public void setMsgEndTime(String msgEndTime) {
		this.msgEndTime = msgEndTime;
	}


	public String getMsgTimes() {
		return msgTimes;
	}


	public void setMsgTimes(String msgTimes) {
		this.msgTimes = msgTimes;
	}


	public String isIsvalide() {
		return isvalide;
	}


	public void setIsvalide(String isvalide) {
		this.isvalide = isvalide;
	}


	public S2CMsg() {
		super();
	}


	public S2CMsg(String msgRoomName, String msgTableName, String msgType, String msgStartTime, String msgWaiter,
				  String msgCkTime, String msgEndTime, String msgTimes, String isvalide) {
		super();
		this.msgRoomName = msgRoomName;
		this.msgTableName = msgTableName;
		this.msgType = msgType;
		this.msgStartTime = msgStartTime;
		this.msgWaiter = msgWaiter;
		this.msgCkTime = msgCkTime;
		this.msgEndTime = msgEndTime;
		this.msgTimes = msgTimes;
		this.isvalide = isvalide;
	}
	
	 
 
	

}
