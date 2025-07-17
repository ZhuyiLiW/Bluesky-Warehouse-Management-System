package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.PriceListRepository;
import com.example.blueskywarehouse.Dao.TaskRepository;
import com.example.blueskywarehouse.Entity.Task;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    @Test
    void testInsertTask_success() {
        ApiResponse<?> response = taskService.insertTask(1, LocalDate.now(), LocalDate.now().plusDays(1), "Test content", 0, "Test remark");
        verify(taskRepository).insertTask(anyInt(), any(), any(), anyString(), anyInt(), anyString());
        assertEquals("任务插入成功", response.getMessage());
    }

    @Test
    void testInsertTask_invalidUserId() {
        assertThrows(InvalidParameterException.class, () -> {
            taskService.insertTask(0, LocalDate.now(), LocalDate.now().plusDays(1), "Test", 0, "Remark");
        });
    }

    @Test
    void testDeleteTask_success() {
        ApiResponse<?> response = taskService.deleteTask(1);
        verify(taskRepository).deleteTask(1);
        assertEquals("任务删除成功", response.getMessage());
    }

    @Test
    void testDeleteTask_invalidId() {
        assertThrows(InvalidParameterException.class, () -> taskService.deleteTask(0));
    }

    @Test
    void testUpdateTaskStatus_success() {
        // 准备一个 Task 对象
        Task task = new Task();
        task.setId(1);
        task.setIfFinished(0);
        task.setRemark("");

        // mock findById 返回 task
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // 调用方法
        ApiResponse<?> response = taskService.updateTaskStatus(1, 1, "Remark");

        // 验证 save() 被调用，且 task 字段被修改了
        verify(taskRepository).save(task);
        assertEquals(1, task.getIfFinished());
        assertEquals("Remark", task.getRemark());

        // 断言返回信息
        assertEquals("任务更新成功", response.getMessage());
    }


    @Test
    void testFindTasksByUserId_success() {
        List<Task> tasks = List.of(new Task());
        when(taskRepository.findTasksByUserId(1)).thenReturn(tasks);

        ApiResponse<?> response = taskService.findTasksByUserId(1);
        verify(taskRepository).findTasksByUserId(1);
        assertEquals("任务获取成功", response.getMessage());
        assertEquals(tasks, response.getData());
    }

    @Test
    void testFindTasksByUserId_notFound() {
        when(taskRepository.findTasksByUserId(1)).thenReturn(null);
        assertThrows(BusinessException.class, () -> taskService.findTasksByUserId(1));
    }

}
