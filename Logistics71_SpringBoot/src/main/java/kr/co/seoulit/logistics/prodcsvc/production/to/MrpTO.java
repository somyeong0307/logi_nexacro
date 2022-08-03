package kr.co.seoulit.logistics.prodcsvc.production.to;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.BaseTO;
import kr.co.seoulit.logistics.sys.annotation.Dataset;
import kr.co.seoulit.logistics.sys.annotation.RemoveColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="MRP")
@Dataset(name="gds_mrp")
public class MrpTO extends BaseTO implements Persistable<String>  {
	@Id
	private String mrpNo;
	private String mpsNo;
	private String mrpGatheringNo;
	private String itemClassification;
	private String itemCode;
	private String itemName;
	private String unitOfMrp;
	private int requiredAmount;
	private String orderDate;
	private String requiredDate;
	private String mrpGatheringStatus;

	 @Transient
	 @RemoveColumn
	 @CreatedDate
	  private LocalDateTime createdDate;
	 
		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isNew() {
			// TODO Auto-generated method stub
			return createdDate==null;
		}
}