package kr.co.seoulit.logistics.busisvc.logisales.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import kr.co.seoulit.logistics.busisvc.logisales.mapper.ContractMapper;
import kr.co.seoulit.logistics.busisvc.logisales.mapper.EstimateMapper;
import kr.co.seoulit.logistics.busisvc.logisales.repository.ContractRepository;
import kr.co.seoulit.logistics.busisvc.logisales.repository.EstimateDetailRepository;
import kr.co.seoulit.logistics.busisvc.logisales.repository.EstimateRepository;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractInfoTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateDetailTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class LogisalesServiceImpl implements LogisalesService {
	
	
	private final ContractMapper contractMapper;
	private final EstimateMapper estimateMapper;
	private final EstimateRepository estimateRepository;
	private final EstimateDetailRepository estimateDetailRepository;
	private final ContractRepository contractRepository;
	
	@Override
	public ArrayList<EstimateTO> getEstimateList(String dateSearchCondition, String startDate, String endDate) {

		ArrayList<EstimateTO> estimateTOList = null;
		
		HashMap<String, String> map = new HashMap<>();
		
		map.put("dateSearchCondition", dateSearchCondition);
		map.put("startDate", startDate);
		map.put("endDate", endDate);

		estimateTOList = estimateMapper.selectEstimateList(map);

		return estimateTOList;
	}

	@Override
	public ArrayList<EstimateDetailTO> getEstimateDetailList(String estimateNo) {

		ArrayList<EstimateDetailTO> estimateDetailTOList = null;

		estimateDetailTOList = estimateMapper.selectEstimateDetailList(estimateNo);

		return estimateDetailTOList;
	}

	//????????????
	@Override
	public ModelMap addNewEstimate(String estimateDate, EstimateTO newEstimateTO) {

		ModelMap resultMap = new ModelMap();

		int cnt = 1;

		// ????????? ?????????????????? ??????
		String newEstimateNo = getNewEstimateNo(estimateDate);		
		System.out.println("?????????????????????"+newEstimateNo);
		// ???????????? ????????? ?????? Bean ??? ????????? ?????????????????? set  view?????????  ????????? null ??????
		newEstimateTO.setEstimateNo(newEstimateNo);
		
		// ???????????? Bean ??? Insert
		List<EstimateDetailTO> estimateDetailTOList = newEstimateTO.getEstimateDetailTOList();
		
		StringBuffer newEstimateDetailNoInsert = new StringBuffer();
		// ???????????? List - ???????????? bean 
		for (EstimateDetailTO bean : estimateDetailTOList) {
			
			// ????????????????????? ??????
			StringBuffer newEstimateDetailNo = new StringBuffer();
			newEstimateDetailNo.append("ES");
			newEstimateDetailNo.append(newEstimateNo);
			newEstimateDetailNo.append("-"); 
			newEstimateDetailNo.append(String.format("%02d", cnt++));	
			bean.setEstimateDetailNo(newEstimateDetailNo.toString());
			System.out.println("?????????????????????"+newEstimateNo);
			// jpa?????? ?????? ????????????.
    		bean.setEstimateNo(newEstimateNo);
		
			// ?????? ????????? ??????????????????????????? ??????
			newEstimateDetailNoInsert.append(newEstimateDetailNo.toString()+",");
		
		}
			
		//jpa ?????? ??????TO INSERT
		estimateRepository.save(newEstimateTO);
		
		// ?????? ?????? "INSERT" ???????????? ?????? ????????? ???????????????????????? ????????? ??????
		resultMap.put("INSERT", newEstimateDetailNoInsert);

		// ?????? ?????? "newEstimateNo" ???????????? ?????? ????????? ?????????????????? ??????
		resultMap.put("newEstimateNo", newEstimateNo);

		return resultMap;
	}
	

	public String getNewEstimateNo(String estimateDate) {

		StringBuffer newEstimateNo = null;

		int i = estimateMapper.selectEstimateCount(estimateDate);

		newEstimateNo = new StringBuffer();
		newEstimateNo.append("ES");
		newEstimateNo.append(estimateDate.replace("-", ""));
		newEstimateNo.append(String.format("%02d", i)); 
			
		return newEstimateNo.toString();
	}
	
	public String getNewEstimateDetailNo(String estimateNo) {

		StringBuffer newEstimateDetailNo = null;

		int i = estimateMapper.selectEstimateDetailSeq(estimateNo);

		newEstimateDetailNo = new StringBuffer();
		newEstimateDetailNo.append("ES");
		newEstimateDetailNo.append(estimateNo.replace("-", ""));
		newEstimateDetailNo.append("-"); 
		newEstimateDetailNo.append(String.format("%02d", i));		   

		return newEstimateDetailNo.toString();
	}

	@Override
	public ModelMap removeEstimate(String estimateNo, String status) {

		ModelMap resultMap = null;

		estimateMapper.deleteEstimate(estimateNo);
			
		ArrayList<EstimateDetailTO> estimateDetailTOList = getEstimateDetailList(estimateNo);
			
		for (EstimateDetailTO bean : estimateDetailTOList) {
				
			bean.setStatus(status);
				
		}
			
		resultMap = batchEstimateDetailListProcess(estimateDetailTOList);

		resultMap.put("removeEstimateNo", estimateNo);

		return resultMap;
	}

	@Override
	public ModelMap batchEstimateDetailListProcess(ArrayList<EstimateDetailTO> estimateDetailTOList) {
		
		ModelMap resultMap = new ModelMap();
		
		for (EstimateDetailTO bean : estimateDetailTOList) {

			String status = bean.getStatus();
			switch (status) {

			
			case "update":
				estimateDetailRepository.save(bean);
				break;
				//????????? ?????? ???????????? ??????
			case "delete":
				//jpa ??????
				estimateDetailRepository.delete(bean);
			   
				break;
			}

		}

		return resultMap;
	}

	
	@Override
	public ArrayList<ContractInfoTO> getContractList(HashMap<String, String> map) {
		 
		 ArrayList<ContractInfoTO> contractInfoTOList = contractMapper.selectContractList(map);
		 return contractInfoTOList;
		 
	}

	
	@Override
	public ArrayList<ContractDetailTO> getContractDetailList(String contractNo) {

		ArrayList<ContractDetailTO> contractDetailTOList = null;

		contractDetailTOList = contractMapper.selectContractDetailList(contractNo);

		return contractDetailTOList;
	}

	@Override
	public ArrayList<EstimateTO> getEstimateListInContractAvailable(String startDate, String endDate) {

		ArrayList<EstimateTO> estimateListInContractAvailable = null;
		HashMap<String, String> map = new HashMap<>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);

		estimateListInContractAvailable = contractMapper.selectEstimateListInContractAvailable(map);

		for (EstimateTO bean : estimateListInContractAvailable) {

			bean.setEstimateDetailTOList(estimateMapper.selectEstimateDetailList(bean.getEstimateNo()));//ES2022011360

		}

		return estimateListInContractAvailable;
	}

	@Override
	public HashMap<String, Object> addNewContract(String contractDate, String personCodeInCharge,
			ContractTO workingContractBean) {

		HashMap<String, Object> resultMap = new HashMap<>();
		
		// ????????? ?????????????????? ??????
		String newContractNo = getNewContractNo(contractDate); //CO + contractDate + 01 <= 01??? ??????????????? ??? 2???????????? 02 ??? ????????? ???
		workingContractBean.setContractNo(newContractNo); // ????????? ?????????????????? ??????
		workingContractBean.setContractDate(contractDate); // ????????? ????????? ???????????? ??????
		
		// jpa - ContractTO??? ?????? (contractDetailTO - procedure ????????? ??????)
		contractRepository.save(workingContractBean);

		// ?????? ???????????? ???????????? "Y" ??? ??????
		Optional<EstimateTO> estimateTo = estimateRepository.findByEstimateNo(workingContractBean.getEstimateNo());	
			estimateTo.ifPresent(estimateToUpdate ->{
				estimateToUpdate.setContractStatus("Y");
				estimateRepository.save(estimateToUpdate);
			});
		
		// ContractDetail ??????
		// jpa ????????? - procedure ??????
		HashMap<String, Object> map = new HashMap<>();
		map.put("estimateNo", workingContractBean.getEstimateNo());//--????????????????????? ????????? ??????
		map.put("contractNo", newContractNo); // ?????????????????? ????????? ?????? 
		map.put("contractType", workingContractBean.getContractType()); //STOCK_AMOUNT ??????????????? 

		contractMapper.insertContractDetail(map);
		
		resultMap.put("errorCode", map.get("ERROR_CODE"));
		resultMap.put("errorMsg", map.get("ERROR_MSG"));
		
		return resultMap;
	}
	

	public String getNewContractNo(String contractDate) {
		
		StringBuffer newContractNo = null;

		int i = contractMapper.selectContractCount(contractDate);
		newContractNo = new StringBuffer();
		newContractNo.append("CO"); //CO
		newContractNo.append(contractDate.replace("-", "")); 
		newContractNo.append(String.format("%02d", i));

		return newContractNo.toString();
	}
	
	@Override
	public ModelMap batchContractDetailListProcess(ArrayList<ContractDetailTO> contractDetailTOList) {

		ModelMap resultMap = new ModelMap();

		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();
		
		for (ContractDetailTO bean : contractDetailTOList) {

			String status = bean.getStatus();

			switch (status) {

			case "INSERT":
				
				//contractMapper.insertContractDetail(bean);
				insertList.add(bean.getContractDetailNo());

				break;

			case "UPDATE":
				
				//contractMapper.updateContractDetail(bean);
				updateList.add(bean.getContractDetailNo());

				break;
					
			case "DELETE":

				contractMapper.deleteContractDetail(bean);
				deleteList.add(bean.getContractDetailNo());

				break;

			}

		}

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	@Override
	public void changeContractStatusInEstimate(String estimateNo, String contractStatus) {

		HashMap<String, String> map = new HashMap<>();

		map.put("estimateNo", estimateNo);
		map.put("contractStatus", contractStatus);
		
		estimateMapper.changeContractStatusOfEstimate(map);

	}

	
}
