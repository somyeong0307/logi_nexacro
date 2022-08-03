package kr.co.seoulit.logistics.logiinfosvc.compinfo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CodeTO;


public interface CodeRepository extends JpaRepository<CodeTO, String> {
	 List<CodeTO> findAll();
}
