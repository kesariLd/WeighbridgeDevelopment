package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for accessing transaction log data.
 */
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Integer> {

    /**
     * Retrieves a transaction log entry based on the specified ticket number.
     *
     * @param ticketNo The ticket number to search for.
     * @return The transaction log entry corresponding to the provided ticket number, if found.
     */
    List<TransactionLog> findByTicketNo(Integer ticketNo);

    @Query("select tg.statusCode from TransactionLog tg where tg.ticketNo=:ticketNo")
    List<String> findStatusCodesByTicket(Integer ticketNo);

    /**
     * Retrieves a transaction log entry based on the specified ticket number and status code.
     *
     * @param ticketNo   The ticket number to search for.
     * @param statusCode The status code to search for.
     * @return The transaction log entry corresponding to the provided ticket number and status code, if found.
     */

    @Query("select t from TransactionLog t where t.ticketNo=:ticketNo and t.statusCode=:statusCode")
    <Optional>TransactionLog findByTicketNoAndStatusCode(@Param("ticketNo") Integer ticketNo,@Param("statusCode") String statusCode);

    boolean existsByTicketNoAndStatusCode(Integer ticketNo, String statusCode);
}

