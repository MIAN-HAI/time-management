package com.example.time_management.repositories;
//数据库访问层，也称DAO层

//导入User实体类，让UserRepository可以访问到User实体类（或者说users表中）的数据
import com.example.time_management.models.User;


//Spring Data JPA 提供的接口，可以自动生成数据库的查询方法
import org.springframework.data.jpa.repository.JpaRepository;

//标记这个类时数据库访问层
import org.springframework.stereotype.Repository;

//防止查询结果为空返回null，避免NullPointerException
import java.util.Optional;

//该注解就是用来标记此类为数据库访问层的，spring会自动管理它
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  // 定义了一个接口，继承自JpaRepository接口，指定了User实体类和主键类型
  // 继承JpaRepository接口后，Spring Data JPA会自动生成一些基本的数据库操作方法，如查询、保存、删除等。
  // 所以无需手写SQL

  /*
   * save(User user) 保存用户（INSERT 或 UPDATE）
   * findById(Long id) 通过 id 查询用户
   * findAll() 查询所有用户
   * deleteById(Long id) 通过 id 删除用户
   * count() 获取用户总数
   */

  // 以下两个自定义的方法spring boot会自动实现这个方法，不需要手写SQL
  // 返回Optional对象，防止查询结果为空返回null，避免NullPointerException
  // 通过用户名查找用户
  Optional<User> findByUsername(String username);

  // 通过手机号码查找用户
  Optional<User> findByPhone(String phone);
}
