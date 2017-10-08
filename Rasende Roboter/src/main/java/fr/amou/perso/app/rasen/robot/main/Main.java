package fr.amou.perso.app.rasen.robot.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import fr.amou.perso.app.rasen.robot.controller.Controller;

@Configuration
@ComponentScan(basePackages = "fr.amou.perso")
public class Main {

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        Controller controller = context.getBean(Controller.class);
        try {
            controller.run();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
