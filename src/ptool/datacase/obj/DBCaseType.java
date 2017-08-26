package ptool.datacase.obj;

public class DBCaseType {
	private String type_id;
	private String type_name;
	private String icon_name;
	private String mark;
	private String is_lock;
	private String enhance_sql;
	private String last_md_date;
	private String has_synced;
	private int sequence;
	
	public String getTypeId() {
		return type_id;
	}
	public void setTypeId(String type_id) {
		this.type_id = type_id;
	}
	public String getTypeName() {
		return type_name;
	}
	public void setTypeName(String type_name) {
		this.type_name = type_name;
	}
	public String getIconName() {
		return icon_name;
	}
	public void setIconName(String icon_name) {
		this.icon_name = icon_name;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getIsLock() {
		return is_lock;
	}
	public void setIsLock(String is_lock) {
		this.is_lock = is_lock;
	}
	public String getEnhanceSql() {
		return enhance_sql;
	}
	public void setEnhanceSql(String enhance_sql) {
		this.enhance_sql = enhance_sql;
	}
	public String getLastMdDate() {
		return last_md_date;
	}
	public void setLastMdDate(String last_md_date) {
		this.last_md_date = last_md_date;
	}
	public String getHasSynced() {
		return has_synced;
	}
	public void setHasSynced(String has_synced) {
		this.has_synced = has_synced;
	}
 
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
