package kr.co.seoulit.logistics.logiinfosvc.hr.to;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import kr.co.seoulit.logistics.sys.annotation.Dataset;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name="AUTHORITY_GROUP")
@Dataset(name="gds_AuthorityGroup")
public class AuthorityGroupTO {
	@Id
	private String authorityGroupCode;
	private String authorityGroupName;
	@Transient
	private String userAuthorityGroupCode;
	
	@Transient
	private String authority;
	@Transient
	private String status;
}
