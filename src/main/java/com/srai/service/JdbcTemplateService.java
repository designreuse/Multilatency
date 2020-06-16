package com.srai.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.srai.tenant.TenantContext;
import com.srai.tenant.hibernate.MultiTenantConnectionProviderImpl;

import java.util.Objects;

@Service
public class JdbcTemplateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTemplateService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MultiTenantConnectionProviderImpl multi;

	private Connection conn = null;

	public void createTable(String tablename, ArrayList<Map<String, String>> arrayList) throws SQLException {
		conn = getConnection();
		Statement stmt = conn.createStatement();
		StringBuffer buffer = new StringBuffer();
		for (Map<String, String> map : arrayList) {
			buffer.append(",");
			for (Entry<String, String> mapEntry : map.entrySet()) {
				String key = mapEntry.getKey();
				String value = mapEntry.getValue();
				if (key.equalsIgnoreCase("columnname")) {
					buffer.append(value + " ");
				}
				if (key.equalsIgnoreCase("datatype")) {
					buffer.append(value + " ");
				}
				if (key.equalsIgnoreCase("length")) {
					buffer.append("(" + value + ")" + " ");
				}
			}
		}
		String createTableSql = "create table if not exists " + tablename + "("
				+ "id int(100) primary key auto_increment" + "" + buffer.toString() + ")";
		stmt.executeUpdate(createTableSql);
	}

	public void addColumn(String tableName, ArrayList<Map<String, String>> arrayList) throws SQLException {
		conn = getConnection();
		Statement stmt = conn.createStatement();
		for (Map<String, String> map : arrayList) {
			StringBuffer buffer = new StringBuffer();
			for (Entry<String, String> mapEntry : map.entrySet()) {
				String key = mapEntry.getKey();
				String value = mapEntry.getValue();
				LOGGER.info("key ::: {} , values ::: {} ", key, value);
				if (key.equalsIgnoreCase("columnname")) {
					buffer.append(mapEntry.getValue() + " ");
				}
				if (key.equalsIgnoreCase("datatype")) {
					buffer.append(mapEntry.getValue());
				}
				if (key.equalsIgnoreCase("length")) {
					buffer.append("(" + mapEntry.getValue() + ")");
				}
			}
			String query = "alter table " + tableName + " add column " + buffer.toString();
			stmt.executeUpdate(query);
		}
	}

	public ArrayList<HashMap<String, Object>> fetchTableName() throws SQLException {
		ArrayList<HashMap<String, Object>> listOfTables = new ArrayList<>();
		conn = getConnection();
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet rs = dbm.getTables(null, null, null, new String[] { "TABLE" });
		while (rs.next()) {
			HashMap<String, Object> map = new HashMap<>();
			String tableName = rs.getString("TABLE_NAME");
			Map<String, String> column = fetchCoulumns(conn, tableName);
			if (Objects.isNull(column)) {
				continue;
			}
			map.put("tablename", tableName);
			map.put("column", column);
			listOfTables.add(map);
		}
		return listOfTables;
	}

	public String loadData(String tableName) throws SQLException {
		conn = getConnection();
		StringBuffer buffer = new StringBuffer();
		Statement statement = conn.createStatement();
		ResultSet results = statement.executeQuery("SELECT * FROM " + tableName);
		ResultSetMetaData metadata = results.getMetaData();
		int columnCount = metadata.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = metadata.getColumnName(i);
			if (columnName.equalsIgnoreCase("id")) {
			} else {
				buffer.append(columnName + ",");
			}
		}
		return buffer.toString();
	}

	private Map<String, String> fetchCoulumns(Connection conn, String tablename) {
		LOGGER.info("tablename :: {} ", tablename);
		Map<String, String> map = new HashMap<>();
		Statement statement;
		try {
			statement = conn.createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + tablename);
			ResultSetMetaData metadata = results.getMetaData();
			int columnCount = metadata.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metadata.getColumnName(i);
				String columnType = metadata.getColumnTypeName(i);
				map.put(columnName, columnType);
			}
		} catch (SQLException e) {
			return null;
		}

		return map;
	}

	public void uploadData(String tableName, ArrayList<HashMap<String, Object>> list) throws SQLException {
		conn = getConnection();
		String columnValue = getColumnData(list, tableName);
		int totalNoOfColumns = count(tableName);
		String INSERT_USERS_SQL = "INSERT INTO " + tableName + "" + columnValue + ";";
		PreparedStatement pstmt = conn.prepareStatement(INSERT_USERS_SQL);
		for (Map<String, Object> map : list) {
			int no = 1;
			for (Entry<String, Object> mapEntry : map.entrySet()) {
				String key = mapEntry.getKey();
				String value = String.valueOf(mapEntry.getValue());
				if (no > totalNoOfColumns) {
					break;
				}
				pstmt.setObject(no, value);
				no++;
			}
			pstmt.addBatch();
		}
		pstmt.executeBatch();
	}

	public ArrayList<HashMap<String, Object>> fetchTableRecord(String tableName) throws SQLException {
		conn = getConnection();
		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM " + tableName);
		ResultSetMetaData metadata = results.getMetaData();
		int columnCount = metadata.getColumnCount();
		while (results.next()) {
			HashMap<String, Object> hashMap = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				hashMap.put(metadata.getColumnName(i), results.getObject(i));
			}
			list.add(hashMap);
		}
		return list;
	}

	private int count(String tableName) throws SQLException {
		int count = 1;
		conn = getConnection();
		Statement statement = conn.createStatement();
		ResultSet results = statement.executeQuery("SELECT * FROM " + tableName);
		ResultSetMetaData metadata = results.getMetaData();
		int columnCount = metadata.getColumnCount();
		while (count <= columnCount) {
			count++;
		}
		return count;
	}

	private String getColumnData(ArrayList<HashMap<String, Object>> list, String tableName) throws SQLException {
		conn = getConnection();
		int count = 0;
		StringBuffer buffer = new StringBuffer();
		ArrayList<String> listArray = new ArrayList<>();
		for (Map<String, Object> map : list) {
			for (Entry<String, Object> mapEntry : map.entrySet()) {
				String key = mapEntry.getKey();
				listArray.add(key);
			}
			break;
		}
		buffer.append("(");
		for (int j = 0; j < listArray.size(); j++) {
			if (j < listArray.size() - 1) {
				buffer.append(listArray.get(j) + ",");
			} else {
				buffer.append(listArray.get(j));
			}
		}
		buffer.append(")").append("values ");
		buffer.append("(");
		while (count < listArray.size()) {
			if (count < listArray.size() - 1) {
				buffer.append("?").append(",");
			} else {
				buffer.append("?");
			}
			count++;
		}
		buffer.append(")");
		return buffer.toString();
	}

	private Connection getConnection() throws SQLException {
		LOGGER.info("connection value :: {} ", TenantContext.getCurrentTenant());
		return DataSourceBuilder.create().username("root").password("root")
				.url("jdbc:mysql://localhost:3306/" + "" + TenantContext.getCurrentTenant()
						+ "+?useSSL=false&useUnicode=true&characterEncoding=UTF-8")
				.driverClassName("com.mysql.cj.jdbc.Driver").build().getConnection();
	}

}
