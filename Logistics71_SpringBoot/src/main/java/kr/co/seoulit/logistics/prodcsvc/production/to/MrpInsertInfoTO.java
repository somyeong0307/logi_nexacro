package kr.co.seoulit.logistics.prodcsvc.production.to;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Dataset(name="gds_mrpInsertInf")
public class MrpInsertInfoTO {
	private String firstMrpNo;
	private String lastMrpNo;
	private String length;

}
