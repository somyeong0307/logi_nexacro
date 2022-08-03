package kr.co.seoulit.logistics.busisvc.logisales.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractDetailTO;


public interface ContractDetailRepository extends CrudRepository<ContractDetailTO, String> {

	Optional<ContractDetailTO> findByContractDetailNo(String contractDetailNo);
	
}
