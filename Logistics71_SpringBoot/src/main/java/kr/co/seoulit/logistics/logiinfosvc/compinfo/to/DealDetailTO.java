package kr.co.seoulit.logistics.logiinfosvc.compinfo.to;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Dataset(name="ds_deal_detail")
public class DealDetailTO extends BaseTO {
	String itemName, unit, amount, unitPrice, sumPrice, blank;
}
