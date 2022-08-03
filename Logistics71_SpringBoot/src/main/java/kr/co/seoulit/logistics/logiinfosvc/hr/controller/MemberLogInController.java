package kr.co.seoulit.logistics.logiinfosvc.hr.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.CompanyTO;
import kr.co.seoulit.logistics.logiinfosvc.compinfo.to.WorkplaceTO;
import kr.co.seoulit.logistics.logiinfosvc.hr.service.HRService;
import kr.co.seoulit.logistics.logiinfosvc.hr.to.EmpInfoTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/hr/*")
public class MemberLogInController {
    
	
	private HRService hrService;
	private final DatasetBeanMapper datasetBeanMapper;
	
	@RequestMapping(value="/login")
    public void LogInCheck(HttpServletRequest request, HttpServletResponse response) throws Exception{

        
		PlatformData reqData = (PlatformData) request.getAttribute("reqData");
		PlatformData resData = (PlatformData) request.getAttribute("resData");
		
		String companyCode = datasetBeanMapper.datasetToBean(reqData, CompanyTO.class).getCompanyCode();
		String workplaceCode = datasetBeanMapper.datasetToBean(reqData, WorkplaceTO.class).getWorkplaceCode();
		String userId = reqData.getVariable("userId").getString();
		String userPassword = reqData.getVariable("userPassWord").getString();
		
        EmpInfoTO TO = hrService.accessToAuthority(companyCode, workplaceCode, userId, userPassword);
        
        if (TO != null) {
        	datasetBeanMapper.beanToDataset(resData, TO, EmpInfoTO.class);
        }
    }
	
}
