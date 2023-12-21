package pe.nico.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.nico.modelo.Pelicula;

public interface IPeliculaRepository extends JpaRepository<Pelicula, Integer>{

}
