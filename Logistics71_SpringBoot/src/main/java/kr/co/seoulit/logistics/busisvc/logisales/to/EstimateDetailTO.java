package kr.co.seoulit.logistics.busisvc.logisales.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.BaseTO;
import kr.co.seoulit.logistics.sys.annotation.Dataset;
import kr.co.seoulit.logistics.sys.annotation.RemoveColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="ESTIMATE_DETAIL")
@Dataset(name="gds_estimateDetail")
public class EstimateDetailTO extends BaseTO {
	@Id
	private String estimateDetailNo;
	private String unitOfEstimate;
	private String estimateNo;
	private int unitPriceOfEstimate;
	private int sumPriceOfEstimate;
	private String description;
	private String itemCode;
	private int estimateAmount;
	private String dueDateOfEstimate;
	private String itemName;
	@Transient
	private String checked;
	
	@RemoveColumn
	@Transient
	private String status;

}