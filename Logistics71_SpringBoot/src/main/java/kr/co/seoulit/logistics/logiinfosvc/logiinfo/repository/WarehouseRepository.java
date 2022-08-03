package kr.co.seoulit.logistics.logiinfosvc.logiinfo.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.WarehouseTO;

public interface WarehouseRepository  extends CrudRepository<WarehouseTO, String> {
	List<WarehouseTO> findAll();
}
