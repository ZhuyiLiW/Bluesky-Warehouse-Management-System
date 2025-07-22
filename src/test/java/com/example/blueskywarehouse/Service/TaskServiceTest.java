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
        ApiResponse<?> response = taskService.insertTask(1, LocalDate.now(), LocalDate.now().plusDays(1), "Testinhalt", 0, "Testbemerkung");
        verify(taskRepository).insertTask(anyInt(), any(), any(), anyString(), anyInt(), anyString());
        assertEquals("Aufgabe erfolgreich eingefügt", response.getMessage());
    }

    @Test
    void testInsertTask_invalidUserId() {
        assertThrows(InvalidParameterException.class, () -> {
            taskService.insertTask(0, LocalDate.now(), LocalDate.now().plusDays(1), "Test", 0, "Bemerkung");
        });
    }

    @Test
    void testDeleteTask_success() {
        ApiResponse<?> response = taskService.deleteTask(1);
        verify(taskRepository).deleteTask(1);
        assertEquals("Aufgabe erfolgreich gelöscht", response.getMessage());
    }

    @Test
    void testDeleteTask_invalidId() {
        assertThrows(InvalidParameterException.class, () -> taskService.deleteTask(0));
    }

    @Test
    void testUpdateTaskStatus_success() {
        // Vorbereitung eines Task-Objekts
        Task task = new Task();
        task.setId(1);
        task.setIfFinished(0);
        task.setRemark("");

        // Simuliere findById Rückgabe von task
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Aufruf der Methode
        ApiResponse<?> response = taskService.updateTaskStatus(1, 1, "Bemerkung");

        // Verifiziere, dass save() aufgerufen wurde und Felder geändert wurden
        verify(taskRepository).save(task);
        assertEquals(1, task.getIfFinished());
        assertEquals("Bemerkung", task.getRemark());

        // Überprüfe Rückgabemeldung
        assertEquals("Aufgabe erfolgreich aktualisiert", response.getMessage());
    }

    @Test
    void testFindTasksByUserId_success() {
        List<Task> tasks = List.of(new Task());
        when(taskRepository.findTasksByUserId(1)).thenReturn(tasks);

        ApiResponse<?> response = taskService.findTasksByUserId(1);
        verify(taskRepository).findTasksByUserId(1);
        assertEquals("Aufgaben erfolgreich abgerufen", response.getMessage());
        assertEquals(tasks, response.getData());
    }

    @Test
    void testFindTasksByUserId_notFound() {
        when(taskRepository.findTasksByUserId(1)).thenReturn(null);
        assertThrows(BusinessException.class, () -> taskService.findTasksByUserId(1));
    }
}
