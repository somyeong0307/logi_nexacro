package kr.co.seoulit.logistics.purcstosvc.stock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import kr.co.seoulit.logistics.purcstosvc.stock.mapper.BomMapper;
import kr.co.seoulit.logistics.purcstosvc.stock.mapper.StockMapper;
import kr.co.seoulit.logistics.purcstosvc.stock.repository.StockRepository;
import kr.co.seoulit.logistics.purcstosvc.stock.to.BomDeployTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.BomInfoTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.BomTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockChartTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockLogTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockTO;

@Service
public class StockServiceImpl implements StockService {

	@Autowired
	private BomMapper bomMapper;
	@Autowired
	private StockMapper stockMapper;
	@Autowired
	private StockRepository stockRepository;
	
	@Override
	public ArrayList<BomDeployTO> getBomDeployList(String deployCondition, String itemCode,
			String itemClassificationCondition) {

		ArrayList<BomDeployTO> bomDeployList = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("deployCondition", deployCondition);
		map.put("itemCode", itemCode);
		map.put("itemClassificationCondition", itemClassificationCondition);

		bomDeployList = bomMapper.selectBomDeployList(map);

		return bomDeployList;
	}

	@Override
	public ArrayList<BomInfoTO> getBomInfoList(String parentItemCode) {

		ArrayList<BomInfoTO> bomInfoList = null;

		bomInfoList = bomMapper.selectBomInfoList(parentItemCode);

		return bomInfoList;
	}


	@Override
	public ArrayList<BomInfoTO> getAllItemWithBomRegisterAvailable() {

		ArrayList<BomInfoTO> allItemWithBomRegisterAvailable = null;

		allItemWithBomRegisterAvailable = bomMapper.selectAllItemWithBomRegisterAvailable();

		return allItemWithBomRegisterAvailable;
	}

	@Override
	public HashMap<String, Object> batchBomListProcess(ArrayList<BomTO> batchBomList) {

		HashMap<String, Object> resultMap = new HashMap<>();

		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;

		for (BomTO TO : batchBomList) {

			String status = TO.getStatus();

			switch (status) {

			case "INSERT":

				bomMapper.insertBom(TO);

				insertCount++;

				break;

			case "UPDATE":

				bomMapper.updateBom(TO);

				updateCount++;

				break;

			case "DELETE":

				bomMapper.deleteBom(TO);

				deleteCount++;

				break;

			}

		}

		resultMap.put("INSERT", insertCount);
		resultMap.put("UPDATE", updateCount);
		resultMap.put("DELETE", deleteCount);

		return resultMap;

	}


	@Override
	public ArrayList<StockTO> getStockList() {

		List<StockTO> stockList = stockRepository.findAll(Sort.by(Sort.Direction.ASC, "itemCode"));
		return new ArrayList<>(stockList);
		
	}

	@Override
	public ArrayList<StockLogTO> getStockLogList(String startDate, String endDate) {

		HashMap<String, String> map = new HashMap<>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		
		return stockMapper.selectStockLogList(map);
		
	}


	@Override
	public HashMap<String,Object> warehousing(ArrayList<String> orderNoArr) {

		HashMap<String, String> map = new HashMap<>();
		String orderNoList = orderNoArr.toString().replace("[", "").replace("]", "");
		map.put("orderNoList", orderNoList);
		
		stockMapper.warehousing(map);
		
		HashMap<String, Object> resultMap = new HashMap<>();
		resultMap.put("errorCode", map.get("ERROR_CODE"));
		resultMap.put("errorMsg", map.get("ERROR_MSG"));
		
    	return resultMap;
	}


	@Override
	public ModelMap changeSafetyAllowanceAmount(String itemCode, String itemName,
			String safetyAllowanceAmount) {

		ModelMap resultMap = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("itemCode", itemCode);
		map.put("itemName", itemName);
		map.put("safetyAllowanceAmount", safetyAllowanceAmount);

		resultMap = stockMapper.updatesafetyAllowance(map);

		return resultMap;
	}

	
	@Override
	public ArrayList<StockChartTO> getStockChart() {

		ArrayList<StockChartTO> stockChart  = null;

		stockChart = stockMapper.selectStockChart();

		return stockChart;

	}
	
	@Override
	public ArrayList<StockTO> getWarehouseStockList(String warehouseCode) {

		return stockRepository.findByWarehouseCode(warehouseCode);
		
	}
	
	@Override
	public void batchStockProcess(ArrayList<StockTO> stockTOList) {

		for (StockTO bean : stockTOList) {
				
			String status = bean.getStatus();
				
			switch (status) {
				
				case "delete":
					stockMapper.deleteStock(bean);
					break;
						
				case "insert":
					stockMapper.insertStock(bean);
						break;
					
				case "update":
					stockMapper.updateStock(bean);
			}
				
		}

	}
}
