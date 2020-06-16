package com.srai.model;

import java.util.ArrayList;
import java.util.Map;

public class TableDto {

	private ArrayList<Map<String, String>> tableData = new ArrayList<>();

	public ArrayList<Map<String, String>> getTableData() {
		return tableData;
	}

	public void setTableData(ArrayList<Map<String, String>> tableData) {
		this.tableData = tableData;
	}

}
