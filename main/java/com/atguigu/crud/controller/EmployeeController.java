package com.atguigu.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.SysexMessage;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.bean.Msg;
import com.atguigu.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
@Controller
//处理员工CRUD请求
public class EmployeeController {
	@Autowired
	EmployeeService employeeService;
	
	/*单个批量二合一：
	批量删除：1-2-3
	单个删除：1*/
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg deleteEmpById(@PathVariable("ids")String ids) {
		if(ids.contains("-")) {
			List<Integer> del_ids=new ArrayList<>();
			String[] str_ids=ids.split("-");
			for(String string:str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		}else {
			Integer id=Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		return Msg.success();
		
	}
	/*AJAX发送PUT请求引发的血案：
		PUT请求，请求体中的数据，request,getParameter("empName")拿不到
		Tomcat一看是PUT不会封装请求体中的数据为map,只有POST形式的请求才封装请求体为map
	原因：
		Tomcat：1、将请求体中的数据，封装一个map
			2、request.getParameter("empName")就会从这个map中取值
			3、Springmvc封装POJO对象时，会把POJO中每个属性的值，request.getParameter("email");
	员工跟新方法*/
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.POST)
	public Msg saveEmp(Employee employee) {
		employeeService.updateEmp(employee);
		return Msg.success();
		
	}
	
	//根据id查询员工
	@RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id) {
		Employee employee=employeeService.getEmp(id);
		return Msg.success().add("emp", employee);
	}
	@RequestMapping("/checkeuser")
	@ResponseBody//返回json数据
	public Msg checkUser(@RequestParam("empName")String empName) {
		//先判断用户名是否是合法的表达式
		String regx="(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
		if(!empName.matches(regx)) {
			return Msg.fail().add("va_msg", "用户名必须是6-16位数字字母组合或2-6中文");
		}
		//以上校验成功后，数据库才能进行用户名重复校验（ajax发送请求，对应的是前端校验）
		boolean b=employeeService.checkUser(empName);
		if(b) {
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg", "用户名不可用");
		}
	}
	
	//员工保存:1.支持JSR303校验     2.导入Hibernate-Validator
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee,BindingResult result) {
		System.out.print("提交成功"+"   "+employee.toString());
		if(result.hasErrors()) {

			//校验失败，应该返回失败，在模态框中显示校验失败的错误信息
			Map<String,Object> map=new HashMap<>();
			List<FieldError> errors=result.getFieldErrors();
			for(FieldError fieldError:errors) {
				System.out.println("错误的字段名"+fieldError.getField());
				System.out.println("错误信息："+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(employee);
			return Msg.success();
		}
	}
	
	//ResponseBody这个注解就可以使返回json数据;需要导入jackson包
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue="1")Integer pn) {
		PageHelper.startPage(pn,5);
		List<Employee> emps=employeeService.getAll();
		for(Employee e:emps) {
			System.out.print(e.toString());
		}
		PageInfo page=new PageInfo(emps,5);
		return Msg.success().add("pageInfo", page);
		
	}
	/*//查询员工页面（分页查询）
	@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn",defaultValue="1")Integer pn,Model model) {
		//这不是分页查询,引入PageHelper分页插件,在查询之前只需要调用，传入页码以及每页的大小(测试了一下，因为下面还有一共两个每页大小，这是起决定作用的5)
		PageHelper.startPage(pn,5);
		
		List<Employee> emps=employeeService.getAll();
		//使用pageInfo包装查询后的结果,只需要将pageIndo交给页面就行了，封装了详细的分页信息，包括有我们查询出来的数据;传入连续显示的页数
		PageInfo page=new PageInfo(emps,5);
		model.addAttribute("pageInfo",page);
		return "list";
	}*/
}
