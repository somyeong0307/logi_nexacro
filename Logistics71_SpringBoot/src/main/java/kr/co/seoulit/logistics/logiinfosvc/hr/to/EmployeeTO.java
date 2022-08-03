package kr.co.seoulit.logistics.logiinfosvc.hr.to;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.BaseTO;
import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="EMPLOYEE")
@Dataset(name="gds_employee")
public class EmployeeTO extends BaseTO {
    @Id
    private String empCode;
    private String empName;
    private String companyCode;
    private String workplaceCode;
    private String deptCode;
    private String positionCode;
    private String socialSecurityNumber;
    private String birthDate;
    private String gender;
    private String email;
    private String phoneNumber;
    private String image;
    private String userPw;
    private String zipCode;
    private String basicAddress;
    private String detailAddress;
    private String levelOfEducation;
    private String userOrNet;
    private String deptName;
    private String positionName;
    @Transient
    private String status;

   

}