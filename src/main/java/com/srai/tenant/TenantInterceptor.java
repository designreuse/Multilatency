//package com.srai.tenant;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//import com.srai.model.MasterTenant;
//import com.srai.model.repository.MasterTenantRepository;
//import com.srai.tenant.hibernate.JwtTokenUtil;
//
//import java.util.Objects;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Component
//public class TenantInterceptor extends HandlerInterceptorAdapter {
//
//	private static final String TENANT_HEADER = "X-TenantID";
//
//	@Autowired
//	private MasterTenantRepository masterRepo;
//	@Autowired
//	private JwtTokenUtil jwtTokenUtil;
//
//	@Override
//	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
//		String tenant = req.getHeader("Authorization");
//		String audience = jwtTokenUtil.getAudienceFromToken(tenant);
//		boolean tenantSet = false;
//		if (StringUtils.isEmpty(tenant)) {
//			TenantContext.setCurrentTenant(TenantContext.DEFAULT_TENANT);
//			tenantSet = true;
//		} else {
//			MasterTenant tenantDetail = masterRepo.findByDatabaseName(tenant);
//			if (Objects.isNull(tenantDetail)) {
//				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//				res.setContentType(MediaType.APPLICATION_JSON_VALUE);
//				res.getWriter().write("{\"error\": \"No Database Schema Found\"}");
//				res.getWriter().flush();
//			} else {
//				TenantContext.setCurrentTenant(tenantDetail.getDatabaseName());
//				tenantSet = true;
//			}
//		}
//		return tenantSet;
//	}
//
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			ModelAndView modelAndView) throws Exception {
//		TenantContext.clear();
//	}
//}