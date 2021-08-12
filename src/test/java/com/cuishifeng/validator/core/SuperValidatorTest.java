/*
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.cuishifeng.validator.core;


import java.util.Set;

import org.apache.ibatis.reflection.property.PropertyNamer;
import org.junit.Test;

import com.cuishifeng.validator.entity.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * @author cuishifeng <cuishifeng0207@163.com>
 * Created on 2021-08-12
 */
public class SuperValidatorTest {


    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void test() throws Exception {
        User user = new User("");

        String filedName = SuperValidator.getInstance().getFiledName(User::getName);

        Set<ConstraintViolation<User>> violations = VALIDATOR.validateProperty(user, filedName);

        ConstraintViolation<User> userConstraintViolation = violations.stream().findFirst().get();

        violations.stream().forEach(c -> System.out.println(c.getMessage()+" - "+
                c.getConstraintDescriptor()));
    }

    @Test
    public void test1() throws Exception {
        String methodName = SuperValidator.getInstance().getFiledName(User::getCode);
        System.out.println("methodName:" + methodName);
        String filedName = PropertyNamer.methodToProperty(methodName);
        System.out.println("filedName:" + filedName);

    }
}