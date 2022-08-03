package kr.co.seoulit.logistics.busisvc.logisales.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.busisvc.logisales.service.LogisalesService;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractInfoTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/logisales/*")
public class ContractController {
	
	@Autowired
	private LogisalesService logisalesService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	ModelMap map=null;
		
	
	//수주조회
	@RequestMapping(value="/contract/list")
	public void searchContract(@RequestAttribute("reqData")PlatformData reqData,
            					@RequestAttribute("resData")PlatformData resData)throws Exception {

		String searchCondition = reqData.getVariableList().getString("searchCondition");
		String startDate = reqData.getVariableList().getString("firstDate");
		String endDate = reqData.getVariableList().getString("endDate");
		String customerCode =reqData.getVariableList().getString("customerCode");

		HashMap<String, String> map = new HashMap<>();
		map.put("searchCondition",searchCondition);
		map.put("startDate",startDate);
		map.put("endDate",endDate);
		map.put("customerCode",customerCode);
		
		ArrayList<ContractInfoTO> contractInfoTOList = logisalesService.getContractList(map);
		
		List<ContractDetailTO> contractDetailList = new ArrayList<>();
		
		for(ContractInfoTO contractInfoTO : contractInfoTOList) {
			for(ContractDetailTO contractDetailTO : contractInfoTO.getContractDetailTOList()) {
				contractDetailList.add(contractDetailTO);
			}
		}
		
		datasetBeanMapper.beansToDataset(resData, contractInfoTOList, ContractInfoTO.class);
		datasetBeanMapper.beansToDataset(resData, contractDetailList, ContractDetailTO.class);
		
	}

	
	//수주가능한 견적조회
	@RequestMapping(value= "/estimate/list/contractavailable")
	public void searchEstimateInContractAvailable(@RequestAttribute("reqData")PlatformData reqData,
            @RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String startDate = reqData.getVariable("startDate").getString();
		String endDate = reqData.getVariable("endDate").getString();
		
		ArrayList<EstimateTO> estimateListInContractAvailable = logisalesService.getEstimateListInContractAvailable(startDate, endDate);

		List<EstimateDetailTO> estimateDetailList = new ArrayList<>();
		
		for(EstimateTO estimateTO : estimateListInContractAvailable) {
			for(EstimateDetailTO estimateDetailTO : estimateTO.getEstimateDetailTOList()) {
				estimateDetailList.add(estimateDetailTO);
			}
		}
		
		datasetBeanMapper.beansToDataset(resData, estimateListInContractAvailable, EstimateTO.class);
		datasetBeanMapper.beansToDataset(resData, estimateDetailList, EstimateDetailTO.class);
		
	}

	
	//수주등록
	@RequestMapping(value="/contract/new")
	public void addNewContract(@RequestAttribute("reqData")PlatformData reqData,
        						@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String contractDate = reqData.getVariable("contractDate").getString();
		String personCodeInCharge = reqData.getVariable("personCodeInCharge").getString();
		
		
		ContractTO workingContractTO = datasetBeanMapper.datasetToBean(reqData, ContractTO.class); //수주
		List<ContractDetailTO> contractDetailList = datasetBeanMapper.datasetToBeans(reqData, ContractDetailTO.class);
		workingContractTO.setContractDetailTOList(contractDetailList);
		
		HashMap<String,Object> map = logisalesService.addNewContract(contractDate,personCodeInCharge,workingContractTO);
		
		resData.getVariableList().add("g_procedureCode", map.get("errorCode"));
		resData.getVariableList().add("g_procedureMsg", map.get("errorMsg"));
	}

}
