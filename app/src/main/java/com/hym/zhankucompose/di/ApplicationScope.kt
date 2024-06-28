package com.hym.zhankucompose.di

import me.tatarka.inject.annotations.Scope

/**
 * @author hehua2008
 * @date 2024/6/28
 */
@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class ApplicationScope
