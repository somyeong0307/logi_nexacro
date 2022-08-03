package kr.co.seoulit.logistics.prodcsvc.production.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import kr.co.seoulit.logistics.busisvc.logisales.mapper.ContractMapper;
import kr.co.seoulit.logistics.busisvc.logisales.repository.ContractDetailRepository;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailInMpsAvailableTO;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailTO;
import kr.co.seoulit.logistics.busisvc.sales.mapper.SalesPlanMapper;
import kr.co.seoulit.logistics.prodcsvc.production.mapper.MpsMapper;
import kr.co.seoulit.logistics.prodcsvc.production.mapper.MrpMapper;
import kr.co.seoulit.logistics.prodcsvc.production.repository.MpsRepository;
import kr.co.seoulit.logistics.prodcsvc.production.repository.MrpGatheringRepository;
import kr.co.seoulit.logistics.prodcsvc.production.repository.MrpRepository;
import kr.co.seoulit.logistics.prodcsvc.production.to.MpsTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpGatheringTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpInsertInfoTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.OpenMrpTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.SalesPlanInMpsAvailableTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProductionServiceImpl implements ProductionService {
	
	private final MpsMapper mpsMapper;
	private final ContractMapper contractMapper;
	private final SalesPlanMapper salesPlanMapper;
	private final MrpMapper mrpMapper;
	private final MpsRepository mpsRepository;
	private final ContractDetailRepository contractDetailRepository;
	private final MrpRepository mrpRepository;
	private final  MrpGatheringRepository mrpGatheringRepository;
	
	@Override
	public ArrayList<MpsTO> getMpsList(String startDate, String endDate, String includeMrpApply) {
		
		ArrayList<MpsTO> selectMpsList = null;
		
		if(includeMrpApply.equals("excludeMrpApply")) {
			//mrp적용상태가 null인놈 찾기
			selectMpsList = mpsRepository.findByMpsPlanDateBetweenAndMrpApplyStatusIsNull(startDate, endDate);
		}else { 
			//날짜만으로 검색
			selectMpsList = mpsRepository.findByMpsPlanDateBetween(startDate, endDate);
		}
		
		return selectMpsList;
	}
	

	@Override
	public ArrayList<ContractDetailInMpsAvailableTO> getContractDetailListInMpsAvailable(String searchCondition,
			String startDate, String endDate) {

		HashMap<String, String> map = new HashMap<>();
		map.put("searchCondition", searchCondition);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		return contractMapper.selectContractDetailListInMpsAvailable(map);
	}

	@Override
	public ArrayList<SalesPlanInMpsAvailableTO> getSalesPlanListInMpsAvailable(String searchCondition,
			String startDate, String endDate) {

		ArrayList<SalesPlanInMpsAvailableTO> salesPlanInMpsAvailableList = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("searchCondition", searchCondition);
		map.put("startDate", startDate);
		map.put("endDate", endDate);

		salesPlanInMpsAvailableList = salesPlanMapper.selectSalesPlanListInMpsAvailable(map);

		return salesPlanInMpsAvailableList;

	}

	
	@Override
	public List<MpsTO> convertContractDetailToMps(
			ArrayList<ContractDetailInMpsAvailableTO> contractDetailInMpsAvailableList) {

		List<MpsTO> insertList = new ArrayList<MpsTO>();

		// getNewMpsNo()메서드를 쓰지 않는 이유는 이렇게하면 커리문이 한번만 나가는데 이전에는 for문안으로 들어가여 여러번 동일한
		// 커리문을 호출하였음!
		ContractDetailInMpsAvailableTO contractDetail = contractDetailInMpsAvailableList.get(0);
		MpsTO mps = mpsMapper.getNewMpsNo(contractDetail.getMpsPlanDate());
		int no = 1;
		if (mps != null) {
			String mpsNo = mps.getMpsNo();
			no = Integer.parseInt(mpsNo.substring(mpsNo.length() - 2, mpsNo.length())) + 1;
		}

		StringBuffer newEstimateNo = new StringBuffer();
		// MPS 에 등록할 수주상세 Bean 의 정보를 새로운 MPS Bean 에 세팅, status : "INSERT"
		for (ContractDetailInMpsAvailableTO bean : contractDetailInMpsAvailableList) {

			MpsTO newMpsBean = new MpsTO();
			// 수주상세번호 생성 mps

			newEstimateNo.append("PS");
			newEstimateNo.append(bean.getMpsPlanDate().replace("-", ""));
			newEstimateNo.append(String.format("%02d", no++));

			newMpsBean.setMpsPlanClassification(bean.getPlanClassification());
			newMpsBean.setContractDetailNo(bean.getContractDetailNo());
			newMpsBean.setItemCode(bean.getItemCode());
			newMpsBean.setItemName(bean.getItemName());
			newMpsBean.setUnitOfMps(bean.getUnitOfContract());
			newMpsBean.setMpsPlanDate(bean.getMpsPlanDate());
			newMpsBean.setMpsPlanAmount(bean.getProductionRequirement());
			newMpsBean.setDueDateOfMps(bean.getDueDateOfContract());
			newMpsBean.setScheduledEndDate(bean.getScheduledEndDate());
			newMpsBean.setDescription(bean.getDescription());
			newMpsBean.setMpsNo(newEstimateNo.toString());// 주생산계획번호 저장 pk

			insertList.add(newMpsBean);

			// jpa 사용

			mpsRepository.save(newMpsBean);

			// 초기화 <이렇게하면 속도가 좋다고함>
			newEstimateNo.delete(0, newEstimateNo.length());

			// MPS TO 의 수주상세번호가 존재하면, 수주상세 테이블에서 해당 번호의 MPS처리상태 (PROCESSING_STATUS)를 'Y' 로
			// 변경
			if (bean.getContractDetailNo() != null) {

				// jpa사용
				Optional<ContractDetailTO> contractDetailNo = contractDetailRepository
						.findByContractDetailNo(bean.getContractDetailNo());

				contractDetailNo.ifPresent(contractDetailUpdate -> {
					contractDetailUpdate.setProcessingStatus("Y"); // Mps처리상태를 기존 null에서 Y로
					contractDetailRepository.save(contractDetailUpdate);
				});
			}
		}
		return insertList;
	}

	
	@Override
	public HashMap<String, Object> convertSalesPlanToMps(
			ArrayList<SalesPlanInMpsAvailableTO> salesPlanInMpsAvailableList) {

		HashMap<String, Object> resultMap = null;

		ArrayList<MpsTO> mpsTOList = new ArrayList<>();

		MpsTO newMpsBean = null;

		for (SalesPlanInMpsAvailableTO bean : salesPlanInMpsAvailableList) {

			newMpsBean = new MpsTO();

			newMpsBean.setStatus("INSERT");

			newMpsBean.setMpsPlanClassification(bean.getPlanClassification());
			newMpsBean.setSalesPlanNo(bean.getSalesPlanNo());
			newMpsBean.setItemCode(bean.getItemCode());
			newMpsBean.setItemName(bean.getItemName());
			newMpsBean.setUnitOfMps(bean.getUnitOfSales());
			newMpsBean.setMpsPlanDate(bean.getMpsPlanDate());
			newMpsBean.setMpsPlanAmount(bean.getSalesAmount());
			newMpsBean.setDueDateOfMps(bean.getDueDateOfSales());
			newMpsBean.setScheduledEndDate(bean.getScheduledEndDate());
			newMpsBean.setDescription(bean.getDescription());

			mpsTOList.add(newMpsBean);

		}

		resultMap = batchMpsListProcess(mpsTOList);

		return resultMap;

	}

	@Override
	public HashMap<String, Object> batchMpsListProcess(ArrayList<MpsTO> mpsTOList) {

		HashMap<String, Object> resultMap = new HashMap<>();
		
		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();

		for (MpsTO bean : mpsTOList) {

			String status = bean.getStatus();
							
			switch (status) {

			case "INSERT":

				String newMpsNo = getNewMpsNo(bean.getMpsPlanDate());

				bean.setMpsNo(newMpsNo);

				mpsMapper.insertMps(bean);

				insertList.add(newMpsNo);

				if (bean.getContractDetailNo() != null) {

					changeMpsStatusInContractDetail(bean.getContractDetailNo(), "Y");

				} else if (bean.getSalesPlanNo() != null) {

					changeMpsStatusInSalesPlan(bean.getSalesPlanNo(), "Y");

				}

				break;

			case "UPDATE":

				mpsMapper.updateMps(bean);

				updateList.add(bean.getMpsNo());

				break;

			case "DELETE":

				mpsMapper.deleteMps(bean);

				deleteList.add(bean.getMpsNo());

				break;

			}

		}

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;

	}

	@Override
	public ArrayList<MrpTO> searchMrpList(String mrpGatheringStatusCondition) {

		ArrayList<MrpTO> mrpList = null;

		if(mrpGatheringStatusCondition.equals("null")) //초기값은 NULL
			mrpList = mrpRepository.findByMrpGatheringStatusIsNullOrderByMrpNo();//소요량 취합전 null
		else
			mrpList = mrpRepository.findByMrpGatheringStatusIsNotNullOrderByMrpNo(); //소요량 취합후

		return mrpList;

	}

	
	@Override
	public ArrayList<MrpTO> selectMrpListAsDate(String dateSearchCondtion, String startDate, String endDate) {
		HashMap<String, String> map = new HashMap<>();
		map.put("dateSearchCondtion", dateSearchCondtion);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		return mrpMapper.selectMrpListAsDate(map);
	}
	

	@Override
	public ArrayList<MrpTO> searchMrpListAsMrpGatheringNo(String mrpGatheringNo) {
		return mrpMapper.selectMrpListAsMrpGatheringNo(mrpGatheringNo);
	}

	@Override
	public ArrayList<MrpGatheringTO> searchMrpGatheringList(String dateSearchCondtion, String startDate,
			String endDate) {

		ArrayList<MrpGatheringTO> mrpGatheringList = null;
		
		HashMap<String, String> map = new HashMap<>();

		map.put("dateSearchCondtion", dateSearchCondtion);
		map.put("startDate", startDate);
		map.put("endDate", endDate);

		mrpGatheringList = mrpMapper.selectMrpGatheringList(map);

		for(MrpGatheringTO bean : mrpGatheringList)    {
	            
	    	bean.setMrpTOList(  mrpMapper.selectMrpListAsMrpGatheringNo( bean.getMrpGatheringNo()) );
	         
		}

		return mrpGatheringList;
	}

	
	@Override
	public HashMap<String, Object> openMrp(ArrayList<String> mpsNoArr) {
		
		String mpsNoList = mpsNoArr.toString().replace("[", "").replace("]", "");
		HashMap<String,Object> params=new HashMap<>();
		params.put("mpsNoList", mpsNoList);
        
		mrpMapper.openMrp(params);
        
		@SuppressWarnings("unchecked")
		ArrayList<OpenMrpTO> openMrpList=(ArrayList<OpenMrpTO>) params.get("RESULT");
		
		HashMap<String,Object> resultMap = new HashMap<>();
		resultMap.put("gridRowJson", openMrpList);
		resultMap.put("errorCode",params.get("ERROR_CODE"));
	    resultMap.put("errorMsg", params.get("ERROR_MSG"));
		
		return resultMap;
	}
	
	
	@Override
	public HashMap<String, Object> registerMrp(String mrpRegisterDate, String mpsNoList) {

		HashMap<String, Object> resultMap = new HashMap<>();
		HashMap<String, Object> params = new HashMap<>();
		params.put("mrpRegisterDate", mrpRegisterDate);

		mrpMapper.insertMrpList(params);

		@SuppressWarnings("unchecked")
		MrpInsertInfoTO mi = ((ArrayList<MrpInsertInfoTO>) params.get("RESULT")).get(0);

		resultMap.put("MrpInsertInfoTO", mi);

		resultMap.put("errorCode", params.get("ERROR_CODE"));
		resultMap.put("errorMsg", params.get("ERROR_MSG"));

		// jpa 구현
		// MPS 테이블에서 해당 mpsNo 의 MRP 적용상태를 "Y" 로 변경
		// 1개만 날라오면 "," 얘가 없더라도 배열로 나눠짐.
		String[] SplitMpsNo = mpsNoList.split(",");
		for (String mpsNo : SplitMpsNo) {
			Optional<MpsTO> mpsTO = mpsRepository.findByMpsNo(mpsNo);
			mpsTO.ifPresent(mpsTOUpdate -> {
				mpsTOUpdate.setMrpApplyStatus("Y");
				mpsRepository.save(mpsTOUpdate);
			});
		}
		return resultMap;
	}

	
	@Override
	public HashMap<String, Object> batchMrpListProcess(ArrayList<MrpTO> mrpTOList) {
		
		HashMap<String, Object> resultMap = new HashMap<>();

		ArrayList<String> insertList = new ArrayList<>();
		ArrayList<String> updateList = new ArrayList<>();
		ArrayList<String> deleteList = new ArrayList<>();

		for (MrpTO bean : mrpTOList) {

			String status = bean.getStatus();

			switch (status) {

				case "INSERT":

	               mrpMapper.insertMrp(bean);

	               insertList.add(bean.getMrpNo());

	               break;

	            case "UPDATE":

	               mrpMapper.updateMrp(bean);
	               
	               updateList.add(bean.getMrpNo());

	               break;

	            case "DELETE":

	               mrpMapper.deleteMrp(bean);

	               deleteList.add(bean.getMrpNo());

	               break;

	            }

	         }

		resultMap.put("INSERT", insertList);
		resultMap.put("UPDATE", updateList);
		resultMap.put("DELETE", deleteList);

		return resultMap;
	}

	
	@Override
	public ArrayList<MrpGatheringTO> getMrpGathering(ArrayList<String> mrpNoArr) {
		
		ArrayList<MrpGatheringTO> mrpGatheringList = null;

		String mrpNoList = mrpNoArr.toString().replace("[", "").replace("]", "");
		mrpGatheringList = mrpMapper.getMrpGathering(mrpNoList);

		return mrpGatheringList;
	}
	
	
	//소요량 취합 등록
	@Override
	public HashMap<String, Object> registerMrpGathering(String mrpGatheringRegisterDate, ArrayList<String> mrpNoArr,
					HashMap<String, String> mrpNoAndItemCodeMap) {
		
		HashMap<String, Object> resultMap = new HashMap<>();
		HashMap<String, String> itemCodeAndMrpGatheringNoMap = new HashMap<>();
	    
	    int seq=0;
		int i=1;
		
		MrpGatheringTO mgt= mrpMapper.selectMrpGatheringCount(mrpGatheringRegisterDate); 
		// 이거떄문에 해당날짜의 첫번째가 없으면 nullpoint가 생겼음 그래서 npe체크추가.
		if(mgt!=null) {
		   String mrpNumber=mgt.getMrpGatheringNo();
			i=Integer.parseInt(mrpNumber.substring(mrpNumber.length() - 2, mrpNumber.length()))+1;  // 숫자안 - 제거하기위해
		}
			//시퀀스번호 생성
			seq = mrpMapper.getMGSeqNo(); 
			
    	// 새로운 mrpGathering번호를 bean에 입력,mrp_gathering_no IS NULL 인 mrp  
		ArrayList<MrpGatheringTO> mrpGatheringList = getMrpGathering(mrpNoArr);
		// 생성된 mrp 일련번호를 저장할 TreeSet
		TreeSet<String> mrpGatheringNoSet = new TreeSet<>();

		StringBuffer newMrpGatheringNo = new StringBuffer();
		newMrpGatheringNo.append("MG");
		newMrpGatheringNo.append(mrpGatheringRegisterDate.replace("-", ""));
		newMrpGatheringNo.append("-");

		StringBuffer sb = new StringBuffer();
		
		for (MrpGatheringTO bean : mrpGatheringList) { 
			//jpa 적용 ( 새 소요량취합번호 and 시퀀스 번호 저장)
			bean.setMrpGatheringNo(newMrpGatheringNo.toString() + String.format("%03d", i++)); //소요량 취합번호
		  
			bean.setMrpGatheringSeq(seq); 
			
			//MrpGatheringTO(소요량취합TO)에 소요량취합번호<pk> , 시퀀스번호(다같음) 추가해서 등록완료!!!
			mrpGatheringRepository.save(bean);

			//treeset
			mrpGatheringNoSet.add(bean.getMrpGatheringNo());
			
			// mrpGathering 빈의 itemCode 와 mrpGatheringNo 를 키값:밸류값으로 map 에 저장
			itemCodeAndMrpGatheringNoMap.put(bean.getItemCode(), bean.getMrpGatheringNo());
		
			sb.append(bean.getMrpGatheringNo());
			sb.append(",");
			
		}
		
	    //view단에서 받아옴 map	 mrpNo:itemCode
		for (String mrpNo : mrpNoAndItemCodeMap.keySet()) {   
			String itemCode = mrpNoAndItemCodeMap.get(mrpNo);
			String mrpGatheringNo = itemCodeAndMrpGatheringNoMap.get(itemCode); //새로추가된 mrpGatheringNo 구함 
	
			Optional<MrpTO> mrpTO = mrpRepository.findByMrpNo(mrpNo);
			
			// 여기서 먼저 mrp테이블에 소요량취합번호를 입력해줘야 아래 수주상세 프로시저에서 mrp 테이블을 이용하여 수주상세에 취합번호등록가능!!
			
			mrpTO.ifPresent(mrpTOUpdate -> {
			mrpTOUpdate.setMrpGatheringNo(mrpGatheringNo);
				mrpTOUpdate.setMrpGatheringStatus("Y");
			
				mrpRepository.save(mrpTOUpdate);
				
			});	
		}
		
		//프로시저 !(jpa미적용) 수주상세(contract_detail)에   새로운 소요량취합 (MrpGatheringNo) 번호를 추가
		
				sb.delete(sb.toString().length() - 1, sb.toString().length());  // 마지막 , 없앰
				HashMap<String, String> map = new HashMap<>();
				map.put("mrpGatheringNo",sb.toString());
				
				mrpMapper.updateMrpGatheringContract(map);   
				
				
				resultMap.put("firstMrpGatheringNo", mrpGatheringNoSet.pollFirst()); // 최초 mrpGathering 일련번호를 결과 Map 에 저장
		        resultMap.put("lastMrpGatheringNo", mrpGatheringNoSet.pollLast()); // 마지막 mrpGathering 일련번호를 결과 Map 에 저장
	
		return resultMap;	
	}
	
	
	public String getNewMpsNo(String mpsPlanDate) {
		StringBuffer newEstimateNo = null;
		List<MpsTO> mpsTOlist = mpsMapper.selectMpsCount(mpsPlanDate);
		TreeSet<Integer> intSet = new TreeSet<>();
		for (MpsTO bean : mpsTOlist) {
			String mpsNo = bean.getMpsNo();
			// MPS 일련번호에서 마지막 2자리만 가져오기
			int no = Integer.parseInt(mpsNo.substring(mpsNo.length() - 2, mpsNo.length()));
			intSet.add(no);	
		}
		int i=1;
		if (!intSet.isEmpty()) {
			i=intSet.pollLast() + 1;
		}

		newEstimateNo = new StringBuffer();
		newEstimateNo.append("PS");
		newEstimateNo.append(mpsPlanDate.replace("-", ""));
		newEstimateNo.append(String.format("%02d", i)); //PS2020042401

		return newEstimateNo.toString();
	}

	
	public void changeMpsStatusInContractDetail(String contractDetailNo, String mpsStatus) {

		HashMap<String, String> map = new HashMap<>();

		map.put("contractDetailNo", contractDetailNo);
		map.put("mpsStatus", mpsStatus);
		
		contractMapper.changeMpsStatusOfContractDetail(map);

	}

	public void changeMpsStatusInSalesPlan(String salesPlanNo, String mpsStatus) {

		HashMap<String, String> map = new HashMap<>();

		map.put("salesPlanNo", salesPlanNo);
		map.put("mpsStatus", mpsStatus);
		
		salesPlanMapper.changeMpsStatusOfSalesPlan(map);

	}

	public HashMap<String, Object> batchMrpGatheringListProcess(ArrayList<MrpGatheringTO> mrpGatheringTOList) {

		HashMap<String, Object> resultMap = new HashMap<>();

		 HashMap<String, String> insertListMap = new HashMap<>(); 
		 ArrayList<String> insertList = new ArrayList<>();
		 ArrayList<String> updateList = new ArrayList<>();
		 ArrayList<String> deleteList = new ArrayList<>();

		 for (MrpGatheringTO bean : mrpGatheringTOList) {
		            
			 String status = bean.getStatus();
		            
			 switch (status) {

			 	case "INSERT":

			 			mrpMapper.insertMrpGathering(bean);
		               
			 			insertList.add(bean.getMrpGatheringNo());

			 			insertListMap.put(bean.getItemCode(), bean.getMrpGatheringNo());

			 			break;

			 	case "UPDATE":

			 		mrpMapper.updateMrpGathering(bean);

			 		updateList.add(bean.getMrpGatheringNo());

			 		break;

			 	case "DELETE":

			 		mrpMapper.deleteMrpGathering(bean);

			 		deleteList.add(bean.getMrpGatheringNo());

			 		break;

			 }

		 }

		 resultMap.put("INSERT_MAP", insertListMap); //key(ItemCode) : value(getMrpGatheringNo)
		 resultMap.put("INSERT", insertList); //소요량취합번호
		 resultMap.put("UPDATE", updateList);
		 resultMap.put("DELETE", deleteList);

		 return resultMap;
		   }
}
