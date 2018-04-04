package bean.kitchenmanage.device;
/**
 * @ClassName: Deviceinfo
 * @Description：在线设备信息
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 */
public class DeviceInfo {
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className="DeviceInfo";
	/**
	 * docId
	 */
	private String id;
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "UserData";
	/**
	 * 设备的wifi mac，用于设备唯一身份确定
	 */
    private String macAddress;
	/**
	 * 硬件信息
	 * Build.MANUFACTURER + Build.MODEL
	 */
    private String hardInfo;
	/**
	 * 系统版本信息
	 */
    private String sysInfo;
	/**
	 * 百度地图坐标，经度
	 */
	private double  longitude;
	/**
	 * 百度地图坐标，纬度
	 */
	private double  latitude;


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public DeviceInfo() {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
