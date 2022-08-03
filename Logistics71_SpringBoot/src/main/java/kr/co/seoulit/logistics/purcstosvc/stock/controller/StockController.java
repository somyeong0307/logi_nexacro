package kr.co.seoulit.logistics.purcstosvc.stock.controller;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.purcstosvc.stock.service.StockService;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockLogTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/stock/*")
public class StockController {

	@Autowired
	private StockService stockService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	

	@RequestMapping(value="/sto/list")
	public void searchStockList(@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		ArrayList<StockTO> stockList = stockService.getStockList();
		datasetBeanMapper.beansToDataset(resData, stockList, StockTO.class);
		
	}
	

	//재고리스트
	@RequestMapping(value="/sto/log-list")
	public void searchStockLogList(@RequestAttribute("resData")PlatformData resData,
            								@RequestAttribute("reqData")PlatformData reqData) throws Exception {
		
		String startDate = reqData.getVariable("startDate").getString();
		String endDate = reqData.getVariable("endDate").getString();
		ArrayList<StockLogTO> stockLogList = stockService.getStockLogList(startDate,endDate);
		datasetBeanMapper.beansToDataset(resData, stockLogList, StockLogTO.class);
		
	}
	
	
	//입고
	@RequestMapping(value="/sto/warehousing")
	public void warehousing(@RequestAttribute("reqData") PlatformData reqData,
            					@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		String orderNoList = reqData.getVariableList().getString("orderNoList");
		ArrayList<String> orderNoArr = new ArrayList<>();
		orderNoArr.add(orderNoList);
		
		HashMap<String, Object> resultMap = stockService.warehousing(orderNoArr);
		resData.getVariableList().add("g_procedureMsg", resultMap.get("errorMsg"));
		resData.getVariableList().add("g_procedureCode", resultMap.get("errorCode"));
		
	}
	
		
	//창고 재고조회
	@RequestMapping(value="/sto/warehousestocklist")
	public void searchWarehouseStockList(@RequestAttribute("reqData") PlatformData reqData,
								@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		String houseCode = reqData.getVariable("houseCode").getString();	
		ArrayList<StockTO> stockList = stockService.getWarehouseStockList(houseCode);
		datasetBeanMapper.beansToDataset(resData, stockList, StockTO.class);
		
	}
	
	//창고자재일괄처리
	@RequestMapping(value="/sto/batch")
	public void modifyStockInfo(@RequestAttribute("reqData") PlatformData reqData,
							@RequestAttribute("resData") PlatformData resData) throws Exception {

		ArrayList<StockTO> stockTOList = (ArrayList<StockTO>) datasetBeanMapper.datasetToBeans(reqData, StockTO.class);			
		stockService.batchStockProcess(stockTOList);
		
	}
	
}
