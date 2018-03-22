package bean.kitchenmanage.mymsg;

import java.util.Date;

public class Queue {

	private String mac;// 手机mac地址
	private Date datetime;// 记录生成时间
	private Integer peoplenum; // 人数
	private Integer ordernum;// 排队序号
	private Integer waitingnum;// 前面等待人数
	private Boolean valid;// 记录是否有效
	private String phonenum;// 电话号码
	private String weixinnum;// 微信号

	public Queue() {

	}

	public Queue(String mac, Date datetime, Integer peoplenum,
			Integer ordernum, Integer waitingnum, Boolean valid,
			String phonenum, String weixinnum) {
		this.mac = mac;
		this.datetime = datetime;
		this.peoplenum = peoplenum;
		this.ordernum = ordernum;
		this.waitingnum = waitingnum;
		this.valid = valid;
		this.phonenum = phonenum;
		this.weixinnum = weixinnum;

	}

	public void SetMac(String mac) {
		this.mac = mac;
	}

	public String GetMac() {
		return this.mac;
	}

	public void SetDateTime(Date datetime) {
		this.datetime = datetime;
	}

	public Date GetDateTime() {
		return this.datetime;
	}

	public void SetPeopleNum(int peoplenum) {
		this.peoplenum = peoplenum;
	}

	public Integer GetPeopleNum() {
		return this.peoplenum;
	}

	public void SetOrderNum(int ordernum) {
		this.ordernum = ordernum;
	}

	public Integer GetOrderNum() {
		return this.ordernum;
	}

	public void SetWaitingNum(int waitingnum) {
		this.waitingnum = waitingnum;
	}

	public Integer GetWaitingNum() {
		return this.waitingnum;
	}

	public void SetValid(Boolean valid) {
		this.valid = valid;
	}

	public Boolean GetValid() {
		return this.valid;
	}

	public void SetPhoneNum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String GetPhoneNum() {
		return this.phonenum;
	}

	public void SetWeiXinNum(String weixinnum) {
		this.weixinnum = weixinnum;
	}

	public String GetWeiXinNum() {
		return this.weixinnum;
	}

}
