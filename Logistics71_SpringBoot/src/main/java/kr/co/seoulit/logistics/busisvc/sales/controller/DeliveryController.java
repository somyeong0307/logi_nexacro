package kr.co.seoulit.logistics.busisvc.sales.controller;

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

import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractInfoTO;
import kr.co.seoulit.logistics.busisvc.sales.service.SalesService;
import kr.co.seoulit.logistics.busisvc.sales.to.DeliveryInfoTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/sales/*")
public class DeliveryController {
	
	@Autowired
	private SalesService salesService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	ModelMap map=null;
	private static Gson gson = new GsonBuilder().serializeNulls().create(); // 속성값이 null 인 속성도 변환
	
	
	@RequestMapping(value="/deliver/list/contractavailable")
	public void searchDeliverableContractList(@RequestAttribute("reqData")PlatformData reqData,
            								@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		HashMap<String, String> map = new HashMap<>();
		map.put("searchCondition", reqData.getVariable("searchCondition").getString());
		map.put("startDate",  reqData.getVariable("startDate").getString());
		map.put("endDate", reqData.getVariable("endDate").getString());
		map.put("customerCode", reqData.getVariable("customerCode").getString());
		
		ArrayList<ContractInfoTO> deliveryInfoList = salesService.getDeliverableContractList(map);
		
		ArrayList<ContractDetailTO> deliverableContractDetailList = new ArrayList<>();
		for(ContractInfoTO contract : deliveryInfoList) {
			for(ContractDetailTO contractDetailTO : contract.getContractDetailTOList()) {
				deliverableContractDetailList.add(contractDetailTO);
			}
		}
		
		datasetBeanMapper.beansToDataset(resData, deliveryInfoList, ContractInfoTO.class);
		datasetBeanMapper.beansToDataset(resData, deliverableContractDetailList, ContractDetailTO.class);
	}
	
	
	//납풉현황조회
	@RequestMapping(value="/delivery/list")
	public void searchDeliveryInfoList(@RequestAttribute("resData")PlatformData resData,
            								@RequestAttribute("reqData")PlatformData reqData) throws Exception {
				
		ArrayList<DeliveryInfoTO> deliveryInfoList = salesService.getDeliveryInfoList();
		datasetBeanMapper.beansToDataset(resData, deliveryInfoList, DeliveryInfoTO.class);
	
	}
	

	@RequestMapping(value="/delivery/batch" ,method=RequestMethod.POST)
	public ModelMap deliveryBatchListProcess(HttpServletRequest request, HttpServletResponse response) {
		String batchList = request.getParameter("batchList");
		map = new ModelMap();
		try {
			List<DeliveryInfoTO> deliveryTOList = gson.fromJson(batchList, new TypeToken<ArrayList<DeliveryInfoTO>>() {
			}.getType());
			HashMap<String, Object> resultMap = salesService.batchDeliveryListProcess(deliveryTOList);

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


	//납품
	@RequestMapping(value="/deliver")
	public void deliver(@RequestAttribute("reqData")PlatformData reqData,
							@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String contractDetailNo = reqData.getVariable("contractDetailNo").getString();
		System.out.println("테스트징기"+contractDetailNo);
		HashMap<String,Object> resultMap = salesService.deliver(contractDetailNo);
		resData.getVariableList().add("g_procedureMsg",resultMap.get("errorMsg"));
	}

}
