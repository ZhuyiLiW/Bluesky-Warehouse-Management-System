package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Entity.WorkLog;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Repository.UserRepository;
import com.example.blueskywarehouse.Repository.WorkLogRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;


import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkLogServiceTest {
    @Mock
    private WorkLogRepository workLogRepository;
    @InjectMocks
    private WorkLogService workLogService;

    // for method InsertNewWorklog
    @Test
    public void testInsertNewWorklog_stockNotEnough_shouldThrowException() {
        // Gesamter Lagerbestand unzureichend → BusinessException erwartet
        when(workLogRepository.getAllStockCount(anyInt())).thenReturn(5);

        assertThrows(BusinessException.class, () ->
                workLogService.insertNewWorklog("Kunde1", LocalDateTime.now(), 1, 10, 0, null)
        );
    }

    @Test
    public void testInsertNewWorklog_binNotEnough_shouldThrowException() {
        // Bestand im angegebenen Lagerplatz unzureichend → BusinessException erwartet
        when(workLogRepository.getAllStockCount(anyInt())).thenReturn(10);
        when(workLogRepository.findOptimalBin(anyInt())).thenReturn(Collections.singletonList("Ptest"));
        when(workLogRepository.getStock(anyInt(), eq("Ptest"))).thenReturn(1);

        assertThrows(BusinessException.class, () ->
                workLogService.insertNewWorklog("Kunde1", LocalDateTime.now(), 1, 3, 0, "Ptest")
        );
    }

    @Test
    public void testInsertNewWorklog_outSuccess_partialStock() {
        // Ausgang < aktueller Bestand → normale Reduzierung, keine Löschung von Palette/Platz
        when(workLogRepository.getAllStockCount(anyInt())).thenReturn(10);
        when(workLogRepository.findOptimalBin(anyInt())).thenReturn(Collections.singletonList("Ptest-08-1"));
        when(workLogRepository.getStock(anyInt(), eq("Ptest-08-1"))).thenReturn(10);

        assertDoesNotThrow(() ->
                workLogService.insertNewWorklog("Kunde1", LocalDateTime.now(), 1, 3, 0, null)
        );

        verify(workLogRepository, atLeastOnce()).save(ArgumentMatchers.<WorkLog>any());
        verify(workLogRepository, times(1)).minusStock(eq(1), eq(3), eq("Ptest-08-1"), eq(10));
        verify(workLogRepository, never()).deletePalettFromBin(anyInt());
        verify(workLogRepository, never()).deleteItemId(anyInt());
        verify(workLogRepository, times(1)).deleteEmptyBin("Ptest-08-1");
    }

    @Test
    public void testInsertNewWorklog_outSuccess_stockExhausted() {
        // Ausgang == aktueller Bestand → Bestand auf 0, Palette/Platz werden gelöscht
        when(workLogRepository.getAllStockCount(anyInt())).thenReturn(10);
        when(workLogRepository.findOptimalBin(anyInt())).thenReturn(Collections.singletonList("Ptest-08-1"));
        when(workLogRepository.getStock(anyInt(), eq("Ptest-08-1"))).thenReturn(10);

        assertDoesNotThrow(() ->
                workLogService.insertNewWorklog("Kunde1", LocalDateTime.now(), 1, 10, 0, null)
        );

        verify(workLogRepository, atLeastOnce()).save(ArgumentMatchers.<WorkLog>any());
        verify(workLogRepository, times(1)).minusStock(eq(1), eq(10), eq("Ptest-08-1"), eq(10));
        verify(workLogRepository, times(1)).deletePalettFromBin(anyInt());
        verify(workLogRepository, times(1)).deleteItemId(anyInt());
        verify(workLogRepository, times(1)).deleteEmptyBin("Ptest-08-1");
    }

    @Test
    public void testInsertNewWorklog_inSuccess_shouldAddStock() {
        // Eingang: Lagerplatz hat bereits Bestand → addStock() wird aufgerufen
        when(workLogRepository.getStock(anyInt(), eq("BIN01"))).thenReturn(1);

        assertDoesNotThrow(() ->
                workLogService.insertNewWorklog("KundeA", LocalDateTime.now(), 1, 5, 1, "BIN01")
        );

        verify(workLogRepository, atLeastOnce()).save(ArgumentMatchers.<WorkLog>any());
        verify(workLogRepository, times(1)).addStock(eq(1), eq(1), eq(5), eq("BIN01"));
    }

    @Test
    public void testInsertNewWorklog_inSuccess_shouldInsertNewStock() {
        // Eingang: Lagerplatz leer → neue Palette + neuer Platz werden angelegt
        when(workLogRepository.getStock(anyInt(), eq("BIN01"))).thenReturn(0);

        assertDoesNotThrow(() ->
                workLogService.insertNewWorklog("KundeA", LocalDateTime.now(), 1, 5, 1, "BIN01")
        );

        verify(workLogRepository, atLeastOnce()).save(ArgumentMatchers.<WorkLog>any());
        verify(workLogRepository, times(1)).insertStockPalett(eq(1), eq(5), eq("BIN01"));
        verify(workLogRepository, times(1)).insertStockBin(eq("BIN01"));
    }


    // for method invalidWorklog

    @Test
    public void testInvalidWorklog_notFound_shouldThrowException() {
        // Log existiert nicht → BusinessException erwartet
        when(workLogRepository.getWorkLogById(anyInt())).thenReturn(null);

        assertThrows(BusinessException.class, () ->
                workLogService.invalidWorklog(1)
        );
    }

    @Test
    public void testInvalidWorklog_outWithStock_shouldRollback() {
        // OUT-Log vorhanden + Bestand > 0 → rollbackWorklog0() wird ausgeführt
        WorkLog log = new WorkLog();
        log.setStatus(0); // OUT
        log.setItemId(1);
        log.setBin_code("BIN01");
        log.setItemsCount(5);
        log.setOperationDate(Timestamp.valueOf(LocalDateTime.now()));

        when(workLogRepository.getWorkLogById(1)).thenReturn(log);
        when(workLogRepository.getUnitstockFromWorklog(log.getItemId(), log.getBin_code())).thenReturn(30.0);

        assertDoesNotThrow(() -> workLogService.invalidWorklog(1));

        verify(workLogRepository).rollbackWorklog0(30.0, log.getItemsCount(), log.getItemId(), log.getBin_code());
        verify(workLogRepository).worklogExpired(1);
    }

    @Test
    public void testInvalidWorklog_outNoStock_shouldInsertNewPalett() {
        // OUT-Log vorhanden + Bestand == 0 → neue Palette wird eingefügt
        WorkLog log = new WorkLog();
        log.setStatus(0); // OUT
        log.setItemId(1);
        log.setBin_code("BIN01");
        log.setItemsCount(5);
        log.setOperationDate(Timestamp.valueOf(LocalDateTime.now()));

        when(workLogRepository.getWorkLogById(1)).thenReturn(log);
        when(workLogRepository.getUnitstockFromWorklog(log.getItemId(), log.getBin_code())).thenReturn(0.0);

        assertDoesNotThrow(() -> workLogService.invalidWorklog(1));

        verify(workLogRepository).insertStockPalett(log.getItemId(), log.getItemsCount(), log.getBin_code());
        verify(workLogRepository).insertStockBin(log.getBin_code());
        verify(workLogRepository).worklogExpired(1);
    }

    @Test
    public void testInvalidWorklog_inNoStock_shouldThrowException() {
        // IN-Log vorhanden + Bestand == 0 → BusinessException erwartet
        WorkLog log = new WorkLog();
        log.setStatus(1); // IN
        log.setItemId(1);
        log.setBin_code("BIN01");
        log.setItemsCount(5);
        log.setOperationDate(Timestamp.valueOf(LocalDateTime.now()));

        when(workLogRepository.getWorkLogById(1)).thenReturn(log);
        when(workLogRepository.getUnitstockFromWorklog(log.getItemId(), log.getBin_code())).thenReturn(0.0);

        assertThrows(BusinessException.class, () ->
                workLogService.invalidWorklog(1)
        );

        verify(workLogRepository,never()).worklogExpired(1);
    }

    @Test
    public void testInvalidWorklog_inWithStock_shouldRollback() {
        // IN-Log vorhanden + Bestand > 0 → rollbackWorklog1() wird ausgeführt
        WorkLog log = new WorkLog();
        log.setStatus(1); // IN
        log.setItemId(1);
        log.setBin_code("BIN01");
        log.setItemsCount(5);
        log.setOperationDate(Timestamp.valueOf(LocalDateTime.now()));

        when(workLogRepository.getWorkLogById(1)).thenReturn(log);
        when(workLogRepository.getUnitstockFromWorklog(log.getItemId(), log.getBin_code())).thenReturn(30.0);

        assertDoesNotThrow(() -> workLogService.invalidWorklog(1));

        verify(workLogRepository).rollbackWorklog1(30.0, log.getItemsCount(), log.getItemId(), log.getBin_code());
        verify(workLogRepository).worklogExpired(1);
    }

    @Test
    public void testInvalidWorklog_inWithStock_andItemStockIsZero_shouldDeletePalett() {
        // IN-Log vorhanden + Bestand > 0, aber getStock() == 0 → Palette wird gelöscht
        WorkLog log = new WorkLog();
        log.setStatus(1); // IN
        log.setItemId(1);
        log.setBin_code("BIN01");
        log.setItemsCount(5);
        log.setOperationDate(Timestamp.valueOf(LocalDateTime.now()));

        when(workLogRepository.getWorkLogById(1)).thenReturn(log);
        when(workLogRepository.getUnitstockFromWorklog(log.getItemId(), log.getBin_code())).thenReturn(30.0);
        when(workLogRepository.getStock(log.getItemId(), log.getBin_code())).thenReturn(0);

        assertDoesNotThrow(() -> workLogService.invalidWorklog(1));

        verify(workLogRepository).rollbackWorklog1(30.0, log.getItemsCount(), log.getItemId(), log.getBin_code());
        verify(workLogRepository).deletePalettByItemId(log.getItemId(), log.getBin_code());
        verify(workLogRepository).worklogExpired(1);
    }



}
