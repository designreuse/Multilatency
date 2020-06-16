
package com.srai.service;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.srai.model.User;

public interface UserService {

    User save(User user) throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException;

    @Query("select p from User p where p.username = :username and p.tenant = :tenant")
    User findByUsernameAndTenantname(@Param("username") String username,
            @Param("tenant") String tenant) throws Exception;

    List<User> findAllUsers();

	ResponseEntity<Object> addDriver(MultipartFile file, MultipartFile driver) throws Exception;

	ResponseEntity<Object> dataSourceDetail(Long datasourceId, Integer userId,String databasename) throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException;
}
