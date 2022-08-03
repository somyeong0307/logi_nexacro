package kr.co.seoulit.logistics.prodcsvc.quality.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;

@Data
@Entity
@Table(name="WORK_ORDER_INFO")
@Dataset(name="gds_workOrderInfo")
public class WorkOrderInfoTO {
	
	@Id
	private String workOrderNo;
	private String mrpNo;
	private String mrpGatheringNo;
	private String itemClassification;
	private String itemCode;
	private String itemName;
	private String unitOfMrp;
	private String requiredAmount;
	private String workSiteCode;
	private String workSiteName;
	private String productionProcessCode;
	private String productionProcessName;
	private String inspectionStatus;
	private String productionStatus;
	private String completionStatus;

	private String operationCompleted;   //처음에는 null 
	@Transient
	private String checked;
	@Transient
	private String actualCompletionAmount;
	
}
