package kr.co.seoulit.logistics.prodcsvc.production.to;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.BaseTO;
import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="MRP_GATHERING")
@Dataset(name="gds_mrpGathering")
public class MrpGatheringTO extends BaseTO {
	@Id
	private String mrpGatheringNo;
	private String orderOrProductionStatus;
	private String itemCode;
	private String itemName;
	private String unitOfMrpGathering;
	private String claimDate;
	private String dueDate;
	private int necessaryAmount;
	private int mrpGatheringSeq;
	
	@Transient
	private ArrayList<MrpTO> mrpTOList;

}