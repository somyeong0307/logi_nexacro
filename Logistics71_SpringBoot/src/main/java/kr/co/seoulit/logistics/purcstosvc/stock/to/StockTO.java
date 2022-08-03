package kr.co.seoulit.logistics.purcstosvc.stock.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.BaseTO;
import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="STOCK")
@Dataset(name="gds_stock")
@EqualsAndHashCode(callSuper = false)
public class StockTO extends BaseTO {
	
	private String warehouseCode;
	@Id
	private String itemCode;
	private String itemName;
	private String unitOfStock;
	private String safetyAllowanceAmount;
	private String stockAmount;
	private String orderAmount;
	private String inputAmount;
	private String deliveryAmount;
	private String totalStockAmount;

}
