package kr.co.seoulit.logistics.busisvc.sales.repository;

import java.util.ArrayList;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kr.co.seoulit.logistics.busisvc.sales.to.DeliveryInfoTO;


@Repository
public interface DeliveryInfoRepository extends CrudRepository<DeliveryInfoTO, String> {
   
	ArrayList<DeliveryInfoTO> findAll(Sort sort);
	ArrayList<DeliveryInfoTO> findAllByCustomerCodeOrderByDeliveryDateDesc(String CustomerCode);
}
