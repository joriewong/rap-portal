package com.rap.portal.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
     * @param curUserId
     * @param corpId
     * @return
     */
    @Path("/productlines")
    @GET
    public String getProductlines(@QueryParam("curuserid") int curUserId,
    		@QueryParam("corpid") int corpId){
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
     * @param curUserId
     * @param corpId
     * @return
     */
    @Path("/create")
    @POST
    public String createProductline(@QueryParam("name") String name,
    		@QueryParam("curuserid") int curUserId,
    		@QueryParam("corpid") int corpId) {
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
     * @param curUserId
     * @param id
     * @return
     */
    @Path("/delete")
    @DELETE
    public String deleteProductline(@QueryParam("curuserid") int curUserId,
    		@QueryParam("id") int id) {
    	if (!organizationMgr.canUserManageProductionLine(curUserId, id)) {
            return error;
        }
    	return organizationMgr.removeProductionLine(id).toString();
    }
    /**
	 * 修改产品线
	 * @param curUserId
	 * @param id
	 * @param name
	 * @return
	 */
	@Path("/update")
	@PUT
	public String updateGroup(@QueryParam("curuserid") int curUserId,
			@QueryParam("id") int id,@QueryParam("name") String name) {
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
