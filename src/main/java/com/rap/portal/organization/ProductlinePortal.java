package com.rap.portal.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.taobao.rigel.rap.organization.bo.ProductionLine;
import com.taobao.rigel.rap.organization.service.OrganizationMgr;

@Path("/productline")
@Produces("application/json;charset=UTF-8")
public class ProductlinePortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private OrganizationMgr organizationMgr = context.getBean("organizationMgr", OrganizationMgr.class);
    private Gson gson = new Gson();
    private String error = "{\"msg\":\"error\"}";
	private String success = "{\"msg\":\"success\"}";
    /**
     * 产品线列表
     * @param corpId
     * @return
     */
    @Path("/productlines")
    @GET
    public String getProductlines(@Context HttpServletRequest request,
    		@QueryParam("corpid") int corpId){
    	HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
    	if (!organizationMgr.canUserAccessCorp(curUserId, corpId)) {
            return error;
        }
    	Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<ProductionLine> lineModels = organizationMgr
                .getProductionLineList(corpId);
        for (ProductionLine lineModel : lineModels) {
            Map<String, Object> line = new HashMap<String, Object>();
            line.put("id", lineModel.getId());
            line.put("name", lineModel.getName());
            line.put("count", lineModel.getProjectNum());
            items.add(line);
        }
        result.put("items", items);
    	return gson.toJson(result);
    }
    /**
     * 创建产品线
     * @param name
     * @param corpId
     * @return
     */
    @Path("/create")
    @POST
    public String createProductline(@QueryParam("name") String name,
    		@Context HttpServletRequest request,
    		@QueryParam("corpid") int corpId) {
    	HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
    	if (!organizationMgr.canUserManageProductionLineList(curUserId, corpId)) {
            return error;
        }
    	ProductionLine line = new ProductionLine();
    	line.setName(name);
    	line.setUserId(curUserId);
    	line.setCorporationId(corpId);
    	int id = organizationMgr.addProductionLine(line);
    	Map<String, Object> p = new HashMap<String, Object>();
    	p.put("id", id);
    	p.put("name", name);
    	return "{\"items\":[" + gson.toJson(p) + "]}";
    }
    /**
     * 删除产品线
     * @param id
     * @return
     */
    @Path("/delete")
    @DELETE
    public String deleteProductline(@Context HttpServletRequest request,
    		@QueryParam("id") int id) {
    	HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
    	if (!organizationMgr.canUserManageProductionLine(curUserId, id)) {
            return error;
        }
    	return organizationMgr.removeProductionLine(id).toString();
    }
    /**
	 * 修改产品线
	 * @param id
	 * @param name
	 * @return
	 */
	@Path("/update")
	@PUT
	public String updateGroup(@Context HttpServletRequest request,
			@QueryParam("id") int id,@QueryParam("name") String name) {
		HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
		if (!organizationMgr.canUserManageProductionLine(curUserId, id)) {
            return error;
        }
		
        ProductionLine line = new ProductionLine();
        line.setId(id);
        line.setName(name);
        organizationMgr.updateProductionLine(line);
        return success;
	}
}
