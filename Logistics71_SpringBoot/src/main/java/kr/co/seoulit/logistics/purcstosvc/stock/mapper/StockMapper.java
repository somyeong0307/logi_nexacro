package kr.co.seoulit.logistics.purcstosvc.stock.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.ui.ModelMap;

import kr.co.seoulit.logistics.purcstosvc.stock.to.StockChartTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockLogTO;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockTO;

@Mapper
public interface StockMapper {
	
	public ArrayList<StockTO> selectStockList();
	
	public ArrayList<StockTO> selectWarehouseStockList(String warehouseCode);
	
	public ArrayList<StockLogTO> selectStockLogList(HashMap<String, String> map);
	
	public void warehousing(HashMap<String, String> map);
	
	public ModelMap updatesafetyAllowance(HashMap<String, String> map);
	
	public ArrayList<StockChartTO> selectStockChart();
	
	public void insertStock(StockTO bean);
	
	public void updateStock(StockTO bean);
	
	public void deleteStock(StockTO bean);

}
