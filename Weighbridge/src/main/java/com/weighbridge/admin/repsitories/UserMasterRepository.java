package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.UserMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, String> {

    Page<UserMaster> findAll(Pageable pageable);
    Page<UserMaster> findAllByUserStatus(Pageable pageable,String userStatus);

    boolean existsByUserEmailIdAndUserIdNotOrUserContactNoAndUserIdNot(String emailId, String userId, String contactNo, String userId1);

    @Query("SELECT um FROM UserMaster um JOIN FETCH um.company JOIN FETCH um.site WHERE um.userId = :userId")
    Optional<UserMaster> findByUserIdWithCompanyAndSite(@Param("userId") String userId);

    @Query("SELECT COUNT(um) FROM UserMaster um WHERE um.userStatus = :active")
    long countByUserStatus(String active);

    @Query("SELECT COUNT(um) > 0 FROM UserMaster um WHERE um.userEmailId = :emailId")
    boolean existsByUserEmailId(@Param("emailId") String emailId);

    long countByCompanyCompanyId(String companyId);

    Optional<UserMaster> findByUserEmailId(String emailId);

    boolean existsByUserEmailIdAndUserIdNot(String emailId, String userId);

    @Query("SELECT um FROM UserMaster um WHERE um.userId = :userId")
    UserMaster findByUserId(@Param("userId") String userId);
}
