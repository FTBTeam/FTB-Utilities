package latmod.ftbu.api.readme;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ReadmeInfo
{
	public String key() default "";
	public String info();
	public String def() default "";
}