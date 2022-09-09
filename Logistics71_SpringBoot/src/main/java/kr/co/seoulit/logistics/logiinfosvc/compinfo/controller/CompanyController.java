package kr.co.seoulit.logistics.logiinfosvc.compinfo.controller;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.service.CompInfoService;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CompanyTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CustomerInfoTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.DealDetailTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.DealTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/compinfo/*")
public class CompanyController {

	@Autowired
	private CompInfoService compInfoService;
	
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;

	@RequestMapping(value = "/company/list")
	public void searchCompanyList(HttpServletRequest request)  throws Exception {
		
		PlatformData resData = (PlatformData) request.getAttribute("resData");		
		ArrayList<CompanyTO> companyList  = compInfoService.getCompanyList();	
		datasetBeanMapper.beansToDataset(resData, companyList, CompanyTO.class);

	}

	//매출,매입 합계 조회
	@RequestMapping(value = "/companyinfo/sum")
	public void searchCompanyDealInfoList(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {
			
		String customerCode = reqData.getVariable("customer_code").getString();
		
		CustomerInfoTO customerInfo = compInfoService.getCustomerInfo(customerCode);
		ArrayList<DealTO> dealList = compInfoService.getDealList(customerCode);
		
		
		datasetBeanMapper.beanToDataset(resData, customerInfo, CustomerInfoTO.class);
		datasetBeanMapper.beansToDataset(resData, dealList, DealTO.class);
	}
	
	//거래내역 조회
	@RequestMapping(value = "/dealinfo")
	public void searchDealInfo(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {
			
		String dealCode = reqData.getVariable("deal_code").getString();
		DealDetailTO dealDetailInfo = compInfoService.getDealDetailInfo(dealCode);
		datasetBeanMapper.beanToDataset(resData, dealDetailInfo, DealDetailTO.class);
	}
	
}
