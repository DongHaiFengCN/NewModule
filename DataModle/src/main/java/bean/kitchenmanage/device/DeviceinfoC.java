package bean.kitchenmanage.device;
/**
 * @ClassName: Deviceinfo
 * @Description：设备信息
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class DeviceinfoC {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className;
	/**
	 * docId
	 */
	private String _id;
	/**
	 * 设备的wifi mac
	 */
    private String mac;
	/**
	 * 硬件信息
	 * Build.MANUFACTURER + Build.MODEL
	 */
    private String hardInfo;
	/**
	 * 系统信息
	 */
    private String sysInfo;
	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "userData";

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public DeviceinfoC() {
	}

	public DeviceinfoC(String company_id) {
		this.channelId = company_id;
		this.className="DeviceinfoC";
	}



	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}


	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getHardInfo() {
		return hardInfo;
	}

	public void setHardInfo(String hardInfo) {
		this.hardInfo = hardInfo;
	}

	public String getSysInfo() {
		return sysInfo;
	}

	public void setSysInfo(String sysInfo) {
		this.sysInfo = sysInfo;
	}
}
