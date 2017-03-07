package com.flyhero.flyapi.web;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.jms.Destination;

import com.alibaba.fastjson.JSONObject;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.flyhero.flyapi.pojo.JSONResult;
import com.flyhero.flyapi.service.ModuleService;
import com.flyhero.flyapi.service.ProjectService;
import com.flyhero.flyapi.service.UserProjectService;
import com.flyhero.flyapi.service.UserService;
import com.flyhero.flyapi.service.InterfaceService;
import com.flyhero.flyapi.utils.Constant;
import com.flyhero.flyapi.utils.IPAddressUtil;
import com.flyhero.flyapi.utils.Md5Util;
import com.flyhero.flyapi.activemq.producer.ProducerService;
import com.flyhero.flyapi.entity.Module;
import com.flyhero.flyapi.entity.Project;
import com.flyhero.flyapi.entity.User;
import com.flyhero.flyapi.entity.UserProject;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户控制器
 * 
 * @ClassName: UserController
 * @author flyhero(http://flyhero.top)
 * @date 2016年10月28日 下午5:50:47
 *
 */
@Controller
@RequestMapping("user")
public class UserController extends BaseController {

	private Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private UserProjectService userProjectService;
	@Autowired
	private ModuleService moduleService;
	@Autowired
	private InterfaceService InterfaceService;
	@Autowired
	private ProjectService projectService;

	
	   @Autowired  
	   private ProducerService producerService;  
	   @Autowired  
	   private Destination demoQueueDestination;  
	     
	   @RequestMapping("mq")
	   public void testSend() {  
	   	logger.info("this is a test");
	       for (int i=0; i<2; i++) {  
	           producerService.sendMessage(demoQueueDestination, "你好，生产者！这是消息：" + (i+1));  
	       }  
	   }  
	/**
	 * restful
	 * @Title: get 
	 * @author flyhero(http://flyhero.top)  
	 * @date 2017年3月1日 上午10:06:17 
	 * @param @param id
	 * @param @return   
	 * @return JSONResult    
	 * @throws
	 */
	@RequestMapping(value = "/hello/{id}", method = RequestMethod.GET)
	@ResponseBody
	public JSONResult get(@PathVariable("id") Integer id) {
		System.out.println("get" + id);
		return new JSONResult(Constant.MSG_OK, Constant.CODE_200);
	}

	/**
	 * 用户注册
	 * 
	 * @Title: register
	 * @author flyhero
	 * @date 2016年10月12日
	 * @param @param user
	 * @param @return
	 * @param @throws Exception 参数
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "register.do")
	@ResponseBody
	public JSONResult register(User user) throws Exception {
		String ip = IPAddressUtil.getIPAddr(request);
		user.setLoginIp(ip);
		user.setAddress(IPAddressUtil.getPosition(ip, "UTF-8"));
		user.setPassword(Md5Util.textToMD5L16(user.getPassword()));
		user.setCreateTime(new Date(System.currentTimeMillis()));
		user.setAvatarUrl("/static/images/head.jpg");
		int flag = userService.insertSelective(user);
		if (flag != 0) {
			userService.updateLoginCount(user);
			return new JSONResult(Constant.MSG_OK, Constant.CODE_200);
		}
		return new JSONResult(Constant.MSG_ERROR, Constant.CODE_200);
	}

	/**
	 * 验证用户名是否存在
	 * 
	 * @Title: checkUserName
	 * @author flyhero
	 * @date 2016年10月12日
	 * @param @param user
	 * @param @return 参数
	 * @return JSONObject 返回类型
	 * @throws
	 */
	@RequestMapping("checkUserName.do")
	@ResponseBody
	public JSONObject checkUserName(User user) {
		logger.info("正在验证用户:" + user.getUserName() + " 是否存在");
		User u = userService.findByUserName(user);
		if (u != null) {
			json.put("valid", false);
		} else {
			json.put("valid", true);
		}
		return json;
	}

	/**
	 * 用户登录
	 * 
	 * @Title: login
	 * @author flyhero
	 * @date 2016年10月12日
	 * @param @param user
	 * @param @return
	 * @param @throws Exception 参数
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "login.do")
	public ModelAndView login(User user) throws Exception {
		if (getCuUser() != null) {
			return new ModelAndView("redirect:/forward/main.html");
		}

		user.setPassword(Md5Util.textToMD5L16(user.getPassword()));
		User user1 = userService.findByLogin(user);
		if (user1 != null) {
			userService.updateLoginCount(user1);
			session.setAttribute("user", user1);
			return new ModelAndView("redirect:/forward/main.html");
		}
		mv.setViewName("register");
		return mv;
	}

	/**
	 * 
	 * @Title: logout
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author flyhero
	 * @date 2016年10月12日下午5:56:57
	 * @param @return
	 * @param @throws Exception 参数
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "logout.do")
	public String logout() throws Exception {
		session.removeAttribute("user");
		return "login";
	}

	@RequestMapping("goToModule.do")
	public ModelAndView goToModule(int projectId) {
		Project p = projectService.selectByPrimaryKey(projectId);
		UserProject up = new UserProject();
		User u = (User) session.getAttribute("user");
		up.setUserId(u.getUserId());
		up.setProjectId(projectId);
		UserProject up1 = userProjectService.selectByIdAndPro(up);
		mv.addObject("project", p);
		mv.addObject("up", up1);
		mv.setViewName("moduleList");
		return mv;
	}

	@RequestMapping("goToInterface.do")
	public ModelAndView goToInterface(int moduleId, int isEdit,
			String projectName) {
		String name = null;
		try {
			name = new String(projectName.getBytes("iso8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Module m = moduleService.selectByPrimaryKey(moduleId);
		mv.addObject("module", m);
		mv.addObject("isEdit", isEdit);
		mv.addObject("projectName", name);
		mv.setViewName("interfaceList");
		return mv;
	}

}
