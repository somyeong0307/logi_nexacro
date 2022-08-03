package kr.co.seoulit.logistics.logiinfosvc.logiinfo.to;

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
@Table(name="ITEM")
@Dataset(name="ds_item")
public class ItemTO extends BaseTO {
	 @Id
	 private String itemCode;
	 private String itemGroupCode;
	 private String leadTime;
	 private String unitOfStock;
	 private int standardUnitPrice;
	 private String description;
	 private String itemClassification;
	 private String lossRate;
	 private String itemName;
	 
	@Transient
	private String checked;
	 
	@RemoveColumn
	@Transient
	private String status;
	 
}