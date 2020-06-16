package com.srai.controller;


import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.srai.model.TableDto;
import com.srai.model.TableUpload;
import com.srai.service.TableService;

@RestController
public class TableCreationController {

	@Autowired
	private TableService tableService;
	
	@PostMapping("/create")
	public ResponseEntity<Object>CreateTable(@RequestParam String tableName ,@RequestBody TableDto tableDto) throws SQLException{
		return tableService.CreateTable(tableName,tableDto);
		
	}
	
	@GetMapping("/table/list")
	public ResponseEntity<Object>fetchTableList() throws SQLException{
		return tableService.fetchTableList();	
	}
	
	@PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object>uploadFile(@RequestBody MultipartFile file,String tablename) throws Exception{
		return tableService.uploadFile(file,tablename);
	}
	
	@PutMapping("/update/column")
	public ResponseEntity<Object>updateColumn(@RequestParam String tableName ,@RequestBody TableDto tableDto) throws Exception{
		return tableService.updateColumn(tableName,tableDto);
	}
	
	@GetMapping("/table")
	public ResponseEntity<Object>fetchTableData(@RequestParam String tableName) throws SQLException{
		return tableService.fetchTableData(tableName);	
	}
}
