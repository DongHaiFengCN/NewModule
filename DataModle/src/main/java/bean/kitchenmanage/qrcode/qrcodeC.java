package bean.kitchenmanage.qrcode;

/**
 * Class description ：活动关联的菜品
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class qrcodeC {

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
	 *淘宝二维码内容字符串
	 */
	private String wxUrl;
	/**
	 *支付宝二维码内容字符串
	 */
	private String zfbUrl;

	/**
	 *打印份数
	 */
	private int nums;
	/**
	 *是否淘宝打印二维码到小票
	 */
	private boolean wxPrintFlag;

	/**
	 *是否支付宝打印二维码到小票
	 */
	private boolean zfbPrintFlag;
	/**
	 * 是否接收微信小程序订单并打印
	 */
	private boolean wxReceiveFlag;
	/**
	 * 数据分两大类，一个是基础数据 basic，一个业务实时数据 business
	 */
	private String dataType = "BaseData";//basi,代表basic数据,busi,代表业务数据

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public qrcodeC()
	{


	}

	public qrcodeC(String company_id) {
		this.channelId = company_id;
		this.className="qrcodeC";
	}


	public boolean isWxPrintFlag() {
		return wxPrintFlag;
	}

	public void setWxPrintFlag(boolean wxPrintFlag) {
		this.wxPrintFlag = wxPrintFlag;
	}

	public boolean isWxReceiveFlag() {
		return wxReceiveFlag;
	}

	public void setWxReceiveFlag(boolean wxReceiveFlag) {
		this.wxReceiveFlag = wxReceiveFlag;
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

	public String getWxUrl() {
		return wxUrl;
	}

	public void setWxUrl(String wxUrl) {
		this.wxUrl = wxUrl;
	}

	public String getZfbUrl() {
		return zfbUrl;
	}

	public void setZfbUrl(String zfbUrl) {
		this.zfbUrl = zfbUrl;
	}

	public int getNums() {
		return nums;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}


	public boolean isZfbPrintFlag() {
		return zfbPrintFlag;
	}

	public void setZfbPrintFlag(boolean zfbPrintFlag) {
		this.zfbPrintFlag = zfbPrintFlag;
	}
}
