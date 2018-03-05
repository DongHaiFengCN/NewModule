package bean.kitchenmanage.advertisement;

import java.util.List;

/**
 * @ClassName: adsrc
 * @Description：广告推送记录
 * @author loongsun
 * @date 2016-01-01 上午1:19:08
 *
 */
public class adsrc
{
	/**
	 * 广告更新的id
	 */
	private String id;
	/**
	 *更新过来的广告图片
	 */
	private List<adpic> adpicobjects;

	public adsrc() {
	}

	public adsrc(String id, List<adpic> adpicobjects) {
		this.id = id;
		this.adpicobjects = adpicobjects;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<adpic> getAdpicobjects() {
		return adpicobjects;
	}

	public void setAdpicobjects(List<adpic> adpicobjects) {
		this.adpicobjects = adpicobjects;
	}


	public void addAutoInfo(adpic _objadpic) 
	{
		if (null == this.adpicobjects)
			this.adpicobjects = new java.util.ArrayList<adpic>();
		this.adpicobjects.add(_objadpic);
	}
	
}
