package kr.co.seoulit.logistics.busisvc.logisales.repository;

import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.busisvc.logisales.to.ContractTO;

public interface ContractRepository extends CrudRepository<ContractTO, String> {

}
