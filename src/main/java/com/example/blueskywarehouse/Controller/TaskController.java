package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.PriceListService;
import com.example.blueskywarehouse.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/TaskController")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/addTask")
    public ApiResponse<?> insertPriceList(@RequestParam int userId, @RequestParam LocalDate taskDate, @RequestParam LocalDate deadline, @RequestParam String content, @RequestParam int ifFinished, @RequestParam String remark)  {
        return taskService.insertTask(userId,taskDate,deadline,content,ifFinished,remark);
    }
    @PreAuthorize("hasRole('1') or hasRole('3')")
    @PostMapping("/deleteTask")
    public ApiResponse<?> insertPriceList(@RequestParam int taskId)  {
        return taskService.deleteTask(taskId);
    }
    @PreAuthorize("hasRole('1')  or hasRole('3')")
    @PostMapping("/updateTask")
    public ApiResponse<?> updateTaskStatus(@RequestParam int taskId,@RequestParam int ifFinished,@RequestParam String remark) {
        return taskService.updateTaskStatus(taskId,ifFinished,remark);
    }

    // @RequestParam(required = false) – Wenn kein Parameter übergeben wird, einfach null zurückgeben
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/searchTask")
    public ApiResponse<?> searchTask(@RequestParam(required = false) Integer userId,
                                     @RequestParam(required = false) LocalDate taskStartDate,
                                     @RequestParam(required = false) LocalDate taskEndDate) {

        boolean hasUserId = userId != null;
        boolean hasStartDate = taskStartDate != null;
        boolean hasEndDate = taskEndDate != null;

        if (!hasUserId && !hasStartDate && !hasEndDate) {
            return taskService.findCompletedTasks();
        }

        if (hasUserId && !hasStartDate && !hasEndDate) {
            return taskService.findCompletedTasksByUserId(userId);
        }

        if (!hasUserId && hasStartDate && hasEndDate) {
            return taskService.findTasksByDateRange(taskStartDate, taskEndDate);
        }

        if (hasUserId && hasStartDate && hasEndDate) {
            return taskService.findTasksByUserIdAndDateRange(userId, taskStartDate, taskEndDate);
        }

        throw new InvalidParameterException("Ungültige Parameter: Entweder Start- und Enddatum zusammen angeben oder nur userId.");
    }


    @PreAuthorize("hasRole('1') or hasRole('3')")
    @PostMapping("/searchFailedTask")
    public ApiResponse<?> searchFailedTask(@RequestParam(required = false) Integer userId) {
        boolean hasUserId=userId!=null;
        if (!hasUserId) {
            return taskService.findFailedTasks(); //Alle fehlgeschlagenen Aufgaben abrufen
        } else {
            return taskService.findFailedTasksByUserId(userId); // Fehlgeschlagene Aufgaben eines bestimmten Benutzers abrufen
        }
    }



}
