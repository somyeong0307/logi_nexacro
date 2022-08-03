package kr.co.seoulit.logistics.prodcsvc.quality.service;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import kr.co.seoulit.logistics.prodcsvc.quality.mapper.WorkOrderMapper;
import kr.co.seoulit.logistics.prodcsvc.quality.to.ProductionPerformanceInfoTO;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkOrderInfoTO;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkOrderSimulationTO;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkSiteLog;

@Service
public class QualityServiceImpl implements QualityService {
	
	@Autowired
	private WorkOrderMapper workOrderMapper;
	
	@Override
	public HashMap<String, Object> getWorkOrderableMrpList() {
		
		HashMap<String, Object> resultMap = new HashMap<>();
		HashMap<String, Object> map = new HashMap<>();
		
		workOrderMapper.getWorkOrderableMrpList(map);
		System.out.println("서비스 테스트"+map.get("RESULT"));
		resultMap.put("gridRowJson", map.get("RESULT"));
        resultMap.put("errorCode",map.get("ERROR_CODE"));
        resultMap.put("errorMsg", map.get("ERROR_MSG"));
		return resultMap;
		
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<WorkOrderSimulationTO> getWorkOrderSimulationList(String mrpNo ,String mrpGatheringNo) {
		
		String mrpNoList = mrpNo.toString().replace("[", "").replace("]", "");
		String mrpGatheringNoList = mrpGatheringNo.toString().replace("[", "").replace("]", "");
		
		// jpa 미구현 - procedure 호출
		HashMap<String, Object> map = new HashMap<>();
		
		map.put("mrpGatheringNoList",mrpGatheringNoList);
		map.put("mrpNoList", mrpNoList);
        
        
		workOrderMapper.getWorkOrderSimulationList(map);
		
		ArrayList<WorkOrderSimulationTO> workSiteSimulationTOList = (ArrayList<WorkOrderSimulationTO>)map.get("RESULT");

		return workSiteSimulationTOList;
	}
	
	
	@Override
	public void workOrder(String mrpGatheringNo,String workPlaceCode,String productionProcess,String mrpNo) {
		
		mrpGatheringNo=mrpGatheringNo.replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace("\"", "");
		mrpNo=mrpNo.replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace("\"", "");

		ModelMap resultMap = new ModelMap();
		
		HashMap<String, String> map = new HashMap<>();

		map.put("mrpGatheringNo", mrpGatheringNo);
		map.put("workPlaceCode", workPlaceCode);
		map.put("productionProcess", productionProcess);
		map.put("mrpNo", mrpNo);
        
		workOrderMapper.workOrder(map);
		
		resultMap.put("errorCode", map.get("ERROR_CODE"));
		resultMap.put("errorMsg", map.get("ERROR_MSG"));

	}

	@Override
	public ArrayList<WorkOrderInfoTO> getWorkOrderInfoList() {

	      return workOrderMapper.selectWorkOrderInfoList();
		
	}

	@Override
	public HashMap<String,Object> workOrderCompletion(String workOrderNo,String actualCompletionAmount) {

		HashMap<String, Object> map = new HashMap<>();
		map.put("workOrderNo", workOrderNo);
		map.put("actualCompletionAmount", actualCompletionAmount);
		workOrderMapper.workOrderCompletion(map);
		
    	return map;
		
	}

	@Override
	public ArrayList<ProductionPerformanceInfoTO> getProductionPerformanceInfoList() {

		ArrayList<ProductionPerformanceInfoTO> productionPerformanceInfoList = null;

		productionPerformanceInfoList = workOrderMapper.selectProductionPerformanceInfoList();

		return productionPerformanceInfoList;

	}

	@Override
	public HashMap<String, Object> showWorkSiteSituation(String workSiteCourse,String workOrderNo,String itemClassIfication) {

		HashMap<String,Object> map = new HashMap<String, Object>();
		
		HashMap<String, Object> resultMap = new ModelMap();
		
		map.put("workOrderNo", workOrderNo);
		map.put("workSiteCourse", workSiteCourse);
		map.put("itemClassIfication", itemClassIfication);
		
		workOrderMapper.selectWorkSiteSituation(map);

		resultMap.put("gridRowJson", map.get("RESULT"));
		resultMap.put("errorCode", map.get("ERROR_CODE"));
		resultMap.put("errorMsg", map.get("ERROR_MSG"));
		
		return resultMap;
	}

	@Override
	public void workCompletion(String workOrderNo, String itemCode ,  ArrayList<String> itemCodeListArr) {

		String itemCodeList=itemCodeListArr.toString().replace("[", "").replace("]", "");
		
		HashMap<String, String> map = new HashMap<>();

		map.put("workOrderNo", workOrderNo);
		map.put("itemCode", itemCode);
		map.put("itemCodeList", itemCodeList);

		workOrderMapper.updateWorkCompletionStatus(map);

	}

	@Override
	public HashMap<String, Object> workSiteLogList(String workSiteLogDate) {
		
		ArrayList<WorkSiteLog> list = workOrderMapper.workSiteLogList(workSiteLogDate);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();		
		resultMap.put("gridRowJson",list);
		return resultMap;
		
	}

}
