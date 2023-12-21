package pe.nico.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pe.nico.modelo.Genero;
import pe.nico.modelo.Pelicula;
import pe.nico.repositorios.IGeneroRepository;
import pe.nico.repositorios.IPeliculaRepository;
import pe.nico.servicio.impl.AlmacenServiceImpl;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private IPeliculaRepository peliculaRepository;
	
	@Autowired
	private IGeneroRepository generoRepository;
	
	@Autowired
	private AlmacenServiceImpl service;
	
	@GetMapping("")
	public ModelAndView verPaginaDeInicio(@PageableDefault(sort = "titulo", size = 5) Pageable pageable) {
		Page<Pelicula> peliculas = peliculaRepository.findAll(pageable);
		
		return new ModelAndView("admin/index").addObject("peliculas", peliculas);
	}
	
	@GetMapping("/peliculas/nuevo")
	public ModelAndView mostrarFormularioDeNuevaPelicula() {
		List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));
		
		return new ModelAndView("admin/nueva-pelicula")
				.addObject("pelicula", new Pelicula())
				.addObject("generos", generos);
	}
	
	@PostMapping("/peliculas/nuevo")
	public ModelAndView registrarPelicula(@Validated Pelicula pelicula, BindingResult bindingResult) {
		if(bindingResult.hasErrors() || pelicula.getPortada().isEmpty()) {
			if(pelicula.getPortada().isEmpty()) {
				bindingResult.rejectValue("portada", "MultipartNotEmpty");
			}
			
			List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));
			
			return new ModelAndView("admin/nueva-pelicula")
					.addObject("pelicula", pelicula)
					.addObject("generos", generos);
		}
		
		String rutaPortada = service.almacenarArchivo(pelicula.getPortada());
		pelicula.setRutaPortada(rutaPortada);	
		peliculaRepository.save(pelicula);
		
		return new ModelAndView("redirect:/admin");
	}
	
	@GetMapping("/peliculas/{id}/editar")
	public ModelAndView mostrarFormularioEditarPelicula(@PathVariable Integer id) {
		Pelicula pelicula = peliculaRepository.getOne(id);
		List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));
		
		return new ModelAndView("admin/editar-pelicula")
				.addObject("pelicula", pelicula)
				.addObject("generos", generos);
	}
	
	@PostMapping("/peliculas/{id}/editar")
	public ModelAndView actualizarPelicula(@PathVariable Integer id, @Validated Pelicula pelicula, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));
			
			return new ModelAndView("admin/editar-pelicula")
					.addObject("pelicula", pelicula)
					.addObject("generos", generos);
		}
		
		Pelicula peliculaDB = peliculaRepository.getOne(id);
		peliculaDB.setTitulo(pelicula.getTitulo());
		peliculaDB.setSinopsis(pelicula.getSinopsis());
		peliculaDB.setFechaEstreno(pelicula.getFechaEstreno());
		peliculaDB.setYoutubeTrailerId(pelicula.getYoutubeTrailerId());
		peliculaDB.setGeneros(pelicula.getGeneros());
		
		if(!pelicula.getPortada().isEmpty()) {
			service.eliminarArchivo(peliculaDB.getRutaPortada());
			String rutaPortada = service.almacenarArchivo(pelicula.getPortada());
			peliculaDB.setRutaPortada(rutaPortada);
		}
		
		peliculaRepository.save(peliculaDB);
		
		return new ModelAndView("redirect:/admin");
	}
	
	@PostMapping("/peliculas/{id}/eliminar")
	public String eliminarPelicula(@PathVariable Integer id) {
		Pelicula pelicula = peliculaRepository.getOne(id);
		peliculaRepository.delete(pelicula);
		service.eliminarArchivo(pelicula.getRutaPortada());
		
		return "redirect:/admin";
	}
}
