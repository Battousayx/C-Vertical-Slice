package br.com.music.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.music.api.Domain.Regional;

public interface RegionalRepository extends JpaRepository<Regional, Integer> {

}

