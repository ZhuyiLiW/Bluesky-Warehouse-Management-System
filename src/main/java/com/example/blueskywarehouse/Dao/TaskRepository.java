package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.PriceList;
import com.example.blueskywarehouse.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO warehouse_worker_tasks (user_id, task_date, deadline, content, if_finished, remark) " +
                    "VALUES (:userId, :taskDate, :deadline, :content, :ifFinished, :remark)",
            nativeQuery = true
    )
    void insertTask(@Param("userId") int userId,
                    @Param("taskDate") LocalDate taskDate,
                    @Param("deadline") LocalDate deadline,
                    @Param("content") String content,
                    @Param("ifFinished") int ifFinished,
                    @Param("remark") String remark);



    // 查找指定用户的所有任务
    @Query(value = "SELECT id,user_id,task_date,deadline,content,if_finished,remark,0 as version  FROM warehouse_worker_tasks WHERE user_id = :userId", nativeQuery = true)
    List<Task> findTasksByUserId(@Param("userId") int userId);

    // 查找指定用户在特定日期范围内的任务
    @Query(value = "SELECT id,user_id,task_date,deadline,content,if_finished,remark,0 as version   FROM warehouse_worker_tasks WHERE user_id = :userId AND task_date BETWEEN :taskStartDate AND :taskEndDate", nativeQuery = true)
    List<Task> findTasksByUserIdAndDateRange(@Param("userId") int userId,
                                             @Param("taskStartDate") LocalDate taskStartDate,
                                             @Param("taskEndDate") LocalDate taskEndDate);

    // 查找所有在特定日期范围内的任务
    @Query(value = "SELECT id,user_id,task_date,deadline,content,if_finished,remark,0 as version   FROM warehouse_worker_tasks WHERE task_date BETWEEN :taskStartDate AND :taskEndDate", nativeQuery = true)
    List<Task> findTasksByDateRange(@Param("taskStartDate") LocalDate taskStartDate,
                                    @Param("taskEndDate") LocalDate taskEndDate);

    // 查找所有已完成的任务
    @Query(value = "SELECT id,user_id,task_date,deadline,content,if_finished,remark,0 as version   FROM warehouse_worker_tasks ", nativeQuery = true)
    List<Task> findCompletedTasks();

    // 查找指定用户已完成的任务
    @Query(value = "SELECT id,user_id,task_date,deadline,content,if_finished,remark ,0 as version  FROM warehouse_worker_tasks WHERE user_id = :userId ", nativeQuery = true)
    List<Task> findCompletedTasksByUserId(@Param("userId") int userId);

    // 查找所有失败的任务
    @Query(value = "SELECT id,user_id,task_date,deadline,content,if_finished,remark ,0 as version  FROM warehouse_worker_tasks WHERE if_finished = 0 order by remark", nativeQuery = true)
    List<Task> findFailedTasks();

    // 查找指定用户失败的任务
    @Query(value = "SELECT id,user_id,task_date,deadline,content,if_finished,remark ,0 as version  FROM warehouse_worker_tasks WHERE user_id = :userId AND if_finished =0 order by remark", nativeQuery = true)
    List<Task> findFailedTasksByUserId(@Param("userId") int userId);


    // 删除任务
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM warehouse_worker_tasks WHERE id = :taskId", nativeQuery = true)
    void deleteTask(@Param("taskId") int taskId);

}
