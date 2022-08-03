package kr.co.seoulit.logistics.logiinfosvc.compinfo.to;

import lombok.Data;
import lombok.EqualsAndHashCode;
import kr.co.seoulit.logistics.sys.annotation.Dataset;

@Data
@EqualsAndHashCode(callSuper = false)
@Dataset(name="gds_company")
public class CompanyTO extends BaseTO {
   	 private String companyTelNumber;
	 private String companyDivision;
	 private String companyBasicAddress;
	 private String companyOpenDate;
	 private String companyBusinessItems;
	 private String businessLicenseNumber;
	 private String companyName;
	 private String companyDetailAddress;
	 private String companyFaxNumber;
	 private String companyCeoName;
	 private String companyEstablishmentDate;
	 private String companyCode;
	 private String homepage;
	 private String corporationLicenseNumber;
	 private String companyBusinessConditions;
	 private String companyZipCode;

}