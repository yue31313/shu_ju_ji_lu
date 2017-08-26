package ptool.datacase.obj;

public class DBCaseValues {
	private String  value_id;
	private String  type_id;
	private String  char_id;
	private String  value_name;
	private String last_md_date;
	private String has_synced;
	
	public String getValueId() {
		return value_id;
	}
	public void setValueId(String value_id) {
		this.value_id = value_id;
	}
	public String getTypeId() {
		return type_id;
	}
	public void setTypeId(String type_id) {
		this.type_id = type_id;
	}
	public String getCharId() {
		return char_id;
	}
	public void setCharId(String char_id) {
		this.char_id = char_id;
	}
	public String getValueName() {
		return value_name;
	}
	public void setValueName(String value_name) {
		this.value_name = value_name;
	}
	public String getHasSynced() {
		return has_synced;
	}

	public void setHasSynced(String has_synced) {
		this.has_synced = has_synced;
	}

 
	public String getLastMdDate() {
		return last_md_date;
	}

	public void setLastMdDate(String last_md_date) {
		this.last_md_date = last_md_date;
	}


}
