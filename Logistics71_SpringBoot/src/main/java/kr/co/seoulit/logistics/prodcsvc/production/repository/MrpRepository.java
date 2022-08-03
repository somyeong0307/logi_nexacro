package kr.co.seoulit.logistics.prodcsvc.production.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpTO;



public interface MrpRepository extends CrudRepository<MrpTO, String> {

	Optional<MrpTO> findByMrpNo(String mrpNo);
	
	ArrayList<MrpTO> findByMrpGatheringStatusIsNullOrderByMrpNo();
	
	ArrayList<MrpTO> findByMrpGatheringStatusIsNotNullOrderByMrpNo();
	
	List<MrpTO> findByOrderDateBetween(String startDate, String endDate);

	List<MrpTO> findByRequiredDateBetween(String startDate, String endDate);
	
	MrpTO findByMrpGatheringNo(String mrpGatheringNo);
	
}
