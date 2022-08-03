package kr.co.seoulit.logistics.logiinfosvc.compinfo.to;

import kr.co.seoulit.logistics.sys.annotation.Dataset;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Dataset(name="ds_deal")
public class DealTO extends BaseTO {
	String deal_code,deal_day,separation,collect_money,payment_money;
}
