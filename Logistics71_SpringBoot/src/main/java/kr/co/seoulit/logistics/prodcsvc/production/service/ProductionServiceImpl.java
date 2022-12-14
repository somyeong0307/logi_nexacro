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
			//mrp??????????????? null?????? ??????
			selectMpsList = mpsRepository.findByMpsPlanDateBetweenAndMrpApplyStatusIsNull(startDate, endDate);
		}else { 
			//??????????????? ??????
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

		// getNewMpsNo()???????????? ?????? ?????? ????????? ??????????????? ???????????? ????????? ???????????? ???????????? for???????????? ???????????? ????????? ?????????
		// ???????????? ???????????????!
		ContractDetailInMpsAvailableTO contractDetail = contractDetailInMpsAvailableList.get(0);
		MpsTO mps = mpsMapper.getNewMpsNo(contractDetail.getMpsPlanDate());
		int no = 1;
		if (mps != null) {
			String mpsNo = mps.getMpsNo();
			no = Integer.parseInt(mpsNo.substring(mpsNo.length() - 2, mpsNo.length())) + 1;
		}

		StringBuffer newEstimateNo = new StringBuffer();
		// MPS ??? ????????? ???????????? Bean ??? ????????? ????????? MPS Bean ??? ??????, status : "INSERT"
		for (ContractDetailInMpsAvailableTO bean : contractDetailInMpsAvailableList) {

			MpsTO newMpsBean = new MpsTO();
			// ?????????????????? ?????? mps

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
			newMpsBean.setMpsNo(newEstimateNo.toString());// ????????????????????? ?????? pk

			insertList.add(newMpsBean);

			// jpa ??????

			mpsRepository.save(newMpsBean);

			// ????????? <??????????????? ????????? ????????????>
			newEstimateNo.delete(0, newEstimateNo.length());

			// MPS TO ??? ????????????????????? ????????????, ???????????? ??????????????? ?????? ????????? MPS???????????? (PROCESSING_STATUS)??? 'Y' ???
			// ??????
			if (bean.getContractDetailNo() != null) {

				// jpa??????
				Optional<ContractDetailTO> contractDetailNo = contractDetailRepository
						.findByContractDetailNo(bean.getContractDetailNo());

				contractDetailNo.ifPresent(contractDetailUpdate -> {
					contractDetailUpdate.setProcessingStatus("Y"); // Mps??????????????? ?????? null?????? Y???
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

		if(mrpGatheringStatusCondition.equals("null")) //???????????? NULL
			mrpList = mrpRepository.findByMrpGatheringStatusIsNullOrderByMrpNo();//????????? ????????? null
		else
			mrpList = mrpRepository.findByMrpGatheringStatusIsNotNullOrderByMrpNo(); //????????? ?????????

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

		// jpa ??????
		// MPS ??????????????? ?????? mpsNo ??? MRP ??????????????? "Y" ??? ??????
		// 1?????? ???????????? "," ?????? ???????????? ????????? ?????????.
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
	
	
	//????????? ?????? ??????
	@Override
	public HashMap<String, Object> registerMrpGathering(String mrpGatheringRegisterDate, ArrayList<String> mrpNoArr,
					HashMap<String, String> mrpNoAndItemCodeMap) {
		
		HashMap<String, Object> resultMap = new HashMap<>();
		HashMap<String, String> itemCodeAndMrpGatheringNoMap = new HashMap<>();
	    
	    int seq=0;
		int i=1;
		
		MrpGatheringTO mgt= mrpMapper.selectMrpGatheringCount(mrpGatheringRegisterDate); 
		// ??????????????? ??????????????? ???????????? ????????? nullpoint??? ????????? ????????? npe????????????.
		if(mgt!=null) {
		   String mrpNumber=mgt.getMrpGatheringNo();
			i=Integer.parseInt(mrpNumber.substring(mrpNumber.length() - 2, mrpNumber.length()))+1;  // ????????? - ??????????????????
		}
			//??????????????? ??????
			seq = mrpMapper.getMGSeqNo(); 
			
    	// ????????? mrpGathering????????? bean??? ??????,mrp_gathering_no IS NULL ??? mrp  
		ArrayList<MrpGatheringTO> mrpGatheringList = getMrpGathering(mrpNoArr);
		// ????????? mrp ??????????????? ????????? TreeSet
		TreeSet<String> mrpGatheringNoSet = new TreeSet<>();

		StringBuffer newMrpGatheringNo = new StringBuffer();
		newMrpGatheringNo.append("MG");
		newMrpGatheringNo.append(mrpGatheringRegisterDate.replace("-", ""));
		newMrpGatheringNo.append("-");

		StringBuffer sb = new StringBuffer();
		
		for (MrpGatheringTO bean : mrpGatheringList) { 
			//jpa ?????? ( ??? ????????????????????? and ????????? ?????? ??????)
			bean.setMrpGatheringNo(newMrpGatheringNo.toString() + String.format("%03d", i++)); //????????? ????????????
		  
			bean.setMrpGatheringSeq(seq); 
			
			//MrpGatheringTO(???????????????TO)??? ?????????????????????<pk> , ???????????????(?????????) ???????????? ????????????!!!
			mrpGatheringRepository.save(bean);

			//treeset
			mrpGatheringNoSet.add(bean.getMrpGatheringNo());
			
			// mrpGathering ?????? itemCode ??? mrpGatheringNo ??? ??????:??????????????? map ??? ??????
			itemCodeAndMrpGatheringNoMap.put(bean.getItemCode(), bean.getMrpGatheringNo());
		
			sb.append(bean.getMrpGatheringNo());
			sb.append(",");
			
		}
		
	    //view????????? ????????? map	 mrpNo:itemCode
		for (String mrpNo : mrpNoAndItemCodeMap.keySet()) {   
			String itemCode = mrpNoAndItemCodeMap.get(mrpNo);
			String mrpGatheringNo = itemCodeAndMrpGatheringNoMap.get(itemCode); //??????????????? mrpGatheringNo ?????? 
	
			Optional<MrpTO> mrpTO = mrpRepository.findByMrpNo(mrpNo);
			
			// ????????? ?????? mrp???????????? ???????????????????????? ??????????????? ?????? ???????????? ?????????????????? mrp ???????????? ???????????? ??????????????? ????????????????????????!!
			
			mrpTO.ifPresent(mrpTOUpdate -> {
			mrpTOUpdate.setMrpGatheringNo(mrpGatheringNo);
				mrpTOUpdate.setMrpGatheringStatus("Y");
			
				mrpRepository.save(mrpTOUpdate);
				
			});	
		}
		
		//???????????? !(jpa?????????) ????????????(contract_detail)???   ????????? ??????????????? (MrpGatheringNo) ????????? ??????
		
				sb.delete(sb.toString().length() - 1, sb.toString().length());  // ????????? , ??????
				HashMap<String, String> map = new HashMap<>();
				map.put("mrpGatheringNo",sb.toString());
				
				mrpMapper.updateMrpGatheringContract(map);   
				
				
				resultMap.put("firstMrpGatheringNo", mrpGatheringNoSet.pollFirst()); // ?????? mrpGathering ??????????????? ?????? Map ??? ??????
		        resultMap.put("lastMrpGatheringNo", mrpGatheringNoSet.pollLast()); // ????????? mrpGathering ??????????????? ?????? Map ??? ??????
	
		return resultMap;	
	}
	
	
	public String getNewMpsNo(String mpsPlanDate) {
		StringBuffer newEstimateNo = null;
		List<MpsTO> mpsTOlist = mpsMapper.selectMpsCount(mpsPlanDate);
		TreeSet<Integer> intSet = new TreeSet<>();
		for (MpsTO bean : mpsTOlist) {
			String mpsNo = bean.getMpsNo();
			// MPS ?????????????????? ????????? 2????????? ????????????
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
		 resultMap.put("INSERT", insertList); //?????????????????????
		 resultMap.put("UPDATE", updateList);
		 resultMap.put("DELETE", deleteList);

		 return resultMap;
		   }
}
