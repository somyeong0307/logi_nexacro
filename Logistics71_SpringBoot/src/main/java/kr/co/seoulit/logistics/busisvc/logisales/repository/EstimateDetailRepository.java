package kr.co.seoulit.logistics.busisvc.logisales.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateDetailTO;


@Repository
public interface EstimateDetailRepository extends CrudRepository<EstimateDetailTO, String> {
	
	List<EstimateDetailTO> findByEstimateNo(String estimateNo);	


}
