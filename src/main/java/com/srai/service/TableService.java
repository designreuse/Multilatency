package com.srai.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.srai.model.TableDto;
import com.srai.model.TableUpload;

@Service
public class TableService {

	@Autowired
	private JdbcTemplateService jdbcService;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private FileReadService fileService;

	public ResponseEntity<Object> CreateTable(String tableName, TableDto tableDto) throws SQLException {
		jdbcService.createTable(tableName, tableDto.getTableData());
		Map<String, Object> value = new HashMap<>();
		value.put("table name ", tableName);
		value.put("column", tableDto.getTableData());
		return ResponseEntity.ok(value);

	}

	public ResponseEntity<Object> fetchTableList() throws SQLException {
		return ResponseEntity.ok(jdbcService.fetchTableName());
	}

	public ResponseEntity<Object> uploadFile(MultipartFile file, String tablename) throws Exception {
		String fileName = fileStorageService.storeFile(file);
		fileService.start(tablename,fileName);
		return null;
	}

	public ResponseEntity<Object> updateColumn(String tableName, TableDto tableDto) throws SQLException {
		jdbcService.addColumn(tableName, tableDto.getTableData());
		Map<String, Object> value = new HashMap<>();
		value.put("table name ", tableName);
		value.put("column", tableDto.getTableData());
		return ResponseEntity.ok(value);
	}

	public ResponseEntity<Object> fetchTableData(String tableName) throws SQLException {
		return ResponseEntity.ok(jdbcService.fetchTableRecord(tableName));
	}

}
