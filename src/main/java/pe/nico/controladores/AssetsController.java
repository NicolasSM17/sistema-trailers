package pe.nico.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.nico.servicio.impl.AlmacenServiceImpl;

@RestController
@RequestMapping("/assets")
public class AssetsController {
	
	@Autowired
	private AlmacenServiceImpl service;
	
	@GetMapping("/{filename:.+}")
	public Resource obtenerRecurso(@PathVariable("filename") String filename) {
		return service.cargarComoRecurso(filename);
	}
}
