package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Repository.TaskRepository;
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

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    /**
     * Fügt einen Aufgaben-Eintrag hinzu
     */
    @Transactional
    public ApiResponse<?> insertTask(int userId, LocalDate taskDate, LocalDate deadline, String content, int ifFinished, String remark) {
        if (userId <= 0) {
            logger.warn("Aufgabeneinfügung fehlgeschlagen: ungültige Parameter userId={}", userId);
            throw new InvalidParameterException("Ungültige Parameter");
        }
        Task newTask=new Task();
        newTask.setUserId(userId);
        newTask.setTaskDate(taskDate);
        newTask.setDeadline(deadline);
        newTask.setTaskContent(content);
        newTask.setIfFinished(ifFinished);
        newTask.setRemark(remark);
        taskRepository.save(newTask);
        logger.info("Aufgabe erfolgreich eingefügt: userId={}, taskDate={}, deadline={}, ifFinished={}, remark={}",
                userId, taskDate, deadline, ifFinished, remark);
        return ApiResponse.success("Aufgabe erfolgreich eingefügt", null);
    }

    /**
     * Löscht eine Aufgabe anhand der Aufgaben-ID
     */
    @Transactional
    public ApiResponse<?> deleteTask(int taskId) {
        if (taskId <= 0) {
            logger.warn("Aufgabenlöschung fehlgeschlagen: ungültige Parameter taskId={}", taskId);
            throw new InvalidParameterException("Ungültige Parameter");
        }

        taskRepository.deleteById((long) taskId);
        logger.info("Aufgabe erfolgreich gelöscht taskId={}", taskId);
        return ApiResponse.success("Aufgabe erfolgreich gelöscht", null);
    }

    /**
     * Aktualisiert den Status einer Aufgabe
     */
    @Transactional
    public ApiResponse<?> updateTaskStatus(int taskId, int ifFinished, String remark) {
        if (taskId <= 0) {
            logger.warn("Aufgabenaktualisierung fehlgeschlagen: ungültige Parameter taskId={}", taskId);
            throw new InvalidParameterException("Ungültige Parameter");
        }
        Task task  = taskRepository.findById((long) taskId)
                .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden, id=" + taskId));
        task.setIfFinished(ifFinished);
        task.setRemark(remark);
        taskRepository.save(task);

        logger.info("Aufgabenstatus erfolgreich aktualisiert taskId={}, ifFinished={}, remark={}", taskId, ifFinished, remark);
        return ApiResponse.success("Aufgabe erfolgreich aktualisiert", null);
    }

    /**
     * Holt Aufgaben anhand der Benutzer-ID
     */
    public ApiResponse<?> findTasksByUserId(@Param("userId") int userId) {
        if (userId <= 0) {
            logger.warn("Aufgabensuche fehlgeschlagen: ungültige Parameter userId={}", userId);
            throw new InvalidParameterException("Ungültige Parameter");
        }

        List<Task> getTaskByUser = taskRepository.findTasksByUserId(userId);
        if (getTaskByUser == null) {
            logger.warn("Keine Aufgaben gefunden userId={}", userId);
            throw new BusinessException("Benutzeraufgaben nicht gefunden");
        }

        logger.info("Benutzeraufgaben erfolgreich abgefragt userId={}, Anzahl={}", userId, getTaskByUser.size());
        return ApiResponse.success("Aufgaben erfolgreich abgerufen", getTaskByUser);
    }

    /**
     * Holt Aufgaben anhand von Benutzer-ID und Zeitbereich
     */
    public ApiResponse<?> findTasksByUserIdAndDateRange(int userId, LocalDate taskStartDate, LocalDate taskEndDate) {
        if (userId <= 0) {
            logger.warn("Aufgabensuche fehlgeschlagen: ungültige Parameter userId={}", userId);
            throw new InvalidParameterException("Ungültige Parameter");
        }

        List<Task> getTaskByUser = taskRepository.findTasksByUserIdAndDateRange(userId, taskStartDate, taskEndDate);
        if (getTaskByUser == null) {
            logger.warn("Keine Aufgaben gefunden userId={}, Zeitraum: {} ~ {}", userId, taskStartDate, taskEndDate);
            throw new BusinessException("Benutzeraufgaben nicht gefunden");
        }

        logger.info("Aufgaben nach Benutzer und Zeitraum erfolgreich abgefragt userId={}, Anzahl={}", userId, getTaskByUser.size());
        return ApiResponse.success("Aufgaben erfolgreich abgerufen", getTaskByUser);
    }

    /**
     * Holt alle Aufgaben anhand eines Zeitbereichs
     */
    public ApiResponse<?> findTasksByDateRange(LocalDate taskStartDate, LocalDate taskEndDate) {
        List<Task> getTaskByUser = taskRepository.findTasksByDateRange(taskStartDate, taskEndDate);
        if (getTaskByUser == null) {
            logger.warn("Aufgabensuche nach Zeitraum fehlgeschlagen: {} ~ {}", taskStartDate, taskEndDate);
            throw new BusinessException("Benutzeraufgaben nicht gefunden");
        }

        logger.info("Aufgaben nach Zeitraum erfolgreich abgefragt, Anzahl={}", getTaskByUser.size());
        return ApiResponse.success("Aufgaben erfolgreich abgerufen", getTaskByUser);
    }

    /**
     * Holt alle erledigten Aufgaben
     */
    public ApiResponse<?> findCompletedTasks() {
        List<Task> getAllFinishedTask = taskRepository.findAll();
        logger.info("Alle erledigten Aufgaben erfolgreich abgefragt, Anzahl={}", getAllFinishedTask.size());
        return ApiResponse.success("Aufgaben erfolgreich abgerufen", getAllFinishedTask);
    }

    /**
     * Holt erledigte Aufgaben eines bestimmten Benutzers
     */
    public ApiResponse<?> findCompletedTasksByUserId(int userId) {
        List<Task> getAllCompletedTasksByUserId = taskRepository.findTasksByUserId(userId);
        logger.info("Erledigte Aufgaben für Benutzer erfolgreich abgefragt userId={}, Anzahl={}", userId, getAllCompletedTasksByUserId.size());
        return ApiResponse.success("Aufgaben erfolgreich abgerufen", getAllCompletedTasksByUserId);
    }

    /**
     * Holt alle fehlgeschlagenen Aufgaben
     */
    public ApiResponse<?> findFailedTasks() {
        List<Task> getAllFailedTasks = taskRepository.findFailedTasks();
        logger.info("Alle fehlgeschlagenen Aufgaben erfolgreich abgefragt, Anzahl={}", getAllFailedTasks.size());
        return ApiResponse.success("Aufgaben erfolgreich abgerufen", getAllFailedTasks);
    }

    /**
     * Holt fehlgeschlagene Aufgaben eines bestimmten Benutzers
     */
    public ApiResponse<?> findFailedTasksByUserId(int userId) {
        List<Task> getAllFailedTasks = taskRepository.findFailedTasksByUserId(userId);
        logger.info("Fehlgeschlagene Aufgaben für Benutzer erfolgreich abgefragt userId={}, Anzahl={}", userId, getAllFailedTasks.size());
        return ApiResponse.success("Aufgaben erfolgreich abgerufen", getAllFailedTasks);
    }

}
