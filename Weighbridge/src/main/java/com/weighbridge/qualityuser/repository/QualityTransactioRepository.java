package com.weighbridge.qualityuser.repository;

import com.weighbridge.qualityuser.entites.QualityTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QualityTransactioRepository extends JpaRepository<QualityTransaction,Integer> {

}
