package kr.co.seoulit.logistics.logiinfosvc.logiinfo.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemTO;


public interface ItemRepository extends CrudRepository<ItemTO, String> {
	
	 Optional<ItemTO> findByItemCode(String itemCode);
	 
}
