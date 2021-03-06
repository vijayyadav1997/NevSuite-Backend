package net.naffets.nevsuite.backgroundprocesses.domain.repository;

import net.naffets.nevsuite.backgroundprocesses.domain.entity.BackgroundProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author br4sk4
 * created on 03.07.2016
 */
@Repository
public interface BackgroundProcessRepository extends JpaRepository<BackgroundProcess, String> {
}
