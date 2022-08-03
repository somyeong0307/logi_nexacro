package kr.co.seoulit.logistics.prodcsvc.production.to;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.BaseTO;
import kr.co.seoulit.logistics.sys.annotation.Dataset;
import kr.co.seoulit.logistics.sys.annotation.RemoveColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "MPS")
@Dataset(name = "gds_mps")
public class MpsTO extends BaseTO implements Persistable<String> {
	@Id
	private String mpsNo;
	private String mpsPlanDate;
	private String contractDetailNo;
	private String dueDateOfMps;
	private String salesPlanNo;
	private String itemCode;
	private String itemName;
	private String mpsPlanAmount;
	private String mrpApplyStatus;
	private String description;
	private String unitOfMps;
	private String mpsPlanClassification;
	private String scheduledEndDate;
	@Transient
	private String checked;

	@Transient
	@RemoveColumn
	@CreatedDate
	private LocalDateTime createdDate;

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return createdDate == null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}