package kr.co.seoulit.logistics.logiinfosvc.logiinfo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.seoulit.logistics.logiinfosvc.logiinfo.to.ItemGroupTO;


public interface ItemGroupRepository extends JpaRepository<ItemGroupTO, String> {
	 List<ItemGroupTO> findAll();
}
