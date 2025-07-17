package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.TaskRepository;
import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Entity.Task;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    /**
     * 插入任务记录
     */
    @Transactional
    public ApiResponse<?> insertTask(int userId, LocalDate taskDate, LocalDate deadline, String content, int ifFinished, String remark) {
        if (userId <= 0) {
            logger.warn("插入任务失败：参数不合法 userId={}", userId);
            throw new InvalidParameterException("参数不合法");
        }

        taskRepository.insertTask(userId, taskDate, deadline, content, ifFinished, remark);
        logger.info("成功插入任务：userId={}, taskDate={}, deadline={}, ifFinished={}, remark={}",
                userId, taskDate, deadline, ifFinished, remark);
        return ApiResponse.success("任务插入成功",null);
    }

    /**
     * 根据任务ID删除任务
     */
    @Transactional
    public ApiResponse<?> deleteTask(int taskId) {
        if (taskId <= 0) {
            logger.warn("删除任务失败：参数不合法 taskId={}", taskId);
            throw new InvalidParameterException("参数不合法");
        }

        taskRepository.deleteTask(taskId);
        logger.info("成功删除任务 taskId={}", taskId);
        return ApiResponse.success("任务删除成功",null);
    }

    /**
     * 更新任务完成状态
     */
    @Transactional
    public ApiResponse<?> updateTaskStatus(int taskId, int ifFinished, String remark) {
        if (taskId <= 0) {
            logger.warn("更新任务失败：参数不合法 taskId={}", taskId);
            throw new InvalidParameterException("参数不合法");
        }
        Task task  = taskRepository.findById((long) taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在，id=" + id));
        task.setIfFinished(ifFinished);
        task.setRemark(remark);
        taskRepository.save(task);

        logger.info("成功更新任务状态 taskId={}, ifFinished={}, remark={}", taskId, ifFinished, remark);
        return ApiResponse.success("任务更新成功",null);
    }

    /**
     * 根据用户ID获取任务列表
     */
    public ApiResponse<?> findTasksByUserId(@Param("userId") int userId) {
        if (userId <= 0) {
            logger.warn("查询任务失败：参数不合法 userId={}", userId);
            throw new InvalidParameterException("参数不合法");
        }

        List<Task> getTaskByUser = taskRepository.findTasksByUserId(userId);
        if (getTaskByUser == null) {
            logger.warn("未找到任务记录 userId={}", userId);
            throw new BusinessException("用户任务不存在");
        }

        logger.info("查询用户任务成功 userId={}, 数量={}", userId, getTaskByUser.size());
        return ApiResponse.success("任务获取成功", getTaskByUser);
    }

    /**
     * 根据用户ID和时间范围获取任务
     */
    public ApiResponse<?> findTasksByUserIdAndDateRange(int userId, LocalDate taskStartDate, LocalDate taskEndDate) {
        if (userId <= 0) {
            logger.warn("查询任务失败：参数不合法 userId={}", userId);
            throw new InvalidParameterException("参数不合法");
        }

        List<Task> getTaskByUser = taskRepository.findTasksByUserIdAndDateRange(userId, taskStartDate, taskEndDate);
        if (getTaskByUser == null) {
            logger.warn("未找到任务记录 userId={}, 日期范围：{} ~ {}", userId, taskStartDate, taskEndDate);
            throw new BusinessException("用户任务不存在");
        }

        logger.info("按用户和时间范围查询任务成功 userId={}, 数量={}", userId, getTaskByUser.size());
        return ApiResponse.success("任务获取成功", getTaskByUser);
    }

    /**
     * 根据时间范围获取所有任务
     */
    public ApiResponse<?> findTasksByDateRange(LocalDate taskStartDate, LocalDate taskEndDate) {
        List<Task> getTaskByUser = taskRepository.findTasksByDateRange(taskStartDate, taskEndDate);
        if (getTaskByUser == null) {
            logger.warn("按日期范围查询任务失败：{} ~ {}", taskStartDate, taskEndDate);
            throw new BusinessException("用户任务不存在");
        }

        logger.info("按时间范围查询任务成功，数量={}", getTaskByUser.size());
        return ApiResponse.success("任务获取成功", getTaskByUser);
    }

    /**
     * 获取所有已完成任务
     */
    public ApiResponse<?> findCompletedTasks() {
        List<Task> getAllFinishedTask = taskRepository.findCompletedTasks();
        logger.info("查询所有已完成任务成功，数量={}", getAllFinishedTask.size());
        return ApiResponse.success("任务获取成功", getAllFinishedTask);
    }

    /**
     * 根据用户ID获取已完成任务
     */
    public ApiResponse<?> findCompletedTasksByUserId(int userId) {
        List<Task> getAllCompletedTasksByUserId = taskRepository.findTasksByUserId(userId);
        logger.info("查询指定用户已完成任务成功 userId={}, 数量={}", userId, getAllCompletedTasksByUserId.size());
        return ApiResponse.success("任务获取成功", getAllCompletedTasksByUserId);
    }

    /**
     * 获取所有失败任务
     */
    public ApiResponse<?> findFailedTasks() {
        List<Task> getAllFailedTasks = taskRepository.findFailedTasks();
        logger.info("查询所有失败任务成功，数量={}", getAllFailedTasks.size());
        return ApiResponse.success("任务获取成功", getAllFailedTasks);
    }

    /**
     * 根据用户ID获取失败任务
     */
    public ApiResponse<?> findFailedTasksByUserId(int userId) {
        List<Task> getAllFailedTasks = taskRepository.findFailedTasksByUserId(userId);
        logger.info("查询指定用户失败任务成功 userId={}, 数量={}", userId, getAllFailedTasks.size());
        return ApiResponse.success("任务获取成功", getAllFailedTasks);
    }

}
