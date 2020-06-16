package com.srai.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.srai.configuration.ConnectionCreation;
import com.srai.model.ConnectionEstablish;
import com.srai.model.DataSource;
import com.srai.model.DriverDetail;
import com.srai.model.MasterTenant;
import com.srai.model.User;
import com.srai.model.repository.ConnectionRepository;
import com.srai.model.repository.DataSourceRepository;
import com.srai.model.repository.DriverStorage;
import com.srai.model.repository.MasterTenantRepository;
import com.srai.model.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MasterTenantRepository repo;

	@Autowired
	private ConnectionCreation connection;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private DriverStorage storage;

	@Autowired
	private ConnectionRepository connectionRepo;

	@Autowired
	private DataSourceRepository dataRepo;

	@Autowired
	private JdbcTemplateService jdbc;

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public User save(User user) throws MalformedURLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		user.setPassword(bcrypt.encode(user.getPassword()));
		user.setUserName(user.getUserName());
		user.setTenant(user.getTenant());
		User justSavedUser = userRepository.save(user);
		LOG.info("User:" + justSavedUser.getUserName() + " saved.");
		return justSavedUser;
	}

	@Override
	public User findByUsernameAndTenantname(String username, String tenant) throws Exception {
		User user = userRepository.findByUserNameAndTenantname(username, tenant);
		if (user == null) {
			throw new Exception(
					String.format("Username not found for domain, " + "username=%s, tenant=%s", username, tenant));
		}
		LOG.info("Found user with username:" + user.getUserName() + " from tenant:" + user.getTenant());
		return user;
	}

	@Override
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public ResponseEntity<Object> addDriver(MultipartFile file, MultipartFile driver) throws Exception {
		String fileName = fileStorageService.storeFile(file);
		String driverFile = fileStorageService.storeFile(driver);
		DriverDetail detail = new DriverDetail();
		detail.setFilePath(fileName);
		detail.setDriverJarPath(driverFile);
		DriverDetail saveData = storage.save(detail);
		Map<String, Object> fileData = readFile(saveData.getFilePath());
		DataSource source = new DataSource();
		for (Map.Entry<String, Object> entry : fileData.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("databasename"))
				source.setDatabaseName("" + entry.getValue());
			if (entry.getKey().equalsIgnoreCase("databaseurl"))
				source.setDatabaseUrl("" + entry.getValue());
			if (entry.getKey().equalsIgnoreCase("username"))
				source.setUserName("" + entry.getValue());
			if (entry.getKey().equalsIgnoreCase("password"))
				source.setPassword("" + entry.getValue());
		}
		dataRepo.save(source);
		return ResponseEntity.ok(saveData);
	}

	private Map<String, Object> readFile(String filePath) {
		Map<String, Object> hashMap = new HashMap<>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split("=");
				System.out.println("line value ::::::::::::::    " + split[0]);
				hashMap.put(split[0], split[1]);
			}
			return hashMap;
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
		return null;
	}

	@Override
	public ResponseEntity<Object> dataSourceDetail(Long datasourceId, Integer userId, String databaseName)
			throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException,
			SQLException {
		DataSource source = dataRepo.findById(datasourceId).orElse(null);
		User user = userRepository.findById(userId).orElse(null);
		ConnectionEstablish establish = new ConnectionEstablish();
		establish.setDatabaseName(databaseName);
		establish.setUserId(userId);
		establish.setDataSourceId(datasourceId);
		ConnectionEstablish saveData = connectionRepo.save(establish);
		if (Objects.nonNull(saveData)) {
			MasterTenant tenant = new MasterTenant();
			tenant.setPassword(source.getPassword());
			tenant.setTenantId(user.getTenant());
			tenant.setUsername(source.getUserName());
			tenant.setDatabaseName(user.getTenant() + "_" + databaseName);
			tenant.setUrl(source.getDatabaseUrl() + "/" + user.getTenant() + "_" + databaseName
					+ "?autoReconnect=true&useSSL=false");
			repo.save(tenant);
			connection.connection(user.getTenant() + "_" + databaseName);
		}
		return ResponseEntity.ok(establish);
	}

}
