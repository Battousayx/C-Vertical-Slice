package br.com.music.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.music.api.Domain.AlbumImagem;

public interface AlbumImagemRepository extends JpaRepository<AlbumImagem, Long> {

}
