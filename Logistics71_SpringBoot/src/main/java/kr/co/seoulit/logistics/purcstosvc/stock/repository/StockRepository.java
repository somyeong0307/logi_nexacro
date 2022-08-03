package kr.co.seoulit.logistics.purcstosvc.stock.repository;


import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.purcstosvc.stock.to.StockTO;

public interface StockRepository extends CrudRepository<StockTO, String> {

	List<StockTO> findAll(Sort sort);
	ArrayList<StockTO> findByWarehouseCode(String houseCode);
}
