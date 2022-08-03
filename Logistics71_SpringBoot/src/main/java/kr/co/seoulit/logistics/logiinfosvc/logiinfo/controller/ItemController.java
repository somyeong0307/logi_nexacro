package kr.co.seoulit.logistics.logiinfosvc.logiinfo.controller;

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

import kr.co.seoulit.logistics.logiinfosvc.logiinfo.service.LogiInfoService;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemGroupTO;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemInfoTO;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/logiinfo/*")
public class ItemController {

	@Autowired
	private LogiInfoService logiInfoService;
	
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	ModelMap map = null;

	private static Gson gson = new GsonBuilder().serializeNulls().create();

	
	@RequestMapping(value="/item/standardunitprice")
	public void getStandardUnitPrice(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PlatformData reqData = (PlatformData) request.getAttribute("reqData");
		PlatformData resData = (PlatformData) request.getAttribute("resData");
		
		String itemCode = reqData.getVariable("itemCode").getString();

		ItemTO itemTo = logiInfoService.getStandardUnitPrice(itemCode);

		datasetBeanMapper.beanToDataset(resData, itemTo, ItemTO.class);
		 
	}
	
	
	@RequestMapping(value="/item/list", method=RequestMethod.GET)
	public ModelMap searchItem(HttpServletRequest request, HttpServletResponse response) {
		String searchCondition = request.getParameter("searchCondition");
		String itemClassification = request.getParameter("itemClassification");
		String itemGroupCode = request.getParameter("itemGroupCode");
		String minPrice = request.getParameter("minPrice");
		String maxPrice = request.getParameter("maxPrice");
		map = new ModelMap();

		ArrayList<ItemInfoTO> itemInfoList = null;
		String[] paramArray = null;
		try {
			switch (searchCondition) {
				case "ALL":
					paramArray = null;
					break;
				case "ITEM_CLASSIFICATION":
					paramArray = new String[] { itemClassification };
					break;
				case "ITEM_GROUP_CODE":
					paramArray = new String[] { itemGroupCode };
					break;
				case "STANDARD_UNIT_PRICE":
					paramArray = new String[] { minPrice, maxPrice };
					break;
			}

			itemInfoList = logiInfoService.getItemInfoList(searchCondition, paramArray);

			map.put("gridRowJson", itemInfoList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");

		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());

		}  
		return map;
	}
	
	
	@RequestMapping(value="/item/standardunitprice-box", method=RequestMethod.POST)
	public ModelMap getStandardUnitPriceBox(HttpServletRequest request, HttpServletResponse response) {
		String itemCode = request.getParameter("itemCode");
		map = new ModelMap();
		int price = 0;
		try {
			price = logiInfoService.getStandardUnitPriceBox(itemCode);

			map.put("gridRowJson", price);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		} 
		return map;
	}
	
	@RequestMapping(value="/item/batch", method=RequestMethod.POST)
	public ModelMap batchListProcess(HttpServletRequest request, HttpServletResponse response) {
		String batchList = request.getParameter("batchList");
		map = new ModelMap();
		ArrayList<ItemTO> itemTOList = gson.fromJson(batchList, new TypeToken<ArrayList<ItemTO>>() {
		}.getType());
		try {
			HashMap<String, Object> resultMap = logiInfoService.batchItemListProcess(itemTOList);

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

	
	// 품목조회
	@RequestMapping(value = "/searchItemList")
	public void searchItemList(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		ArrayList<ItemGroupTO> searchItem = logiInfoService.searchItemList();
		datasetBeanMapper.beansToDataset(resData, searchItem, ItemGroupTO.class);
		
	}
	
	
	// 품목상세조회
	@RequestMapping(value = "/searchItemDetailList")
	public void searchItemDetailList(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {
		
		String eNumber = reqData.getVariable("eNumber").getString();
		ArrayList<ItemTO> searchParticularsItem = logiInfoService.getitemInfoList(eNumber);

		datasetBeanMapper.beansToDataset(resData, searchParticularsItem, ItemTO.class);
	}
	
	
	//품목그룹조회
	@RequestMapping(value="/item/group-list" , method=RequestMethod.GET)
	public ModelMap searchitemGroupList(HttpServletRequest request, HttpServletResponse response) {
		String ableContractInfo =request.getParameter("ableContractInfo");
		map = new ModelMap();

		HashMap<String,String> ableSearchConditionInfo = gson.fromJson(ableContractInfo, new TypeToken<HashMap<String,String>>() {
		}.getType());
		ArrayList<ItemGroupTO> itemGroupList = null;
		try {
			itemGroupList = logiInfoService.getitemGroupList(ableSearchConditionInfo);
	
			map.put("gridRowJson", itemGroupList);
			map.put("errorCode", 0);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		} 
		return map;
	}


	  //품목그룹삭제
	@RequestMapping(value="/item/group" , method=RequestMethod.DELETE)
	public ModelMap deleteItemGroup(HttpServletRequest request, HttpServletResponse response) {
		  String ableContractInfo =request.getParameter("ableContractInfo");
		  map = new ModelMap();
		  
		  HashMap<String,String> ableSearchConditionInfo =
		  gson.fromJson(ableContractInfo, new TypeToken<HashMap<String,String>>() {}.getType());
		try {
			logiInfoService.getdeleteitemgroup(ableSearchConditionInfo);
			map.put("errorCode", 0); 
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		} 
		return map;
	}


	//일괄저장
	@RequestMapping(value="/item/batchsave")
	public void itemBatchSave(@RequestAttribute("reqData") PlatformData reqData) throws Exception {
		
		ArrayList<ItemTO> itemDetailList = (ArrayList<ItemTO>) datasetBeanMapper.datasetToBeans(reqData, ItemTO.class);
		logiInfoService.getbatchSave(itemDetailList);
		
	}
	
}
