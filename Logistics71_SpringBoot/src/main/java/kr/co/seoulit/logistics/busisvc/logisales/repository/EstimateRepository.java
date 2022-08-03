package kr.co.seoulit.logistics.busisvc.logisales.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kr.co.seoulit.logistics.busisvc.logisales.to.EstimateTO;



@Repository
public interface EstimateRepository extends CrudRepository<EstimateTO, String> {
	
	int countByEstimateDate(String estimateDate);	
	Optional<EstimateTO> findByEstimateNo(String estimateNo);
 
}
