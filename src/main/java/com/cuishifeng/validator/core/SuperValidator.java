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
import java.util.stream.Collectors;

import org.apache.ibatis.reflection.property.PropertyNamer;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.cuishifeng.validator.constant.Constants;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * @author cuishifeng <cuishifeng0207@163.com>
 * Created on 2021-08-12
 */
public class SuperValidator {

    private static volatile SuperValidator superValidator = null;
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();


    public static SuperValidator getInstance() {
        if (superValidator == null) {
            synchronized (SuperValidator.class) {
                if (superValidator == null) {
                    superValidator = new SuperValidator();
                }
            }
        }
        return superValidator;
    }


    public <T, R> ValidationResult validateProperty(T object, SFunction<T, R> sFunction, Class<?>... groups) {
        return validateProperty(true, object, sFunction, groups);
    }

    public <T, R> ValidationResult validateProperty(boolean condition, T object, SFunction<T, R> sFunction, Class<?>... groups) {
        return validateProperty(condition, object, getFiledName(sFunction), groups);
    }


    public <T> ValidationResult validateObj(boolean condition, T object, Class<?>... groups) {
        if (!condition) {
            return new ValidationResult(false);
        }
        Assert.notNull(object, "SuperValidator validateProperty first parameter is null");
        Set<ConstraintViolation<T>> violationSet = VALIDATOR.validate(object, groups);
        return ofValidationResult(violationSet);
    }

    public <T> ValidationResult validateProperty(boolean condition, T object, String propertyName, Class<?>... groups) {
        if (!condition) {
            return new ValidationResult(false);
        }
        // todo Assert 要替换掉 因为抛出的异常是mybatis plus的
        Assert.notNull(object, "SuperValidator validateProperty first parameter is null");
        Assert.notNull(object, "SuperValidator validateProperty second parameter is null");
        Set<ConstraintViolation<T>> violationSet = VALIDATOR.validateProperty(object, propertyName, groups);
        return ofValidationResult(violationSet);
    }

    private <T> ValidationResult ofValidationResult(Set<ConstraintViolation<T>> violations) {
        ValidationResult<T> validationResult = new ValidationResult(violations);
        Set<String> set = violations.stream()
                .map(m -> String.format(Constants.FILED_CONTENT_IS_ERROR, m.getPropertyPath(), m.getMessage()))
                .collect(Collectors.toSet());
        validationResult.setErrorMessages(set);
        return validationResult;
    }


    public <R, T> String getFiledName(SFunction<R, T> sFunction) {
        LambdaMeta lambdaMeta = LambdaUtils.extract(sFunction);
        return PropertyNamer.methodToProperty(lambdaMeta.getImplMethodName());
    }


}
