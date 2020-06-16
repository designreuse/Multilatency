package com.srai.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.srai.util.FileUtil;
import com.srai.util.ValidationUtil;

@Service
public class FileReadService {

	@Autowired
	private JdbcTemplateService jdbcService;
	private ArrayList<HashMap<String, Object>> list;
	private StringBuffer errorRecord;

	public FileReadService() {
		list = new ArrayList();
		errorRecord = new StringBuffer();
	}

	public void start(String tableName, String fileName) throws Exception {
		loadDataFromCSV(tableName, fileName);
	}

	public void loadDataFromCSV(String tableName, String fileName) throws Exception {
		String data = loadData(fileName);
		String tableColumndata = jdbcService.loadData(tableName);
		String[] splitLine = data.split("\n");
		for (int i = 0; i < splitLine.length; i++) {
			String[] splitComma = splitLine[i].split(",");
			if (ValidationUtil.isNumber(splitComma[0]) && ValidationUtil.isCharacter(splitComma[1])
					&& ValidationUtil.isCharacter(splitComma[2]) && ValidationUtil.isNumber(splitComma[3])
					&& ValidationUtil.isCharacter(splitComma[4]) && ValidationUtil.isAddress(splitComma[5]))
				list.add(getEmployee(tableColumndata, splitComma));
			else
				errorRecord.append(getEmployee(tableColumndata, splitComma));
		}
		jdbcService.uploadData(tableName,list);
		FileUtil.fileWrite(errorRecord.toString(), "error.log");
	}

	private String loadData(String fileName) throws Exception {
		return FileUtil.readFile(fileName);
	}

	private HashMap<String, Object> getEmployee(String tableColumndata, String[] data) throws Exception {
		HashMap<String, Object> map = new HashMap<>();
		String[] splitLine = tableColumndata.split(",");
		System.out.println("length of the table column :: {} " + splitLine.length);
		if (splitLine.length != data.length) {
			throw new Exception("table column not matched with the file column");
		}
		
		map.put(splitLine[0], Integer.parseInt(data[0].trim()));
		map.put(splitLine[1], data[1]);
		map.put(splitLine[2], data[2]);
		map.put(splitLine[3], Integer.parseInt(data[3]));
		map.put(splitLine[4], data[4]);
		map.put(splitLine[5], data[5]);
		return map;
	}

}
