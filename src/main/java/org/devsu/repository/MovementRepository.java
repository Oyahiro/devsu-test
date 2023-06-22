package org.devsu.repository;

import org.devsu.entity.Account;
import org.devsu.entity.Movement;
import org.devsu.enums.MovementType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementRepository extends AbstractEntityRepository<Movement> {

    Optional<Movement> findTopByAccountAccountNumberOrderByDateDesc(String accountNumber);

    Optional<Movement> findTopByAccountAndDateBefore(Account account, LocalDateTime date);

    List<Movement> findByAccountAccountNumberAndDateBetween(String accountNumber, LocalDateTime start, LocalDateTime end);

    List<Movement> findAllByAccountAndDateAfter(Account account, LocalDateTime date);
    List<Movement> findAllByAccountAndDateAfterOrderByDateAsc(Account account, LocalDateTime date);



    @Query("SELECT SUM(m.value) FROM Movement m WHERE m.account.accountNumber = :accountNumber " +
            "AND m.movementType = :movementType AND DATE(m.date) = CURRENT_DATE")
    Double findTotalValueByMovementTypeAndAccountNumberForToday(@Param("accountNumber") String accountNumber,
                                                                @Param("movementType") MovementType movementType);

}
