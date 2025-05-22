package com.javafx.javafxv1;

import com.javafx.javafxv1.config.AppConfig;
import com.javafx.javafxv1.ui.ClienteForm;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class Javafxv1Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				new SpringApplicationBuilder(AppConfig.class).headless(false).run(args);

		SwingUtilities.invokeLater(() -> {
			ClienteForm form = context.getBean(ClienteForm.class);
			form.setVisible(true);
		});
	}

}
