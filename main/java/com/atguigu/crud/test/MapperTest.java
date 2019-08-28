package com.atguigu.crud.test;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atguigu.crud.bean.Department;
import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.dao.DepartmentMapper;
import com.atguigu.crud.dao.EmployeeMapper;

/*测试dao层的工作,推荐Spring的项目可以使用Spring的单元测试，可以自动注入我们需要的组件
1.导入SpringTest模块
2.@ContextConfiguration指定Spring配置文件的位置
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:applicationContext.xml"})
public class MapperTest {
	@Autowired
	DepartmentMapper departmentMapper;
	@Autowired
	EmployeeMapper employeeMapper;
	@Autowired
	SqlSession sqlSession;
	//测试departmentMapper
	@Test
	public void testCRUD() {
		/*1.创建springioc容器
		ApplicationContext ioc=new ClassPathXmlApplicationContext("applicationContext.xml");
		2.从容器中获取mapper
		DepartmentMapper bean=(DepartmentMapper) ioc.getBean("DepartmentMapper.class");
		System.out.println(departmentMapper);*/
		
		
		//1.插入几个部门
		/*departmentMapper.insertSelective(new Department(null,"development department"));
		departmentMapper.insertSelective(new Department(null,"test depaertment"));*/
		//2.生成员工数据，测试员工插入
		employeeMapper.insertSelective(new Employee(null,"jerry","M","jerry@atguigu.com",1));
		//3.批量插入多个员工，使用可以执行批量操作的sqlSession
		/*for() {
			employeeMapper.insertSelective(new Employee(null,"jerry","M","jerry@atguigu.com",1));
		}*/
		EmployeeMapper mapper=sqlSession.getMapper(EmployeeMapper.class);
		for(int i=0;i<1000;i++) {
			String uid=UUID.randomUUID().toString().substring(0, 5)+i;
			mapper.insertSelective(new Employee(null,uid,"M",uid+"@atguigu.com",i%2==0?1:2));
		}
		System.out.println("批量完成");
	}
}
