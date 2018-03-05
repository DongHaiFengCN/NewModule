package bean.kitchenmanage.advertisement;

/**
 * @ClassName: adpic
 * @Description：广告图片
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class adpic 
{
	/**
	 * 位置 1代表加载界面，2：代表点餐界面
	 */
    private int position;
	/**
	 * 图片的base64
	 */
    private String picbase64str;

	public adpic() {
	}

	/**
	 *
	 */

	public adpic(int position, String picbase64str) {
		this.position = position;
		this.picbase64str = picbase64str;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getPicbase64str() {
		return picbase64str;
	}

	public void setPicbase64str(String picbase64str) {
		this.picbase64str = picbase64str;
	}
}
