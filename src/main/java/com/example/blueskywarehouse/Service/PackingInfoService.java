package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.AmazonProductRepository;
import com.example.blueskywarehouse.Dao.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Dao.PackingInfoRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackingInfoService {
    @Autowired
    private PackingInfoRepository packingInfoRepository;
    @Autowired
    private AmazonProductRepository amazonProductRepository;
    private final int weightConfirmed=0;
    private final int weightNotConfirmed=1;

    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);
    public ApiResponse<?> insertPackingInfo(String packingDate,String packingNumber,String customerName,String oldSku,String newSku,int quantity,double weight,String cartonSize,int operationNumber,int isWeightConfirmed) {

        String isPackingNumberExisted=packingInfoRepository.getPackingNumber(packingNumber);
        if(isPackingNumberExisted!=""&&!isPackingNumberExisted.isEmpty())throw new BusinessException("装箱号已存在");
        Integer isCustomerExisted=amazonProductRepository.getCustomerByName(customerName);
        if(isCustomerExisted==null)throw new BusinessException("请检查客户名，该客户不存在");
        String getRealNewSku=amazonProductRepository.getNewBarCode(oldSku);
        if(getRealNewSku!=newSku)throw new BusinessException("新标签错误，请检查");
        double realWeight=amazonProductRepository.getWeight(customerName);
        if(realWeight*quantity>20||weight>20&&isWeightConfirmed==weightNotConfirmed) throw new BusinessException("统计重量超重，请检查重量是否超重，或更改单个产品重量");
        Integer isOperationNumber=amazonProductRepository.getOperationNumber(isCustomerExisted);
        if(isOperationNumber!=operationNumber)throw new BusinessException("操作错误，请核对操作要求");
        packingInfoRepository.insertPackingInfo(packingDate,packingNumber,isCustomerExisted,oldSku,newSku,quantity,weight,cartonSize,operationNumber,isWeightConfirmed);
        return ApiResponse.success("装箱信息插入成功",null);

    }
}
