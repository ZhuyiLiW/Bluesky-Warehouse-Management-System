package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
    SELECT t
    FROM Task t
    WHERE t.userId = :userId
""")
    List<Task> findTasksByUserId(@Param("userId") int userId);


    @Query("""
    SELECT t
    FROM Task t
    WHERE t.userId = :userId
      AND t.taskDate BETWEEN :taskStartDate AND :taskEndDate
""")
    List<Task> findTasksByUserIdAndDateRange(
            @Param("userId") int userId,
            @Param("taskStartDate") LocalDate taskStartDate,
            @Param("taskEndDate") LocalDate taskEndDate
    );


    @Query("""
    SELECT t
    FROM Task t
    WHERE t.taskDate BETWEEN :taskStartDate AND :taskEndDate
""")
    List<Task> findTasksByDateRange(
            @Param("taskStartDate") LocalDate taskStartDate,
            @Param("taskEndDate") LocalDate taskEndDate
    );


    @Query("""
    SELECT t
    FROM Task t
    WHERE t.ifFinished = 0
    ORDER BY t.remark
""")
    List<Task> findFailedTasks();

    @Query("""
    SELECT t
    FROM Task t
    WHERE t.userId = :userId
      AND t.ifFinished = 0
    ORDER BY t.remark
""")
    List<Task> findFailedTasksByUserId(@Param("userId") int userId);



}
