package kr.co.seoulit.logistics.logiinfosvc.hr.controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tobesoft.xplatform.data.PlatformData;
import kr.co.seoulit.logistics.logiinfosvc.hr.service.HRService;
import kr.co.seoulit.logistics.logiinfosvc.hr.to.AuthorityGroupTO;
import kr.co.seoulit.logistics.logiinfosvc.hr.to.AuthorityInfoGroupTO;
import kr.co.seoulit.logistics.sys.util.DatasetBeanMapper;

@RestController
@RequestMapping(value = "/hr/*")
public class AuthorityGroupController {
	
	@Autowired
	private HRService hrService;
	@Autowired
	private DatasetBeanMapper datasetBeanMapper;
	
	ModelMap map = null;
		
	//권한조회
	@RequestMapping(value="/authoritygroup/user")
	public void getUserAuthorityGroup(@RequestAttribute("reqData")PlatformData reqData,
								@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		String empCode = reqData.getVariable("empCode").getString();
		
		ArrayList<AuthorityGroupTO> authorityGroupTOList = hrService.getUserAuthorityGroup(empCode);
		datasetBeanMapper.beansToDataset(resData, authorityGroupTOList, AuthorityGroupTO.class);
	}
	
	//권한저장
	@RequestMapping(value="/modifyEmployeeAuthorityGroup")
	public void modifyEmployeeAuthorityGroup(@RequestAttribute("reqData")PlatformData reqData,
			@RequestAttribute("resData")PlatformData resData) throws Exception {
	
		ArrayList<AuthorityGroupTO> authGroupTO = (ArrayList<AuthorityGroupTO>) datasetBeanMapper.datasetToBeans(reqData, AuthorityGroupTO.class);
		String empCode = reqData.getVariableList().getString("empCode");
		for(AuthorityGroupTO authGroup : authGroupTO) {
			String authority=authGroup.getAuthority();
			
			if(authority.equals("1")) {
				hrService.insertEmployeeAuthorityGroup(empCode, authGroup.getAuthorityGroupCode() );
			}
		}
	}
	
	@RequestMapping(value="/authoritygroup")
	public void getAuthorityGroup(@RequestAttribute("reqData")PlatformData reqData,
							@RequestAttribute("resData")PlatformData resData) throws Exception {
		
		ArrayList<AuthorityInfoGroupTO> authorityGroupTOList = hrService.getAuthorityGroup();
		datasetBeanMapper.beansToDataset(resData, authorityGroupTOList, AuthorityInfoGroupTO.class);
	
	}
	
}
