package fr.amou.perso.app.rasen.robot.context;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "fr.amou.perso")
public class RasendeContext {

	@Bean
	public Unmarshaller unmarshaller() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance("fr.amou.perso.app.rasen.robot.xsd");
		return jaxbContext.createUnmarshaller();

	}

	@Bean
	public Random random() throws NoSuchAlgorithmException {
		return SecureRandom.getInstanceStrong();
	}
}
