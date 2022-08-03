package kr.co.seoulit.logistics.prodcsvc.quality.controller;

import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.prodcsvc.quality.service.QualityService;
import kr.co.seoulit.logistics.prodcsvc.quality.to.ProductionPerformanceInfoTO;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkOrderInfoTO;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkOrderSimulationTO;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkOrderableMrpListTO;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkSiteLog;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkSiteSimulationTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/quality/*")
public class WorkOrderController {

	@Autowired
	private QualityService qualityService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;

	ModelMap map = null;

	private static Gson gson = new GsonBuilder().serializeNulls().create();
	
	
	//작업지시 필요목록 조회
	@RequestMapping(value="/workorder/mrpavailable")	
	public void getWorkOrderableMrpList(@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		
		HashMap<String, Object> resultMap = qualityService.getWorkOrderableMrpList();
		@SuppressWarnings("unchecked")
		ArrayList<WorkOrderableMrpListTO> workOderableMrpList = (ArrayList<WorkOrderableMrpListTO>)resultMap.get("gridRowJson");
		System.out.println("테스트"+workOderableMrpList);
		datasetBeanMapper.beansToDataset(resData, workOderableMrpList, WorkOrderableMrpListTO.class);
	}
	
	
    //작업지시 시뮬레이션 실행과 동시에 받아오는 값.
	@RequestMapping(value="/workorder/dialog")
	public void showWorkOrderDialog(@RequestAttribute("reqData") PlatformData reqData,@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		String mrpNo = reqData.getVariableList().getString("mrpNo");
		String mrpGatheringNo = reqData.getVariableList().getString("mrpGatheringNo");

	    ArrayList<WorkOrderSimulationTO> workOrderSimulationList = qualityService.getWorkOrderSimulationList(mrpNo,mrpGatheringNo);
	    datasetBeanMapper.beansToDataset(resData, workOrderSimulationList, WorkOrderSimulationTO.class);
	}
	
	
	//전개된 결과 작업지시!!
	@RequestMapping(value="/workorder")
	public void workOrder(@RequestAttribute("reqData")PlatformData reqData  ) throws Exception {
		
		String workPlaceCode = reqData.getVariable("workPlaceCode").getString();
		String productionProcess = reqData.getVariable("productionProcessCode").getString();	
		String mrpGatheringNo = reqData.getVariableList().getString("mrpGatheringNo");
		String mrpNo = reqData.getVariableList().getString("mrpNo");
		
		qualityService.workOrder(mrpGatheringNo,workPlaceCode,productionProcess,mrpNo);
		
	}

	
	//작업지시 현황조회  , 작업장 현황 조회
	@RequestMapping(value="/workorder/list")
	public void showWorkOrderInfoList(@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		ArrayList<WorkOrderInfoTO> workOrderInfoList = qualityService.getWorkOrderInfoList();
		datasetBeanMapper.beansToDataset(resData, workOrderInfoList, WorkOrderInfoTO.class);
		
	}
	
	
	// 작업완료등록
	@RequestMapping(value="/workorder/completion")
	public void workOrderCompletion(@RequestAttribute("reqData") PlatformData reqData,
					@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		String workOrderNo = reqData.getVariable("workOrderNo").getString(); 
		String actualCompletionAmount = reqData.getVariable("actualCompletionAmount").getString(); 
		System.out.println("테스트1"+workOrderNo);
		System.out.println("테스트2"+actualCompletionAmount);
		HashMap<String, Object> map = qualityService.workOrderCompletion(workOrderNo, actualCompletionAmount);

		resData.getVariableList().add("ProcedureErrorCode", map.get("errorCode"));
		resData.getVariableList().add("ProcedureErrorMsg", map.get("errorMsg"));
	}
	
	
	@RequestMapping(value="/workorder/performance-list")
	public void getProductionPerformanceInfoList(@RequestAttribute("reqData") PlatformData reqData,
														@RequestAttribute("resData") PlatformData resData ) throws Exception {
		
		ArrayList<ProductionPerformanceInfoTO> productionPerformanceInfoList = qualityService.getProductionPerformanceInfoList();
		datasetBeanMapper.beansToDataset(resData, productionPerformanceInfoList, ProductionPerformanceInfoTO.class);
	}
	
	
	//작업시뮬레이션시작
	@RequestMapping(value="/worksite/situation")
	public void showWorkSiteSituation(@RequestAttribute("reqData") PlatformData reqData
            							,@RequestAttribute("resData") PlatformData resData ) throws Exception {
		
		String workSiteCourse = reqData.getVariable("workSiteCourse").getString();
		String workOrderNo = reqData.getVariable("workOrderNo").getString();
		String itemClassIfication = reqData.getVariable("itemClassIfication").getString();
		HashMap<String, Object> resultMap = qualityService.showWorkSiteSituation(workSiteCourse,workOrderNo,itemClassIfication);
		
		@SuppressWarnings("unchecked")
		ArrayList<WorkSiteSimulationTO> workSiteSimulationTO = (ArrayList<WorkSiteSimulationTO>)resultMap.get("gridRowJson");
		
		datasetBeanMapper.beansToDataset(resData, workSiteSimulationTO, WorkSiteSimulationTO.class);
		
	}
	
	
	//작업장에서 제품제작
	@RequestMapping(value="/workorder/workcompletion")
	public void workCompletion(@RequestAttribute("reqData")PlatformData reqData) throws Exception {
		
		String workOrderNo = reqData.getVariable("workOrderNo").getString(); 
		String itemCode = reqData.getVariable("itemCode").getString(); 
		String itemCodeList = reqData.getVariable("itemCodeList").getString();
		
		
		System.out.println("변수체크위함임"+itemCodeList);
		
		ArrayList<String> itemCodeListArr = gson.fromJson(itemCodeList, new TypeToken<ArrayList<String>>() {}.getType());
		qualityService.workCompletion(workOrderNo,itemCode,itemCodeListArr);
		
	}
	
	
	
	//작업장로그조회
	@RequestMapping(value="/workorder/worksitelog")
	public void workSiteLogList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		PlatformData reqData = (PlatformData)request.getAttribute("reqData");
		PlatformData resData = (PlatformData)request.getAttribute("resData");
		
		String workSiteLogDate = reqData.getVariable("workSiteLogDate").getString();
		
		HashMap<String, Object> resultMap = new HashMap<>();
		resultMap = qualityService.workSiteLogList(workSiteLogDate);
		
		@SuppressWarnings("unchecked")
		ArrayList<WorkSiteLog> WorkSiteLogList = (ArrayList<WorkSiteLog>)resultMap.get("gridRowJson");		
		datasetBeanMapper.beansToDataset(resData, WorkSiteLogList, WorkSiteLog.class);
		
	}
	
}
