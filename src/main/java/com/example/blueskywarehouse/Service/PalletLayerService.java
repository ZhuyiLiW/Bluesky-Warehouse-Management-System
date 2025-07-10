package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.PalletLayerRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PalletLayerService {

    @Autowired
    private PalletLayerRepository palletLayerRepository;
    private final ReentrantLock lock = new ReentrantLock();
    Logger logger = LoggerFactory.getLogger(WorkLogService.class);

    /**
     * 更新托盘位置：从旧仓位移动部分或全部库存到新仓位
     */

    @Transactional
    public ApiResponse<?> updatePalett(String oldBinCode, String newBinCode, int itemId, int unitCount) {

            lock.lock();
        try {
        // 获取旧仓位库存
            Integer oldStock = palletLayerRepository.getStock(itemId, oldBinCode);
            int currentOldStock = (oldStock != null) ? oldStock : 0;

            // 判断旧仓位库存是否足够移动
            if (currentOldStock > unitCount) {
                palletLayerRepository.minusStock(itemId, unitCount, oldBinCode, currentOldStock);
                logger.info("从旧仓位移除部分库存：itemId={}, oldBinCode={}, 移动数量={}, 当前库存={}", itemId, oldBinCode, unitCount, currentOldStock);
            } else if (currentOldStock == unitCount) {
                palletLayerRepository.changePalettStatusInto49(itemId, oldBinCode);
                palletLayerRepository.deleteItemId49();
                logger.info("旧仓位库存正好全部转移，已设置状态为49并清除记录：itemId={}, binCode={}", itemId, oldBinCode);
            } else {
                logger.warn("库存不足：无法从旧仓位移动库存。itemId={}, oldBinCode={}, 请求移动={}, 实际库存={}",
                        itemId, oldBinCode, unitCount, currentOldStock);
                throw new BusinessException("库存不足");
            }

            // 获取新仓位库存
            Integer newStock = palletLayerRepository.getStock(itemId, newBinCode);
            int currentNewStock = (newStock != null) ? newStock : 0;

            // 添加库存到新仓位
            if (currentNewStock != 0) {
                palletLayerRepository.addStock(currentNewStock, itemId, unitCount, newBinCode);
                logger.info("向已有的新仓位添加库存：itemId={}, newBinCode={}, 添加数量={}, 原库存={}", itemId, newBinCode, unitCount, currentNewStock);
            } else {
                palletLayerRepository.insertStockPalett(itemId, unitCount, newBinCode);
                palletLayerRepository.insertStockBin(newBinCode);
                logger.info("新建新仓位托盘并插入库存：itemId={}, newBinCode={}, 数量={}", itemId, newBinCode, unitCount);
            }

            return ApiResponse.success("托盘位置移动成功",null);  }
         finally {
            lock.unlock();
        }

    }
    /**
     * 清空指定仓位中的所有托盘数据。
     *
     * @param binCode 仓位编码
     * @return 操作结果响应
     */
    @Transactional
    public ApiResponse<?> deleteAllPalettFromBin(String binCode) {
            palletLayerRepository.deleteAllPalettFromBin(binCode);
            palletLayerRepository.deleteBin(binCode);
            logger.info("仓位清空成功：{}", binCode);
            return ApiResponse.success("仓位清空成功",null);
    }
    public ApiResponse<?> searchAllItemFromBin( String binCode) {
        List<Object[]>allItemFromBin=palletLayerRepository.searchAllItemFromBin(binCode);
        logger.info("仓位搜索：{}", allItemFromBin);
        return ApiResponse.success("仓位搜索成功",allItemFromBin);
    }
}
