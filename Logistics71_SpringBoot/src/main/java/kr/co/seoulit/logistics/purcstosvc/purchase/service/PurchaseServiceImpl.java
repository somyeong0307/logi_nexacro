package kr.co.seoulit.logistics.purcstosvc.purchase.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import kr.co.seoulit.logistics.purcstosvc.purchase.mapper.OrderMapper;
import kr.co.seoulit.logistics.purcstosvc.purchase.mapper.OutSourcingMapper;
import kr.co.seoulit.logistics.purcstosvc.purchase.repository.OrderRepository;
import kr.co.seoulit.logistics.purcstosvc.purchase.to.OrderInfoTO;
import kr.co.seoulit.logistics.purcstosvc.purchase.to.OutSourcingTO;

@Service
public class PurchaseServiceImpl implements PurchaseService{

	@Autowired
	private OutSourcingMapper outSourcingMapper;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public ArrayList<OutSourcingTO> searchOutSourcingList(String fromDate, String toDate, String customerCode,
			String itemCode, String materialStatus) {

		ArrayList<OutSourcingTO> OutSourcingList = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("fromDate", fromDate);
		map.put("toDate", toDate);
		map.put("customerCode", customerCode);
		map.put("itemCode", itemCode);
		map.put("materialStatus", materialStatus);

		OutSourcingList = outSourcingMapper.selectOutSourcingList(map);

		return OutSourcingList;
	}

	
	@Override
	public HashMap<String,Object> getOrderList(String startDate, String endDate) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		
		orderMapper.getOrderList(map);
		
		HashMap<String, Object> resultMap = new HashMap<>();
		
		resultMap.put("gridRowJson", map.get("RESULT"));
		resultMap.put("errorCode",map.get("ERROR_CODE"));
		resultMap.put("errorMsg", map.get("ERROR_MSG"));

		return resultMap;
	}

	
	@Override
	public HashMap<String,Object> getOrderDialogInfo(String mrpGatheringNoListStr) {
		
		HashMap<String, String> map = new HashMap<>();
		String mrpGatheringNoList = mrpGatheringNoListStr.replace("[", "").replace("]", "").replace("\"", "");
		map.put("mrpGatheringNoList", mrpGatheringNoList);
			
		orderMapper.getOrderDialogInfo(map);
		
		HashMap<String,Object> resultMap = new HashMap<>();			
		resultMap.put("orderDialogInfoList", map.get("RESULT"));

		return resultMap;

	}

	@Override
	public HashMap<String,Object> order(ArrayList<String> mrpGaNoArr) {

		HashMap<String, String> map = new HashMap<>();
		String mrpGaNoList = mrpGaNoArr.toString().replace("[", "").replace("]", "");
		map.put("mrpGaNoList", mrpGaNoList);
		
		orderMapper.order(map);
		
		HashMap<String,Object> resultMap = new HashMap<>();		
    	resultMap.put("errorCode",map.get("ERROR_CODE"));
    	resultMap.put("errorMsg", map.get("ERROR_MSG")); 
    	return resultMap;

	}

	@Override
	public ModelMap optionOrder(String itemCode, String itemAmount) {

		ModelMap resultMap = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("itemCode", itemCode);
		map.put("itemAmount", itemAmount);


		resultMap = orderMapper.optionOrder(map);

		return resultMap;

	}

	
	@Override
	public ArrayList<OrderInfoTO> getOrderInfoListOnDelivery() {

		List<String> orderInfoStatusList = new ArrayList<>();
		orderInfoStatusList.add("운송중");
		orderInfoStatusList.add("검사완료");

		List<OrderInfoTO> orderInfoTOList = orderRepository.findByOrderInfoStatusIn(orderInfoStatusList);

		return new ArrayList<>(orderInfoTOList);

	}

	
	@Override
	public ArrayList<OrderInfoTO> getOrderInfoList(String startDate, String endDate) {

		List<OrderInfoTO> orderInfoList = orderRepository.findByOrderDateBetween(startDate, endDate);
		System.out.println("@@@@@@@@@@@@2 orderInfoList " + orderInfoList);
		return new ArrayList<>(orderInfoList);		

	}

	@Override
	public HashMap<String,Object> checkOrderInfo(ArrayList<String> orderNoArr) {
		
		HashMap<String,String> params = new HashMap<>();
		String orderNoList = orderNoArr.toString().replace("[", "").replace("]", "");
		params.put("orderNoList", orderNoList);
		
		orderMapper.updateOrderInfo(params);

		HashMap<String,Object> resultMap = new HashMap<>();
		resultMap.put("errorCode",params.get("ERROR_CODE"));
    	resultMap.put("errorMsg", params.get("ERROR_MSG"));

		return resultMap;
	}

}
