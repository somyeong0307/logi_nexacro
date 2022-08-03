package kr.co.seoulit.logistics.sys.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.Debugger;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformRequest;
import com.tobesoft.xplatform.tx.HttpPlatformResponse;
import com.tobesoft.xplatform.tx.PlatformType;


@Component
public class XplatformInterceptor extends HandlerInterceptorAdapter {

	/* 전처리 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    	// HTTP 요청으로부터 데이터(PlatformData)를 수신받는다.
    
    	
        HttpPlatformRequest httpPlatformRequest = new HttpPlatformRequest(request);
        
        /*
		송수신 형식(contentType)이 설정되지 않은 경우 HTTP 헤더의 ContentType 값으로부터 판단하며, 다음과 같이 적용된다.
		HTTP 헤더의 ContentType		적용되는 송수신 형식(contentType)
		text/xml					PlatformType.CONTENT_TYPE_XML
		application/octet-stream	PlatformType.CONTENT_TYPE_BINARY
		그 외							PlatformType.DEFAULT_CONTENT_TYPE
		*/
        
        //// XML parsing
     // 클라이언트 쪽에서 넘어온 xml데이터를 할당받는 부분
        httpPlatformRequest.receiveData(); 

        // 엑플에서 transaction 요청할 때, 클라이언트쪽에서 넘어온 데이터를 받기 위한 PlatformData
        PlatformData reqData = httpPlatformRequest.getData();
    
     
        // 서버에서  클라이언트에게 보내기위해 객체 생성 
        PlatformData resData = new PlatformData();
     
        //클라이언트쪽에서 받아온 데이터셋과 변수를 debug
        debug(reqData.getDataSetList(), reqData.getVariableList());

        // HttpServletRequest 객체에 set을 해준뒤 컨트롤러에서 getAttribute()꺼낼수있다.
        request.setAttribute("reqData", reqData);
        request.setAttribute("variableList", reqData.getVariableList());
        request.setAttribute("resData", resData);
        
        System.out.println("		@XplatformInterceptor 의 preHandle 성공(true)");
        System.out.println("요청컨트롤러"+handler.toString());
       
        return true;
    }

    
    
    
    
    
    
    /* 컨트롤러 진입 후 view가 랜더링 되기 전 수행이 됩니다. */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	
    	
    	System.out.println("		@XplatformInterceptor 의 postHandle 성공(true)");
    	super.postHandle(request, response, handler, modelAndView);
    }

    
    /* 컨트롤러 진입 후 view가 정상적으로 랜더링 된 후 제일 마지막에 실행이 되는 메서드입니다. */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
    	
    	//컨트롤러단에서 HttpServletRequest객페에 setAttribute 해줬던걸 꺼낸다.
        PlatformData resData = (PlatformData) request.getAttribute("resData");

        
          System.out.println("		@XplatformInterceptor 의     afterCompletion 성공(true)");
        
        // 단일 데이터를 가지고 있는 Variable들을 저장한다. Variable은 식별자(name) 또는 위치(index)를 통하여 참조할 수 있다.
        VariableList variableList = resData.getVariableList();

        //서버에서 exception 발생여부를 확인후 아래 변수에 값을 넣어 view단으로 보내 콜백함수 호출시  이용!
        if (exception != null) { //실패
            variableList.add("ErrorCode", -1);
            variableList.add("ErrorMsg", exception.getMessage());
        } else {//성공
            variableList.add("ErrorCode", 0);
            variableList.add("ErrorMsg", "success");
        }

        // HTTP 응답으로 데이터(PlatformData)를 송신한다.
        // HttpServletResponse, 송수신 형식(contentType)과 문자셋(charset)을 가지는 생성자이다.
        // HttpServletResponse  객체를 이용하여 HttpPlatformResponse 생성
        // 생성자   (HttpServletResponse httpRes, String contentType, String charset)
        HttpPlatformResponse httpPlatformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, "UTF-8");

        //데이터 셋팅
        //(PlatformData data)
        httpPlatformResponse.setData(resData);
        // 데이터 송신
        httpPlatformResponse.sendData();

        //디버그
        debug(resData.getDataSetList(), resData.getVariableList());

        resData = null;
        super.afterCompletion(request, response, handler, exception);
    }

    private void debug(DataSetList dataSetList, VariableList variableList) {
        
        
        //투비소프트 제공
    	Debugger debugger = new Debugger();
        
    	// DEBUG - DataSet
        int dataSetListSize = dataSetList.size();
        for (int n = 0; n < dataSetListSize; n++) {
        	
        	// 개발시에 유용한 DataSetList의 자세한 정보를 반환한다.(DataSet ds)
            System.out.println("디버그 dataSetList:"+debugger.detail(dataSetList.get(n)));
        }
        
        
        // DEBUG - VariableList 변수
        int variableListSize = variableList.size();
        for (int n = 0; n < variableListSize; n++) {
        	
        	// 개발시에 유용한 VariableList의 자세한 정보를 반환한다.
            System.out.println("VariableList:"+debugger.detail(variableList.get(n)));
        }
    }
}