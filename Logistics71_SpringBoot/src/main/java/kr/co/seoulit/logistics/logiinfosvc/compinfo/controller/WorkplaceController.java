package kr.co.seoulit.logistics.logiinfosvc.compinfo.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tobesoft.xplatform.data.PlatformData;

import kr.co.seoulit.logistics.logiinfosvc.compinfo.service.CompInfoService;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CompanyTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.WorkplaceTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/compinfo/*")
public class WorkplaceController {

	@Autowired
	private CompInfoService compInfoService;
	
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	ModelMap map = null;

	private static Gson gson = new GsonBuilder().serializeNulls().create();

	@RequestMapping(value="/workplace/list")
	public void searchWorkplaceList(HttpServletRequest request) throws Exception{
		
		PlatformData reqData = (PlatformData) request.getAttribute("reqData");
		PlatformData resData = (PlatformData) request.getAttribute("resData");
		
		CompanyTO companyCode = datasetBeanMapper.datasetToBean(reqData, CompanyTO.class);
		System.out.println("디버그용companyCode @@@@@@@@ workplaceList"+companyCode.getCompanyCode());
		
		ArrayList<WorkplaceTO> workplaceList = compInfoService.getWorkplaceList(companyCode.getCompanyCode());
	
		System.out.println("@@@@@@@@ workplaceList: " + workplaceList);
		datasetBeanMapper.beansToDataset(resData, workplaceList, WorkplaceTO.class);
		
	}
	

	@RequestMapping(value = "/workplace/batch", method = RequestMethod.POST)
	public ModelMap batchListProcess(HttpServletRequest request, HttpServletResponse response) {
		String batchList = request.getParameter("batchList");
		map = new ModelMap();
		try {
			ArrayList<WorkplaceTO> workplaceList = gson.fromJson(batchList, new TypeToken<ArrayList<WorkplaceTO>>() {
				}.getType());
			HashMap<String, Object> resultMap = compInfoService.batchWorkplaceListProcess(workplaceList);
	
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
