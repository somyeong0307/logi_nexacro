package kr.co.seoulit.logistics.purcstosvc.purchase.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;

@Data
@Entity
@Table(name="ORDER_INFO")
@Dataset(name="gds_orderInfo")
public class OrderInfoTO {
	
	@Id
	private String orderNo;
	private String orderDate;
	private String orderInfoStatus;
	private String orderSort;
	private String itemCode;
	private String itemName;
	private String orderAmount;
	private String inspectionStatus;

	@Transient //체크 상태 그리드의
	private String checked;
}
