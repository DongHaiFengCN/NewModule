package bean.kitchenmanage.kitchen;

/**
 * @ClassName: KitchenMsg
 * @Description: 通知厨房的消息类
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class KitchenMsg
{
	/**
	 * 通知发生时间
	 */
    private String datetime;
	/**
	 * 房间名称
	 */
    private String roomname;
	/**
	 * 桌位名称
	 */
    private String tablename;
	/**
	 * 菜品名称
	 */
    private String dishesName;
	/**
	 * 所选口味
	 */
    private String dishesTaste;
	/**
	 * 数量
	 */
    private String dishesNums;
	/**
	 * 状态，1,排队中；2、制作中；3、制作完毕，等待传菜，4、已传菜
	 */
    private int state;

	public KitchenMsg() {
	}

	public KitchenMsg(String datetime, String roomname, String tablename, String dishesName, String dishesTaste, String dishesNums, int state) {
		this.datetime = datetime;
		this.roomname = roomname;
		this.tablename = tablename;
		this.dishesName = dishesName;
		this.dishesTaste = dishesTaste;
		this.dishesNums = dishesNums;
		this.state = state;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getRoomname() {
		return roomname;
	}

	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getDishesName() {
		return dishesName;
	}

	public void setDishesName(String dishesName) {
		this.dishesName = dishesName;
	}

	public String getDishesTaste() {
		return dishesTaste;
	}

	public void setDishesTaste(String dishesTaste) {
		this.dishesTaste = dishesTaste;
	}

	public String getDishesNums() {
		return dishesNums;
	}

	public void setDishesNums(String dishesNums) {
		this.dishesNums = dishesNums;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
