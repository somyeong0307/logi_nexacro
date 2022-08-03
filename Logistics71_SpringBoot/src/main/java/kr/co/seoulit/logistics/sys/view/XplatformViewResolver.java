package kr.co.seoulit.logistics.sys.view;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class XplatformViewResolver implements ViewResolver {
	
    @Override
    public View resolveViewName(String s, Locale locale) throws Exception {
        return (map, httpServletRequest, httpServletResponse) -> {};
    }
}
