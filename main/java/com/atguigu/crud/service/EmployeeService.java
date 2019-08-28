package com.atguigu.crud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.crud.bean.DepartmentExample.Criteria;
import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.bean.EmployeeExample;
import com.atguigu.crud.dao.EmployeeMapper;
@Service
public class EmployeeService {

	@Autowired
	EmployeeMapper employeeMapper;
	//查询所有员工
	public List<Employee> getAll() {
		
		return employeeMapper.selectByExampleWithdept(null);
	}
	//员工保存
	public void saveEmp(Employee employee) {
		employeeMapper.insertSelective(employee);
	}
	//校验用户名是否可用：true则代表当前姓名可用，false则代表不可用
	public boolean checkUser(String empName) {
		EmployeeExample example=new EmployeeExample();
		com.atguigu.crud.bean.EmployeeExample.Criteria criteria=example.createCriteria();
		criteria.andEmpNameEqualTo(empName);
		long count=employeeMapper.countByExample(example);
		return count==0;
	}
	//按照员工id查询员工
	public Employee getEmp(Integer id) {
		Employee employee=employeeMapper.selectByPrimaryKey(id);
		return employee;
	}
	//员工更新
	public void updateEmp(Employee employee) {
		employeeMapper.updateByPrimaryKeySelective(employee);
	}
	public void deleteEmp(Integer id) {
		employeeMapper.deleteByPrimaryKey(id);
	}
	public void deleteBatch(List<Integer> ids) {
		EmployeeExample example=new EmployeeExample();
		com.atguigu.crud.bean.EmployeeExample.Criteria criteria=example.createCriteria();
		criteria.andEmpIdIn(ids);
		employeeMapper.deleteByExample(example);
	}

	
}
