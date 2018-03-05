package bean.kitchenmanage.mymsg;

public class C2SMsg {
	/**
	 * 点餐客户呼叫的内容
	 */
	private String content;// 内容
	/**
	 * 点餐客户呼叫产生时间
	 */
	private String datetime;// 时间
	/**
	 * 点餐客户所属桌位号
	 */
	private String desknum;// 桌位
	/**
	 * 点餐客户所属房间号
	 */
	private String roomnum;// 房间号
	/**
	 * 点餐客户呼叫的内容
	 */
	private String mac;
	/**
	 * 点餐客户呼叫的内容
	 */
	private String orderid;

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getDesknum() {
		return desknum;
	}

	public void setDesknum(String desknum) {
		this.desknum = desknum;
	}

	public String getRoomnum() {
		return roomnum;
	}

	public void setRoomnum(String roomnum) {
		this.roomnum = roomnum;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

}
