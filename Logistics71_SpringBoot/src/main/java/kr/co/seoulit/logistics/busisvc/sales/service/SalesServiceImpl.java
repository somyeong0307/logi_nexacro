package kr.co.seoulit.logistics.busisvc.sales.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import kr.co.seoulit.logistics.busisvc.logisales.mapper.ContractMapper;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractInfoTO;
import kr.co.seoulit.logistics.busisvc.sales.mapper.DeliveryMapper;
import kr.co.seoulit.logistics.busisvc.sales.mapper.SalesPlanMapper;
import kr.co.seoulit.logistics.busisvc.sales.to.DeliveryInfoTO;
import kr.co.seoulit.logistics.busisvc.sales.to.SalesPlanTO;

@Service
public class SalesServiceImpl implements SalesService {
	
	@Autowired
	private ContractMapper contractMapper;
	@Autowired
	private SalesPlanMapper salesPlanMapper;
	@Autowired
	private DeliveryMapper deliveryMapper;
	

	@Override
	public ArrayList<ContractInfoTO> getDeliverableContractList(HashMap<String, String> map) {

		ArrayList<ContractInfoTO> contractInfoTOList = contractMapper.selectDeliverableContractList(map);
	      for (ContractInfoTO bean : contractInfoTOList) { // 해당 수주의 수주상세 리스트 세팅 

	          bean.setContractDetailTOList(contractMapper.selectDeliverableContractDetailList(bean.getContractNo()));

	       }

		return contractInfoTOList;
	}
	
	
	@Override
	public ArrayList<SalesPlanTO> getSalesPlanList(String dateSearchCondition, String startDate, String endDate) {

		ArrayList<SalesPlanTO> salesPlanTOList = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("dateSearchCondition", dateSearchCondition);
		map.put("startDate", startDate);
		map.put("endDate", endDate);

		salesPlanTOList = salesPlanMapper.selectSalesPlanList(map);
		
		return salesPlanTOList;
	}

	@Override
	public ModelMap batchSalesPlanListProcess(ArrayList<SalesPlanTO> salesPlanTOList) {

		ModelMap resultMap = new ModelMap();
		
		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();

		for (SalesPlanTO bean : salesPlanTOList) {

			String status = bean.getStatus();

			switch (status) {

			case "INSERT":

				String newSalesPlanNo = getNewSalesPlanNo(bean.getSalesPlanDate());

				bean.setSalesPlanNo(newSalesPlanNo);

				salesPlanMapper.insertSalesPlan(bean);

				insertList.add(newSalesPlanNo);

				break;

			case "UPDATE":

				salesPlanMapper.updateSalesPlan(bean);

				updateList.add(bean.getSalesPlanNo());
				
				break;

			case "DELETE":

				salesPlanMapper.deleteSalesPlan(bean);

				deleteList.add(bean.getSalesPlanNo());

				break;

			}

		}

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	public String getNewSalesPlanNo(String salesPlanDate) {

		StringBuffer newEstimateNo = null;

		int newNo = salesPlanMapper.selectSalesPlanCount(salesPlanDate);

		newEstimateNo = new StringBuffer();

		newEstimateNo.append("SA");
		newEstimateNo.append(salesPlanDate.replace("-", ""));
		newEstimateNo.append(String.format("%02d", newNo)); // 2자리 숫자
		
		return newEstimateNo.toString();
	}

	@Override
	public ArrayList<DeliveryInfoTO> getDeliveryInfoList() {

		ArrayList<DeliveryInfoTO> deliveryInfoList = null;

		deliveryInfoList = deliveryMapper.selectDeliveryInfoList();

		return deliveryInfoList;
	}

	@Override
	public ModelMap batchDeliveryListProcess(List<DeliveryInfoTO> deliveryTOList) {
		
		ModelMap resultMap = new ModelMap();
		
		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();

		for (DeliveryInfoTO bean : deliveryTOList) {

			String status = bean.getStatus();

			switch (status.toUpperCase()) {

			case "INSERT":

				String newDeliveryNo = "새로운";

				bean.setDeliveryNo(newDeliveryNo);
				deliveryMapper.insertDeliveryResult(bean);
				insertList.add(newDeliveryNo);
					
				break;

			case "UPDATE":

				deliveryMapper.updateDeliveryResult(bean);

				updateList.add(bean.getDeliveryNo());

				break;

			case "DELETE":

				deliveryMapper.deleteDeliveryResult(bean);

				deleteList.add(bean.getDeliveryNo());
				
				break;

			}

		}

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	@Override
	public HashMap<String,Object> deliver(String contractDetailNo) {
		
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("contractDetailNo", contractDetailNo);
        
        deliveryMapper.deliver(map);
        
        HashMap<String,Object> resultMap = new ModelMap();
		resultMap.put("errorCode", map.get("ERROR_CODE"));
		resultMap.put("errorMsg", map.get("ERROR_MSG"));
		
		return resultMap;
	}
	
}
