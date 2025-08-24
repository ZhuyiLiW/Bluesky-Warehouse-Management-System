package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dto.CustomerDto;
import com.example.blueskywarehouse.Entity.WorkLog;
import com.example.blueskywarehouse.Repository.WorkLogRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Util.DateTimeUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class WorkLogService {

    @Autowired
    private WorkLogRepository workLogRepository;
    Logger logger = LoggerFactory.getLogger(WorkLogService.class);
    final int OUT = 0;  // Ausgang (Lagerabgang)
    final int IN = 1;   // Eingang (Lagerzugang)
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Suche Arbeitslogs anhand von Inhalt (Test-API, kann ignoriert werden)
     */
    public List<WorkLog> searchWorkLogsByContent(String content) {
        return workLogRepository.findByContentContainingIgnoreCaseNative(content);
    }

    /**
     * Fügt ein neues Arbeitslog hinzu und aktualisiert den Lagerbestand je nach Status
     */
    @Transactional
    @CacheEvict(value = "allStock", allEntries = true)
    public ApiResponse<?> insertNewWorklog(String customerName, LocalDateTime operationDate, int itemId, int itemsCount, int status, String optimalBin) {
        Integer allStock = Optional.ofNullable(workLogRepository.getAllStockCount(itemId)).orElse(0);

        if (allStock < itemsCount && status == OUT) {
            logger.warn("Unzureichender Lagerbestand: itemId={}, angeforderte Menge={}, aktueller Bestand={}", itemId, itemsCount, allStock);
            throw new BusinessException("Unzureichender Lagerbestand");
        }

        List<String> binCodes = (optimalBin == null || optimalBin.trim().isEmpty())
                ? workLogRepository.findOptimalBin(itemId)
                : Collections.singletonList(optimalBin);

        int remainingItems = itemsCount;
        if (binCodes.size() == 1 && (optimalBin != null && !optimalBin.trim().isEmpty())) {
            Integer currentStock = Optional.ofNullable(workLogRepository.getStock(itemId, optimalBin)).orElse(0);
            if (currentStock < remainingItems && status == OUT)
                throw new BusinessException("Bestand im angegebenen Lagerplatz unzureichend");
        }

        lock.lock();
        try {
            for (String binCode : binCodes) {
                if (remainingItems <= 0) break;

                Integer currentStock = Optional.ofNullable(workLogRepository.getStock(itemId, binCode)).orElse(0);
                WorkLog workLog=new WorkLog();
                workLog.setBin_code(binCode);
                workLog.setStatus(status);
                workLog.setItemsCount(remainingItems);
                workLog.setItemId(itemId);
                workLog.setOperationDate(Timestamp.valueOf(operationDate));
                workLog.setCustomerName(customerName);
                if (remainingItems < currentStock || status == IN) {
                    workLogRepository.save(workLog);

                } else {
                    workLog.setItemsCount(currentStock);
                    workLogRepository.save(workLog);
                }

                if (status == OUT) {
                    remainingItems = handleStockOut(itemId, remainingItems, binCode, currentStock);
                } else {
                    handleStockIn(itemId, itemsCount, binCode, currentStock);
                    break; // Eingang nur einmal ausführen
                }

                workLogRepository.deleteEmptyBin(binCode);
            }

            logger.info("Arbeitslog erfolgreich hinzugefügt: Kunde={}, itemId={}, Menge={}, Status={}, Zeit={}", customerName, itemId, itemsCount, status, operationDate);
        } finally {
            lock.unlock();
        }

        return ApiResponse.success("Arbeitslog erfolgreich eingefügt", null);
    }

    // Lagerabgang verarbeiten
    private int handleStockOut(int itemId, int itemsToRemove, String binCode, int stock) {
        Integer palettId = workLogRepository.getPalettId(itemId, binCode);
        if (stock >= itemsToRemove) {
            workLogRepository.minusStock(itemId, itemsToRemove, binCode, stock);
            if (stock == itemsToRemove && palettId != null) {
                workLogRepository.deletePalettFromBin(palettId);
                workLogRepository.deleteItemId(palettId);
            }
            return 0;
        } else if (stock > 0) {
            workLogRepository.minusStock(itemId, stock, binCode, stock);
            workLogRepository.deletePalettFromBin(palettId);
            workLogRepository.deleteItemId(palettId);
            return itemsToRemove - stock;
        } else {
            logger.warn("Lagerbestand unzureichend");
            return itemsToRemove;
        }
    }

    // Lagerzugang verarbeiten
    private void handleStockIn(int itemId, int itemsCount, String binCode, int stock) {
        if (stock > 0) {
            workLogRepository.addStock(stock, itemId, itemsCount, binCode);
        } else {
            insertStockIntoPalettAndBin(itemId, itemsCount, binCode);
        }
    }

    /**
     * Markiert Arbeitslog als ungültig und rollt Lagerbestandsänderungen zurück
     */
    @Transactional
    @CacheEvict(value = "allStock", allEntries = true)
    public ApiResponse<?> invalidWorklog(int worklogId) {
        lock.lock();
        try {
            WorkLog log = workLogRepository.getWorkLogById(worklogId);
            if (log == null) {
                logger.warn("Arbeitslog nicht gefunden, worklogId={}", worklogId);
                throw new BusinessException("Arbeitslog nicht gefunden");
            }

            Double originalUnitStock = Optional.ofNullable(workLogRepository.getUnitstockFromWorklog(log.getItemId(), log.getBin_code())).orElse(0.0);

            if (originalUnitStock == 0) {
                if (log.getStatus() == OUT) {
                    logger.info("Lagerbestand bei Rücknahme 0, erstelle neue Palette. logId={}, itemId={}, binCode={}",
                            worklogId, log.getItemId(), log.getBin_code());
                    insertStockIntoPalettAndBin(log.getItemId(), log.getItemsCount(), log.getBin_code());
                } else if (log.getStatus() == IN) {
                    logger.warn("Rücknahme fehlgeschlagen. logId={}, itemId={}, binCode={}",
                            worklogId, log.getItemId(), log.getBin_code());
                    throw new BusinessException("Unbekannter Log-Status");
                }
            }

            if (log.getStatus() == OUT) {
                if (originalUnitStock != 0.0)
                    workLogRepository.rollbackWorklog0(originalUnitStock, log.getItemsCount(), log.getItemId(), log.getBin_code());

            } else {
                workLogRepository.rollbackWorklog1(originalUnitStock, log.getItemsCount(), log.getItemId(), log.getBin_code());
                Integer getStock = workLogRepository.getStock(log.getItemId(), log.getBin_code());
                if (getStock == 0) {
                    workLogRepository.deletePalettByItemId(log.getItemId(), log.getBin_code());
                }
            }

            workLogRepository.worklogExpired(worklogId);
        } finally {
            lock.unlock();
        }

        return ApiResponse.success("Log erfolgreich gelöscht", null);
    }

    // Insert Lagerbestand in Palette und Bin
    public void insertStockIntoPalettAndBin(int itemId, int itemsCount, String binCode) {
        workLogRepository.insertStockPalett(itemId, itemsCount, binCode);
        workLogRepository.insertStockBin(binCode);
    }

    /**
     * Arbeitslogs anhand Datum abrufen
     */
    public ApiResponse<?> getWorklogByDate(String date) {
        List<WorkLog> workLogList = new ArrayList<>();

        List<Object[]> worklogsAtDate = workLogRepository.getWorkLogByDate(date);

        for (Object[] worklogByDateItem : worklogsAtDate) {
            int id = (int) worklogByDateItem[0];
            String customerName = (String) worklogByDateItem[1];
            int itemsCount = (int) worklogByDateItem[4];
            int status = (int) worklogByDateItem[5];
            String binCode = (String) worklogByDateItem[6];
            String remarks = (String) worklogByDateItem[7];
            String itemName = (String) worklogByDateItem[3];
            int itemId = (int) worklogByDateItem[8];
            Timestamp operationDate = (Timestamp) worklogByDateItem[2];

            WorkLog workLog = new WorkLog((long) id, customerName, itemsCount, status, binCode, remarks, itemName, itemId, operationDate);

            workLogList.add(workLog);
        }

        logger.info("Arbeitslogs erfolgreich für Datum {} abgerufen", date);
        return ApiResponse.success(date + " Arbeitslogs erfolgreich abgerufen", workLogList);
    }

    /**
     * Arbeitslogs eines Kunden im Zeitraum abrufen (überladene Methoden)
     */
    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate,int page,int size) {

        Timestamp startTs = DateTimeUtil.toTimestamp(startDate);
        Timestamp endTs   = DateTimeUtil.toTimestamp(endDate);

        Pageable pageable = (Pageable) PageRequest.of(page, size); // Spring Data JPA Paginierungsobjekt, beginnend mit der ersten Seite, 10 Einträge pro Seite.
        Page<WorkLog> workLogPage = workLogRepository.getWorklistByPeriode(
                startTs ,
                endTs  ,
                pageable);

        return ApiResponse.success(workLogPage);
    }


    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate, String customerName) {
        Timestamp startTs = DateTimeUtil.toTimestamp(startDate);
        Timestamp endTs   = DateTimeUtil.toTimestamp(endDate);
        customerName = customerName.trim();
        List<WorkLog> workLogList = workLogRepository.getWorklistByPeriodeAndCustomerName(
                startTs,
                endTs,
                customerName);
        return ApiResponse.success(workLogList);
    }

    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate, int itemId) {
        Timestamp startTs = DateTimeUtil.toTimestamp(startDate);
        Timestamp endTs   = DateTimeUtil.toTimestamp(endDate);
        List<WorkLog> workLogList = workLogRepository.getWorklistByPeriodeAndItemId(
                startTs,
                endTs ,
                itemId);
        return ApiResponse.success(workLogList);
    }

    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate, String customerName, int itemId) {
        customerName = customerName.trim();
        Timestamp startTs = DateTimeUtil.toTimestamp(startDate);
        Timestamp endTs   = DateTimeUtil.toTimestamp(endDate);
        List<WorkLog> workLogList = workLogRepository.getWorklistByPeriodeAndItemIdAndCName(
                startTs,
                endTs ,
                itemId,
                customerName);
        return ApiResponse.success(workLogList);
    }

    /**
     * Kundendaten gruppiert nach Datum abrufen
     */
    public Map<java.sql.Date, List<CustomerDto>> getCustomerRecordsGroupedByDate(Date start, Date end) {
        Map<java.sql.Date, List<CustomerDto>> groupedRecords = new HashMap<>();
        List<CustomerDto> allRecords = workLogRepository.findByDateBetween(  new Timestamp(start.getTime()),
                new Timestamp(end.getTime()));

        groupedRecords = allRecords.stream()
                .collect(Collectors.groupingBy(CustomerDto::getDate));

        return groupedRecords;
    }

    /**
     * Kundendaten in Excel exportieren (pro Datum ein Blatt)
     */
    public ByteArrayInputStream exportToExcel(Date startDate, Date endDate) throws IOException {
        Map<java.sql.Date, List<CustomerDto>> dataMap = getCustomerRecordsGroupedByDate(startDate, endDate);
        List<java.sql.Date> sortedDates = new ArrayList<>(dataMap.keySet());
        sortedDates.sort(Comparator.naturalOrder());

        XSSFWorkbook workbook = new XSSFWorkbook();

        for (java.sql.Date date : sortedDates) {
            String sheetName = date.toString();
            XSSFSheet sheet = workbook.createSheet(sheetName);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Arbeitsnummer", "Kundenversandort", "Produkt", "Menge", "Bemerkung"};

            for (int i = 0; i < headers.length; i++) {
                XSSFFont boldFont = workbook.createFont();
                boldFont.setBold(true);
                XSSFCellStyle boldStyle = workbook.createCellStyle();
                boldStyle.setFont(boldFont);

                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

            sheet.setColumnWidth(1, 25 * 256);
            sheet.setColumnWidth(2, 30 * 256);

            List<CustomerDto> records = dataMap.get(date);
            int count = 1;
            for (int i = 0; i < records.size(); i++) {
                CustomerDto r = records.get(i);
                Row row = sheet.createRow(i + 2 + count - 1);
                row.createCell(0).setCellValue(count++);
                row.createCell(1).setCellValue(r.getDeliveryLocation());
                row.createCell(2).setCellValue(r.getProductName());
                row.createCell(3).setCellValue(r.getQuantity());
                row.createCell(4).setCellValue(r.getRemark());
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

}

