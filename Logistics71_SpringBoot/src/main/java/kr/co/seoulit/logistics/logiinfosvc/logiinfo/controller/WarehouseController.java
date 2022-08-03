package kr.co.seoulit.logistics.logiinfosvc.logiinfo.controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.service.LogiInfoService;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.WarehouseTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/logiinfo/*")
public class WarehouseController {

	@Autowired
	private LogiInfoService logiInfoService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	
	//창고조회
	@RequestMapping(value = "/warehouse/list")
	public void getWarehouseList(@RequestAttribute("reqData") PlatformData reqData,
									@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		ArrayList<WarehouseTO> WarehouseTOList = logiInfoService.getWarehouseInfoList();
		
		datasetBeanMapper.beansToDataset(resData, WarehouseTOList, WarehouseTO.class);
	}

	
	//창고 수정,삭제
	@RequestMapping(value = "/warehouse/batch")
	public void modifyWarehouseInfo(@RequestAttribute("reqData") PlatformData reqData) throws Exception {
		
		
		ArrayList<WarehouseTO> warehouseTOList = (ArrayList<WarehouseTO>) datasetBeanMapper.datasetToBeans(reqData, WarehouseTO.class);
		System.out.println("넘어온창고정보들"+warehouseTOList);
		logiInfoService.batchWarehouseInfo(warehouseTOList);
			
		
	}
	
}
