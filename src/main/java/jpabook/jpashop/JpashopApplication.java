package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		return hibernate5Module;

	}

	/**
	 * hibernate5Module 는 기본적으로 LAZY 로딩인 컬럼에 대해서는 엔티티 조회 시 null 로 출력
	 * configure 메서드로 설정 변경 가능 hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true); 이렇게
	 * 근데 이 옵션은 쓰지 마시오.
	 */

}
