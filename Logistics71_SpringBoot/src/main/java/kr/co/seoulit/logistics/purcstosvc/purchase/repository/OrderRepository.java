package kr.co.seoulit.logistics.purcstosvc.purchase.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.purcstosvc.purchase.to.OrderInfoTO;

public interface OrderRepository extends CrudRepository<OrderInfoTO, String> {

	List<OrderInfoTO> findByOrderInfoStatusIn(List<String> orderInfoStatusList);
	List<OrderInfoTO> findByOrderDateBetween(String startDate, String endDate);
	
}
