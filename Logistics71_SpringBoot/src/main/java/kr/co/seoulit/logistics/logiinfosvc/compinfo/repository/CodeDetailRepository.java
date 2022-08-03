package kr.co.seoulit.logistics.logiinfosvc.compinfo.repository;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CodeDetailTO;


public interface CodeDetailRepository extends CrudRepository<CodeDetailTO, String>{
	
	ArrayList<CodeDetailTO> findByDivisionCodeNoLike(String divisionCodeNo);

}
