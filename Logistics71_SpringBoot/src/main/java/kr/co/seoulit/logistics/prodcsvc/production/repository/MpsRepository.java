package kr.co.seoulit.logistics.prodcsvc.production.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.seoulit.logistics.prodcsvc.production.to.MpsTO;


public interface MpsRepository extends JpaRepository<MpsTO, String> {
	
	List<MpsTO> findByMpsPlanDate(String mpsPlanDate);
	
	ArrayList<MpsTO> findByMpsPlanDateBetween(String startDate, String endDate);
	
	ArrayList<MpsTO> findByMpsPlanDateBetweenAndMrpApplyStatusIsNull(String startDate, String endDate);
	
	Optional<MpsTO> findByMpsNo(String mpsNo);

}
