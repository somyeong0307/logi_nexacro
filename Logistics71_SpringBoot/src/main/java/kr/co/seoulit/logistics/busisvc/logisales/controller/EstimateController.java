package kr.co.seoulit.logistics.busisvc.logisales.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.busisvc.logisales.service.LogisalesService;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/logisales/*")
public class EstimateController {

	private final LogisalesService logisalesService;
	private final DatasetBeanMapper datasetBeanMapper;

	
	//견적조회
	@RequestMapping(value = "/estimate/list")
	public void searchEstimateInfo(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {

		String dateSearchCondition = reqData.getVariable("dateSearchCondition").getString();
		String startDate = reqData.getVariable("startDate").getString();
		String endDate = reqData.getVariable("endDate").getString();
		ArrayList<EstimateTO> estimateTOList = logisalesService.getEstimateList(dateSearchCondition, startDate,
				endDate);
		ArrayList<EstimateDetailTO> estimateDetailTOList = new ArrayList<>();

		for (EstimateTO estimate : estimateTOList) {
			for (EstimateDetailTO estimateDetailList : estimate.getEstimateDetailTOList()) {
				estimateDetailTOList.add(estimateDetailList);
				System.out.println("est ch" + estimateDetailTOList);
			}
		}
		
		datasetBeanMapper.beansToDataset(resData, estimateTOList, EstimateTO.class);
		datasetBeanMapper.beansToDataset(resData, estimateDetailTOList, EstimateDetailTO.class);
		
	}

	//견적등록(수정)
	@RequestMapping(value = "/estimate/new")
	public void addNewEstimate(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {

		EstimateTO newEstimateTO = datasetBeanMapper.datasetToBean(reqData, EstimateTO.class);
		List<EstimateDetailTO> newEstimateDeatailTO = datasetBeanMapper.datasetToBeans(reqData, EstimateDetailTO.class);
		String estimateDate = newEstimateTO.getEstimateDate();

		newEstimateTO.setEstimateDetailTOList(newEstimateDeatailTO);
		HashMap<String, Object> resultList = logisalesService.addNewEstimate(estimateDate, newEstimateTO);

		EstimateTO estimateTO = new EstimateTO();
		estimateTO.setEstimateNo(resultList.get("newEstimateNo").toString());

		resData.getVariableList().add("EstimateDtNo", resultList.get("INSERT").toString());
		datasetBeanMapper.beanToDataset(resData, estimateTO, EstimateTO.class);

	}

	//일괄저장
	@RequestMapping(value = "/estimatedetail/batch")
	public void batchListProcess(@RequestAttribute("reqData")PlatformData reqData) throws Exception {

		ArrayList<EstimateDetailTO> estimateDetailList 
				= (ArrayList<EstimateDetailTO>) datasetBeanMapper.datasetToBeans(reqData, EstimateDetailTO.class);
		
		logisalesService.batchEstimateDetailListProcess(estimateDetailList);
		
	}
			 
}
