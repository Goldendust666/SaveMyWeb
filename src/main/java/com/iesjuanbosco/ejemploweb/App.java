package com.iesjuanbosco.ejemploweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

	//Comentario de ejemplo
	public static void main(String[] args) {

		//Extraemos un objeto que va a contener el "contexto de la app", y desde él podemos acceder a todos los beans (controladores, repositorios, etc.)
		ApplicationContext contex = SpringApplication.run(App.class, args);

		//El siguiente código se ejecutará cada vez que se ejecute la app

		/*Así podemos acceder a un Bean (un respositorio, un controlador, una entidad, etc.) de Spring
		para, por ejemplo, insertar datos de ejemplo nada más ejecutar la app
		*/

		/*
		AnuncioRepository productoRepository =  contex.getBean(AnuncioRepository.class);
		CategoriaRepository categoriaRepository = contex.getBean(CategoriaRepository.class);

		Categoria categoria = Categoria
				.builder()
				.nombre("Móviles")
				.descripcion("Los mejores móviles")
				.productos(new ArrayList<Anuncio>())	//Al ser una categoría nueva, creamos el arrayList de los productos. Al utilizar el patrón Builder no se inicializa el ArrayList aunque lo hayamos puesto en la clase
				.build();
		Anuncio p1 = Anuncio.builder()
				.cantidad(50)
				.precio(500.0)
				.titulo("Anuncio 1")
				.categoria(categoria)
				.build();
		Anuncio p2 = Anuncio.builder()
				.cantidad(100)
				.precio(200.0)
				.titulo("Anuncio 2")
				.categoria(categoria)
				.build();

		categoria.getAnuncio().add(p1);
		categoria.getAnuncio().add(p2);

		//Como la relación tiene CASCADE.ALL se guardan en cascada y guarda los productos de la categoría
		categoriaRepository.save(categoria);

		 */


	}

}
