package kr.co.seoulit.logistics.prodcsvc.production.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tobesoft.xplatform.data.PlatformData;

import kr.co.seoulit.logistics.prodcsvc.production.service.ProductionService;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpGatheringTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpInsertInfoTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.OpenMrpTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/production/*")
public class MrpController {

	@Autowired
	private ProductionService productionService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	ModelMap map = null;

	private static Gson gson = new GsonBuilder().serializeNulls().create();	
	
	@RequestMapping(value="/mrp/list")
	public void getMrpList(@RequestAttribute("reqData")PlatformData reqData,
            						@RequestAttribute("resData")PlatformData resData) throws Exception {

		String mrpGatheringStatusCondition = reqData.getVariable("mrpGatheringStatusCondition").getString();  //처음에는 null
		String dateSearchCondition = reqData.getVariable("dateSearchCondition").getString(); // ''
		String mrpStartDate = reqData.getVariable("mrpStartDate").getString();//''
		String mrpEndDate = reqData.getVariable("mrpEndDate").getString();//''
		String mrpGatheringNo = reqData.getVariable("mrpGatheringNo").getString(); //'' 소요량 취합번호
		
		
			ArrayList<MrpTO> mrpList = null;
			
			if(mrpGatheringStatusCondition != null ) {
				mrpList = productionService.searchMrpList(mrpGatheringStatusCondition);
			} else if (dateSearchCondition != null) {
				mrpList = productionService.selectMrpListAsDate(dateSearchCondition, mrpStartDate, mrpEndDate);
			} else if (mrpGatheringNo != null) {
				mrpList = productionService.searchMrpListAsMrpGatheringNo(mrpGatheringNo);
			}
			
			datasetBeanMapper.beansToDataset(resData, mrpList, MrpTO.class);
	}
	
	
	//소요랑전개
	@RequestMapping(value = "/mrp/open")
	public void openMrp(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {

		String mpsNoList = reqData.getVariableList().getString("mpsNoList"); // 변수의 이름을 적용 하면 VALUE값을 얻어온다
		
		ArrayList<String> mpsNoArr = new ArrayList<>();
		mpsNoArr.add(mpsNoList);
		
		HashMap<String, Object> mrpMap = productionService.openMrp(mpsNoArr);
		
		@SuppressWarnings("unchecked")
		ArrayList<OpenMrpTO> openMrpList = (ArrayList<OpenMrpTO>)mrpMap.get("gridRowJson");
		
		datasetBeanMapper.beansToDataset(resData, openMrpList, OpenMrpTO.class);
	}

	
	//쇼요량전개된것 등록
	@RequestMapping(value="/mrp")
	public void registerMrp(@RequestAttribute("reqData")PlatformData reqData,
            					@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String mrpRegisterDate = reqData.getVariable("mrpRegisterDate").getString();
		String mpsNo = reqData.getVariable("mpsNoCollection").getString();
		
		HashMap<String, Object> resultMap = productionService.registerMrp(mrpRegisterDate, mpsNo);	 
        
		MrpInsertInfoTO mi=(MrpInsertInfoTO)resultMap.get("MrpInsertInfoTO");
		datasetBeanMapper.beanToDataset(resData,mi,MrpInsertInfoTO.class);
		
	}
	
	
	
	@RequestMapping(value="/mrp/gathering-list")
	public void getMrpGatheringList(@RequestAttribute("reqData")PlatformData reqData,
            							@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String mrpNoList = reqData.getVariableList().getString("mrpNoList"); //VariableList:name=mrpNoList, 
		ArrayList<String> mrpNoArr = new ArrayList<>();
		mrpNoArr.add(mrpNoList);
		
		ArrayList<MrpGatheringTO> mrpGatheringList = productionService.getMrpGathering(mrpNoArr);
		datasetBeanMapper.beansToDataset(resData, mrpGatheringList, MrpGatheringTO.class);
	}
	
	
	//소요량 취합 등록
	@RequestMapping(value="/mrp/gathering")
	public void registerMrpGathering(@RequestAttribute("reqData")PlatformData reqData,
            							@RequestAttribute("resData")PlatformData resData) throws Exception {

		String mrpGatheringRegisterDate = reqData.getVariable("mrpGatheringRegisterDate").getString();
		String mrpNoList = reqData.getVariableList().getString("mrpNoList");
		String mrpNoAndItemCodeList = reqData.getVariableList().getString("mrpNoAndItemCodeList");
		
		ArrayList<String> mrpNoArr = new ArrayList<>();
		mrpNoArr.add(mrpNoList);
		
		HashMap<String, String> mrpNoAndItemCodeMap =  gson.fromJson(mrpNoAndItemCodeList, //mprNO : ItemCode 
	              new TypeToken<HashMap<String, String>>() { }.getType());
		
		HashMap<String, Object> resultMap  = productionService.registerMrpGathering(mrpGatheringRegisterDate, mrpNoArr,mrpNoAndItemCodeMap);
		
		for(String key: resultMap.keySet()) {
			resData.getVariableList().add(key,resultMap.get(key));
		}
	}
	

	@RequestMapping(value="/mrp/mrpgathering/list", method=RequestMethod.GET)
	public ModelMap searchMrpGathering(HttpServletRequest request, HttpServletResponse response) {
		String searchDateCondition = request.getParameter("searchDateCondition");
		String startDate = request.getParameter("mrpGatheringStartDate");
		String endDate = request.getParameter("mrpGatheringEndDate");
		map = new ModelMap();
		try {
			ArrayList<MrpGatheringTO> mrpGatheringList = 
					productionService.searchMrpGatheringList(searchDateCondition, startDate, endDate);
				
			map.put("gridRowJson", mrpGatheringList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}
	
}
