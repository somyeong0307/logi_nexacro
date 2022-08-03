package kr.co.seoulit.logistics.prodcsvc.quality.to;

import javax.persistence.Transient;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;

@Data
@Dataset(name="gds_workOrderableMrpList")
public class WorkOrderableMrpListTO {
	
	private String mrpNo;
	@Transient
	private String mpsNo;	
	private String mrpGatheringNo;	
	private String itemClassification;	
	private String itemCode;
	private String itemName;	
	private String unitOfMrp;	
	private int requiredAmount;	
	private String orderDate;
	private String requiredDate;
	@Transient
	private String checked;
}
