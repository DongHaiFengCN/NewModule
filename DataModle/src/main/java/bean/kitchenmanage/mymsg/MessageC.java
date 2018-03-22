package bean.kitchenmanage.mymsg;

public class MessageC
{
	private String content;
	private String mac;
	private String time;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String class_name;

	/**
	 *
	 * docId
	 */
	private String _id;

	/**
	 *公司唯一身份id,用于数据同步
	 */
	private String company_id;

	public MessageC() {
	}

	public MessageC(String company_id) {
		this.company_id = company_id;
		this.class_name="MessageC";
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}
}
