package br.com.music.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.music.api.Domain.Artista;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {

}
