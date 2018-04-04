package bean.kitchenmanage.qrcode;

/**
 * Class description ：收款二维码设置信息
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */
public class Qrcode {
	/**
	 * docId
	 */
	private String id;
	/**
	 * 公司唯一身份id,用于数据同步,做为唯一管道符
	 */
	private String channelId;
	/**
	 * 类名，用于数据库查询类过滤
	 */
	private String className = "Qrcode";
	/**
	 * 数据分两大类，一个是基础数据 BaseData，一个业务实时数据 UserData
	 */
	private String dataType = "BaseData";
	/**
	 *淘宝二维码内容字符串
	 */
	private String wxUrl;
	/**
	 *是否淘宝打印二维码到小票
	 */
	private boolean wxPrintFlag;
	/**
	 *支付宝二维码内容字符串
	 */
	private String zfbUrl;
	/**
	 *是否支付宝打印二维码到小票
	 */
	private boolean zfbPrintFlag;
	/**
	 *打印份数
	 */
	private int numbers;

	public Qrcode() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getWxUrl() {
		return wxUrl;
	}

	public void setWxUrl(String wxUrl) {
		this.wxUrl = wxUrl;
	}

	public boolean isWxPrintFlag() {
		return wxPrintFlag;
	}

	public void setWxPrintFlag(boolean wxPrintFlag) {
		this.wxPrintFlag = wxPrintFlag;
	}

	public String getZfbUrl() {
		return zfbUrl;
	}

	public void setZfbUrl(String zfbUrl) {
		this.zfbUrl = zfbUrl;
	}

	public boolean isZfbPrintFlag() {
		return zfbPrintFlag;
	}

	public void setZfbPrintFlag(boolean zfbPrintFlag) {
		this.zfbPrintFlag = zfbPrintFlag;
	}

	public int getNumbers() {
		return numbers;
	}

	public void setNumbers(int numbers) {
		this.numbers = numbers;
	}
}
