package kr.co.seoulit.logistics.logiinfosvc.compinfo.controller;

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
import kr.co.seoulit.logistics.logiinfosvc.compinfo.service.CompInfoService;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CodeDetailTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CodeTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.ImageTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.LatLngTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/compinfo/*")
public class CodeController {
	
	@Autowired
	private CompInfoService compInfoService;
	
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	ModelMap map = null;

	private static Gson gson = new GsonBuilder().serializeNulls().create();

	
	@RequestMapping(value = "/codedetail/list")
	public void findCodeDetailList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PlatformData reqData = (PlatformData) request.getAttribute("reqData");
		PlatformData resData = (PlatformData) request.getAttribute("resData");
		String divisionCode = reqData.getVariable("divisionCode").getString();
		
		ArrayList<CodeDetailTO> detailCodeList = compInfoService.getCodeDetailList(divisionCode);
		datasetBeanMapper.beansToDataset(resData, detailCodeList, CodeDetailTO.class);
	
	}

	
	
	@RequestMapping(value = "/codeInfo", method = RequestMethod.POST)
	public ModelMap addCodeInFormation(HttpServletRequest request, HttpServletResponse response) {
		String newcodeInfo = request.getParameter("newCodeInfo");
		map = new ModelMap();
		try { 
			ArrayList<CodeTO> CodeTOList = gson.fromJson(newcodeInfo,
				new TypeToken<ArrayList<CodeTO>>() {}.getType());
			
			compInfoService.addCodeInFormation(CodeTOList);
			     
		    map.put("errorCode", 1);
		    map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

	
	// 코드조회
	@RequestMapping(value = "/code/list")
	public void findCodeList(@RequestAttribute("reqData") PlatformData reqData,
	         						@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		ArrayList<CodeTO> codeList = compInfoService.getCodeList();
		datasetBeanMapper.beansToDataset(resData, codeList, CodeTO.class);
		
	}


	@RequestMapping(value = "/code/duplication", method = RequestMethod.GET)
	public ModelMap checkCodeDuplication(HttpServletRequest request, HttpServletResponse response) {
		String divisionCode = request.getParameter("divisionCode");
		String newDetailCode = request.getParameter("newCode");
		map = new ModelMap();
		try {
			Boolean flag = compInfoService.checkCodeDuplication(divisionCode, newDetailCode);

			map.put("result", flag);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

	
	//코드일괄저장,상세코드일괄저장
	@RequestMapping(value = "/code/batch")
	public void batchListProcess(@RequestAttribute("reqData") PlatformData reqData,
									@RequestAttribute("resData") PlatformData resData) throws Exception {
	
		String code = reqData.getVariableList().getString("code");
			
		 if (code.equals("code")) {
	         ArrayList<CodeTO> codeList = (ArrayList<CodeTO>) datasetBeanMapper.datasetToBeans(reqData, CodeTO.class);
	         compInfoService.batchCodeListProcess(codeList);
	      } else if (code.equals("detailcode")) {
	         ArrayList<CodeDetailTO> detailCodeList = (ArrayList<CodeDetailTO>) datasetBeanMapper.datasetToBeans(reqData, CodeDetailTO.class);
	         compInfoService.batchDetailCodeListProcess(detailCodeList);
	      }

	}

	
	@RequestMapping(value = "/code", method = RequestMethod.PUT)
	public ModelMap changeCodeUseCheckProcess(HttpServletRequest request, HttpServletResponse response) {
		String batchList = request.getParameter("batchList");
		map = new ModelMap();
		try {
			ArrayList<CodeDetailTO> detailCodeList = null;
			HashMap<String, Object> resultMap = null;

			detailCodeList = gson.fromJson(batchList, new TypeToken<ArrayList<CodeDetailTO>>() {
			}.getType());
			resultMap = compInfoService.changeCodeUseCheckProcess(detailCodeList);

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
	
	//창고 위도 경도 가져오기
	@RequestMapping(value = "/code/latlng", method = RequestMethod.GET)
	public ModelMap findLatLngList(HttpServletRequest request, HttpServletResponse response) {

		String wareHouseCodeNo = request.getParameter("wareHouseCodeNo"); 

		map = new ModelMap();

		try {
			ArrayList<LatLngTO> detailCodeList = compInfoService.getLatLngList(wareHouseCodeNo);

			map.put("detailCodeList", detailCodeList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		} 
		return map;
	   }
	   
	 //이미지 띄우기
	@RequestMapping(value = "/code/itemimage", method = RequestMethod.GET)
	public ModelMap findDetailImageList(HttpServletRequest request, HttpServletResponse response) {

		String itemGroupCodeNo = request.getParameter("itemGroupCodeNo");

		map = new ModelMap();

		try {
			ArrayList<ImageTO> detailCodeList = compInfoService.getDetailItemList(itemGroupCodeNo);

			map.put("detailCodeList", detailCodeList);
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
