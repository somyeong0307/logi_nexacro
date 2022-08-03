package kr.co.seoulit.logistics.prodcsvc.production.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailInMpsAvailableTO;
import kr.co.seoulit.logistics.prodcsvc.production.service.ProductionService;
import kr.co.seoulit.logistics.prodcsvc.production.to.MpsTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.SalesPlanInMpsAvailableTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/production/*")
public class MpsController {

	@Autowired
	private ProductionService productionService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	ModelMap map = null;

	private static Gson gson = new GsonBuilder().serializeNulls().create();

	
	@RequestMapping(value="/mps/list")
	public void searchMpsInfo(@RequestAttribute("reqData") PlatformData reqData,
            					@RequestAttribute("resData") PlatformData resData )throws Exception {
		
		String startDate = reqData.getVariable("startDate").getString();
		String endDate = reqData.getVariable("endDate").getString();
		String includeMrpApply = reqData.getVariable("includeMrpApply").getString();
		
		ArrayList<MpsTO> mpsTOList = productionService.getMpsList(startDate, endDate, includeMrpApply);
		datasetBeanMapper.beansToDataset(resData, mpsTOList, MpsTO.class);
	}

	
	@RequestMapping(value = "/mps/contractdetail-available")
	public void searchContractDetailListInMpsAvailable(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {

		String searchCondition = reqData.getVariable("searchCondition").getString();
		String startDate = reqData.getVariable("startDate").getString();
		String endDate = reqData.getVariable("endDate").getString();

		ArrayList<ContractDetailInMpsAvailableTO> contractDetailInMpsAvailableList = productionService
				.getContractDetailListInMpsAvailable(searchCondition, startDate, endDate);
		
		datasetBeanMapper.beansToDataset(resData, contractDetailInMpsAvailableList, ContractDetailInMpsAvailableTO.class);
	}

	
	@RequestMapping(value="/mps/salesplan-available", method=RequestMethod.GET)
	public ModelMap searchSalesPlanListInMpsAvailable(HttpServletRequest request, HttpServletResponse response) {
		String searchCondition = request.getParameter("searchCondition");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		map = new ModelMap();
		try {
			ArrayList<SalesPlanInMpsAvailableTO> salesPlanInMpsAvailableList = 
					productionService.getSalesPlanListInMpsAvailable(searchCondition, startDate, endDate);

			map.put("gridRowJson", salesPlanInMpsAvailableList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

	//mps등록
	@RequestMapping(value="mps/contractdetail")
	public void convertContractDetailToMps(@RequestAttribute("reqData") PlatformData reqData,
            @RequestAttribute("resData") PlatformData resData) throws Exception {
		
		ArrayList<ContractDetailInMpsAvailableTO> contractDetailInMpsAvailableList 
		= (ArrayList<ContractDetailInMpsAvailableTO>)datasetBeanMapper.datasetToBeans(reqData, ContractDetailInMpsAvailableTO.class);
		
		List<MpsTO> mpsNoList = productionService.convertContractDetailToMps(contractDetailInMpsAvailableList);
		datasetBeanMapper.beansToDataset(resData, mpsNoList, MpsTO.class);
	}

	
	@RequestMapping(value="/mps/salesplan", method=RequestMethod.PUT)
	public ModelMap convertSalesPlanToMps(HttpServletRequest request, HttpServletResponse response) {
		String batchList = request.getParameter("batchList");
		map = new ModelMap();
		try {
			ArrayList<SalesPlanInMpsAvailableTO> salesPlanInMpsAvailableList = gson.fromJson(batchList,
					new TypeToken<ArrayList<SalesPlanInMpsAvailableTO>>() {
					}.getType());
			HashMap<String, Object> resultMap = productionService.convertSalesPlanToMps(salesPlanInMpsAvailableList);

			map.put("result", resultMap);
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
