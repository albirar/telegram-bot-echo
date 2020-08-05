package cat.albirar.telegram.bots.echo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import cat.albirar.telegram.bots.echo.service.ServeiEcho;

@SpringBootApplication
@ComponentScan(basePackageClasses = {BotEcho.class, ServeiEcho.class})
public class BotEchoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotEchoApplication.class, args);
	}

}
