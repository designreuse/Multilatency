
package com.srai.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srai.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select p from User p where p.userName = :username and p.tenant = :tenant")
	User findByUserNameAndTenantname(@Param("username") String username, @Param("tenant") String tenant);

	User findByUserName(String userName);

	Optional<User> findById(Integer userId);
}
