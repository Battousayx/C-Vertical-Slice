package br.com.music.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.music.api.Domain.ArtistaAlbum;

public interface ArtistaAlbumRepository extends JpaRepository<ArtistaAlbum, Long> {

}

