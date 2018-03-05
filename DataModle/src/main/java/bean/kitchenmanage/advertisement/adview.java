package bean.kitchenmanage.advertisement;

public class adview 
{
private String notename;
private String noteadress;
private String roomname;
private String tablenum;
private String datetime;
private String adid;
public adview(String notename, String noteadress, String roomname,
		String tablenum, String datetime, String adid) {
	super();
	this.notename = notename;
	this.noteadress = noteadress;
	this.roomname = roomname;
	this.tablenum = tablenum;
	this.datetime = datetime;
	this.adid = adid;
}
public String getNotename() {
	return notename;
}
public void setNotename(String notename) {
	this.notename = notename;
}
public String getNoteadress() {
	return noteadress;
}
public void setNoteadress(String noteadress) {
	this.noteadress = noteadress;
}
public String getRoomname() {
	return roomname;
}
public void setRoomname(String roomname) {
	this.roomname = roomname;
}
public String getTablenum() {
	return tablenum;
}
public void setTablenum(String tablenum) {
	this.tablenum = tablenum;
}
public String getDatetime() {
	return datetime;
}
public void setDatetime(String datetime) {
	this.datetime = datetime;
}
public String getAdid() {
	return adid;
}
public void setAdid(String adid) {
	this.adid = adid;
}


}
