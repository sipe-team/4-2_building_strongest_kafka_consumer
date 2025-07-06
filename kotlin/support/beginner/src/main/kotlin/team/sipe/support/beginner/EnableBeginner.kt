package team.sipe.support.beginner

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Configuration
@ComponentScan(basePackages = ["team.sipe.support.beginner"])
annotation class EnableBeginner
