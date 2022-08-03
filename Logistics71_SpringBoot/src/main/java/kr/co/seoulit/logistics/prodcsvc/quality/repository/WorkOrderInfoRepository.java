package kr.co.seoulit.logistics.prodcsvc.quality.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkOrderInfoTO;


public interface WorkOrderInfoRepository extends CrudRepository<WorkOrderInfoTO, String> {

	List<WorkOrderInfoTO> findByOperationCompletedIsNull();
	
}
