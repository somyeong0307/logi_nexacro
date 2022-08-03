package kr.co.seoulit.logistics.prodcsvc.production.repository;

import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpGatheringTO;

public interface MrpGatheringRepository extends CrudRepository<MrpGatheringTO, String> {
	

}
