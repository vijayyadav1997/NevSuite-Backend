package net.naffets.nevsuite.backend.framework.domain.repository.persistent;

import net.naffets.nevsuite.backend.framework.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author br4sk4 / created on 12.10.2017
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
