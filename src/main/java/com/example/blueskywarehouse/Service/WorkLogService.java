package com.example.blueskywarehouse.Service;
import com.example.blueskywarehouse.Dto.CustomerRecord;
import com.example.blueskywarehouse.Entity.WorkLog;
import com.example.blueskywarehouse.Dao.WorkLogRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class WorkLogService {

    @Autowired
    private WorkLogRepository workLogRepository;
    Logger logger = LoggerFactory.getLogger(WorkLogService.class);
    final int OUT = 0;  // 出库
    final int IN = 1;   // 入库
    final String startDateSuffix=" 00:00:00";
    final String endDateSuffix=" 23:59:59";
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * 通过 content 关键字模糊查询工作日志 (这个是测试接口不做处理 及测试API是否可以联通postman 可以忽略)
     */

    public List<WorkLog> searchWorkLogsByContent(String content) {
        return workLogRepository.findByContentContainingIgnoreCaseNative(content);
    }

    /**
     * 插入新的工作日志记录，并根据状态更新库存信息
     */
    @Transactional
    public ApiResponse<?> insertNewWorklog(String customerName, LocalDateTime operationDate, int itemId, int itemsCount, int status, String optimalBin) {


            // 获取 itemId 对应的总库存
            Integer allStock = Optional.ofNullable(workLogRepository.getAllStockCount(itemId)).orElse(0);

            // 出库时检查库存是否足够
            if (allStock < itemsCount && status == OUT) {
                logger.warn("库存不足：itemId={}, 请求数量={}, 实际库存={}", itemId, itemsCount, allStock);
                throw new BusinessException("库存不足");
            }


            // 自动或指定匹配仓位
            List<String> binCodes = (optimalBin == null || optimalBin.trim().isEmpty())
                    ? workLogRepository.findOptimalBin(itemId)
                    : Collections.singletonList(optimalBin);

            int remainingItems = itemsCount;
            if(binCodes.size()==1&&(optimalBin != null&& !optimalBin.trim().isEmpty())){
                Integer currentStock = Optional.ofNullable(workLogRepository.getStock(itemId, optimalBin)).orElse(0);
                if(currentStock<remainingItems&& status==OUT)throw new BusinessException("指定仓位库存不足");

            }
        lock.lock();
        try{
            for (String binCode : binCodes) {
                if (remainingItems <= 0) break;

                Integer currentStock = Optional.ofNullable(workLogRepository.getStock(itemId, binCode)).orElse(0);

                    if(remainingItems<currentStock||status==IN){
                    // 插入工作日志记录
                        workLogRepository.insertWorklog(customerName, operationDate, itemId, remainingItems, status, binCode);}

                    else{
                        workLogRepository.insertWorklog(customerName, operationDate, itemId, currentStock, status, binCode);
                    }

                if (status == OUT) { // 出库逻辑
                    remainingItems = handleStockOut(itemId, remainingItems, binCode, currentStock);
                } else { // 入库逻辑
                    handleStockIn(itemId, itemsCount, binCode, currentStock);
                    break; // 入库只执行一次
                }

                // 如果该仓位已空，则清理
                workLogRepository.deleteEmptyBin(binCode);
            }

            logger.info("成功插入工作日志：customer={}, itemId={}, 数量={}, 状态={}, 操作时间={}", customerName, itemId, itemsCount, status, operationDate);}
            finally {
                lock.unlock();

            }

        return ApiResponse.success("工作日志插入成功", null);
    }


    // 出库操作，对应insertNewWorklog方法

    private int handleStockOut(int itemId, int itemsToRemove, String binCode, int stock) {

        Integer palettId = workLogRepository.getPalettId(itemId, binCode);
        //如果现存单仓位库存大于等于需要出库库存
        if (stock >= itemsToRemove) {
            workLogRepository.minusStock(itemId, itemsToRemove, binCode, stock);

            // 如果托盘货物数量和需要移除数量相等
            if (stock == itemsToRemove) {

                if (palettId != null) {
                    workLogRepository.deletePalettFromBin(palettId);
                    workLogRepository.deleteItemId(palettId);
                }
            }

            return 0;
        } else if (stock > 0) {
            //如果托盘数量小于需要移除数量
            workLogRepository.minusStock(itemId, stock, binCode, stock);
            workLogRepository.deletePalettFromBin(palettId);
            workLogRepository.deleteItemId(palettId);

            return itemsToRemove - stock;
        } else {
            System.out.println("库存不足（Lagerbestandsmangel）");
            return itemsToRemove;
        }
    }

    //入库操作，对应insertNewWorklog方法
    private void handleStockIn(int itemId, int itemsCount, String binCode, int stock) {
        //如果本身有库存在仓位上，那就说明托盘存在，不需要额外添加托盘可以直接在现存托盘上直接增加数量
        if (stock > 0) {
            workLogRepository.addStock(stock, itemId, itemsCount, binCode);
        } else {
            insertStockIntoPalettAndBin(itemId, itemsCount, binCode);
        }
    }

    /**
     * 使工作日志无效，并回滚库存变更
     */
    @Transactional
    public ApiResponse<?> invalidWorklog(int worklogId) {
        lock.lock();
        try {

            //通过日志id找到日志所有信息
            WorkLog log = workLogRepository.getWorkLogById(worklogId);

            if (log == null) {
                logger.warn("未找到工作日志，worklogId={}", worklogId);
                throw new BusinessException( "找不到对应的工作日志");
            }

            //获取目前仓位对应的日志item库存数量
            Double originalUnitStock = Optional.ofNullable(
                    workLogRepository.getUnitstockFromWorklog(log.getItemId(), log.getBin_code())
            ).orElse(0.0);

            //如果originalUnitStock == 0 就表示没有相关托盘，需要获取新托盘.但是在这种情况下撤回的日志类型只能是发货
            if (originalUnitStock == 0) {
                if (log.getStatus() == OUT) {
                    logger.info("出库日志回滚时发现原库存为0，重新创建托盘。logId={}, itemId={}, binCode={}",
                            worklogId, log.getItemId(), log.getBin_code());
                    insertStockIntoPalettAndBin(log.getItemId(), log.getItemsCount(), log.getBin_code());
                } else if (log.getStatus() == IN) {
                    logger.warn("尝试回滚失败。logId={}, itemId={}, binCode={}",
                            worklogId, log.getItemId(), log.getBin_code());
                    throw new BusinessException("日志状态未知");
                }
            }

            // 0为出库日志，1为入库日志 getStatus不是0就是1，没有其他情况
            if (log.getStatus() == OUT) {
                //如果原始托盘该物料数量不为0
                if(originalUnitStock!=0.0)
                workLogRepository.rollbackWorklog0(originalUnitStock, log.getItemsCount(), log.getItemId(), log.getBin_code());
                else {
                    //原始托盘该物料数量为0，这时托盘已经被清除，需要重新建立托盘插入bin
                    workLogRepository.creatNewPalettForRollback(log.getItemsCount(), log.getItemId());
                    Integer newPalettId=workLogRepository.getPalettForRollback();
                    String slotCode = log.getBin_code().split("-")[0];
                    workLogRepository.insertNewPalettIntoBin(newPalettId, slotCode,log.getBin_code());
                }

            } else {
                workLogRepository.rollbackWorklog1(originalUnitStock, log.getItemsCount(), log.getItemId(), log.getBin_code());
                Integer getStock=workLogRepository.getStock(log.getItemId(),log.getBin_code());
                if(getStock==0){
                    workLogRepository.deletePalettByItemId(log.getItemId(),log.getBin_code());
                }

            }

            //回滚完成日志数量后 标记日志作废在数据表note上
            workLogRepository.worklogExpired(worklogId);      }
        finally {
            lock.unlock();

        }

        return ApiResponse.success("日志清除成功", null);

    }



     //向托盘和仓位中插入库存数据
    public void insertStockIntoPalettAndBin(int itemId, int itemsCount, String binCode) {
        workLogRepository.insertStockPalett(itemId, itemsCount, binCode);
        workLogRepository.insertStockBin(binCode);
    }

    /**
     * 根据指定日期获取工作日志
     *
     * 该方法通过调用 workLogRepository 获取指定日期的所有工作日志记录，并将其转换为 WorkLog 对象。
     * 如果获取数据成功，则返回包含工作日志的响应。如果发生异常，则返回错误响应并记录异常日志。
     *
     * @param date 指定的日期，用于查询工作日志
     */

    public ApiResponse<?> getWorklogByDate(String date) {
        List<WorkLog> workLogList = new ArrayList<>();

            // 从数据库获取指定日期的工作日志列表
            List<Object[]> worklogsAtDate = workLogRepository.getWorkLogByDate(date);

            // 遍历每一条工作日志记录
            for (Object[] worklogByDateItem : worklogsAtDate) {
                // 从 Object[] 数组中提取数据，并将其转换为相应类型
                int id = (int) worklogByDateItem[0];  // 假设第一个字段是 id
                String customerName = (String) worklogByDateItem[1];  // 假设第二个字段是 customerName
                int itemsCount = (int) worklogByDateItem[4];  // 假设第三个字段是 itemsCount
                int status = (int) worklogByDateItem[5];  // 假设第四个字段是 status
                String binCode = (String) worklogByDateItem[6];  // 假设第五个字段是 bin_code
                String remarks = (String) worklogByDateItem[7];  // 假设第六个字段是 remarks
                String itemName = (String) worklogByDateItem[3];  // 假设第七个字段是 itemName
                int itemId = (int) worklogByDateItem[8];  // 假设第八个字段是 itemId
                Timestamp operationDate=(Timestamp)worklogByDateItem[2];

                // 使用从数据库中提取的数据创建新的 WorkLog 对象
                WorkLog workLog = new WorkLog((long) id, customerName, itemsCount, status, binCode, remarks, itemName, itemId,operationDate);

                // 将 WorkLog 对象添加到列表中
                workLogList.add(workLog);
            }

            // 记录日志，输出成功获取数据的信息
            logger.info("成功获取日期 {} 的工作日志", date);

            // 返回 API 响应，包含成功信息和工作日志列表
            return ApiResponse.success(date + "的工作日志获取成功", workLogList);

}
    /**
     * 查看客户在某个时间段内的工作日志 重载4个方法
     *
     * @param startDate 搜索开始时间
     * @param endDate 搜索结束时间
     * 如果开始结束时间相同，则为同一天
     */

    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate) {
            List<WorkLog> workLogList = workLogRepository.getWorklistByPeriode(startDate + startDateSuffix, endDate + endDateSuffix);
            return ApiResponse.success(workLogList);
    }


    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate, String customerName) {
            customerName = customerName.trim();
            List<WorkLog> workLogList = workLogRepository.getWorklistByPeriodeAndCustomerName(
                    startDate + startDateSuffix,
                    endDate + endDateSuffix,
                    customerName);
            return ApiResponse.success(workLogList);
        }



    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate, int itemId) {

            List<WorkLog> workLogList = workLogRepository.getWorklistByPeriodeAndItemId(
                    startDate + startDateSuffix,
                    endDate + endDateSuffix,
                    itemId);
            return ApiResponse.success(workLogList);
    }


    public ApiResponse<?> getWorklogByPeriode(String startDate, String endDate, String customerName, int itemId) {
            customerName = customerName.trim();
            List<WorkLog> workLogList = workLogRepository.getWorklistByPeriodeAndItemIdAndCName(
                    startDate + startDateSuffix,
                    endDate + endDateSuffix,
                    itemId,
                    customerName);
            return ApiResponse.success(workLogList);

    }


    /**
     * 根据传入的起始时间和结束时间，从数据库中查询客户记录，
     * 并按操作日期（精确到天）进行分组。
     *
     * @param start 起始日期（包含）
     * @param end   结束日期（包含）
     * @return Map<操作日期, 该日期下的客户记录列表>。如果查询失败，返回空 Map。
     */

    public Map<java.sql.Date, List<CustomerRecord>> getCustomerRecordsGroupedByDate(Date start, Date end) {

        Map<java.sql.Date, List<CustomerRecord>> groupedRecords = new HashMap<>();

            // 将 Date 转为 yyyy-MM-dd 字符串格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startFormatted = sdf.format(start);
            String endFormatted = sdf.format(end);

            // 查询指定日期范围内的所有客户记录
            List<CustomerRecord> allRecords = workLogRepository.findByDateBetween(startFormatted, endFormatted);

            // 使用 Java 8 Stream 将记录按操作日期分组
            groupedRecords = allRecords.stream()
                    .collect(Collectors.groupingBy(CustomerRecord::getDate));

        return groupedRecords;
    }

    /**
     * 导出客户记录数据到 Excel 文件（按日期分 Sheet）
     *
     * 此方法将指定时间范围内的客户记录数据按照日期进行分组，
     * 每个日期作为一个工作表（Sheet），并生成一个包含所有日期的 Excel 文件。
     * 表头加粗，特定列设置了合适的列宽。
     *
     * @param startDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 包含 Excel 文件内容的 ByteArrayInputStream，用于下载
     */
    public ByteArrayInputStream exportToExcel(Date startDate, Date endDate) throws IOException {
        // 获取按日期分组的数据：Map<日期, 该日期的客户记录列表>
        Map<java.sql.Date, List<CustomerRecord>> dataMap = getCustomerRecordsGroupedByDate(startDate, endDate);

        // 对日期进行排序，确保工作表按时间顺序排列
        List<java.sql.Date> sortedDates = new ArrayList<>(dataMap.keySet());
        sortedDates.sort(Comparator.naturalOrder());

        // 创建 Excel 工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 遍历每个日期，创建对应的工作表
        for (java.sql.Date date : sortedDates) {
            String sheetName = date.toString();  // 工作表名为日期，例如 "2024-05-01"
            XSSFSheet sheet = workbook.createSheet(sheetName);

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"工作号", "客户发货地", "货品", "数量", "备注"};

            for (int i = 0; i < headers.length; i++) {
                // 创建加粗字体样式
                XSSFFont boldFont = workbook.createFont();
                boldFont.setBold(true);
                XSSFCellStyle boldStyle = workbook.createCellStyle();
                boldStyle.setFont(boldFont);

                // 创建表头单元格并设置样式
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

            // 设置特定列宽：客户发货地和货品
            sheet.setColumnWidth(1, 25 * 256); // 第2列
            sheet.setColumnWidth(2, 30 * 256); // 第3列

            // 填充数据行
            List<CustomerRecord> records = dataMap.get(date);
            int count = 1;
            for (int i = 0; i < records.size(); i++) {
                CustomerRecord r = records.get(i);
                Row row = sheet.createRow(i + 2 + count -1);  // 从第3行开始填充数据（空一行）
                row.createCell(0).setCellValue(count++);  // 工作号（递增序号）
                row.createCell(1).setCellValue(r.getDeliveryLocation());
                row.createCell(2).setCellValue(r.getProductName());
                row.createCell(3).setCellValue(r.getQuantity());
                row.createCell(4).setCellValue(r.getRemark());
            }
        }

        // 写入输出流并返回
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }



}
