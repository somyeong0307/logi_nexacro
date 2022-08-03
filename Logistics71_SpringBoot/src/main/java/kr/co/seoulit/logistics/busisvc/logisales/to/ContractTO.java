package kr.co.seoulit.logistics.busisvc.logisales.to;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name="CONTRACT")
@Dataset(name="gds_contract")
public class ContractTO extends BaseTO  implements Persistable<String>{
	
	@Id
	private String contractNo;
	private String contractType;
	private String estimateNo;
	private String contractDate;
	private String description;
	private String contractRequester;
	private String customerCode;
	private String personCodeInCharge;
	

	 @Transient
	 @RemoveColumn
	 @CreatedDate
	  private LocalDateTime createdDate;
	
	
	@RemoveColumn  //x pltaform 
	@Transient
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL) // 지연로딩. 
	@JoinColumn(name="contractNo") //없으면 중간에 조인 테이블을 따로 생성
	private List<ContractDetailTO> contractDetailTOList;


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