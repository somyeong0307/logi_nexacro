package kr.co.seoulit.logistics.logiinfosvc.logiinfo.to;

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
@Table(name="WAREHOUSE")
@Dataset(name="ds_warehouseList")

public class WarehouseTO extends BaseTO {
	@Id
	private String warehouseCode;
	private String warehouseName;
	private String warehouseUseOrNot;
	private String description;
	
	@Transient
	private String checked;
}