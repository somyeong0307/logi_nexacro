package kr.co.seoulit.logistics.purcstosvc.purchase.controller;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.purcstosvc.purchase.service.PurchaseService;
import kr.co.seoulit.logistics.purcstosvc.purchase.to.OrderDialogTempTO;
import kr.co.seoulit.logistics.purcstosvc.purchase.to.OrderInfoTO;
import kr.co.seoulit.logistics.purcstosvc.purchase.to.OrderTempTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping("/purchase/*")
public class OrderController {
	
	@Autowired
	private PurchaseService purchaseService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	ModelMap map = null;

	
	
	@RequestMapping(value="/orderinfo")
	public void checkOrderInfo(@RequestAttribute("reqData")PlatformData reqData,
									@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String orderNoList = reqData.getVariableList().getString("orderNoList");
		
		ArrayList<String> orderNoArr = new ArrayList<>();
		orderNoArr.add(orderNoList);
			
		HashMap<String,Object> map = purchaseService.checkOrderInfo(orderNoArr);
		
		resData.getVariableList().add("g_procedureMsg", map.get("errorMsg"));
		resData.getVariableList().add("g_procedureCode", map.get("errorCode"));
		
	}
	
	
	@RequestMapping(value = "/order/list")
	public void getOrderList(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {

		String startDate = reqData.getVariable("startDate").getString();
		String endDate = reqData.getVariable("endDate").getString();

		HashMap<String, Object> resultMap = purchaseService.getOrderList(startDate, endDate);
		
		@SuppressWarnings("unchecked")
		ArrayList<OrderTempTO> OrderList = (ArrayList<OrderTempTO>) resultMap.get("gridRowJson");
		System.out.println("쨘"+OrderList);
		datasetBeanMapper.beansToDataset(resData, OrderList, OrderTempTO.class);
	}

	
	// 모의재고 처리및 취합발주
	@RequestMapping(value = "/order/dialog")
	public void openOrderDialog(@RequestAttribute("reqData") PlatformData reqData,
			@RequestAttribute("resData") PlatformData resData) throws Exception {

		String mrpGatheringNoListStr = reqData.getVariable("mrpGatheringNoList").getString();
		HashMap<String, Object> resultMap =  purchaseService.getOrderDialogInfo(mrpGatheringNoListStr);

		@SuppressWarnings("unchecked")
		//발주필요목록조회 취합 발주 
		ArrayList<OrderDialogTempTO> orderDialogList = (ArrayList<OrderDialogTempTO>)resultMap.get("orderDialogInfoList");
		
		datasetBeanMapper.beansToDataset(resData, orderDialogList, OrderDialogTempTO.class);
	}

	
	@RequestMapping(value="/order/info")
	public void showOrderInfo(@RequestAttribute("reqData")PlatformData reqData,
            							@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String startDate = reqData.getVariable("startDate").getString();
		String endDate = reqData.getVariable("endDate").getString();

		ArrayList<OrderInfoTO> orderInfoList = purchaseService.getOrderInfoList(startDate,endDate);
		
		datasetBeanMapper.beansToDataset(resData, orderInfoList, OrderInfoTO.class);
		
	}
	
	
	@RequestMapping(value="/order/delivery")
	public void searchOrderInfoListOnDelivery(@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		ArrayList<OrderInfoTO> orderInfoListOnDelivery = purchaseService.getOrderInfoListOnDelivery();
		datasetBeanMapper.beansToDataset(resData, orderInfoListOnDelivery, OrderInfoTO.class);
		
	}

	
	//발주 및 재고처리
	@RequestMapping(value="/order")
	public void order(@RequestAttribute("reqData")PlatformData reqData) throws Exception {
		
		String mrpGatheringNoList = reqData.getVariableList().getString("mrpGatheringNoList");
		ArrayList<String> mrpGaNoArr = new ArrayList<>();
		mrpGaNoArr.add(mrpGatheringNoList);	
		purchaseService.order(mrpGaNoArr);
		
	}


}
