package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<UserHistory,Long> {
    UserHistory findByUserId(String userId);

    Boolean existsByuserId(String userId);


}
