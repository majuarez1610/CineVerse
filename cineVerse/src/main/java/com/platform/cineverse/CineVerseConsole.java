package com.platform.cineverse;

import com.platform.cineverse.main.Main;
import com.platform.cineverse.repository.TvShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CineVerseConsole implements CommandLineRunner {

	@Autowired
	private TvShowRepository repository;
	public static void main(String[] args) {
		SpringApplication.run(CineVerseConsole.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Main principal = new Main(repository);
		principal.muestraElMenu();




	}
}
