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

	//견적등록
	@Override
	public ModelMap addNewEstimate(String estimateDate, EstimateTO newEstimateTO) {

		ModelMap resultMap = new ModelMap();

		int cnt = 1;

		// 새로운 견적일련번호 생성
		String newEstimateNo = getNewEstimateNo(estimateDate);		
		System.out.println("디버깅확인하기"+newEstimateNo);
		// 뷰단에서 보내온 견적 Bean 에 새로운 견적일련번호 set  view단에서  올때는 null 상태
		newEstimateTO.setEstimateNo(newEstimateNo);
		
		// 견적상세 Bean 을 Insert
		List<EstimateDetailTO> estimateDetailTOList = newEstimateTO.getEstimateDetailTOList();
		
		StringBuffer newEstimateDetailNoInsert = new StringBuffer();
		// 견적상세 List - 견적상세 bean 
		for (EstimateDetailTO bean : estimateDetailTOList) {
			
			// 견적상세일번호 생성
			StringBuffer newEstimateDetailNo = new StringBuffer();
			newEstimateDetailNo.append("ES");
			newEstimateDetailNo.append(newEstimateNo);
			newEstimateDetailNo.append("-"); 
			newEstimateDetailNo.append(String.format("%02d", cnt++));	
			bean.setEstimateDetailNo(newEstimateDetailNo.toString());
			System.out.println("디버깅확인하기"+newEstimateNo);
			// jpa에서 대신 넣어준다.
    		bean.setEstimateNo(newEstimateNo);
		
			// 새로 생성된 견적상세일련번호를 저장
			newEstimateDetailNoInsert.append(newEstimateDetailNo.toString()+",");
		
		}
			
		//jpa 이용 견적TO INSERT
		estimateRepository.save(newEstimateTO);
		
		// 결과 맵에 "INSERT" 키값으로 새로 생성된 견적상세일련번호 리스트 저장
		resultMap.put("INSERT", newEstimateDetailNoInsert);

		// 결과 맵에 "newEstimateNo" 키값으로 새로 생성된 견적일련번호 저장
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
				//기존의 값을 수정했을 경우
			case "delete":
				//jpa 적용
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
		
		// 새로운 수주일련번호 생성
		String newContractNo = getNewContractNo(contractDate); //CO + contractDate + 01 <= 01은 첫번째라는 뜻 2번째이며 02 로 부여가 됨
		workingContractBean.setContractNo(newContractNo); // 새로운 수주일련번호 세팅
		workingContractBean.setContractDate(contractDate); // 뷰에서 전달한 수주일자 세팅
		
		// jpa - ContractTO만 적용 (contractDetailTO - procedure 호출로 저장)
		contractRepository.save(workingContractBean);

		// 견적 테이블에 수주여부 "Y" 로 수정
		Optional<EstimateTO> estimateTo = estimateRepository.findByEstimateNo(workingContractBean.getEstimateNo());	
			estimateTo.ifPresent(estimateToUpdate ->{
				estimateToUpdate.setContractStatus("Y");
				estimateRepository.save(estimateToUpdate);
			});
		
		// ContractDetail 저장
		// jpa 미구현 - procedure 호출
		HashMap<String, Object> map = new HashMap<>();
		map.put("estimateNo", workingContractBean.getEstimateNo());//--견적상세테이블 조회시 사용
		map.put("contractNo", newContractNo); // 수주상세번호 만들때 사용 
		map.put("contractType", workingContractBean.getContractType()); //STOCK_AMOUNT 구하기위해 

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
