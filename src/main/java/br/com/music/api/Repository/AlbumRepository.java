package br.com.music.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.music.api.Domain.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {

}
