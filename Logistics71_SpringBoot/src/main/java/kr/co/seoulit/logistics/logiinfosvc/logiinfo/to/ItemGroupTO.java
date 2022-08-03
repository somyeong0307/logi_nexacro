package kr.co.seoulit.logistics.logiinfosvc.logiinfo.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="ITEM_GROUP")
@Dataset(name="ds_itemGroup")
public class ItemGroupTO {
	 @Id
	 private String itemGroupCode;
	 private String description;
	 private String itemGroupName;
	 
	@Transient
	private String checked;
}