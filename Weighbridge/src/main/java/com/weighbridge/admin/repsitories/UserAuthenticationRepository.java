package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.entities.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Long> {

    boolean existsByUserId(String userId);

    @Query("SELECT ua.roles FROM UserAuthentication ua WHERE ua.userId = :userId")
    Set<RoleMaster> findRolesByUserId(String userId);

    UserAuthentication findByUserId(String userId);
@Query("select u.userPassword from UserAuthentication u where u.userId=:userId")
    String findPasswordByUserId(String userId);

    @Query("SELECT ua FROM UserAuthentication ua LEFT JOIN FETCH ua.roles WHERE ua.userId = :userId")
    UserAuthentication findByUserIdWithRoles(@Param("userId") String userId);
}
