package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
//component也是个bean? Slf4j记录日志的
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点,(..)表示对所有的类中所有的方法匹配所有的参数类型,且满足方法上有AutoFill这个注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")  //表示执行于
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充");

        //1.获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//用方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //从注解得到方法? 视频：获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型
        //2.获取当前被拦截方法参数--实体对象
        Object [] args = joinPoint.getArgs();
        if (args == null || args.length==0){
            return;
        }
        Object entity = args[0];
        //3.准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据当前不同操作类型，为对应属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //为4个公共字段赋值
            try {
//                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime",LocalDateTime.class);
//                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser",Long.class);
//                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
//                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser",Long.class);
                //使用常量代替字符串
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                //利用反射为对象赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            //为两个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                //利用反射为对象赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
        }

    }

}
