package kr.co.seoulit.logistics.logiinfosvc.compinfo.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="WORKPLACE")
@Dataset(name="gds_workplace")
public class WorkplaceTO extends BaseTO {
	private String workplaceCeoName;
	private String isMainOffice;
	private String workplaceDetailAddress;
	private String workplaceBusinessConditions;
	private String workplaceBusinessItems;
	private String workplaceFaxNumber;
	private String workplaceEstablishDate;
	private String businessLicenseNumber;
	private String workplaceTelNumber;
	private String workplaceName;
	private String workplaceBasicAddress;
	private String workplaceCloseDate;
	@Id
	private String workplaceCode;
	private String companyCode;
	private String workplaceOpenDate;
	private String corporationLicenseNumber;
	private String workplaceZipCode;

}