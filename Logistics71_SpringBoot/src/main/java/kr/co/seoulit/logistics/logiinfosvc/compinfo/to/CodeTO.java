package kr.co.seoulit.logistics.logiinfosvc.compinfo.to;

import java.util.ArrayList;
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
@Table(name="CODE")
@Dataset(name="gds_code")
public class CodeTO extends BaseTO {
	
	@Id
	private String divisionCodeNo;
	private String codeType;
	private String divisionCodeName;
	private String codeChangeAvailable;
	private String description;
	
	@Transient
	private ArrayList<CodeDetailTO> codeDetailTOList;

	@Transient
	private String checked;
}